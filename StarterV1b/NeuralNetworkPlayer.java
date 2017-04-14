import DAOFiles.NeuralNetworkDAO;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkBluePrint;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Russell on 2/14/2017.
 *
 * This class extends the player abstract class so it can be used by ESTHER.
 * There are three constructors for this class, each with their own specific purpose.
 * This class us used for playing limit poker.
 */
public class NeuralNetworkPlayer extends Player {

    private NeuralNetworkBluePrint bluePrint;
    private NeuralNetwork neuralNetwork;
    boolean suited;
    int card1Rank, card2Rank, card1Suit, card2Suit;
    String name;
    int handNumber;
    double[] winRateArray = new double[169];

    /**********************************************************************
     * Constructors
     * @param name
     *
     * This Constructor is the default for constructing a player with a single
     * completely random neural network.
     * This constructor will most likely be used for the first generation of
     * our training function. The rest of the generations will use other constructors.
     ***********************************************************************/


    public NeuralNetworkPlayer(String name) throws IOException{
        bluePrint = new NeuralNetworkBluePrint(25, 3);
        neuralNetwork = new NeuralNetwork(bluePrint);
        winRateArray = createWinRateArray();

        this.name = name;

    }

    /*******************************************************************************
     * This constructor will be used when testing the player using a list of
     * neural networks. It will require the name of the file containing all
     * the blueprints of every neural network needed for playing poker.
     * @param name
     * @param fileName
     * @throws IOException
     ********************************************************************************/

    public NeuralNetworkPlayer(String name, String fileName) throws IOException{
        NeuralNetworkDAO neuralNetworkDAO = new NeuralNetworkDAO();
        this.name = name;
        try {
            bluePrint = neuralNetworkDAO.loadNeuralNetworkList(fileName);
        }
        catch (IOException e){
            throw e;
        }
        neuralNetwork = new NeuralNetwork(bluePrint);
        winRateArray = createWinRateArray();

    }

    /*******************************************************************************
     * This constructor will be most likely used for all but the first generation
     * of the training function. It excepts a single neural network blueprint and
     * a name.
     * @param name
     * @param bluePrint
     ******************************************************************************/

    public NeuralNetworkPlayer(String name, NeuralNetworkBluePrint bluePrint)throws IOException{
        this.name = name;
        neuralNetwork = new NeuralNetwork(bluePrint);
        this.bluePrint = bluePrint;
        winRateArray = createWinRateArray();
    }

    public NeuralNetworkPlayer(NeuralNetworkBluePrint bluePrint) throws IOException{
        name = "Samuel";
        neuralNetwork = new NeuralNetwork(bluePrint);
        this.bluePrint = bluePrint;
        winRateArray = createWinRateArray();

    }

    private double[] createWinRateArray() throws IOException {
        Scanner fileIn;
        String winRatesString;
        double winRate;

        try {
            fileIn = new Scanner(new FileInputStream("winRates"));
        } catch (IOException e) {
            throw e;
        }
        for (int i = 0; i < 169; i++){
            winRatesString = fileIn.nextLine();
            winRate = Double.valueOf(winRatesString);
            winRateArray[i] = winRate;
        }
        return winRateArray;

    }

    @Override
    public String getScreenName() {
        return name;
    }

    @Override
    public String getAction(TableData data){              //STILL UNDER CONSTRUCTION!!! THE InputData CLASS STILL NEEDS TO BE IMPLEMENTED.
        String pull = data.getValidActions();

        if(data.getBettingRound() == 1){
            int[] pocketCards = data.getPocket();
            int pocket1Rank = pocketCards[0] % 13;
            int pocket2Rank = pocketCards[1] % 13;
            int pocket1Suit = pocketCards[0] / 13;
            int pocket2Suit = pocketCards[1] / 13;
            int arrayIndex;
            double winRate;


            if (pocket1Suit == pocket2Suit) {
                if (pocket1Rank < pocket2Rank) {
                    arrayIndex = (pocket1Rank * 13) + pocket2Rank;
                }
                else {
                    arrayIndex = (pocket2Rank * 13) + pocket1Rank;
                }
            }

            else {
                if (pocket1Rank < pocket2Rank) {
                    arrayIndex = (pocket2Rank * 13) + pocket1Rank;
                }
                else {
                    arrayIndex = (pocket1Rank * 13) + pocket2Rank;
                }
            }

            winRate = winRateArray[arrayIndex];
            String decision = "fold";
            if (winRate > .7){
                decision = "bet";
            }
            else if (winRate >= .4){
                decision = "call";
            }
            if (decision == "fold" && pull.contains("check")) {
                return "check";
            }
            else if (pull.contains(decision)) {
                return decision;
            }
            else if (decision == "bet" && pull.contains("raise")) {
                return "raise";
            }
            else if (decision == "bet" && !pull.contains("raise")) {
                return "call";
            }
            else if (decision == "call" && pull.contains("check")) {
                return "check";
            }
            else {
                return decision;
            }

        }

        else {

            InputData inputData = new InputData(data);

            String decision = neuralNetwork.makeDecision(inputData.getInputList());

            if (decision == "fold" && pull.contains("check")) {
                return "check";
            } else if (pull.contains(decision)) {
                return decision;
            } else if (decision == "bet" && pull.contains("raise")) {
                return "raise";
            } else if (decision == "bet" && !pull.contains("raise")) {
                return "call";
            } else if (decision == "call" && pull.contains("check")) {
                return "check";
            } else {
                return "fold";
            }
        }
    }

    /************************************************************************
     * This method calculates the index of the neural network trained for the
     * two cards it was dealt. I could explain the mathematical formula used
     * below but it would take some time to explain. For now just understand
     * that all it does is returns the index of a specific neural network.
     * @param pocketCards
     * @return
     ***********************************************************************/

    private int findNeuron(int[] pocketCards){
        card1Suit = pocketCards[0] / 13;
        card2Suit = pocketCards[1] / 13;
        card1Rank = pocketCards[0] % (card1Suit * 13);
        card2Rank = pocketCards[1] % (card2Suit * 13);
        int summation = 0;
        if(card1Suit == card2Suit){
            suited = true;
            for(int i = 0; i < Math.min(card1Rank, card2Rank); i++){
                summation += 12 - i;
            }
            summation += Math.max(card1Rank, card2Rank) - Math.min(card1Rank, card2Rank) - 1 + 91;
        }
        else {
            suited = false;
            for(int i = 0; i < Math.min(card1Rank, card2Rank); i++){
                summation += 13 - i;
            }
            summation += Math.max(card1Rank, card2Rank) - Math.min(card1Rank, card2Rank);
        }
        return summation;
    }

    @Override
    public void newHand(int handNumber, int[] cashBalance){

    }


}
