import NeuralNetwork.NeuralNetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Russell on 2/15/2017.
 */
public class TrainingFunction {
    private ArrayList<NeuralNetwork> networkList;

    public static BestHand bestHand;

    public void generateData(String aFile) throws IOException {

        try {


            String fileName = aFile;
            Dealer dealer = new Dealer(1);
            int[] pocketCards;
            int[] flopCards;
            int turnCard;
            int riverCard;
            FileWriter fileOut = new FileWriter(fileName);
            Random randNum = new Random();
            int handRound;


            fileOut.write("@relation pokerData \r\n");
            for (int i = 1; i < 3; i++) {
                for(int j = 1; j < 8; j++) {
                    fileOut.write("@attribute PocketCard" + String.valueOf(i) + String.valueOf(j) + " {0,1} \r\n");
                    //System.out.println("@attribute PocketCard" + String.valueOf(i) + String.valueOf(j) + " {0,1} \r\n");
                }
            }
            for (int i = 1; i < 6; i++) {
                for(int j = 1; j < 8; j++) {
                    fileOut.write("@attribute BoardCard" + String.valueOf(i) + String.valueOf(j) + " {0,1} \r\n");
                    //System.out.println("@attribute BoardCard" + String.valueOf(i) + String.valueOf(j) + " {0,1} \r\n");
                }
            }
            fileOut.write("@attribute HandClass {0,1,2,3,4,5,6,7,8} \r\n");
            //System.out.println("@attribute HandClass {0,1,2,3,4,5,6,7,8} \r\n");
            fileOut.write("@data \r\n");
            //System.out.println("@data \r\n");

            int[] countArray = {0,0,0,0,0,0,0,0,0};


            for (int i = 0; i < 100000; i++) {
                dealer.shuffle();
                handRound = randNum.nextInt(3) + 2;
                bestHand = EstherTools.getBestHand(dealer.getPocket(0), dealer.getBoard(handRound));
                if(countArray[bestHand.getCombo()] < 250){
                    countArray[bestHand.getCombo()] += 1;
                    pocketCards = dealer.getPocket(0);
                    fileOut.write(cardToBinaryString(pocketCards[0]) + "," + cardToBinaryString(pocketCards[1]) + ",");
                    //System.out.println(cardToBinaryString(pocketCards[0]) + "," + cardToBinaryString(pocketCards[1]) + ",");
                    flopCards = dealer.getFlop();
                    fileOut.write(cardToBinaryString(flopCards[0]) + "," + cardToBinaryString(flopCards[1]) + "," + cardToBinaryString(flopCards[2]) + ",");
                    //System.out.println(cardToBinaryString(flopCards[0]) + "," + cardToBinaryString(flopCards[1]) + "," + cardToBinaryString(flopCards[2]) + ",");
                    if(handRound > 2) {
                        turnCard = dealer.getTurn();
                        fileOut.write(cardToBinaryString(turnCard) + ",");
                    }
                    else{
                        fileOut.write("0,0,0,0,0,0,0,");
                    }
                    if(handRound > 3) {
                        riverCard = dealer.getRiver();
                        fileOut.write(cardToBinaryString(riverCard) + ",");
                        //System.out.println(cardToBinaryString(turnCard) + "," + cardToBinaryString(riverCard) + ",");
                    }
                    else{
                        fileOut.write("0,0,0,0,0,0,0,");
                    }
                    fileOut.write(String.valueOf(bestHand.getCombo()) + "\r\n");
                    //System.out.println(String.valueOf(bestHand.getCombo()) + "\r\n");
                }

            }
            fileOut.close();
        }
        catch(IOException e){
            throw e;
        }


    }

    public String cardToBinaryString(int card){
        int cardRank = card % 13;
        int cardSuit = card / 13;
        String binaryRank;
        String binarySuit;
        switch(cardRank){
            case 0: binaryRank = "0,0,0,1,";
                break;
            case 1: binaryRank = "0,0,1,0,";
                break;
            case 2: binaryRank = "0,0,1,1,";
                break;
            case 3: binaryRank = "0,1,0,0,";
                break;
            case 4: binaryRank = "0,1,0,1,";
                break;
            case 5: binaryRank = "0,1,1,0,";
                break;
            case 6: binaryRank = "0,1,1,1,";
                break;
            case 7: binaryRank = "1,0,0,0,";
                break;
            case 8: binaryRank = "1,0,0,1,";
                break;
            case 9: binaryRank = "1,0,1,0,";
                break;
            case 10: binaryRank = "1,0,1,1,";
                break;
            case 11: binaryRank = "1,1,0,0,";
                break;
            case 12: binaryRank = "1,1,0,1,";
                break;
            default: binaryRank = "0,0,0,0,";
        }
        switch (cardSuit){
            case 0: binarySuit = "0,0,1";
                break;
            case 1: binarySuit = "0,1,0";
                break;
            case 2: binarySuit = "0,1,1";
                break;
            case 3: binarySuit = "1,0,0";
                break;
            default: binarySuit = "0,0,0";
        }
        String binaryCardValue = binaryRank + binarySuit;
        return binaryCardValue;

    }

}


