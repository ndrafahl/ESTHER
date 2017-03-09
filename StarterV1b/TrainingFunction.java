import NeuralNetwork.NeuralNetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 */
public class TrainingFunction {
    private ArrayList<NeuralNetwork> networkList;

    public static BestHand bestHand;

    public static void generateData(String aFile) throws IOException {

        try {


            String fileName = aFile;
            Dealer dealer = new Dealer(1);
            int[] pocketCards;
            int[] flopCards;
            int turnCard;
            int riverCard;
            FileWriter fileOut = new FileWriter(fileName);
            String[] resultsArray;

            fileOut.write("@relation pokerData \r\n");
            for (int i = 0; i < 52; i++) {
                fileOut.write("@attribute Pocket" + EstherTools.intCardToStringCard(i) + " {True, False} \r\n");
            }
            for (int i = 0; i < 52; i++) {
                fileOut.write("@attribute Board" + EstherTools.intCardToStringCard(i) + " {True, False} \r\n");
            }
            fileOut.write("@attribute HandClass {0,1,2,3,4,5,6,7,8} \r\n");
            fileOut.write("@data \r\n");

            resultsArray = new String[105];

            for (int i = 0; i < 104; i++) {
                resultsArray[i] = "False";
            }
            resultsArray[104] = "0";

            for (int i = 0; i < 10000000; i++) {
                dealer.shuffle();
                pocketCards = dealer.getPocket(0);
                resultsArray[pocketCards[0]] = "True";
                resultsArray[pocketCards[1]] = "True";

                flopCards = dealer.getFlop();
                resultsArray[flopCards[0]+52] = "True";
                resultsArray[flopCards[1]+52] = "True";
                resultsArray[flopCards[2]+52] = "True";

                turnCard = dealer.getTurn();
                resultsArray[turnCard +52] = "True";

                riverCard = dealer.getRiver();
                resultsArray[riverCard +52] = "True";

                bestHand = EstherTools.getBestHand(dealer.getPocket(0), dealer.getBoard(4));
                resultsArray[104] = String.valueOf(bestHand.getCombo());

                for (int j = 0; j < 104; j++) {
                    fileOut.write(resultsArray[j] + ", ");
                }
                fileOut.write(resultsArray[104] + "\r\n");

                resultsArray[pocketCards[0]] = "False";
                resultsArray[pocketCards[1]] = "False";
                resultsArray[flopCards[0]+52] = "False";
                resultsArray[flopCards[1]+52] = "False";
                resultsArray[flopCards[2]+52] = "False";
                resultsArray[turnCard +52] = "False";
                resultsArray[riverCard +52] = "False";

            }
            fileOut.close();
        }
        catch(IOException e){
            throw e;
        }


    }

}


