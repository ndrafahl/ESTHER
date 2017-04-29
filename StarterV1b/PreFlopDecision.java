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

    /**
     * This function creates the win rate array values by playing several thousand hands and keeping track of which two
     * card combination wins the most by dividing the number of times that hand won by the number of times it was seen.
     * @param numOfPlayers
     * @throws IOException
     */
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


            //Here we have determined to play 10 million hands.
            for (int i = 0; i < 10000000; i++) {
                dealer.shuffle();

                //Here we are getting the rank and suit of each players pocket cards to determine what index in the
                //gamesPlayed array should be incremented indicated it was played this hand.
                for (int j = 0; j < numOfPlayers; j++) {
                    pocketCards = dealer.getPocket(j);
                    int pocket1Rank = pocketCards[0] % 13;
                    int pocket2Rank = pocketCards[1] % 13;
                    int pocket1Suit = pocketCards[0] / 13;
                    int pocket2Suit = pocketCards[1] / 13;
                    int arrayIndex;

                    //Here we are calculating the index for the players pocket card hand.
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
                    //This increments that index indicating it has been seen.
                    gamesPlayed[arrayIndex] += 1;
                }
                //Here we are skipping to the final round of the hand and determining what hand would have won if all
                //the players would have stayed in. Then we increment the value in that hands index by one in the
                //win array indicating that hand would have won.
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

            //Here we are dividing the values in the gamesWon array by the value in the gamesPlayed array to get that hands
            //winning percentage.
            for (int i = 0; i < 169; i++) {
                int tempInt = (int) Math.round(gamesWon[i] * 1.0 / gamesPlayed[i] * 1000);
                winRateArray[i] = tempInt * 1.0 / 1000;
            }
            String fileName = "winRates";

            //Then we write the win rate file to out so it can be read in by our player later.
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
