import NeuralNetwork.NeuralNetworkBluePrint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import static java.lang.Integer.min;

/**
 * Created by Russell on 3/22/2017.
 */
public class GATrainingFunc {

    private int generations, populationSize, mutationCount, selectionCount, newBloodCount, handsToPlay;
    private String outputFile;
    private NeuralNetworkBluePrint[] networkArray;
    private NeuralNetworkBluePrint bestNeuralNetwork;
    private Player[] playerArray;
    private GameManager g;
    private Player[] trainingPlayerList;
    private int[] playerResults;
    private int[] limits = {1,1,1,2,2};
    private Random rand;



    public void GAtrainingFunc(int aGenerations, int aPopulationSize, int aMutationCount, int aSelectionCount,
                               int aNewBloodCount, int aHandsToPlay, String aOutputFile){
        generations = aGenerations;
        populationSize = aPopulationSize;
        mutationCount = aMutationCount;
        selectionCount = aSelectionCount;
        newBloodCount = aNewBloodCount;
        handsToPlay = aHandsToPlay;
        outputFile = aOutputFile;
        rand = new Random(populationSize);
        networkArray = new NeuralNetworkBluePrint[populationSize];
        trainingPlayerList = new Player[6];
        trainingPlayerList[0] = new AgentAlwaysCall(0);
        trainingPlayerList[1] = new AgentAlwaysFold(1);
        trainingPlayerList[2] = new AgentAlwaysRaise(2);
        trainingPlayerList[3] = new AgentRandomPlayer(3);
        trainingPlayerList[4] = new AgentAlwaysCall(4);
        playerResults = new int[populationSize];
        createPopulation();
        beginTraining();
    }

    private void beginTraining(){
        for(int i = 0; i < generations; i++){
            for(int j = 0; j < populationSize; j++) {
                trainingPlayerList[5] = playerArray[j];
                Dealer dealer = new Dealer(6, 123456789);
                g = new GameManager(trainingPlayerList, dealer, false, limits, 3, handsToPlay);
                g.playGame();
                playerResults[j] = g.getBankroll(5);
            }
            sortPopulation();
            nextGeneration();
        }
    }

    private void createPopulation(){
        for(int i = 0; i < populationSize; i++){
            networkArray[i] = new NeuralNetworkBluePrint(40, 3);
            playerArray[i] = new NeuralNetworkPlayer(networkArray[i]);
        }
    }

    private void sortPopulation(){
        int tempInt;
        NeuralNetworkBluePrint tempBlueprint;
        boolean changeMade;
        for(int i = 0; i < playerResults.length - 1; i++){
            changeMade = false;
            for(int j = i; j < playerResults.length - i; j++){
                if(playerResults[j] < playerResults[j+1]){
                        tempInt = playerResults[j];
                        playerResults[j] = playerResults[j+1];
                        playerResults[j+1] = tempInt;
                        tempBlueprint = networkArray[j];
                        networkArray[j] = networkArray[j+1];
                        networkArray[j+1] = tempBlueprint;
                        changeMade = true;
                }
            }
            if(!changeMade){
                break;
            }
        }
    }

    private void nextGeneration(){
        int first;
        int second;
        NeuralNetworkBluePrint[] tempBlueprintList = new NeuralNetworkBluePrint[populationSize];

        NeuralNetworkBluePrint tempBluePrint, previousBlueprint;

        for(int i = 0; i < mutationCount; i++){
            first = rand.nextInt();
            second = rand.nextInt();
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);

        }
        for(int i = 0; i < selectionCount; i++){
            first = rand.nextInt();
            second = rand.nextInt();

            if(first > second){
                first = second;
            }

            tempBlueprintList[mutationCount + i] = networkArray[first];
        }
        for(int i = 0; i < newBloodCount; i++){
            tempBlueprintList[mutationCount+selectionCount+i] = new NeuralNetworkBluePrint(40, 3);
        }

    }


    public NeuralNetworkBluePrint copyBlueprint(NeuralNetworkBluePrint previousBlueprint){
        NeuralNetworkBluePrint tempBluePrint;
        int numOfInputs, numOfHiddenLayers, numOfOutputs;
        int[] numOfNeuronsPerLayer;
        double[] inputBias, outputBias;
        double[][] hiddenLayerBias, outputWeights, inputWeights;
        double[][][] hiddenLayerWeights;
        int arrayLength, arrayLength2;
        double[][] temp2DArray;
        double[][][] temp3DArray;

        numOfInputs = previousBlueprint.getNumOfInputs();
        numOfHiddenLayers = previousBlueprint.getNumOfHiddenLayers();
        numOfOutputs = previousBlueprint.getNumOfOutputs();
        numOfNeuronsPerLayer = Arrays.copyOf(previousBlueprint.getNumOfNeuronsPerLayer(), previousBlueprint.getNumOfNeuronsPerLayer().length);
        inputBias = Arrays.copyOf(previousBlueprint.getInputBias(), previousBlueprint.getInputBias().length);
        outputBias = Arrays.copyOf(previousBlueprint.getOutputBias(),previousBlueprint.getOutputBias().length);
        temp2DArray = previousBlueprint.getHiddenLayerBias();
        arrayLength = temp2DArray.length;
        hiddenLayerBias = new double[arrayLength][];
        for(int j = 0; j < arrayLength; j++){
            hiddenLayerBias[j] = Arrays.copyOf(temp2DArray[j],temp2DArray[j].length);
        }
        temp2DArray = previousBlueprint.getOutputWeights();
        arrayLength = temp2DArray.length;
        outputWeights = new double[arrayLength][];
        for(int j = 0; j < arrayLength; j++){
            outputWeights[j] = Arrays.copyOf(temp2DArray[j],temp2DArray[j].length);
        }
        temp2DArray = previousBlueprint.getInputWeights();
        arrayLength = temp2DArray.length;
        inputWeights = new double[arrayLength][];
        for(int j = 0; j < arrayLength; j++){
            inputWeights[j] = Arrays.copyOf(temp2DArray[j],temp2DArray[j].length);
        }
        temp3DArray = previousBlueprint.getHiddenLayerWeights();
        arrayLength = temp3DArray.length;
        hiddenLayerWeights = new double[arrayLength][][];
        for(int j = 0; j < arrayLength; j++){
            arrayLength2 = temp3DArray[j].length;
            temp2DArray = new double[arrayLength2][];
            for(int k = 0; k < arrayLength2; k++){
                temp2DArray[k] = Arrays.copyOf(temp3DArray[j][k], temp3DArray[j][k].length);
            }
            hiddenLayerWeights[j] = temp2DArray;
        }
        tempBluePrint = new NeuralNetworkBluePrint(numOfInputs, inputWeights, inputBias, numOfHiddenLayers, numOfNeuronsPerLayer,
                hiddenLayerWeights, hiddenLayerBias, numOfOutputs, outputWeights, outputBias);
        return tempBluePrint;
    }

}
