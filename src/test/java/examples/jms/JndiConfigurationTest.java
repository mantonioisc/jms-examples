package examples.jms;

import static org.junit.Assert.assertNotNull;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;


public class JndiConfigurationTest {
	
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
}
