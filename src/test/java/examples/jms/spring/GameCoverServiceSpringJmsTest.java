package examples.jms.spring;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import examples.jms.GameCoverService;

public class GameCoverServiceSpringJmsTest {
	private static ApplicationContext appContext;

	@BeforeClass
	public static void setUp() throws Exception {
		appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		
	}
	
	@Test
	public void test(){
		GameCoverService client = (GameCoverService)appContext.getBean("gameCoverServiceClient");
		GameCoverService server = (GameCoverService)appContext.getBean("gameCoverServiceServer");
		assertNotNull(client);
		assertNotNull(server);//never used, already listener in spring container
		
		String[] codes = {"BLUS30109", "BCUS98111","SLUS21115"};
		for(String code:codes){
			byte[] img = client.getGameCover(code);
			assertNotNull(img);
			assertFalse(img.length==0);
		}
	}
	
	@Test
	public void testGatGameCoverFail(){
		GameCoverService client = (GameCoverService)appContext.getBean("gameCoverServiceClient");
		String code = "9UE00102";//halo 3 sku
		byte[] img = client.getGameCover(code);
		assertNull(img);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		
	}

}
