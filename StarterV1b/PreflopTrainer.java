import java.io.IOException;
import java.util.Dictionary;

/**
 * Created by Russell on 4/15/2017.
 */
public class PreflopTrainer {


    public static void trainPreflop() throws IOException{
        int[] limits = {1,1,1,2,2};
        int handsToPlay = 100;
        int winAmount;
        int maxBankroll;
        int bestPlayer;
        int globalBestBankroll;
        double globaleBestCallThreshold;
        int trainingSize = 10000;
        double[] winningThresholds = new double[trainingSize];
        int[] winningBankroll = new int[trainingSize];
        NeuralNetworkPlayer[] playerList = new NeuralNetworkPlayer[6];
        playerList[0] = new NeuralNetworkPlayer("plus .01");
        playerList[1] = new NeuralNetworkPlayer("minus .01");
        playerList[2] = new NeuralNetworkPlayer("plus .01");
        playerList[3] = new NeuralNetworkPlayer("minus .01");
        playerList[4] = new NeuralNetworkPlayer("plus .01");
        playerList[5] = new NeuralNetworkPlayer("minus .01");

        for(int i = 0; i < 6; i++){
            playerList[i].setRaiseThreshold(.22);
            playerList[i].setCallThreshold(.155);
        }
        Dealer dealer = new Dealer(playerList.length);
        globalBestBankroll = 0;
        globaleBestCallThreshold = 0.0;
        for(int i = 0; i < trainingSize; i++){
            maxBankroll = 0;
            bestPlayer = 0;
            GameManager game = new GameManager(playerList, dealer, false, limits, 3, handsToPlay);
            game.playGame();
            for(int j = 0; j < 6; j++){
                winAmount = game.getBankroll(j);
                if(maxBankroll < winAmount){
                    maxBankroll = winAmount;
                    bestPlayer = j;
                }
            }
            winningThresholds[i] = playerList[bestPlayer].getRaiseThreshold();
            winningBankroll[i] = maxBankroll;
            for(int j = 0; j < 6; j++){
                if(j == bestPlayer){

                }
                else if(j % 2 == 0){
                    playerList[j].setRaiseThreshold(playerList[j].getRaiseThreshold() + .001);
                }
                else{
                    playerList[j].setRaiseThreshold(playerList[j].getRaiseThreshold() - .001);
                }

                if(playerList[j].getRaiseThreshold() < .16 || playerList[j].getRaiseThreshold() > .56){
                    playerList[j].setRaiseThreshold(.16);
                }
            }
        }
        System.out.println("bankrolls");
        for(int i = 0; i < 1; i++){
            System.out.println(String.valueOf(winningBankroll[i]));
        }
        System.out.println("thresholds");
        for(int i = 0; i < 1; i++){
            System.out.println(String.valueOf(winningThresholds[i]));
        }
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
