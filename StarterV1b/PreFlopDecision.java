import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.lang.System.*;
/**
 * Created by stkleiss on 3/22/17.
 */
public class PreFlopDecision {
    int numOfPlayers;
    Dealer dealer;
    public static BestHand bestHand;


 /*   public void getAction(TableData data) {
        int[] pocketCards = data.getPocket();
        int[] pocketArray;
        int pocketCombo;
        int arrayIndex;
        int pocket1Rank;
        int pocket2Rank;
        pocketCombo = pocketCards[0] + pocketCards[1];
        pocket1Rank = pocketCards[0] % 13;
        pocket2Rank = pocketCards[1] % 13;
        int pocket1Suit = pocketCards[0] / 13;
        int pocket2Suit = pocketCards[1] / 13;
        double winRate;

        if (pocket1Suit == pocket2Suit) {
            if (pocket1Rank < pocket2Rank) {
                arrayIndex = (pocket1Rank * 13) + pocket2Rank;
            } else {
                arrayIndex = (pocket2Rank * 13) + pocket1Rank;
            }
        } else {
            if (pocket1Rank < pocket2Rank) {
                arrayIndex = (pocket2Rank * 13) + pocket1Rank;
            } else {
                arrayIndex = (pocket1Rank * 13) + pocket2Rank;
            }
        }

        winRate = winRateArray[arrayIndex];

    }*/

    public static void buildPocketArray(int numOfPlayers) throws IOException {

        try {


            Dealer dealer = new Dealer(numOfPlayers);
            int[] flopCards;
            int turnCard;
            int riverCard;
            int[] bestHands;
            int bestHandVal;
            int bestHandCount;
            int[] pocketCards;
            int winningPlayer = 0;
            ArrayList<Integer> winnerList;
            BestHand[] outcomes = new BestHand[numOfPlayers];

            numOfPlayers = 5;
            bestHands = new int[numOfPlayers];
            int[] gamesPlayed = new int[169];
            int[] gamesWon = new int[169];
            double[] winRateArray = new double[169];


            for (int i = 0; i < 10000000; i++) {
                dealer.shuffle();

                for (int j = 0; j < numOfPlayers; j++) {
                    pocketCards = dealer.getPocket(j);
                    int pocket1Rank = pocketCards[0] % 13;
                    int pocket2Rank = pocketCards[1] % 13;
                    int pocket1Suit = pocketCards[0] / 13;
                    int pocket2Suit = pocketCards[1] / 13;
                    int arrayIndex;

                    if (pocket1Suit == pocket2Suit) {
                        if (pocket1Rank < pocket2Rank) {
                            arrayIndex = (pocket1Rank * 13) + pocket2Rank;
                        } else {
                            arrayIndex = (pocket2Rank * 13) + pocket1Rank;
                        }
                    } else {
                        if (pocket1Rank < pocket2Rank) {
                            arrayIndex = (pocket2Rank * 13) + pocket1Rank;
                        } else {
                            arrayIndex = (pocket1Rank * 13) + pocket2Rank;
                        }
                    }
                    gamesPlayed[arrayIndex] += 1;
                }
                int winningCombo = 0;
                for (int j = 0; j < numOfPlayers; j++) {
                    outcomes[j] = EstherTools.getBestHand(dealer.getPocket(j), dealer.getBoard(4));
                    if (outcomes[j].getCombo() > winningCombo) {
                        winningCombo = outcomes[j].getCombo();
                    }
                }
                int k = 0;
                for (int j = 0; j < numOfPlayers; j++) {
                    if (outcomes[j].getCombo() == winningCombo) {
                        k = outcomes[j].compareTo(outcomes[winningPlayer]);
                        if (k > 0) {
                            winningPlayer = j;
                        }

                    }
                }
                pocketCards = dealer.getPocket(winningPlayer);
                int pocket1Rank = pocketCards[0] % 13;
                int pocket2Rank = pocketCards[1] % 13;
                int pocket1Suit = pocketCards[0] / 13;
                int pocket2Suit = pocketCards[1] / 13;
                int arrayIndex;

                if (pocket1Suit == pocket2Suit) {
                    if (pocket1Rank < pocket2Rank) {
                        arrayIndex = (pocket1Rank * 13) + pocket2Rank;
                    } else {
                        arrayIndex = (pocket2Rank * 13) + pocket1Rank;
                    }
                } else {
                    if (pocket1Rank < pocket2Rank) {
                        arrayIndex = (pocket2Rank * 13) + pocket1Rank;
                    } else {
                        arrayIndex = (pocket1Rank * 13) + pocket2Rank;
                    }
                }
                gamesWon[arrayIndex] += 1;
            }

            for (int i = 0; i < 169; i++) {
                int tempInt = (int) Math.round(gamesWon[i] * 1.0 / gamesPlayed[i] * 100);
                winRateArray[i] = tempInt * 1.0 / 100;
            }
            String fileName = "winRates";

            FileWriter fileOut = new FileWriter(fileName);
            for (int i = 0; i < 169; i++) {
                fileOut.write(String.valueOf(winRateArray[i]) + "\r\n");
            }
            fileOut.close();
        }
        catch(IOException e){
            throw e;
        }
    }
}
