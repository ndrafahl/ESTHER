/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */


/**
 *
 * @author schafer
 */
public class BestHand {
    /*
	The combo in a best hand is an int code equating to:
	8=striaght flush
	7=four of a kind
	6=full house
	5=flush
	4=straight
	3=three of a kind
	2=two pair
	1=one pair
	0=no pair
	-1 error state (Most often obtained when fewer cards were in play)
    */
      private final int combo;
      private static final String[] comboType = {"no pair",
          "one pair",
          "two pair",
          "three of a kind",
          "straight",
          "flush",
          "full house",
          "four of a kind",
          "straight flush"};
              
      
      
      /*
      The cards in a best hand is an int which represents the five cards
	ranked from "first" to "last" when considering a five card rank
	These are in two digit pairs representing
	12=High Ace
	11=King
	10=Queen
	09=Jack
	08=10
	07=9
	...
        01=3
	00=2 
        00=low ace (since this is limited to straight and straight flush 
                    this double assignment shouldn't be an issue for detectibility)
      */
      private final int cards; 
      private static final String[] faces = {" T"," J"," Q"," K"," A"};
   
      
      public BestHand(int co,int ca)
      {
         
         combo=co;
         cards=ca;
      }
      
   
       public int getCombo()
      {
         return combo;
      }
   
       public String getComboString()
       {
           return comboType[combo];
       }
       public int getCards()
      {
         return cards;
      }
   
       public int compareTo(BestHand other)
      {
         if (this.combo>other.combo)
            return 1;
         if (this.combo<other.combo)
            return -1;
         if (this.cards>other.cards)
            return 1;
         if (this.cards<other.cards)
            return -1;
         return 0;
      }
   
      @Override
       public String toString()
      {
         String cardString="";
         if (cards==0302010000)
         {
            cardString=" 5 4 3 2 A";
         }
         else
         {
            int local=cards;
            for (int x=0;x<5;x++)
            {
               int card=local%100;
               local=local/100;
               card=card+2;
               if (card<10)
               {
                  cardString = " "+card+cardString;
               }
               else
               {
                  cardString = faces[card%10]+cardString;
               }
            }
         }
         return getComboString()+","+cardString;
      }
}
