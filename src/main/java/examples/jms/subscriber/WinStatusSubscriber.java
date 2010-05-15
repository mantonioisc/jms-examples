package examples.jms.subscriber;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import examples.jms.LoteriaParticipant;

/**
 * Notifies a participant {@link LoteriaParticipant} that the
 * game is over. It applies to the players and the announcer
 * for them to stop. That's why I use an interface to abstract
 * it and use this listener for both. 
 */
public class WinStatusSubscriber implements MessageListener {
	private static final Logger logger = Logger.getLogger(WinStatusSubscriber.class);
	
	private LoteriaParticipant participant;
	private TopicConnection connection;
	
	
	
	public WinStatusSubscriber(LoteriaParticipant participant) {
		this(participant, "dynamicTopics/LoteriaTopic", "TopicCF");
	}

	public WinStatusSubscriber(LoteriaParticipant participant, String topicName, String connectionFactoryName){
		this.participant = participant;
		try {
			Context context = new InitialContext();
			TopicConnectionFactory connectionFactory = 
				(TopicConnectionFactory)context.lookup(connectionFactoryName);
			Topic topic = (Topic)context.lookup(topicName);
			
			connection = connectionFactory.createTopicConnection();
			TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicSubscriber subscriber = session.createSubscriber(topic);
			
			subscriber.setMessageListener(this);
			
			logger.debug("Connection started for participant: " + participant.getPlayerName());
			connection.start();
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void onMessage(Message message) {
		try {
			MapMessage mapMessage = (MapMessage)message;
			String textMessage = mapMessage.getString("message");
			String winner = mapMessage.getString("winner");
			logger.debug("Message: " + textMessage);
			logger.debug("From: " + winner);
			participant.stopGame(winner);
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public void exitGame(){
		try{
			connection.close();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}
