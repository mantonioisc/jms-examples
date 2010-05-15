package examples.jms.publisher;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Single threaded class, since there will be only one announcer for
 * the cards. Only sends messages to topic nothing more.
 */
public class PlayCardPublisher {
	
	private static final Logger logger = Logger.getLogger(PlayCardPublisher.class);
	private String topicName;
	private String topicFactoryName;
	
	private TopicConnection connection;
	private TopicSession session;
	private Topic topic;
	
	public PlayCardPublisher(){
		this("dynamicTopics/CardTopic", "TopicCF");
	}

	public PlayCardPublisher(String topicName, String topicFactoryName) {
		this.topicName = topicName;
		this.topicFactoryName = topicFactoryName;
	}
	
	public void init(){
		try {
			Context context = new InitialContext();
			TopicConnectionFactory connectionFactory = 
				(TopicConnectionFactory)context.lookup(topicFactoryName);
			topic = (Topic)context.lookup(topicName);
			
			connection = connectionFactory.createTopicConnection();
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			connection.start();
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public void sendMessageToTopic(String textMessage){
		try {
			logger.debug("Message to send: " + textMessage);
			TextMessage message = session.createTextMessage(textMessage);
			TopicPublisher publisher = session.createPublisher(topic);
			publisher.publish(message);
			logger.debug("Message sent");
			publisher.close();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public void closeConnection(){
		try {
			connection.close();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
}
