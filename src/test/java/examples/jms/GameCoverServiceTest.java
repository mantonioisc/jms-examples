package examples.jms;

import static org.junit.Assert.*;

import org.junit.Test;

import examples.jms.listener.GameCoverServerListener;

public class GameCoverServiceTest {
	private GameCoverService gameCoverService = new GameCoverServerListener();

	@Test
	public void testGetGameCoverFromDisk(){
		byte[] bytes = gameCoverService.getGameCover("BLUS30109");
		assertNotNull(bytes);
		assertFalse(bytes.length==0);
		
		bytes = gameCoverService.getGameCover("BCUS98111");
		assertNotNull(bytes);
		assertFalse(bytes.length==0);
	}
}
