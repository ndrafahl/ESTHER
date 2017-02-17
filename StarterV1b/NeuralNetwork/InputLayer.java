package NeuralNetwork;


import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 */
public class InputLayer extends NetworkLayer {

    public InputLayer(int size){
        layerArray = new ArrayList<Neuron>(size);
        for(x = 0; x < size; x++){
            layerArray.add(x, new SigmoidNeuron(size));
        }

    }

}
