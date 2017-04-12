package NeuralNetwork;


import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 *
 * This class is the first layer in the neural network.
 * It will have the same number of neurons as there are
 * input data values. It will only have one weight value
 * because it will only be excepting one value from the
 * input data.
 *
 * Its only method is the beginComputing function.
 * This function begins the process of generating
 * an output for playing poker.
 *
 * The only information that is stored in this object is its
 * list of neurons, the size of the layer, and a list of each
 * neurons output value (this value will be between 0 and 1).
 *
 */
public class InputLayer extends NetworkLayer {

    /***************************************************************
     * Constructors
     * @param size
     * @param weights
     * @param bias
     ***************************************************************/

    public InputLayer(int size, double[][] weights, double[] bias){
        layerArray = new ArrayList<Neuron>(size);
        layerSize = size;
        outputArray = new double[size];
        for(int x = 0; x < size; x++){
            layerArray.add(x, new SigmoidNeuron(weights[x], bias[x]));
        }
    }


    /********************************************************
     * Methods
     *
     * This Method takes in a list of inputs from the neural network
     * and converts it to a double and sends that input to the neuron.
     *******************************************************/


    public void beginComputing(int[] input){
        for(int i = 0; i < layerSize; i++){
            double[] temp = new double[1];
            temp[0] = Double.valueOf(input[i]);
            layerArray.get(i).computeOutput(temp);
            outputArray[i] = layerArray.get(i).getOutput();
        }
    }
}
