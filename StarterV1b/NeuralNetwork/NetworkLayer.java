package NeuralNetwork;
import java.util.ArrayList;

/**
 * Created by Russell on 2/15/2017.
 */
public abstract class NetworkLayer {

    protected ArrayList<Neuron> layerArray;
    protected double[] outputArray;
    protected int x;


    public int layerCount(){return layerArray.size();}

    public double[] getNeuronLayerOutputs(){return outputArray;}

    public Neuron getNeuron(int index){return layerArray.get(index);}




}
