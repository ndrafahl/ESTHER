package NeuralNetwork;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Russell on 2/16/2017.
 */
public class HiddenLayer extends NetworkLayer {

    NetworkLayer previousLayer;

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
