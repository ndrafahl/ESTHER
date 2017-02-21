package NeuralNetwork;

import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 */
public class OutputLayer extends NetworkLayer {

    private NetworkLayer previousLayer;

    public OutputLayer(int size, double[][] weights, double[] bias, NetworkLayer thePreviousLayer){
        layerSize = size;
        outputArray = new double[size];
        layerArray = new ArrayList<Neuron>(size);
        previousLayer = thePreviousLayer;
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
