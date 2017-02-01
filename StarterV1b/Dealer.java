/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

/**
 *
 * @author schafer
 */
import java.util.Random;

public class Dealer {

    private final Random random;
    private int seats;
    private int[] deck;

    public Dealer(int seats, long seed) {
        random = new Random(seed);
        setupTable(seats);
    }

    public Dealer(int seats) {
        random = new Random();
        setupTable(seats);
    }

    private void setupTable(int seats) {
        this.seats = seats;
        deck = new int[52];
        for (int x = 0; x < deck.length; x++) {
            deck[x] = x;
        }
        shuffle();
    }

    public void shuffle() {
        for (int c1 = 0; c1 < deck.length - 1; c1++) {
            //need to get a card from c1 to the end of the deck
            int c2 = random.nextInt(deck.length - c1) + c1;
            int temp = deck[c2];
            deck[c2] = deck[c1];
            deck[c1] = temp;
            //System.out.print(c1+"="+deck[c1]+" ");
        }
        //for (int x = 0; x < deck.length; x++) {
        //    System.out.print(" "+deck[x]);
        //}
        //System.out.println();
    }

    public int[] getPocket(int playerNum) {
        int offset = 2 * playerNum;
        int[] pocket = new int[2];
        pocket[0] = deck[offset];
        pocket[1] = deck[offset + 1];
        return pocket;
    }

    public int[] getFlop() {
        int[] flop = new int[3];
        flop[0] = deck[seats * 2];
        flop[1] = deck[seats * 2 + 1];
        flop[2] = deck[seats * 2 + 2];
        return flop;
    }

    public int getTurn() {
        return deck[seats * 2 + 3];
    }

    public int getRiver() {
        return deck[seats * 2 + 4];
    }

    public int[] getBoard(int round) {
        int[] board;
        if (round == 1) {
            //return null;
            board = new int[0];
            return board;
        }

        if (round == 2) {
            board = new int[3];
        } else if (round == 3) {
            board = new int[4];
        } else if (round == 4) {
            board = new int[5];
        } else {
            System.out.println("ILLEGAL round number given to dealer " + round);
            return null;
        }

        if (round > 1) {
            board[0] = deck[seats * 2];
            board[1] = deck[seats * 2 + 1];
            board[2] = deck[seats * 2 + 2];
        } 
        if (round > 2) {
            board[3] = deck[seats * 2 + 3];
        } 
        if (round > 3) {
            board[4] = deck[seats * 2 + 4];
        }
        return board;
    }
}
