package NeuralNetwork;
import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 */
public abstract class NetworkLayer {

    protected ArrayList<Neuron> layerArray;
    protected double[] outputArray;
    protected int layerSize;


    public double[] getNeuronLayerOutputs(){return outputArray;}


}
