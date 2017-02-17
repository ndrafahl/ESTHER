package NeuralNetwork;

import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 */
public class OutputLayer extends NetworkLayer {
    private double maxOutput;
    private int maxOutputIndex;

    public OutputLayer(int outputLayerSize, int inputLayerSize){
        layerArray = new ArrayList<Neuron>(outputLayerSize);
        for(x = 0; x < outputLayerSize; x++){
            layerArray.add(x, new SigmoidNeuron(inputLayerSize));
        }
    }


    public String computeOutput(double[] input){
        maxOutput = 0;
        maxOutputIndex = 0;
        for(x = 0; x < layerArray.size(); x++) {
            layerArray.get(x).computeOutput(input);
            outputArray[x] = layerArray.get(x).getOutput();
            if(outputArray[x] > maxOutput){
                maxOutput = outputArray[x];
                maxOutputIndex = x;
            }
        }
        switch (maxOutputIndex){
            case 1: return "bet";
            case 2: return "call";
            case 3: return "fold";
            default: return "fold";
        }

    }
}
