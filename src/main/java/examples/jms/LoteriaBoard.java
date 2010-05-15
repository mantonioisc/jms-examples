package examples.jms;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a board and its state, a board can not be
 * cleared or reset to previous state. A new board must
 * be made to be played again.
 */
public class LoteriaBoard {
	/**
	 * A maps that represents the board, using a card name
	 * and a boolean value to know if it's already checked.
	 */
	private Map<LoteriaCard, Boolean> board = new HashMap<LoteriaCard, Boolean>();

	public LoteriaBoard(LoteriaCard[] board) {
		for(LoteriaCard card:board){
			this.board.put(card, Boolean.FALSE);
		}
	}

	/**
	 * When a card is played it must be passed to this method
	 * and it checks if it's in the deck, it is in the card is
	 * checked.
	 * @param card
	 * @return <code>true</code> if the card is in the decked
	 * 		and checked, <code>false</code> is the card is not
	 * 		in the deck
	 */
	public boolean checkCardAndSet(LoteriaCard card) {
		boolean checked = false;
		if(board.containsKey(card)){
			board.put(card, Boolean.TRUE);
			checked = true;
		}
		return checked;
	}

	/**
	 * Checks if this board is a winning board.
	 * @return
	 */
	public boolean loteria() {
		boolean loteria = true;
		for(Map.Entry<LoteriaCard, Boolean> entry: board.entrySet()){
			if(!entry.getValue()){
				loteria = false;
				break;
			}
		}
		return loteria;
	}
}
