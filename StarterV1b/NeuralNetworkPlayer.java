import DAOFiles.NeuralNetworkDAO;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkBluePrint;
import java.io.FileInputStream;
import java.io.IOException;
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
    double[] winRateArray = new double[169];
    double callThreshold, raiseThreshold;
    int[] inputArray;
    InputData inputData;


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
        bluePrint = new NeuralNetworkBluePrint(54, 3);
        neuralNetwork = new NeuralNetwork(bluePrint);
        winRateArray = createWinRateArray();

        this.name = name;
        initThresholds();

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
        initThresholds();

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
        initThresholds();
    }

    public NeuralNetworkPlayer(NeuralNetworkBluePrint bluePrint) throws IOException{
        name = "Samuel";
        neuralNetwork = new NeuralNetwork(bluePrint);
        this.bluePrint = bluePrint;
        winRateArray = createWinRateArray();
        initThresholds();
    }
    public NeuralNetworkPlayer(NeuralNetworkBluePrint bluePrint, double[] winRateArray){
        name = "Samuel";
        neuralNetwork = new NeuralNetwork(bluePrint);
        this.bluePrint = bluePrint;
        this.winRateArray = winRateArray;
    }


    /**
     * This method creates the players preflop array used for making decisions.
     * @return
     * @throws IOException
     */
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

    /**
     * This method sets the players call and raise thresholds. These values were determined using our training
     * function.
     */
    private void initThresholds(){
        callThreshold = .16;
        raiseThreshold = .22;
    }

    public void setRaiseThreshold(double threshold){raiseThreshold = threshold;}

    public void setCallThreshold(double threshold){callThreshold = threshold;}

    public double getCallThreshold(){return callThreshold;}

    public double getRaiseThreshold(){return raiseThreshold;}

    @Override
    public String getScreenName() {
        return name;
    }

    /**
     * getAction takes in the table data from the gameManager and uses either the preflop algorithm or neural network
     * to make a decision on what action to take.
     * @param data a TableData instance passed to you by the ESTHER server
     * @return
     */
    @Override
    public String getAction(TableData data){
        String pull = data.getValidActions();

        //This checks if its in the first betting round to the hand
        if(data.getBettingRound() == 1){
            //This calculates the players two pocket cards rank and suits used in its preflop decision making.
            int[] pocketCards = data.getPocket();
            int pocket1Rank = pocketCards[0] % 13;
            int pocket2Rank = pocketCards[1] % 13;
            int pocket1Suit = pocketCards[0] / 13;
            int pocket2Suit = pocketCards[1] / 13;
            int arrayIndex;
            double winRate;


            //This determines the index where the players pocket card hand win rate is in the win rate array.
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

            //This checks to see if the players preflop hand is good enough to play.
            winRate = winRateArray[arrayIndex];
            String decision = "fold";
            if (winRate > raiseThreshold){
                decision = "bet";
            }
            else if (winRate >= callThreshold){
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


        //This section handles all decisions after the preflop betting round.
        else {


            //This creates the input data for the neural network.
            inputArray = inputData.getInputList(data);


            //This sends the input data array to the neural network and stores what decision it makes.
            String decision = neuralNetwork.makeDecision(inputArray);
            //System.out.println(decision);

            //This determines if the decision the neural network made was a valid choice and if not it makes the appropriate
            //action.
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
}
