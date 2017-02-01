/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */
 
/**
 *
 * @author schafer
 *
 * Your main intelligent agent must be a class named with your UNI login name
 * and it must extend this class (and implement the two abstract methods).
 *
 * For example, my agent would be created in Schafer.java and would start
 *
 * public class Schafer extends Player
 *
 * As a reminder, any additional code not contained in that base class (toolkits
 * that you write) must be contained in classes whose name start with your login
 * name. For example, I might write Schafer_toolkit.java
 */
public abstract class Player {

    /**
     * Returns a string of no more than length eight which is the player name
     * displayed on the graphical representation of the game if used
     *
     * @return String
     */
    public abstract String getScreenName();

    /**
     * The main AI of your agent.
     *
     * It takes in an instance of the TableData class as a parameter which
     * indicates the current state of the hand/game.
     *
     * It then expects your next move as a String from the set check, bet,
     * raise, call, fold
     *
     * Not all options are legal at all times. Illegal actions will
     * automatically be treated as a "fold"
     *
     * @param data a TableData instance passed to you by the ESTHER server
     * @return String your action
     */
    public abstract String getAction(TableData data);

    /**
     * This function is a message from the server to your agent that a new hand
     * is starting. Most will ignore this but you may override this.
     *
     * @param handNumber
     * @param cashBalances
     */
    public void newHand(int handNumber, int[] cashBalances) {
    }

    /**
     * This function is a message from the server to your agent that a hand is
     * finished. It includes information you may need LATER in the semester.
     * Most will ignore this but you may overrride this.
     *
     * @param results
     */
    public void handResults(Results results) {
    }

}
