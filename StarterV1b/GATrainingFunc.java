import NeuralNetwork.NeuralNetworkBluePrint;

import java.util.ArrayList;

/**
 * Created by Russell on 3/22/2017.
 */
public class GATrainingFunc {

    private int generations, populationSize, mutationCount, newBloodCount, handsToPlay;
    private String outputFile;
    private ArrayList<NeuralNetworkBluePrint> networkArray;
    private NeuralNetworkBluePrint bestNeuralNetwork;
    private ArrayList<Player> playerArray, tempPlayerArray;
    private GameManager g;
    private Player[] trainingPlayerList;
    private int[] playerResults;
    private int[] limits = {1,1,1,2,2};


    public void GAtrainingFunc(int aGenerations, int aPopulationSize, int aMutationCount,
                               int aNewBloodCount, int aHandsToPlay, String aOutputFile){
        generations = aGenerations;
        populationSize = aPopulationSize;
        mutationCount = aMutationCount;
        newBloodCount = aNewBloodCount;
        handsToPlay = aHandsToPlay;
        outputFile = aOutputFile;
        networkArray = new ArrayList<>();
        trainingPlayerList = new Player[6];
        trainingPlayerList[0] = new AgentAlwaysCall(0);
        trainingPlayerList[1] = new AgentAlwaysFold(1);
        trainingPlayerList[2] = new AgentAlwaysRaise(2);
        trainingPlayerList[3] = new AgentRandomPlayer(3);
        trainingPlayerList[4] = new AgentAlwaysCall(4);
        playerResults = new int[populationSize];
        createPopulation();
        beginTraining();
    }

    private void beginTraining(){
        for(int i = 0; i < generations; i++){
            for(int j = 0; j < populationSize; j++) {
                trainingPlayerList[5] = playerArray.get(j);
                Dealer dealer = new Dealer(6, 123456789);
                g = new GameManager(trainingPlayerList, dealer, false, limits, 3, handsToPlay);
                g.playGame();
                playerResults[j] = g.getBankroll(5);
            }
            sortPopulation();
            //generate next generation population
        }
    }

    private void createPopulation(){
        for(int i = 0; i < populationSize; i++){
            networkArray.add(new NeuralNetworkBluePrint(40, 3));
            playerArray.add(new NeuralNetworkPlayer(networkArray.get(i)));
        }
    }

    private void sortPopulation(){

    }
}
