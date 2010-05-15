package examples.jms;

/**
 * Interface that has the commons methods for announcer
 * and player. {@link #stopGame(String)} must be called
 * for all participants to be aware that someone won and
 * the game is over.
 */
public interface LoteriaParticipant {
	public String getPlayerName();
	public void stopGame(String winnerName);
}
