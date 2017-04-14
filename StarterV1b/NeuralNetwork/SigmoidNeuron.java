package NeuralNetwork;


/**
 * Created by Russell on 2/15/2017.
 *
 * This class is an implementation of the Neuron class.
 *
 * This class is a sigmoidNeuron and it is different from
 * normal neurons. This neuron will return outputs between
 * 1 and 0 depending on how large its inputs add up to be.
 *
 */
public class SigmoidNeuron implements Neuron {
    private double bias, output;
    private double[] weights;

    /**************************************************************
     * Constructors
     * @param inputWeights
     * @param biasWeight
     *
     * This function only has on constructor. Due to the neural
     * network blue print class this neurons weights and bias will
     * have already been created so all we need to do is pass it in
     * and this object stores that data.
     *************************************************************/

    public SigmoidNeuron(double[] inputWeights, double biasWeight){
        weights = inputWeights;
        bias = biasWeight;
    }


    @Override
    public void computeOutput(double[] inputs) {
        double z = 0;                      //z is the summation of all the outputs of the previous layer multiplied by their weights
        for(int x = 0; x < inputs.length; x++){
           z += inputs[x] * weights[x];
        }
      
        output = 1 / (1 + Math.exp(-z - bias));      //output is a double between 1 and 0 that is calculated from the summation

    }

    @Override
    public double getOutput() {
        return output;
    }



}
