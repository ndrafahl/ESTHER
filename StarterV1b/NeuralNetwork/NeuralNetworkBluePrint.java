package NeuralNetwork;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Russell on 2/20/2017.
 *
 * This function is essentually a blueprint for a neural network.
 * It contains all the necessary information for creating a neural
 * network and is used as a parameter to our neural network for its
 * construction.
 *
 * If this object is constructed with only the number of inputs
 * and outputs it will randomly pick the number of hidden layers,
 * the number of nodes in each layer, the weights for every neuron,
 * and the bias for every neuron.
 *
 * The second constructor takes in all the information and stores
 * it so it can be passed to a neural network.
 */


public class NeuralNetworkBluePrint implements Cloneable {
    private int numOfInputs, numOfHiddenLayers, numOfOutputs;
    private int[] numOfNeuronsPerLayer;
    private double[] inputBias, outputBias;
    private double[][] hiddenLayerBias, outputWeights, inputWeights;
    private double[][][] hiddenLayerWeights;
    private int maxNumOfLayers, maxNumOfNeurons, maxBias, maxWeight;

    public NeuralNetworkBluePrint(int inputSize, int outputSize){
        maxNumOfLayers = 7;            //This is hardcoded so we only have to change the max in spot to change the rest of the network.
        maxNumOfNeurons = 15;          //Same as above for this line.
        maxBias = 5;                   //This is the maximum random bias value for a neuron.
        maxWeight = 1;                 //This is the maximum random weight value for a neuron.
        Random randLayers = new Random(maxNumOfLayers);
        Random randNeurons = new Random(maxNumOfNeurons);
        numOfInputs = inputSize;
        numOfOutputs = outputSize;
        numOfHiddenLayers = randLayers.nextInt();
        numOfNeuronsPerLayer = new int[numOfHiddenLayers];
        for(int x = 0; x < numOfHiddenLayers; x++) {                    //This for-loop is creating random bias and weights for each of the hidden layer neurons.
            numOfNeuronsPerLayer[x] = randNeurons.nextInt();
        }
        generateNeuralNetwork();
    }

    public NeuralNetworkBluePrint(int inputSize, int outputSize, int hiddenLayers, int[] neuronsPerLayer){
        numOfInputs = inputSize;
        numOfOutputs = outputSize;
        numOfHiddenLayers = hiddenLayers;
        numOfNeuronsPerLayer = neuronsPerLayer;
        maxBias = 5;
        maxWeight = 1;
        generateNeuralNetwork();
    }


    public NeuralNetworkBluePrint(int aNumOfInputs, double[][] aInputWeights, double[] aInputBias,
                                  int aNumOfHiddenLayers, int[] aNumOfNeuronsPerLayer, double[][][] aHiddenLayerWeights,
                                  double[][] aHiddenLayerBias, int aNumOfOutputs, double[][] aOutputWeights, double[] aOutputBias){
        numOfInputs = aNumOfInputs;                         //All of these statements are just assigning the parameter values to the objects variables.
        inputWeights = aInputWeights;
        inputBias = aInputBias;
        numOfHiddenLayers = aNumOfHiddenLayers;
        numOfNeuronsPerLayer = aNumOfNeuronsPerLayer;
        hiddenLayerWeights = aHiddenLayerWeights;
        hiddenLayerBias = aHiddenLayerBias;
        numOfOutputs = aNumOfOutputs;
        outputWeights = aOutputWeights;
        outputBias = aOutputBias;
        maxBias = 5;
        maxWeight = 1;
    }


    private void generateNeuralNetwork(){
        inputWeights = new double[numOfInputs][1];
        inputBias = new double[numOfInputs];
        for(int x = 0; x < numOfInputs; x++){                          //This for-loop is creating random bias and weights for the input neurons.
            inputBias[x] = Math.random() * (maxBias * 2) - maxBias;
            inputWeights[x][0] = Math.random() * (maxWeight * 2) - maxWeight;
        }
        hiddenLayerBias = new double[numOfHiddenLayers][];
        hiddenLayerWeights = new double[numOfHiddenLayers][][];
        for(int x = 0; x < numOfHiddenLayers; x++){         //This for-loop is creating random bias and weights for each of the hidden layer neurons.
            double[] biasTemp = new double[numOfNeuronsPerLayer[x]];
            double[][] weigthTemp1 = new double[numOfNeuronsPerLayer[x]][];
            for (int y = 0; y < numOfNeuronsPerLayer[x]; y++){
                biasTemp[y] = Math.random() *  (maxBias * 2) - maxBias;
                if(x > 0){      //This if-statement checks if it is the first hidden layer.
                    double[] weightTemp2 = new double[numOfNeuronsPerLayer[x-1]];
                    for(int z = 0; z < numOfNeuronsPerLayer[x-1]; z++){  //If it is not the first layer it will look at the size of the layer before it and create that many random weights for each neuron.
                        weightTemp2[z] = Math.random() * (maxWeight * 2) - maxWeight;
                    }
                    weigthTemp1[y] = weightTemp2;
                }
                else{
                    double[] weightTemp2 = new double[numOfInputs];
                    for (int z = 0; z < numOfInputs; z++){              //If it is the first layer then it will look at the size of the input layer and create that may random neuron weights.
                        weightTemp2[z] =  Math.random() * (maxWeight * 2) - maxWeight;
                    }
                    weigthTemp1[y] = weightTemp2;
                }
                hiddenLayerBias[x] = biasTemp;
                hiddenLayerWeights[x] = weigthTemp1;
            }
        }
        outputBias = new double[numOfOutputs];
        outputWeights = new double[numOfOutputs][];
        for (int x = 0; x < numOfOutputs; x++){                        //This for-loop randomly creates a bias for each neuron in the output layer.
            outputBias[x] = Math.random() *  (maxBias * 2) - maxBias;
            double[] temp = new double[numOfNeuronsPerLayer[numOfNeuronsPerLayer.length-1]];
            for (int y = 0; y < numOfNeuronsPerLayer[numOfNeuronsPerLayer.length - 1]; y++){     //This for-loop randomly create a weight for every neuron in the last hidden layer for each neuron in the output layer.
                temp[y] = Math.random() * (maxWeight * 2) - maxWeight;
            }
            outputWeights[x] = temp;
        }
    }

    @Override
    public NeuralNetworkBluePrint clone(){
        return this;
    }

    public void mutateInputBias(int inputNeuron){
        inputBias[inputNeuron] = Math.random() * (maxBias * 2) - maxBias;
    }

    public void mutateAInputNeuronWeight(int inputNeuron, int weightNum){
        inputWeights[inputNeuron][weightNum] = Math.random() * (maxWeight * 2) - maxWeight;
    }

    public void mutateAllInputNeuronWeights(int inputNeuron){
        for(int i = 0; i < inputWeights[inputNeuron].length; i++){
            inputWeights[inputNeuron][i] =  Math.random() * (maxWeight * 2) - maxWeight;
        }

    }

    public void mutateInputBiasLayer(){
        for(int i = 0; i < numOfInputs; i++){
            mutateInputBias(i);
        }
    }

    public void mutateInputWeightLayer(){
        for(int i = 0; i < numOfInputs; i++){
            mutateAllInputNeuronWeights(i);
        }
    }

    public void mutateAHiddenLayerWeight(int layerNum, int neuronNum, int weightNum){
        hiddenLayerWeights[layerNum][neuronNum][weightNum] =  Math.random() * (maxWeight * 2) - maxWeight;
    }

    public void mutateAllHiddenLayerNeuronWeights(int layerNum, int neuronNum){
        for(int i = 0; i < hiddenLayerWeights[layerNum][neuronNum].length; i++){
            hiddenLayerWeights[layerNum][neuronNum][i] =  Math.random() * (maxWeight * 2) - maxWeight;
        }
    }

    public void mutateHiddenLayerBias(int layerNum, int neuronNum){
        hiddenLayerBias[layerNum][neuronNum] = Math.random() * (maxBias * 2) - maxBias;
    }

    public void mutateHiddenLayerWeightLayer(int layerNum){
        for(int i = 0; i < numOfNeuronsPerLayer[layerNum]; i++){
            mutateAllHiddenLayerNeuronWeights(layerNum, i);
        }
    }

    public void mutateHiddenLayerBiasLayer(int layerNum){
        for(int i = 0; i < numOfNeuronsPerLayer[layerNum]; i++){
            mutateHiddenLayerBias(layerNum, i);
        }
    }


    public void mutateAOutputNeuronWeight(int outputNeuron, int weightNum){
        outputWeights[outputNeuron][weightNum] = Math.random() * (maxWeight * 2) - maxWeight;
    }

    public void mutateOutputBias(int outputNeuron){
        outputBias[outputNeuron] = Math.random() * (maxBias * 2) - maxBias;
    }

    public void mutateAllOutputNeuronWeights(int outputNeuron){
        for(int i = 0; i < outputWeights[outputNeuron].length; i++){
            outputWeights[outputNeuron][i] =  Math.random() * (maxWeight * 2) - maxWeight;
        }
    }

    public void mutateOutputWeightLayer(){
        for(int i = 0; i < numOfOutputs; i++){
            mutateAllOutputNeuronWeights(i);
        }
    }

    public void mutateOutputBiasLayer(){
        for(int i = 0; i < numOfOutputs; i++){
            mutateOutputBias(i);
        }
    }

    public double[] getInputBias() {
        return inputBias;
    }

    public double[] getOutputBias() {
        return outputBias;
    }

    public double[][] getHiddenLayerBias() {
        return hiddenLayerBias;
    }

    public double[][] getOutputWeights() {
        return outputWeights;
    }

    public double[][] getInputWeights() {
        return inputWeights;
    }

    public int getNumOfHiddenLayers() {
        return numOfHiddenLayers;
    }

    public int getNumOfInputs() {
        return numOfInputs;
    }

    public int getNumOfOutputs() {
        return numOfOutputs;
    }

    public int[] getNumOfNeuronsPerLayer() {
        return numOfNeuronsPerLayer;
    }

    public double[][][] getHiddenLayerWeights() {
        return hiddenLayerWeights;
    }

}
