import NeuralNetwork.NeuralNetworkBluePrint;

import java.util.ArrayList;
import java.util.Random;
import static java.lang.Integer.min;

/**
 * Created by Russell on 3/22/2017.
 */
public class GATrainingFunc {

    private int generations, populationSize, mutationCount, selectionCount, newBloodCount, handsToPlay;
    private String outputFile;
    private NeuralNetworkBluePrint[] networkArray;
    private NeuralNetworkBluePrint bestNeuralNetwork;
    private Player[] playerArray;
    private GameManager g;
    private Player[] trainingPlayerList;
    private int[] playerResults;
    private int[] limits = {1,1,1,2,2};
    private Random rand;



    public void GAtrainingFunc(int aGenerations, int aPopulationSize, int aMutationCount, int aSelectionCount,
                               int aNewBloodCount, int aHandsToPlay, String aOutputFile){
        generations = aGenerations;
        populationSize = aPopulationSize;
        mutationCount = aMutationCount;
        selectionCount = aSelectionCount;
        newBloodCount = aNewBloodCount;
        handsToPlay = aHandsToPlay;
        outputFile = aOutputFile;
        rand = new Random(populationSize);
        networkArray = new NeuralNetworkBluePrint[populationSize];
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
                trainingPlayerList[5] = playerArray[j];
                Dealer dealer = new Dealer(6, 123456789);
                g = new GameManager(trainingPlayerList, dealer, false, limits, 3, handsToPlay);
                g.playGame();
                playerResults[j] = g.getBankroll(5);
            }
            sortPopulation();
            nextGeneration();
        }
    }

    private void createPopulation(){
        for(int i = 0; i < populationSize; i++){
            networkArray[i] = new NeuralNetworkBluePrint(40, 3);
            playerArray[i] = new NeuralNetworkPlayer(networkArray[i]);
        }
    }

    private void sortPopulation(){
        int tempInt;
        NeuralNetworkBluePrint tempBlueprint;
        boolean changeMade;
        for(int i = 0; i < playerResults.length - 1; i++){
            changeMade = false;
            for(int j = i; j < playerResults.length - i; j++){
                if(playerResults[j] < playerResults[j+1]){
                        tempInt = playerResults[j];
                        playerResults[j] = playerResults[j+1];
                        playerResults[j+1] = tempInt;
                        tempBlueprint = networkArray[j];
                        networkArray[j] = networkArray[j+1];
                        networkArray[j+1] = tempBlueprint;
                        changeMade = true;
                }
            }
            if(!changeMade){
                break;
            }
        }
    }

    private void nextGeneration(){
        int first;
        int second;
        NeuralNetworkBluePrint[] tempBlueprintList = new NeuralNetworkBluePrint[populationSize];
        for(int i = 0; i < mutationCount; i++){
            //mutate blueprint and place it in temp
        }
        for(int i = 0; i < selectionCount; i++){
            first = rand.nextInt();
            second = rand.nextInt();
            first = min(first, second);
            tempBlueprintList[mutationCount + i] = networkArray[first];
        }
        for(int i = 0; i < newBloodCount; i++){
            tempBlueprintList[mutationCount+selectionCount+i] = new NeuralNetworkBluePrint(40, 3);
        }

    }
}
