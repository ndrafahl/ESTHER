package DAOFiles;

import NeuralNetwork.NeuralNetworkBluePrint;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Russell on 2/17/2017.
 *
 * This is a Data Access Object meant for loading and saving neural network
 * blue prints to a file so they can be saved and reused.
 */
public class NeuralNetworkDAO  {

    /**********************************************************
     * loadNeuralNetworkList takes in a file name and outputs an array of neural network blueprints.
     * @param filePath
     * @return
     * @throws IOException
     */

    public NeuralNetworkBluePrint loadNeuralNetworkList(String filePath) throws IOException{
        NeuralNetworkBluePrint neuralNetworkBluePrint;
        Scanner fileIn;
        int x, y, numOfInputs, numOfHiddenLayers, numOfOutputs;
        int[] numOfNeuronsPerLayer;
        String[] temp;
        double[] inputBias, outputBias;
        double[][] hiddenLayerBias, outputWeights, inputWeights;
        double[][][] hiddenLayerWeights;

        try{
            fileIn = new Scanner(new FileInputStream(filePath));
        }
        catch (IOException e){
            throw e;
        }
        //This line checks if there is another neural network to be constructed from the file.
        numOfInputs = Integer.valueOf(fileIn.nextLine());   //The first line of each neural network is the number of input neurons.
        inputWeights = new double[numOfInputs][];           //Constructs a double array the size of the number of inputs. This stores an array of each neuron weights.
        inputBias = new double[numOfInputs];                //Same as above but stores the bias for each input neuron.
        for (x = 0; x < numOfInputs; x++) {                   //This for-loop reads the input weights into an array of arrays.
            temp = fileIn.nextLine().split(",");
            inputWeights[x][0] = Double.valueOf(temp[x]);
        }
        temp = fileIn.nextLine().split(",");
        for (x = 0; x < numOfInputs; x++) {                   //This for-loop takes the string bias and turn them to doubles and places them in the input bias array.
            inputBias[x] = Double.valueOf(temp[x]);
        }
        numOfHiddenLayers = Integer.valueOf(fileIn.nextLine());   //This saves the number of hidden layers.
        temp = fileIn.nextLine().split(",");
        numOfNeuronsPerLayer = new int[numOfHiddenLayers];       //Number of neurons per hidden layer.
        for (x = 0; x < numOfHiddenLayers; x++) {
            numOfNeuronsPerLayer[x] = Integer.valueOf(temp[x]);
        }
        hiddenLayerWeights = new double[numOfHiddenLayers][][];
        for (x = 0; x < numOfHiddenLayers; x++) {                  //Reads in the weights for each neuron in each hidden layer.
            for (y = 0; y < numOfNeuronsPerLayer[x]; y++) {
                temp = fileIn.nextLine().split(",");
                for (int z = 0; z < temp.length; z++) {
                    hiddenLayerWeights[x][y][z] = Double.valueOf(temp[z]);
                }
            }
        }
        hiddenLayerBias = new double[numOfHiddenLayers][];
        for (x = 0; x < numOfHiddenLayers; x++) {                    //Reads in the bias for each neuron in each hidden layer.
            temp = fileIn.nextLine().split(",");
            for (y = 0; y < temp.length; y++) {
                hiddenLayerBias[x][y] = Double.valueOf(temp[y]);
            }
        }
        numOfOutputs = Integer.valueOf(fileIn.nextLine());          //Output layer size.
        outputWeights = new double[numOfOutputs][];
        for (x = 0; x < numOfOutputs; x++) {                         //Reads the weights for each neuron in the output layer.
            temp = fileIn.nextLine().split(",");
            for (y = 0; y < temp.length; y++) {
                outputWeights[x][y] = Double.valueOf(temp[y]);
            }
        }
        outputBias = new double[numOfOutputs];
        temp = fileIn.nextLine().split(",");
        for (x = 0; x < numOfOutputs; x++) {                         //Reads the bias for each neuron in the output layer.
            outputBias[x] = Double.valueOf(temp[x]);
        }
        neuralNetworkBluePrint = new NeuralNetworkBluePrint(numOfInputs, inputWeights, inputBias,
                numOfHiddenLayers, numOfNeuronsPerLayer, hiddenLayerWeights,
                hiddenLayerBias, numOfOutputs, outputWeights, outputBias);
            fileIn.close();


        return neuralNetworkBluePrint;
    }

    /*******************************************************************************
     * saveNeuralNetworkList method will most likely only be used during the training function
     * in order to save the best neural networks for a specific pair of cards to a file.
     * This method appends the neural network blueprint to the end of the file so a full
     * working player can created for later playing. This method can only be passed
     * one blueprint at a time. It is not meant and nor should it be used to write an entire
     * array of neural network blueprints.
     *
     * @param neuralNetwork
     * @param filePath
     * @throws IOException
     ********************************************************************************/

    public void saveNeuralNetworkList(NeuralNetwork.NeuralNetworkBluePrint neuralNetwork, String filePath) throws IOException{
        FileWriter fileOut;
        int i;

        try{
            fileOut = new FileWriter(filePath, true);     //This is a file writer that appends to the end of the file instead of overwriting it.
            fileOut.write(String.valueOf(neuralNetwork.getNumOfInputs()) + "\r\n");               //The rest of the code just writes all the variables from the blueprint to its own line in the same way it is read above.
            for(i = 0; i < neuralNetwork.getNumOfInputs(); i++){
                fileOut.write(String.valueOf(neuralNetwork.getInputWeights()[i][0]) + "\r\n");
            }
            for(i = 0; i < neuralNetwork.getInputBias().length - 1; i++){
                fileOut.write(String.valueOf(neuralNetwork.getInputBias()[i]) + ",");
            }
            fileOut.write(String.valueOf(neuralNetwork.getInputBias()[neuralNetwork.getInputBias().length - 1]) + "\r\n");
            fileOut.write(String.valueOf(neuralNetwork.getNumOfHiddenLayers()) + "\r\n");
            for(i = 0; i < neuralNetwork.getNumOfNeuronsPerLayer().length - 1; i++){
                fileOut.write(String.valueOf(neuralNetwork.getNumOfNeuronsPerLayer()[i]) + ",");
            }
            fileOut.write(String.valueOf(neuralNetwork.getNumOfNeuronsPerLayer()[neuralNetwork.getNumOfNeuronsPerLayer().length - 1]) + "\r\n");
            for(i = 0; i < neuralNetwork.getHiddenLayerWeights().length; i++){
                for(int j = 0; j < neuralNetwork.getHiddenLayerWeights()[i].length; j++){
                    for(int k = 0; k < neuralNetwork.getHiddenLayerWeights()[i][j].length - 1; k++){
                        fileOut.write(String.valueOf(neuralNetwork.getHiddenLayerWeights()[i][j][k]) + ",");
                    }
                    fileOut.write(String.valueOf(neuralNetwork.getHiddenLayerWeights()[i][j][neuralNetwork.getHiddenLayerWeights()[i][j].length - 1]) + "\r\n");
                }
            }
            for(i = 0; i < neuralNetwork.getHiddenLayerBias().length; i++){
                for(int j = 0; j < neuralNetwork.getHiddenLayerBias()[i].length - 1; j++){
                    fileOut.write(String.valueOf(neuralNetwork.getHiddenLayerBias()[i][j]) + ",");
                }
                fileOut.write(String.valueOf(neuralNetwork.getHiddenLayerBias()[i][neuralNetwork.getHiddenLayerBias()[i].length - 1]) + "\r\n");
            }
            fileOut.write(String.valueOf(neuralNetwork.getNumOfOutputs()) + "\r\n");
            for(i = 0; i < neuralNetwork.getOutputWeights().length; i++){
                for(int j = 0; j < neuralNetwork.getOutputWeights()[i].length - 1; j++){
                    fileOut.write(String.valueOf(neuralNetwork.getOutputWeights()[i][j]) + ",");
                }
                fileOut.write(String.valueOf(neuralNetwork.getOutputWeights()[i][neuralNetwork.getHiddenLayerBias()[i].length - 1]) + "\r\n");
            }
            for(i = 0; i < neuralNetwork.getOutputBias().length - 1; i++){
                fileOut.write(String.valueOf(neuralNetwork.getOutputBias()[i]) + ",");
            }
            fileOut.write(String.valueOf(neuralNetwork.getNumOfNeuronsPerLayer()[neuralNetwork.getOutputBias().length - 1]) + "\r\n");

            fileOut.close();

        }
        catch (IOException e){
            throw e;
        }
    }

    public double[] loadWinRateArray() throws IOException{
        Scanner fileIn;
        String winRatesString;
        double winRate;
        double[] winRateArray = new double[169];


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
}
