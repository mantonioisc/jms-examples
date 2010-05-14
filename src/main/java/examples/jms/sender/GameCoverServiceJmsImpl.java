package examples.jms.sender;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import examples.jms.GameCoverService;

public class GameCoverServiceJmsImpl implements GameCoverService {
	private static Logger logger = Logger.getLogger(GameCoverServiceJmsImpl.class);
	private Queue queue;
	private Queue replyTo;
	private QueueConnection connection;
	private QueueSession session;
	
	private String connectionFactoryName;
	private String queueName;
	private String replyQueueName;
	
	public GameCoverServiceJmsImpl(){
		connectionFactoryName = "QueueCF";
		queueName = "dynamicQueues/GameCoverQueue";
		replyQueueName = "dynamicQueues/GameCoverReplyQueue";
		
		init();
	}

	public GameCoverServiceJmsImpl(String connectionFactoryName,
			String queueName, String replyQueueName) {
		this.connectionFactoryName = connectionFactoryName;
		this.queueName = queueName;
		this.replyQueueName = replyQueueName;
		
		init();
	}
	
	protected void init(){
		try{
			Context context = new InitialContext();
			QueueConnectionFactory connectionFactory =
				(QueueConnectionFactory)context.lookup(connectionFactoryName);
			queue = (Queue)context.lookup(queueName);
			replyTo = (Queue)context.lookup(replyQueueName);
			
			connection = connectionFactory.createQueueConnection();
			logMetadata(connection.getMetaData());
	
			connection.start();
		}catch(NamingException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JMSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Implementing the request/response pattern with message id correlation 
	 * using reply queue in the outgoing message. <br>
	 * Also we use a custom property for server filtering and processing.
	 * @param sku the game code
	 * @return the bytes of the cover image 
	 */
	@Override
	public byte[] getGameCover(String sku) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			//session is single threaded, and multiple threads may call this method, so we prefer session per thread
			QueueSession session = 
				connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			
			QueueSender sender = session.createSender(queue);
			TextMessage message = session.createTextMessage(sku);
			//set the place where we want our response
			message.setJMSReplyTo(replyTo);
			//set some custom properties
			sender.setTimeToLive(30*1000);//avoid unprocessed messages to set idle in the queue for too long
			message.setStringProperty("console", consoleMap.get(sku));
			sender.send(message);
			
			logQueueMessages(session.createBrowser(replyTo));
			
			//search for our response message using correlation id
			String filter = "JMSCorrelationID = '" + message.getJMSMessageID() + "'";
			QueueReceiver receiver = session.createReceiver(replyTo, filter);
			BytesMessage responseMessage = (BytesMessage)receiver.receive(10*1000);
			
			if(responseMessage==null){
				logger.warn("No response message received for SKU: " + sku);
				session.close();
				return null;
			}
			
			logger.debug("SKU: " + sku);
			logger.debug("Correlation id: " + responseMessage.getJMSCorrelationID());
			
			int count = 0;
			byte[] buffer = new byte[512];
			while((count = responseMessage.readBytes(buffer)) != -1){
				os.write(buffer, 0, count);
			}
			
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	public void closeConnection(){
		try {
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Just log metadata from Connection to get an idea of
	 * how it looks like
	 * @param metaData
	 */
	@SuppressWarnings("unchecked")
	private void logMetadata(ConnectionMetaData metaData){
		try {
			logger.debug("JMSProviderName: " + metaData.getJMSProviderName());
			logger.debug("JMSVersion: " + metaData.getJMSVersion());
			logger.debug("JMSMajorVersion: " + metaData.getJMSMajorVersion());
			logger.debug("JMSMinorVersion: " + metaData.getJMSMinorVersion());
			logger.debug("providerVersion: " + metaData.getProviderVersion());
			logger.debug("JMSX Properties:");
			Enumeration names = metaData.getJMSXPropertyNames();
			while(names.hasMoreElements()){
				String name = names.nextElement().toString();
				logger.debug("\t" + name);
			}
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Browse the queue messages
	 * @param replyTo2
	 */
	@SuppressWarnings("unchecked")
	private void logQueueMessages(QueueBrowser browser) {
		try {
			Enumeration<Message> messages = browser.getEnumeration();
			while(messages.hasMoreElements()){
				Message message = messages.nextElement();
				logger.debug(message.getClass().getSimpleName() + ": " + message.getJMSMessageID() + "\t" + message.getJMSCorrelationID());
			}
			
			browser.close();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> consoleMap = new HashMap<String, String>(){{
		put("BLUS30109", "PS3");
		put("BCUS98111", "PS3");
		put("SLUS21115", "PS2");
		put("9UE00102", "360");
	}};
}
