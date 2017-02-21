/**
 * Created by Russell on 2/18/2017.
 */
public class InputData {

    private int[] inputList;
    private int havePair, haveHighPair, haveTwoPair, haveThreeOfAKind;
    private int haveStraight, haveHighStrainght, haveFlush, haveAceInFlushSuit;
    private int boardTwoCardsFromFlush, haveFullHouse, haveStraightFlush;
    private int singleCardFromTwoPair, twoCardsFromStraightFlush;
    private int singleCardFrom3oak, twoCardsFrom3oak, singleCardFromStraight;
    private int twoCardsFromStraight, singleCardFromFlush, twoCardsFromFlush;
    private int singleCardFrom4oak, twoCardsFrom4oak, singleCardFromStraightFlush;
    private int boardHasPair, boardHas3oak, boardSingleCardFromFlush;
    private int boardOneCardFromStraight, boardTwoCardsFromStraight;
    private int boardOneCardFromStraightFlush, boardTwoCardsFromStraightFlush;

    public InputData(TableData data){

    }

    public int[] getInputList(){
        return inputList;
    }

}
