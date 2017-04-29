import java.io.IOException;
import java.util.Dictionary;

/**
 * Created by Russell on 4/15/2017.
 */
public class PreflopTrainer {


    /**
     * This function determines what the neural networks pre flop call and raise thresholds should be.
     * It creates 6 players that adjust their thresholds up or down if they are not the winner of the game.
     * @throws IOException
     */
    public static void trainPreflop() throws IOException{
        int[] limits = {1,1,1,2,2};
        int handsToPlay = 1000;
        int winAmount;
        int maxBankroll;
        int bestPlayer;
        int trainingSize = 10000;
        double[] winningThresholds = new double[trainingSize];
        int[] winningBankroll = new int[trainingSize];
        NeuralNetworkPlayer[] playerList = new NeuralNetworkPlayer[6];
        playerList[0] = new NeuralNetworkPlayer("plus .001");
        playerList[1] = new NeuralNetworkPlayer("minus .001");
        playerList[2] = new NeuralNetworkPlayer("plus .001");
        playerList[3] = new NeuralNetworkPlayer("minus .001");
        playerList[4] = new NeuralNetworkPlayer("plus .001");
        playerList[5] = new NeuralNetworkPlayer("minus .001");


        //This initializes the players call and raise thresholds to a specified value.
        for(int i = 0; i < 6; i++){
            playerList[i].setRaiseThreshold(.9);
            playerList[i].setCallThreshold(.16);
        }
        Dealer dealer = new Dealer(playerList.length);
        //This loop plays the specified number of games allowing there to be a large enough sample size
        //to give use a good idea of what threshold is the best.
        for(int i = 0; i < trainingSize; i++){
            //maxBankroll keeps track of that games highest bankroll.
            maxBankroll = 0;
            //bestPlayer keeps track of the threshold of the player with the highest bankroll
            bestPlayer = 0;
            //The game manager is created to player the determined number of hands with the determined players.
            GameManager game = new GameManager(playerList, dealer, false, limits, 3, handsToPlay);
            game.playGame();
            //This loops through all the players and checks which on had the highest bankroll and saves its bankroll
            //total and its threshold.
            for(int j = 0; j < 6; j++){
                winAmount = game.getBankroll(j);
                if(maxBankroll < winAmount){
                    maxBankroll = winAmount;
                    bestPlayer = j;
                }
            }
            //Here we are saving the bankroll and threshold of the winning player to be used later
            winningThresholds[i] = playerList[bestPlayer].getCallThreshold();
            winningBankroll[i] = maxBankroll;
            //Here we are adjusting the losing players thresholds up or down .001 depending if they are sitting in an even
            //or an odd seat.
            for(int j = 0; j < 6; j++){
                if(j == bestPlayer){

                }
                else if(j % 2 == 0){
                    playerList[j].setCallThreshold(playerList[j].getCallThreshold() + .001);
                }
                else{
                    playerList[j].setCallThreshold(playerList[j].getCallThreshold() - .001);
                }

                if(playerList[j].getCallThreshold() < .16 || playerList[j].getCallThreshold() > .56){
                    playerList[j].setCallThreshold(.16);
                }
            }
        }
        //From here down we are sorting the winning threshold array so that it is easier to add up all the bankrolls
        //of the same threshold to determine which is the best. We used the bubble sort algorithm to do this so when
        //a threshold hold is switched the corresponding bankroll is switched with it to keep them in the same index.
        int tempInt;
        double tempDouble;
        boolean changeMade;
        for(int i = trainingSize - 1; i > 0; i--) {
            changeMade = false;
            for (int j = 0; j < i; j++) {
                if (winningThresholds[j] < winningThresholds[j + 1]) {
                    tempInt = winningBankroll[j];
                    winningBankroll[j] = winningBankroll[j + 1];
                    winningBankroll[j + 1] = tempInt;
                    tempDouble = winningThresholds[j];
                    winningThresholds[j] = winningThresholds[j + 1];
                    winningThresholds[j + 1] = tempDouble;
                    changeMade = true;
                }
            }
            if (!changeMade) {
                break;
            }

        }
        //This section accumulates the bankroll values until it finds a threshold of a different value. If it finds a
        //different value it checks to see if the current accume value is the highest its seen yet and saves the value
        //and threshold if it is, else it just rests the accume to the value of its current index. The threshold with
        //the highest bankroll total is the printed out to the user.
        int accume = 0;
        double previousThresh = 0.0;
        int maxBankrollSum = 0;
        double bestThreshold = 0.0;
        for(int i = 0; i < trainingSize; i++){
            if(winningThresholds[i] != previousThresh){
                if(accume > maxBankrollSum){
                    maxBankrollSum = accume;
                    bestThreshold = previousThresh;
                }
                accume = winningBankroll[i];
                previousThresh = winningThresholds[i];
            }
            else{
                accume += winningBankroll[i];
            }
        }

        System.out.println(String.valueOf(bestThreshold));
        System.out.println(String .valueOf(maxBankrollSum));
    }
}
