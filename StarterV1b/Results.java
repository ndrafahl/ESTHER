/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

import java.util.ArrayList;

/**
 *
 * @author schafer
 * 
 * This class is used to inform you of who won/split the pot and
 * what cards were revealed.
 * 
 * This information will be useful when you begin to consider agents that
 * attempt to learn opponent strategies
 */
public class Results {
    private final int[] payouts;
    private final int[][] pocketCards;
    private final BestHand[] outcomes;
    private final ArrayList<Integer> whoWon;

    public Results(int[] payouts, int[][] pocketCards, 
            BestHand[] outcomes,ArrayList<Integer> whoWon) {
        this.payouts = payouts;
        this.pocketCards = pocketCards;
        this.outcomes = outcomes;
        this.whoWon = whoWon;
    }

    public int[] getPayouts() {
        return payouts;
    }

    public int[][] getPocketCards() {
        return pocketCards;
    }

    public BestHand[] getOutcomes() {
        return outcomes;
    }
    
    public ArrayList<Integer> getWhoWon() {
        return whoWon;
    }
}
