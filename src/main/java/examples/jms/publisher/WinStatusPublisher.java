package examples.jms.publisher;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
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
 * Only send message to success topic nothing more.
 * Just two methods one for sending success message and one
 * for closing the connection in case success is never achieved
 * and never notified.
 */
public class WinStatusPublisher {
	
	private static final Logger logger = Logger.getLogger(WinStatusPublisher.class);
	
	public static final String LOTERIA = "LOTERIA!";
	
	private Topic topic;
	private TopicConnection connection;
	private TopicSession session;
	
	public WinStatusPublisher(String topicName, String connectionFactoryName) {		
		try{
			Context context = new InitialContext();
			TopicConnectionFactory connectionFactory = 
				(TopicConnectionFactory)context.lookup(connectionFactoryName);
			topic = (Topic)context.lookup(topicName);
			
			connection = connectionFactory.createTopicConnection();
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			connection.start();
		}catch(NamingException e){
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}catch(JMSException e){
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
	}

	public WinStatusPublisher() {
		this("dynamicTopics/LoteriaTopic", "TopicCF");
	}

	/**
	 * This message will be only sent once by some player,
	 * so we send and destroy here.
	 */
	public void loteria(String winnerName){
		try {
			logger.debug(LOTERIA);
			MapMessage loteria = session.createMapMessage();
			loteria.setString("message", LOTERIA);
			loteria.setString("winner", winnerName);
			TopicPublisher winner = session.createPublisher(topic);
			winner.publish(loteria);
			winner.close();
			
			connection.close();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * When other player losses it must accept it and never
	 * say loteria! since other person won. (In reality I
	 * need to close the JMS connection if the success message
	 * is never sent.
	 */
	public void lose(){
		logger.debug("Didn't win :(");
		logger.debug("closing the connection since I won't be using it");
		try {
			connection.close();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
