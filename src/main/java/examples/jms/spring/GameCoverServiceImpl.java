package examples.jms.spring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;

import examples.jms.GameCoverService;

/**
 * Using a {@link JmsTemplate} to send/receive the messages, the
 * Queue is dynamic from ActiveMQ and inject with a JNDI factory
 * bean. <br>
 * Receive timeout and Time to live is configured in xml file.
 */
public class GameCoverServiceImpl implements GameCoverService {
	private JmsTemplate jmsTemplate;
	private Queue replyToQueue;
	private Map<String, String> gameConsoleMap;

	/**
	 * Had to trick spring in order to get the sent message id. Since the spring
	 * approach doesn't allow to inspect the outgoing message once it's gone. <br>
	 * And the variable used to do the trick must be final, since it's called 
	 * inside a annonymous inner class, but if it's already set to null it can't
	 * be set to the message id value. So I used a array of one element as object
	 * holder and marked it final to do the trick.
	 */
	public byte[] getGameCover(final String sku) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try{
			final Message[] singleMessage = new Message[1];
			
			jmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(sku);
					message.setStringProperty("console", gameConsoleMap.get(sku));
					message.setJMSReplyTo(replyToQueue);
					singleMessage[0] = message;
					return message;
				}
			});
			
			String selector = "JMSCorrelationID = '" + singleMessage[0].getJMSMessageID() + "'";
			byte[] buffer =
				(byte[]) jmsTemplate.receiveSelectedAndConvert(replyToQueue, selector);
			
			if(buffer==null){
				return null;
			}
			
			os.write(buffer);	
		}catch(JMSException e){
			throw JmsUtils.convertJmsAccessException(e);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		
		return os.toByteArray();
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setReplyToQueue(Queue replyToQueue) {
		this.replyToQueue = replyToQueue;
	}

	public void setGameConsoleMap(Map<String, String> gameConsoleMap) {
		this.gameConsoleMap = gameConsoleMap;
	}

}
