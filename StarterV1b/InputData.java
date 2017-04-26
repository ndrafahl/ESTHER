/**
 * Created by Russell on 2/18/2017.
 */
public class InputData {



    public static int[] getInputList(TableData data){
        int[] inputArray = new int[57];
        int[] boardCards = data.getBoard();
        TrainingFunction cardConverter = new TrainingFunction();
        BestHand bestHand;
        String inputString = "";

        inputString += cardConverter.cardToBinaryString(data.getPocket()[0]) + ",";
        inputString += cardConverter.cardToBinaryString(data.getPocket()[1]) + ",";


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

        String[] inputList = inputString.split(",");
        for(int i = 0; i< 57; i++){
            inputArray[i] = Integer.valueOf(inputList[i]);
        }

        return inputArray;

    }

}
