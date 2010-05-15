package examples.jms;

import java.util.Arrays;

import org.apache.log4j.Logger;

import examples.jms.publisher.PlayCardPublisher;
import examples.jms.subscriber.WinStatusSubscriber;

/**
 * Represents the announcer of the game, he says all cards one by one
 * and before it ends it must receive the acknowledge that someone
 * won (most of the time).The publisher send the game to the topic which
 * represents the announcer saying the card verse. And the subscriber pools
 * the other topic to check for a winner, this will be represent the moment
 * when a player says loteria! winning by checking all the cards in his board.
 * <br>Check comments for thread synchronization explanation.
 */
public class LoteriaAnnouncer implements LoteriaParticipant{
	private static final Logger logger = Logger.getLogger(LoteriaAnnouncer.class);
	
	private static final String NAME = "Announcer";
	
	private Loteria loteriaGame = new Loteria();
	private PlayCardPublisher publisher = new PlayCardPublisher();
	WinStatusSubscriber subscriber;
	
	public LoteriaAnnouncer(){
		subscriber = new WinStatusSubscriber(this);
		publisher.init();
	}
	
	public void playCard(){
		LoteriaCard card = null;
		
		synchronized (loteriaGame) {//we need to synchronize on this since the game can end in any moment
			card = loteriaGame.getNextCard();	
		}
		
		if(card!=null){
			logger.debug(card + "!!!!!!!!!!!!!!!!!!!!!!");
			publisher.sendMessageToTopic(card.toString());	
		}
	}
	
	public void sendAllCards(){
		logger.debug("Say all cards to players to listen for them");
		boolean next = false;
		//I don't want to synchronize all (check then act) since this will mean all cards will
		//be sent before we acknowledge the game is over, and that opens the possibility
		//that we receive more than one loteria winners acknowledge messages
		synchronized (loteriaGame) {
			next = !loteriaGame.isFinished();
		}
		
		while(next){
			playCard();
			synchronized (loteriaGame) {
				next = !loteriaGame.isFinished();
			}
		}
		logger.debug("Very unlikely, all the cards are gone and no one won");
	}

	/**
	 * Callback method used to stop the game
	 * because someone won! <br>
	 * This is going to be called by another thread (the listener thread)
	 */
	public void stopGame(String winnerName){
		synchronized (loteriaGame) {
			logger.debug("Loteria! the winner is " + winnerName);
			logger.debug("The cards played are: ");
			logger.debug( Arrays.toString(loteriaGame.getCardAppareanceOrder()) );	
			while(loteriaGame.getNextCard()!=null){
				logger.debug("removing remaining cards from deck, to avoid deadlock in sendAllCards()");
			}
		}
		
		publisher.closeConnection();
		subscriber.exitGame();
		logger.debug("Stop listenning to topics");
	}

	public String getPlayerName() {
		return NAME;
	}
}
