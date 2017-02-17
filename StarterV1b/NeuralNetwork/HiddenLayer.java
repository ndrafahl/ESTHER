package NeuralNetwork;

import java.util.ArrayList;

/**
 * Created by Russell on 2/16/2017.
 */
public class HiddenLayer extends NetworkLayer {

    public HiddenLayer(int outputLayerSize, int inputLayerSize){
        layerArray = new ArrayList<Neuron>(outputLayerSize);
        for(x = 0; x < outputLayerSize; x++){
            layerArray.add(x, new SigmoidNeuron(inputLayerSize));
        }
    }
}
