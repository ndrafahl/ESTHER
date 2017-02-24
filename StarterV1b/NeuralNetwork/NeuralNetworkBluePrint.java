package NeuralNetwork;

import java.util.ArrayList;
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


public class NeuralNetworkBluePrint {
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
    }

    private void generateNeuralNetwork(){
        inputWeights = new double[numOfInputs][1];
        inputBias = new double[numOfInputs];
        for(int x = 0; x < numOfInputs; x++){                          //This for-loop is creating random bias and weights for the input neurons.
            inputBias[x] = Math.random() * (maxBias * 2) - maxBias;
            inputWeights[x][0] = Math.random() * (maxWeight * 2) - maxWeight;
        }
        numOfNeuronsPerLayer = new int[numOfHiddenLayers];
        hiddenLayerBias = new double[numOfHiddenLayers][];
        hiddenLayerWeights = new double[numOfHiddenLayers][][];
        for(int x = 0; x < numOfHiddenLayers; x++){                    //This for-loop is creating random bias and weights for each of the hidden layer neurons.
            for (int y = 0; y < numOfNeuronsPerLayer[x]; y++){
                hiddenLayerBias[x][y] = Math.random() *  (maxBias * 2) - maxBias;
                if(x > 0){                                              //This if-statement checks if it is the first hidden layer.
                    for(int z = 0; z < numOfNeuronsPerLayer[x-1]; z++){  //If it is not the first layer it will look at the size of the layer before it and create that many random weights for each neuron.
                        hiddenLayerWeights[x][y][z] = Math.random() * (maxWeight * 2) - maxWeight;
                    }
                }
                else{
                    for (int z = 0; z < numOfInputs; z++){              //If it is the first layer then it will look at the size of the input layer and create that may random neuron weights.
                        hiddenLayerWeights[x][y][z] =  Math.random() * (maxWeight * 2) - maxWeight;
                    }
                }
            }
        }
        outputBias = new double[numOfOutputs];
        outputWeights = new double[numOfOutputs][];
        for (int x = 0; x < numOfOutputs; x++){                        //This for-loop randomly creates a bias for each neuron in the output layer.
            outputBias[x] = Math.random() *  (maxBias * 2) - maxBias;
            for (int y = 0; y < numOfNeuronsPerLayer[numOfNeuronsPerLayer.length]; y++){     //This for-loop randomly create a weight for every neuron in the last hidden layer for each neuron in the output layer.
                outputWeights[x][y] = Math.random() * (maxWeight * 2) - maxWeight;
            }
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

    // We still need to create the methods for mutating this Neural Network
}
