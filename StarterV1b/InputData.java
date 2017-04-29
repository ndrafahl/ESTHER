/**
 * Created by Russell on 2/18/2017.
 */
public class InputData {


    /**
     * This function is used for creating the input data for the neural network.
     * It takes in the table data from the neural network player and creates an integer array.
     * @param data
     * @return
     */
    public static int[] getInputList(TableData data){
        int[] inputArray = new int[57];
        int[] boardCards = data.getBoard();
        TrainingFunction cardConverter = new TrainingFunction();
        BestHand bestHand;
        String inputString = "";

        //cardConverter was created while we were trying to train using weka. It is found in the Training Function
        //class and is the only code still used in that class.
        //All the card converter does is turns a card to a string of ones and zeros divided by commas.
        //The string returned is a binary representation of what that cards value is so it can be used in the neural network.
        //These two lines are only for the players pocket cards
        inputString += cardConverter.cardToBinaryString(data.getPocket()[0]) + ",";
        inputString += cardConverter.cardToBinaryString(data.getPocket()[1]) + ",";


        //This is a check to see what betting round the hand is currently in and creates the appropriate binary string.
        if(data.getBettingRound() == 2){
            for(int i = 0; i < 3; i++){
                inputString += cardConverter.cardToBinaryString(boardCards[i]) + ",";
            }
            inputString += "0,0,0,0,0,0,0,0,0,0,0,0,0,0,";
        }
        else if(data.getBettingRound() == 3){
            for(int i = 0; i < 4; i++){
                inputString += cardConverter.cardToBinaryString(boardCards[i]) + ",";
            }
            inputString += "0,0,0,0,0,0,0,";
        }
        else{
            for(int i = 0; i < 5; i++){
                inputString += cardConverter.cardToBinaryString(boardCards[i]) + ",";
            }
        }

        //This section determines what this players best hand is and creates a binary string representing that hand.
        bestHand = EstherTools.getBestHand(data.getPocket(), data.getBoard());

        switch (bestHand.getCombo()){
            case 0: inputString += "0,0,0,0,0,0,0,0";
                    break;
            case 1: inputString += "0,0,0,0,0,0,0,1";
                    break;
            case 2: inputString += "0,0,0,0,0,0,1,0";
                break;
            case 3: inputString += "0,0,0,0,0,1,0,0";
                break;
            case 4: inputString += "0,0,0,0,1,0,0,0";
                break;
            case 5: inputString += "0,0,0,1,0,0,0,0";
                break;
            case 6: inputString += "0,0,1,0,0,0,0,0";
                break;
            case 7: inputString += "0,1,0,0,0,0,0,0";
                break;
            case 8: inputString += "1,0,0,0,0,0,0,0";
                break;
            default: inputString += "0,0,0,0,0,0,0,0";
        }

        //This converts the binary string to an array of ones and zeros.
        String[] inputList = inputString.split(",");
        for(int i = 0; i< 57; i++){
            inputArray[i] = Integer.valueOf(inputList[i]);
        }

        return inputArray;

    }

}
