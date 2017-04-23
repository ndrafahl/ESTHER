package NeuralNetwork;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Russell on 2/13/2017.
 */
public class NeuralNetwork {

    private ArrayList<HiddenLayer> layerList;
    private OutputLayer outputLayer;
    private InputLayer inputLayer;
    private String output;


    public NeuralNetwork (NeuralNetworkBluePrint aNetwork){
        inputLayer = new InputLayer(aNetwork.getNumOfInputs(), aNetwork.getInputWeights(), aNetwork.getInputBias());
        layerList = new ArrayList<HiddenLayer>(aNetwork.getNumOfHiddenLayers());
        for(int i = 0; i < aNetwork.getNumOfHiddenLayers(); i++){
            if(i > 0){
                layerList.add(new HiddenLayer(aNetwork.getNumOfNeuronsPerLayer()[i], aNetwork.getHiddenLayerWeights()[i],
                        aNetwork.getHiddenLayerBias()[i], layerList.get(i-1)));
            }
            else{
                layerList.add(new HiddenLayer(aNetwork.getNumOfNeuronsPerLayer()[i], aNetwork.getHiddenLayerWeights()[i],
                        aNetwork.getHiddenLayerBias()[i], inputLayer));
            }
        }
        outputLayer = new OutputLayer(aNetwork.getNumOfOutputs(), aNetwork.getOutputWeights(),
                aNetwork.getOutputBias(), layerList.get(layerList.size()-1));
    }

    public String makeDecision(int[] inputs){
        inputLayer.beginComputing(inputs);
        for(int x = 0; x < layerList.size(); x++){
            layerList.get(x).computeOutputs();
        }
        outputLayer.computeOutputs();
        double maxOutput = 0;
        int maxOutputIndex = 0;
        for(int x = 0; x < outputLayer.getNeuronLayerOutputs().length; x++) {
            if(outputLayer.getNeuronLayerOutputs()[x] > maxOutput){
                maxOutput = outputLayer.outputArray[x];
                maxOutputIndex = x;
            }
        }
        switch (maxOutputIndex){
            case 0: output = "bet";
                    break;
            case 1: output = "call";
                    break;
            case 2: output = "fold";
                    break;
            default: output = "fold";
        }
        return output;
    }
}
