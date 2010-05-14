package examples.jms.sender;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.jms.MessageListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import examples.jms.listener.GameCoverServerListener;

/**
 * It also test the message receiver with the {@link MessageListener}
 */
public class GameCoverServiceSenderTest {
	private GameCoverServerListener server;
	private GameCoverServiceJmsImpl client;
	
	private String connectionFactory= "QueueCF";
	private String queue = "dynamicQueues/GameCoverQueue";
	private String replyQueue = "dynamicQueues/GameCoverReplyQueue";
	
	@Before
	public void setUp(){
		server = new GameCoverServerListener(connectionFactory, queue);
		server.connect();
		
		client = new GameCoverServiceJmsImpl(connectionFactory, queue, replyQueue);
	}
	
	@After
	public void tearDown(){
		server.closeConnection();
		client.closeConnection();
	}

	@Test
	public void testGetGameCover(){
		String[] codes = {"BLUS30109", "BCUS98111","SLUS21115"};
		for(String code:codes){
			byte[] img = client.getGameCover(code);
			assertNotNull(img);
			assertFalse(img.length==0);
		}
	}
	
	@Test
	public void testGatGameCoverFail(){
		String code = "9UE00102";//halo 3 sku
		byte[] img = client.getGameCover(code);
		assertNull(img);
	}
}
