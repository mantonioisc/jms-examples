package examples.jms;

import static org.junit.Assert.assertNotNull;

import javax.jms.Connection;
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
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JndiConfigurationTest {
	private Connection connection = null;
	
	@Before
	public void setUp() throws NamingException, JMSException{
		Context context = new InitialContext();
		QueueConnectionFactory queueCF = (QueueConnectionFactory)context.lookup("QueueCF");
		Queue dest = (Queue)context.lookup("dynamicQueues/DestQueue");
		QueueConnection connection = queueCF.createQueueConnection();
		this.connection = connection;
		QueueSession receiverSession = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
		QueueReceiver receiver = receiverSession.createReceiver(dest);
		receiver.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				TextMessage text = (TextMessage)message;
				try {
					System.out.println(text.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	@After
	public void tearDown() throws JMSException{
		connection.close();
	}
	
	@Test
	public void testJndiConfig() throws NamingException{
		Context context = new InitialContext();
		TopicConnectionFactory topicCF = (TopicConnectionFactory)context.lookup("TopicCF");
		QueueConnectionFactory queueCF = (QueueConnectionFactory)context.lookup("QueueCF");
		//geting some dynamic queues/topics to avoid active mq configuration
		//as shown in http://activemq.apache.org/jndi-support.html
		Topic topic = (Topic)context.lookup("dynamicTopics/DynamicTopic");
		Queue queue = (Queue)context.lookup("dynamicQueues/DynamicQueue");
		assertNotNull(topicCF);
		assertNotNull(queueCF);
		assertNotNull(topic);
		assertNotNull(queue);
	}
	
	@Test
	public void testReplyToQueue() throws NamingException, JMSException{
		Context context = new InitialContext();
		QueueConnectionFactory queueCF = (QueueConnectionFactory)context.lookup("QueueCF");
		Queue dest = (Queue)context.lookup("dynamicQueues/DestQueue");
		Queue reply = (Queue)context.lookup("dynamicQueues/ReplyQueue");
		
		QueueConnection connection = queueCF.createQueueConnection();
		QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		QueueSender sender = session.createSender(dest);
		
		Message message = session.createTextMessage("Hello!");
		message.setJMSReplyTo(reply);
		sender.send(message);
		
		/*QueueReceiver receiver = session.createReceiver(reply);
		Message response = receiver.receive();
		
		System.out.println(response.toString());*/
		
		connection.close();
	}
}
