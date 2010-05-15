package examples.jms;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a Loteria game, which has state to avoid the same cards to appear
 * twice.
 */
public class Loteria {

	/**
	 * It may be useful to keep insertion order
	 */
	private Set<LoteriaCard> usedCards = new LinkedHashSet<LoteriaCard>();
	public static final int DECK_SIZE = LoteriaCard.values().length;
	public static final int BOARD_SIZE = 16;

	/**
	 * Returns the next card in the game. Never returns the same card
	 * twice.
	 * @return the next card, <code>null</code> if all cards are played
	 */
	public LoteriaCard getNextCard() {
		if(isFinished()){
			return null;
		}
		LoteriaCard card = getSomeCard();
		while(usedCards.contains(card)){
			card = getSomeCard();
		}
		usedCards.add(card);
		return card;
	}
	
	/**
	 * Gets a random card, it may return the same many times
	 * @return
	 */
	private LoteriaCard getSomeCard(){
		int size = LoteriaCard.values().length;
		int next = (int)(Math.random()*size);
		return LoteriaCard.values()[next];
	}

	/**
	 * Creates an array of {@link LoteriaCard} of size 16
	 * representing a players board, where no card appears
	 * twice.
	 * @return
	 */
	public LoteriaCard[] createBoard() {
		LoteriaCard[] board = new LoteriaCard[BOARD_SIZE];
		Set<LoteriaCard> usedCards = new HashSet<LoteriaCard>();
		
		/*this can never loop forever since the board has many cards
		 * less than the deck */
		for(int i=0;i<board.length;i++){
			LoteriaCard card = getSomeCard();
			while(usedCards.contains(card)){
				card = getSomeCard();
			}
			usedCards.add(card);
			board[i] = card;
		}
		
		return board;
	}

	/**
	 * To check if the game is over
	 * @return <code>true</code> if all cards are played
	 */
	public boolean isFinished() {
		return DECK_SIZE == usedCards.size();
	}
	
	/**
	 * To check if the game has started, no cards played yet
	 * @return
	 */
	public boolean hasStarted(){
		return !usedCards.isEmpty();
	}
	
	/**
	 * Resets the game, {@link #isFinished()} returns false
	 * again, and {@link #getNextCard()} returns cards again
	 */
	public void resetGame(){
		usedCards.clear();
	}
	
	/**
	 * Gets the cards in the order they appeared when playing
	 * @return
	 */
	public LoteriaCard[] getCardAppareanceOrder(){
		LoteriaCard[] cards = new LoteriaCard[usedCards.size()];
		return usedCards.toArray(cards);
	}
}
