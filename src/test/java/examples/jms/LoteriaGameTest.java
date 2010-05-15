package examples.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoteriaGameTest {
	private Loteria loteria = new Loteria();
	private LoteriaBoard board;
	private LoteriaCard[] cards;
	
	private static final Logger logger = Logger.getLogger(LoteriaGameTest.class);
	
	@Before
	public void setUp(){
		cards = loteria.createBoard();
		board = new LoteriaBoard(cards);
	}
	
	@After
	public void tearDown(){
		loteria.resetGame();
	}
	
	@Test
	public void testCardSelection(){
		int i = 0;
		for(;i<Loteria.DECK_SIZE;i++){
			LoteriaCard card = loteria.getNextCard();
			assertNotNull(card);
		}
		
		logger.debug( Arrays.toString(loteria.getCardAppareanceOrder()) );
		
		assertEquals(Loteria.DECK_SIZE, loteria.getCardAppareanceOrder().length);
		
		assertTrue(loteria.isFinished());
		
		LoteriaCard card = loteria.getNextCard();
		assertNull(card);
		
		loteria.resetGame();
		
		assertFalse(loteria.isFinished());
		assertFalse(loteria.hasStarted());
		
		card = loteria.getNextCard();
		assertNotNull(card);
		
		assertTrue(loteria.hasStarted());
		assertFalse(loteria.isFinished());
	}
	
	@Test
	public void testBoardCreation(){
		assertEquals(Loteria.BOARD_SIZE, cards.length);
		
		logger.debug( Arrays.toString(cards) );
		
		assertFalse(board.loteria());
		
		for(LoteriaCard card:cards){
			board.checkCardAndSet(card);
		}
		
		assertTrue(board.loteria());
	}
}
