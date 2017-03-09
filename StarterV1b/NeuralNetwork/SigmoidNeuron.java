package NeuralNetwork;


/**
 * Created by Russell on 2/15/2017.
 */
public class SigmoidNeuron implements Neuron {
    private double bias, z, output;
    private double[] weights;
    private int x;


    public SigmoidNeuron(int sizeOfPreviousLayer){
        weights = new double[sizeOfPreviousLayer];
        bias = Math.random() * 10 - 5;
        for(x = 0; x < sizeOfPreviousLayer; x++){
            weights[x] = Math.random() * 2 - .999;
        }
        output = 0;
    }

    public SigmoidNeuron(double biasWeight, double[] inputWeights){
        weights = inputWeights;
        bias = biasWeight;
        output = 0;
    }


    @Override
    public void setBias(double biasWeight) {
        bias = biasWeight;
    }

    @Override
    public double getBias() {
        return this.bias;
    }

    @Override
    public void computeOutput(double[] inputs) {
        z = 0;
        for(x = 0; x < inputs.length; x++){
           z += inputs[x] * weights[x];
        }
        output = 1 / (1 + Math.exp(-z - bias));
    }

    @Override
    public double getOutput() {
        return output;
    }

    public double[] getWeights(){return weights;}

    public double getWeightAt(int index){return weights[index];}

    public void changeWeights(double[] newWeights){
        weights = newWeights;
    }

    public void changeWeightAt(int index, double newWeight){
        weights[index] = newWeight;
    }
}
