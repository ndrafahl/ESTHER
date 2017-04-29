import DAOFiles.NeuralNetworkDAO;
import NeuralNetwork.NeuralNetworkBluePrint;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


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


    /*************************************************************************
     * This is a genetic training function to train our neural network.
     * @param aGenerations
     * @param aPopulationSize
     * @param aInputWeightMutate
     * @param aInputBiasMutate
     * @param aOutputWeightMutate
     * @param aOutputBiasMutate
     * @param aHiddenWeightMutate
     * @param aHiddenBiasMutate
     * @param aSelectionCount
     * @param aNewBloodCount
     * @param aHandsToPlay
     * @param aOutputFile
     * @throws IOException
     */
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

    /**
     * beginTraining handles our genetic algorithm training function. It creates the dealer and gameManager
     * for every player in each of the generations. It stores each of the players bankrolls after every
     * game and passes them to the sorting algorithm and then to the next generation generator.
     */
    private void beginTraining(){
        //Here I am initializing the global best bankroll to save the best overall neural network throughout the training.
        int bestBankroll = -5000;
        //This loops through the number of generations.
        for(int i = 0; i < generations; i++){
            System.out.println(String.valueOf(i));
            //This is to create the next generation but since the first generation is created before starting the training
            //it must skip this call during generation 0.
            if(i != 0){
                try {
                    nextGeneration();
                }
                catch (IOException e){
                }
            }
            //This will loop through the population adding the new neural network player to the player array,
            //creates a new dealer, creates a new gameManager, plays the specified number of hands and saves
            //the neural networks bankroll to an array so it they can be ranked later.
            for(int j = 0; j < populationSize; j++) {
                trainingPlayerList[5] = playerArray[j];
                Dealer dealer = new Dealer(6, 234124521);
                g = new GameManager(trainingPlayerList, dealer, false, limits, 3, handsToPlay);
                g.playGame();
                playerResults[j] = g.getBankroll(5);
                //System.out.println(String.valueOf(playerResults[j]));
            }
            sortPopulation();
            //This is checking if the best player in this generation is better than the best seen in any other generation.
            if(playerResults[0] > bestBankroll){
                bestBankroll = playerResults[0];
                bestNeuralNetwork = networkArray[0];
            }
        }
        //System.out.println(String.valueOf(bestBankroll));
        for(int i = 0; i < 3; i++){
            System.out.println(String.valueOf(playerResults[i]));
        }
        //This sends the global best neural network and the best three from the last generation to the DAO file writer
        //so they can be loaded and used later.
        try {
            neuralNetworkWriter.saveNeuralNetworkList(bestNeuralNetwork, outputFile + "best");
            neuralNetworkWriter.saveNeuralNetworkList(networkArray[0], outputFile + "first");
            neuralNetworkWriter.saveNeuralNetworkList(networkArray[1], outputFile + "second");
            neuralNetworkWriter.saveNeuralNetworkList(networkArray[2], outputFile + "third");
        }
        catch (IOException e){

        }

    }

    /**
     * This creates the neural networks for first generation of the genetic training function as well as the the neural
     * network players that will play the game using those neural networks.
     */
    private void createPopulation(){
        for(int i = 0; i < populationSize; i++){
            networkArray[i] = new NeuralNetworkBluePrint(57, 3);
            playerArray[i] = new NeuralNetworkPlayer(networkArray[i], winRateArray);
        }
    }

    /**
     * This is essentually a bubble sort function that sorts the player bankrolls (results) while keeping the neural
     * network that scored that bankroll in the same index of the array.
     */
    private void sortPopulation(){
        int tempInt;
        NeuralNetworkBluePrint tempBlueprint;
        boolean changeMade;
        //bubble sort alg.
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

    /**
     * This creates the population for the next generation of the genetic function.
     * @throws IOException
     */
    private void nextGeneration()throws IOException{
        int first;
        int second;
        int tempListNextIndex;
        int neuronNum, weightNum, layerNum;
        NeuralNetworkBluePrint[] tempBlueprintList = new NeuralNetworkBluePrint[populationSize];

        NeuralNetworkBluePrint tempBluePrint, previousBlueprint;

        //This loop picks a neural network to mutate a random input weight. It will do this for X number of the new generation.
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
        //This loop does the same as above only for mutating an input bias.
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
        //This is for an output weight.
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
        //This is for an output bias
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
        //This is for a hidden weight.
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
        //This is for a hidden bias.
        for(int i = 0; i < hiddenBiasMutate; i++){
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
        //This selects a network to pass on to the next generation without being changed
        for(int i = 0; i < selectionCount; i++){
            first = rand.nextInt(populationSize);
            second = rand.nextInt(populationSize);

            if(first > second){
                first = second;
            }

            tempBlueprintList[totalMutations + i] = networkArray[first];
        }
        //This introduces a completely new network to the next generation.
        for(int i = 0; i < newBloodCount; i++){
            tempBlueprintList[totalMutations+selectionCount+i] = new NeuralNetworkBluePrint(57, 3);
        }
        networkArray = tempBlueprintList;
        //This takes all new neural networks created above and creates a new list of neural network players for the next
        //generation of training.
        for(int i = 0; i < populationSize; i++){
            playerArray[i] = new NeuralNetworkPlayer(networkArray[i], winRateArray);
        }


    }


    /**
     * This function makes a copy of a neural network so that the weights and bias can be changed without changing the
     * original network being copied.
     * @param previousBlueprint
     * @return
     */
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

        //Here I am copying all the immutable data and the single dimension arrays that don't require extra work.
        numOfInputs = previousBlueprint.getNumOfInputs();
        numOfHiddenLayers = previousBlueprint.getNumOfHiddenLayers();
        numOfOutputs = previousBlueprint.getNumOfOutputs();
        numOfNeuronsPerLayer = Arrays.copyOf(previousBlueprint.getNumOfNeuronsPerLayer(), previousBlueprint.getNumOfNeuronsPerLayer().length);
        inputBias = Arrays.copyOf(previousBlueprint.getInputBias(), previousBlueprint.getInputBias().length);
        outputBias = Arrays.copyOf(previousBlueprint.getOutputBias(),previousBlueprint.getOutputBias().length);
        temp2DArray = previousBlueprint.getHiddenLayerBias();
        //To copy two or three dimension arrays I must make temp arrays that can store the copied data because the
        //Array.copyOf method does not work for multiple dimensional arrays.
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
        //This is were I take all the copied information from the other neural network and create a new one with it.
        tempBluePrint = new NeuralNetworkBluePrint(numOfInputs, inputWeights, inputBias, numOfHiddenLayers, numOfNeuronsPerLayer,
                hiddenLayerWeights, hiddenLayerBias, numOfOutputs, outputWeights, outputBias);
        return tempBluePrint;
    }

}
