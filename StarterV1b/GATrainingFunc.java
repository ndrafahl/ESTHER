import DAOFiles.NeuralNetworkDAO;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkBluePrint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import static java.lang.Integer.min;

/**
 * Created by Russell on 3/22/2017.
 */
public class GATrainingFunc {

    private int generations, populationSize, selectionCount, newBloodCount, handsToPlay;
    private String outputFile;
    private NeuralNetworkBluePrint[] networkArray;
    private NeuralNetworkBluePrint bestNeuralNetwork;
    private NeuralNetworkPlayer[] playerArray;
    private GameManager g;
    private Player[] trainingPlayerList;
    private int[] playerResults;
    private int[] limits = {1,1,1,2,2};
    private Random rand;
    private int inputWeightMutate, inputBiasMutate, outputWeightMutate, outputBiasMutate, hiddenWeightMutate;
    private int hiddenBiasMutate, totalMutations;
    private double[] winRateArray;
    private NeuralNetworkDAO neuralNetworkWriter = new NeuralNetworkDAO();
    private int[] neurons = {68, 8};



    public void GAtrainingFunc(int aGenerations, int aPopulationSize, int aInputWeightMutate, int aInputBiasMutate,
                               int aOutputWeightMutate, int aOutputBiasMutate, int aHiddenWeightMutate,
                               int aHiddenBiasMutate, int aSelectionCount,
                               int aNewBloodCount, int aHandsToPlay, String aOutputFile) throws IOException{
        long startTime = System.currentTimeMillis();
        long endTime;
        generations = aGenerations;
        populationSize = aPopulationSize;
        inputWeightMutate = aInputWeightMutate;
        inputBiasMutate = aInputBiasMutate;
        outputBiasMutate = aOutputBiasMutate;
        outputWeightMutate = aOutputWeightMutate;
        hiddenWeightMutate = aHiddenWeightMutate;
        hiddenBiasMutate = aHiddenBiasMutate;
        selectionCount = aSelectionCount;
        newBloodCount = aNewBloodCount;
        handsToPlay = aHandsToPlay;
        outputFile = aOutputFile;
        totalMutations = inputBiasMutate + inputWeightMutate + outputWeightMutate + outputBiasMutate + hiddenBiasMutate + hiddenWeightMutate;
        rand = new Random();
        networkArray = new NeuralNetworkBluePrint[populationSize];
        playerArray = new NeuralNetworkPlayer[populationSize];
        trainingPlayerList = new Player[6];
        trainingPlayerList[0] = new AgentAlwaysCall(0);
        trainingPlayerList[1] = new NeuralNetworkPlayer("1", "test5.best");
        trainingPlayerList[2] = new AgentAlwaysRaise(2);
        trainingPlayerList[3] = new NeuralNetworkPlayer("2", "test2.best");
        trainingPlayerList[4] = new NeuralNetworkPlayer("4", "test6.best");
        playerResults = new int[populationSize];
        NeuralNetworkDAO fileDAO = new NeuralNetworkDAO();
        winRateArray = fileDAO.loadWinRateArray();
        createPopulation();
        beginTraining();
        endTime = System.currentTimeMillis() - startTime;
        System.out.println(String.valueOf(endTime));
    }

    private void beginTraining(){
        int bestBankroll = -5000;
        for(int i = 0; i < generations; i++){
            System.out.println(String.valueOf(i));
            if(i != 0){
                try {
                    nextGeneration();
                }
                catch (IOException e){
                }
            }
            for(int j = 0; j < populationSize; j++) {
                trainingPlayerList[5] = playerArray[j];
                Dealer dealer = new Dealer(6, 234124521);
                g = new GameManager(trainingPlayerList, dealer, false, limits, 3, handsToPlay);
                g.playGame();
                playerResults[j] = g.getBankroll(5);
                //System.out.println(String.valueOf(playerResults[j]));
            }
            sortPopulation();
            if(playerResults[0] > bestBankroll){
                bestBankroll = playerResults[0];
                bestNeuralNetwork = networkArray[0];
            }
        }
        System.out.println(String.valueOf(bestBankroll));
        for(int i = 0; i < 3; i++){
            System.out.println(String.valueOf(playerResults[i]));
        }
        try {
            neuralNetworkWriter.saveNeuralNetworkList(bestNeuralNetwork, outputFile + "best");
            neuralNetworkWriter.saveNeuralNetworkList(networkArray[0], outputFile + "first");
            neuralNetworkWriter.saveNeuralNetworkList(networkArray[1], outputFile + "second");
            neuralNetworkWriter.saveNeuralNetworkList(networkArray[2], outputFile + "third");
        }
        catch (IOException e){

        }

    }


    private void createPopulation(){
        for(int i = 0; i < populationSize; i++){
            networkArray[i] = new NeuralNetworkBluePrint(57, 3);
            playerArray[i] = new NeuralNetworkPlayer(networkArray[i], winRateArray);
        }
    }

    private void sortPopulation(){
        int tempInt;
        NeuralNetworkBluePrint tempBlueprint;
        boolean changeMade;
        for(int i = playerResults.length-1; i > 0; i--){
            changeMade = false;
            for(int j = 0; j < i; j++){
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

    private void nextGeneration()throws IOException{
        int first;
        int second;
        int tempListNextIndex;
        int neuronNum, weightNum, layerNum;
        NeuralNetworkBluePrint[] tempBlueprintList = new NeuralNetworkBluePrint[populationSize];

        NeuralNetworkBluePrint tempBluePrint, previousBlueprint;

        for(int i = 0; i < inputWeightMutate; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);
            neuronNum = rand.nextInt(tempBluePrint.getNumOfInputs());
            weightNum = rand.nextInt(tempBluePrint.getInputWeights()[neuronNum].length);
            tempBluePrint.mutateAInputNeuronWeight(neuronNum, weightNum);
            tempBlueprintList[i] = tempBluePrint;
        }
        for(int i = 0; i < inputBiasMutate; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);
            neuronNum = rand.nextInt(tempBluePrint.getNumOfInputs());
            tempBluePrint.mutateInputBias(neuronNum);
            tempBlueprintList[inputWeightMutate + i] = tempBluePrint;
        }
        tempListNextIndex = inputWeightMutate + inputBiasMutate;
        for(int i = 0; i < outputWeightMutate; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);
            neuronNum = rand.nextInt(tempBluePrint.getNumOfOutputs());
            weightNum = rand.nextInt(tempBluePrint.getOutputWeights()[neuronNum].length);
            tempBluePrint.mutateAOutputNeuronWeight(neuronNum, weightNum);
            tempBlueprintList[tempListNextIndex + i] = tempBluePrint;
        }
        tempListNextIndex += outputWeightMutate;
        for(int i = 0; i < inputBiasMutate; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);
            neuronNum = rand.nextInt(tempBluePrint.getNumOfOutputs());
            tempBluePrint.mutateOutputBias(neuronNum);
            tempBlueprintList[tempListNextIndex + i] = tempBluePrint;
        }
        tempListNextIndex += outputBiasMutate;
        for(int i = 0; i < hiddenWeightMutate; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);
            layerNum = rand.nextInt(tempBluePrint.getNumOfHiddenLayers());
            neuronNum = rand.nextInt(tempBluePrint.getHiddenLayerWeights()[layerNum].length);
            weightNum = rand.nextInt(tempBluePrint.getHiddenLayerWeights()[layerNum][neuronNum].length);
            tempBluePrint.mutateAHiddenLayerWeight(layerNum, neuronNum, weightNum);
            tempBlueprintList[tempListNextIndex + i] = tempBluePrint;
        }
        tempListNextIndex += hiddenWeightMutate;
        for(int i = 0; i < hiddenWeightMutate; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);
            if(first > second){
                first = second;
            }
            previousBlueprint = networkArray[first];
            tempBluePrint = copyBlueprint(previousBlueprint);
            layerNum = rand.nextInt(tempBluePrint.getNumOfHiddenLayers());
            neuronNum = rand.nextInt(tempBluePrint.getHiddenLayerWeights()[layerNum].length);
            tempBluePrint.mutateHiddenLayerBias(layerNum, neuronNum);
            tempBlueprintList[tempListNextIndex + i] = tempBluePrint;
        }
        for(int i = 0; i < selectionCount; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);

            if(first > second){
                first = second;
            }

            tempBlueprintList[totalMutations + i] = networkArray[first];
        }
        for(int i = 0; i < newBloodCount; i++){
            tempBlueprintList[totalMutations+selectionCount+i] = new NeuralNetworkBluePrint(57, 3);
        }
        networkArray = tempBlueprintList;
        for(int i = 0; i < populationSize; i++){
            playerArray[i] = new NeuralNetworkPlayer(networkArray[i], winRateArray);
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
