package NeuralNetwork;

/**
 * Created by Russell on 2/15/2017.
 */
public interface Neuron {

    public void setBias(double biasWeight);

    public double getBias();

    public void computeOutput(double[] inputs);

    public double getOutput();

    public double[] getWeights();
}
