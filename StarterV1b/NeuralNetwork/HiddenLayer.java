package NeuralNetwork;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Russell on 2/16/2017.
 *
 * This class is a hidden layer of a neural network.
 * All that is contained in this class is a list of
 * the neurons in this layer, a pointer to the layer
 * before it, the number of neurons in this layer,
 * and an array that contains the outputs of each
 * of the neurons in this layer.
 */
public class HiddenLayer extends NetworkLayer {

    NetworkLayer previousLayer;

    /*************************************************************
     * Constructor
     * @param size
     * @param weights
     * @param bias
     * @param aThePreviousLayer
     *
     *
     */

    public HiddenLayer(int size, double[][] weights, double[] bias, NetworkLayer aThePreviousLayer){
        previousLayer = aThePreviousLayer;
        layerSize = size;
        outputArray = new double[size];
        layerArray = new ArrayList<Neuron>(size);
        for(int x = 0; x < size; x++){
            layerArray.add(new SigmoidNeuron(weights[x], bias[x]));
        }
    }

    public void computeOutputs(){
        double[] inputs = previousLayer.getNeuronLayerOutputs();
        for(int x = 0; x < layerSize; x++){
            layerArray.get(x).computeOutput(inputs);
            outputArray[x] = layerArray.get(x).getOutput();
        }
    }
}
