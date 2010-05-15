package examples.jms;

import org.apache.log4j.Logger;

import examples.jms.publisher.WinStatusPublisher;
import examples.jms.subscriber.PlayCardSubscriber;

public class LoteriaPlayer implements LoteriaParticipant{
	private static final Logger logger = Logger.getLogger(LoteriaPlayer.class);

	private String name;
	private LoteriaBoard board;
	private PlayCardSubscriber subscriber;
	private WinStatusPublisher publisher = new WinStatusPublisher();
	
	public LoteriaPlayer(String name){
		this.name = name;
		LoteriaCard[] cardArray = new Loteria().createBoard();
		board = new LoteriaBoard(cardArray);
		subscriber = new PlayCardSubscriber(this);
	}

	/**
	 * Callback method that is called to notify the player
	 * that a new card is played by the announcer.
	 * @param card
	 * @return
	 */
	public boolean checkCardInBoard(LoteriaCard card){
		boolean checked = false;
		synchronized (board) {//may be called by many listener threads
			checked = board.checkCardAndSet(card);	
		}
		
		boolean didIWin = false;
		synchronized (board) {//same logic here
			didIWin = board.loteria();	
		}
		
		if(didIWin){
			logger.debug("Yeah I won!(" + name + ") notify before someone else claim the prize");
			publisher.loteria(name);
			goHome();
		}
		return checked;
	}
	
	public String getPlayerName(){
		return name;
	}

	/**
	 * Called by winStatusPlublisher in other thread.
	 */
	public void stopGame(String winnerName) {
		logger.debug("Other play has won :( " + winnerName);
		logger.debug("I go home defeated");
		goHome();
	}
	
	private void goHome(){
		subscriber.leaveGame();
	}
}
