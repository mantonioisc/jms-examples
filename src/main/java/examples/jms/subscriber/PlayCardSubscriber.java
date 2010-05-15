package examples.jms.subscriber;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import examples.jms.LoteriaCard;
import examples.jms.LoteriaPlayer;

/**
 * Only pools topic to get the messages, and calls back the
 * player notifying him of the new card. We need this approach
 * since the message arrive asynchronously and we don't have
 * a way to get what arrived for the player. So we need a
 * player reference here to notify him, instead of having 
 * the player pull the value from the topic. So they are 
 * coupled.
 */
public class PlayCardSubscriber implements MessageListener {
	
	private static final Logger logger = Logger.getLogger(PlayCardSubscriber.class);
	
	private LoteriaPlayer player;
	private TopicConnection connection;

	public PlayCardSubscriber(LoteriaPlayer player, String topicName,
			String connectionFactoryName) {
		this.player = player;
		
		try{
			Context context = new InitialContext();
			TopicConnectionFactory connectionFactory = 
				(TopicConnectionFactory)context.lookup(connectionFactoryName);
			Topic topic = (Topic)context.lookup(topicName);
			
			connection = connectionFactory.createTopicConnection();
			TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicSubscriber subscriber = session.createSubscriber(topic);
			subscriber.setMessageListener(this);
			
			logger.debug("Connection initilized for player: " + player.getPlayerName());
			
			connection.start();
		}catch(NamingException e){
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}catch(JMSException e){
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	public PlayCardSubscriber(LoteriaPlayer player) {
		this(player, "dynamicTopics/CardTopic", "TopicCF");
	}

	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage)message;
			String cardText = textMessage.getText();
			LoteriaCard card = LoteriaCard.valueOf(cardText);
			logger.debug("Card received: " + cardText);
			player.checkCardInBoard(card);
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public void leaveGame(){
		try{
			connection.close();
		}catch(JMSException e){
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}
