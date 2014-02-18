package examples.jms.listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
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

public class GameCoverServerListener implements MessageListener, GameCoverService {
	
	private static Logger logger = Logger.getLogger(GameCoverServerListener.class);
	
	private QueueConnection connection;
	private Queue queue;
	private QueueSession session;
	
	private String connectionFactoryName;
	private String queueName;
	
	public GameCoverServerListener(){
		connectionFactoryName = "QueueCF";
		queueName = "dynamicQueues/GameCoverQueue";
	}
	
	public GameCoverServerListener(String connectionFactoryName,
			String queueName) {
		this.connectionFactoryName = connectionFactoryName;
		this.queueName = queueName;
	}

	public void connect(){
		try {
			Context context = new InitialContext();
			QueueConnectionFactory connectionFactory = 
				(QueueConnectionFactory)context.lookup(connectionFactoryName);
			queue = (Queue)context.lookup(queueName);
			
			connection = connectionFactory.createQueueConnection();
			session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
			
			String selector = "console <> '360' ";//do not process crappy xbox games
			QueueReceiver receiver = session.createReceiver(queue, selector);
			receiver.setMessageListener(this);
			
			connection.start();
			
		} catch (NamingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JMSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onMessage(Message message) {
		try{
			//process incoming messages
			TextMessage textMessage = (TextMessage)message;
			String sku = textMessage.getText();
			Queue replyTo = (Queue)textMessage.getJMSReplyTo();	
			
			//prepare the response message
			BytesMessage bytesMessage = session.createBytesMessage();
			bytesMessage.writeBytes(getGameCover(sku));
			bytesMessage.setJMSCorrelationID(message.getJMSMessageID());
			
			logger.debug("SKU: " + sku);
			logger.debug("Reply to: " + replyTo.getQueueName());
			logger.debug("Received message id: " + message.getJMSMessageID());
			
			//send the response message to the "replyTo" Queue
			QueueSender sender = session.createSender(replyTo);
			sender.send(bytesMessage);
			sender.close();
			
			//acknowledge manually in case of errors
			message.acknowledge();
		}catch(JMSException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] getGameCover(String sku) {
		String path = "images";
		String fileName = gameMap.get(sku);
		return readFromFile(path, fileName);
	}
	
	private byte[] readFromFile(String path, String fileName){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try{
			byte[] buffer = new byte[512];
			InputStream is = 
				this.getClass().getClassLoader().getResourceAsStream(path + "/" + fileName);
			int count = 0;
			while( (count = is.read(buffer)) != -1){
				os.write(buffer, 0, count);
			}	
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}
	
	public void closeConnection(){
		try {
			connection.close();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * I want to focus in JMS use, not the implementation of the service
	 */
	private Map<String, String> gameMap = new HashMap<String, String>(){{
		put("BLUS30109", "Tux.png");
		put("BCUS98111", "Tux.png");
		put("SLUS21115", "Tux.png");
	}};
	
}
