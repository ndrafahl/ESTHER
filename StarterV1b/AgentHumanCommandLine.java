/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author schafer
 */
public class AgentHumanCommandLine extends Player {

    private String name = "";
    private final BufferedReader br;
    private int lastRound;
    private int nextIndex;

    public AgentHumanCommandLine() {
        this.br = new BufferedReader(new InputStreamReader(System.in));
        this.name = "HumanCMD";
        this.lastRound = 1;
        this.nextIndex = 0;
    }

    @Override
    public String getScreenName() {
        return name;
    }

    @Override
    public void newHand(int hand, int[] bank) {
        System.out.println("Players start this round with bank totals of:");
        for (int x = 0; x < bank.length; x++) {
            System.out.println((x + 1) + " has " + bank[x]);
        }

        lastRound = 1;
        nextIndex = 0;
    }

    @Override
    public String getAction(TableData data) {
        int round = data.getBettingRound();

        //Print any lingering actions from a prior round
        if (lastRound < round) {
            ArrayList<String> lastRoundActions = data.getHandActions(lastRound);
            System.out.println();

            for (int x = nextIndex; x < lastRoundActions.size(); x++) {
                System.out.println(lastRoundActions.get(x));
            }
            System.out.println();
            System.out.println("Starting next round of betting.");
            nextIndex = 0;
            lastRound = round;
        }

        //This is the actual code to explain the current situation
        //and get the player's decision
        ArrayList<String> thisRoundActions = data.getHandActions(round);

        System.out.println();
        int len = thisRoundActions.size();

        for (int x = nextIndex; x < thisRoundActions.size(); x++) {
            System.out.println(thisRoundActions.get(x));
        }

        nextIndex = thisRoundActions.size() + 1;
        
        System.out.println();

        System.out.println("Your pocket is ");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        System.out.println("The board shows");
        for (int card : data.getBoard()) {
            System.out.print(EstherTools.intCardToStringCard(card) + " ");
        }
        System.out.println(" ");

        System.out.println("Your actions appear to be:");
        System.out.println(data.getValidActions());
        System.out.println("What would you like to do?");

        String selection;
        try {
            selection = br.readLine();
            if (data.getValidActions().contains(selection)) {
                return selection;
            } else {
                System.out.println("That wasn't a valid choice.  You fold.");
                return "fold";
            }
        } catch (IOException ioe) {
            System.out.println("Read failure?");
            return "fold";
        }

    }

    @Override
    public void handResults(Results r) {
        System.out.println();
        System.out.println("Hand is over.");
        System.out.println("Winner(s):");
        int winningHand = -1;
        for (Integer i : r.getWhoWon()) {
            System.out.print(" " + (i + 1));
            winningHand = i;
        }
        System.out.println();

        System.out.println("Winning hand was a "
                + r.getOutcomes()[winningHand].toString());

        System.out.println();
        System.out.println("Starting next round of betting.");

    }
}
