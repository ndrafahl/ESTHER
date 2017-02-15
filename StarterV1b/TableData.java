/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */


import java.util.ArrayList;

/**
 *
 * @author schafer
 */
public class TableData {
    private final int playerCount;       //How many people at the table
    private final int handsRemaining;    //How many hands remain after this one
    private final int handsPlayed;       //What hand # is this hand

    private final int mySeatNumber;      //1 to n, what is my seat #
    private final int button;            //1 to n, who has the button (last bettor)

    private final int bettingRound;      //1 to 4, which round we are in
    private final int[] limitStructure;  //Length 5 array indicating ante, and then limits in the four rounds of betting

    private final boolean[] whosIn;      //length n array with who is in
    private final int[] cashBalances;    //length n array with each player's money
    private final int totalPot;          //how much money is in the pot for this hand
    private final int tablePot;          //how much money each "in" player should have added to the pot this betting round
    private final int playerPot;         //how much money this player HAS put in this betting round
    private final int raisesLeft;          //how many raises remain


    private final int[] pocket;          //This player's two cards
    private final int[] board;           //The community cards (may be length 0, 3, 4, or 5)

    private final Player[] players;      //The array containing the players in the game.

    //The following is a length five array consisting of ArrayLists of Strings
    //Each ArrayList lists, in order, the seat # (index 1) and action taken
    //Index 0 is nothing more than ante
    //Index 1 is the pocket card round of betting
    //Index 2 is the flop round of betting
    //Index 3 is the turn
    //Index 4 is the river
    //The ArrayLists MAY be empty if it is early in the hand
    private final ArrayList<String>[] handActions;  
    
    //This is a string which is a comma separatated list of valid actions
    //for this player at this moment
    //Either "fold,check,bet"  "fold,call,raise" or "fold,call"
    private final String validActions;

    public TableData(int playerCount, int handsRemaining, int handsPlayed, 
            int mySeatNumber, int button, 
            int bettingRound,int[] limitStructure,
            boolean[] whosIn, int[] cashBalances, 
            int totalPot, int tablePot, 
            int playerPot,  int raisesLeft,
            int[] pocket, int[] board,
            ArrayList<String>[] handActions, String validActions, Player[] players) {
        this.playerCount = playerCount;
        this.handsPlayed = handsPlayed;
        this.handsRemaining = handsRemaining;
        this.mySeatNumber = mySeatNumber;
        this.button = button;
        this.bettingRound = bettingRound;
        this.limitStructure = limitStructure;
        this.whosIn = whosIn;
        this.cashBalances = cashBalances;
        this.totalPot = totalPot;
        this.tablePot = tablePot;
        this.playerPot = playerPot;
        this.raisesLeft = raisesLeft;
        this.pocket = pocket;
        this.board = board;
        this.handActions = handActions;
        this.validActions = validActions;
        this.players = players;
    }

    /**
     * Reports the number of players at the current table [Default = 6]
     * @return int
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Reports how many hands of poker remain after this hand.  
     * @return int
     */
    public int getHandsRemaining() {
        return handsRemaining;
    }

    /**
     * Reports which hand is currently in play (how many have been completed 
     * PLUS this hand).
     * @return int
     */
    public int getHandsPlayed() {
        return handsPlayed;
    }

    /**
     * Reports a value from 1 to N (the number of players) indicating the
     * current player's seat number.
     * @return int
     */
    public int getMySeatNumber() {
        return mySeatNumber;
    }

        /**
     * Reports a value from 1 to N (the number of players) indicating the
     * location of the button.
     * @return int
     */
    public int getButton() {
        return button;
    }

     /**
     * Reports a value from 1 to 4 representing the current betting round
     * @return int
     */
    public int getBettingRound() {
        return bettingRound;
    }

     /**
     * Reports a length five array indicating the values of the ante,
     * and the bet value in each of the four rounds of betting.
     * Default is {1,1,1,2,2} but this may change.
     * @return int[]
     */
    public int[] getLimitStructure() {
        return limitStructure;
    }

     /**
     * Reports a length N array of booleans indicating who's hand
     * is still in play in this hand of poker.  
     * Note, that this is zero based indeces.
     * @return boolean[]
     */
    public boolean[] getWhosIn() {
        return whosIn;
    }

     /**
     * Reports a length N array indicating the amount of money CURRENTLY
     * held by each player.  (Note that in the middle of betting rounds this 
     * value is ever changing).
     * Note, that this is zero based indeces.
     * @return int[]
     */
    public int[] getCashBalances() {
        return cashBalances;
    }

     /**
     * Reports the current value of the pot.
     * @return int
     */
    public int getTotalPot() {
        return totalPot;
    }

    /**
     * Reports how much money each player will need to put into the pot during
     * the current round of betting in order to stay in the hand.
     * @return int
     */
    public int getTablePot() {
        return tablePot;
    }

        /**
     * Reports how much money the current player HAS put into the pot during
     * the current round of betting.  The difference between this and the Table
     * Pot represents how much money will be put in if the current player calls.
     * @return int
     */
    public int getPlayerPot() {
        return playerPot;
    }

     /**
     * Reports how many raises remain in this round of betting.  
     * Note that if there is a three raise limit per round of betting this
     * value starts at 4 representing the opening bet and the three additional 
     * raises.
     * @return int
     */
    public int getRaisesLeft() {
        return raisesLeft;
    }

     /**
     * A length two array which returns the  int values of the 
     * individual player's pocket cards.  Read the EstherTools documentation
     * for an explanation of how to translate this number to a <rank><suit>
     * value for human use.
     * @return int[]
     */
    public int[] getPocket() {
        return pocket;
    }

     /**
     * A variable length array which returns the int values of the 
     * cards on the board.  This array may have length 0, 3, 4, or 5 depending
     * on where the hand is at during it's play.
     * Read the EstherTools documentation  for an explanation of how to 
     * translate this number to a <rank><suit> value for human use.
     * @return int[]
     */
    public int[] getBoard() {
        return board;
    }

    
    
    /**
     * This function can be used to find a complete sequence of actions from
     * any of the "five" betting rounds :
     * Index 0 is nothing more than ante
     * Index 1 is the pocket card round of betting
     * Index 2 is the flop round of betting
     * Index 3 is the turn
     * Index 4 is the river
     * 
     * This returns an ArrayList of strings where each individual
     * string in the list is a (<seat#>,<action>) pair.
     * 
     * @param roundNumber
     * @return ArrayList<String>
     */
    public ArrayList<String> getHandActions(int roundNumber) {
        return handActions[roundNumber];
    }

    /**
     * Reports a comma separated string of the two or three valid actions
     * at the current point in the betting round.  There should only be three
     * options:
     * 
     * fold,check,bet
     * fold,call,raise
     * fold,call
     * 
     * @return String
     */
    public String getValidActions() {
        return validActions;
    }

    /**
     * Returns an array containing the Players currently in the running of this game.
     * Needed to simulate the game for MCTS.
     * @return Player []
     */

    public Player[] getPlayers() { return players; }

  
}
