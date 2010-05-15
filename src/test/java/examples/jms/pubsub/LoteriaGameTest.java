package examples.jms.pubsub;

import org.junit.Before;
import org.junit.Test;

import examples.jms.LoteriaAnnouncer;
import examples.jms.LoteriaPlayer;


public class LoteriaGameTest {
	LoteriaAnnouncer announcer;
	LoteriaPlayer[] players = new LoteriaPlayer[10];//Ten boards per game
	
	@Before
	public void setUp(){
		announcer = new LoteriaAnnouncer();
		for(int i=0;i<players.length;i++){
			players[i] = new LoteriaPlayer("Player" + (i+1));
		}
	}
	
	@Test
	public void playGame(){
		announcer.sendAllCards();
	}
	
}
