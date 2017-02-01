/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

/**
 *
 * @author schafer
 *
 * This toolkit contains some functionality that is useful in ESTHER itself but
 * might also be useful to developers of Intelligent Players. They are placed
 * here for access by all. Agents may freely use these methods and expect this
 * Toolkit to be available touring program execution.
 *
 * The functions in this toolkit encompass the following functionality: card
 * representation conversion functions that check for certain poker hands
 * functions that determine the best hand possible from a set of cards
 *
 *
 * ESTHER represents cards as integers from 0-51. Cards 0-12 are clubs Cards
 * 13-25 are diamonds Cards 26-38 are hearts Cards 39-51 are spades
 *
 * Within each set the cards are ordered from 2 up through Ace
 *
 * This toolkit includes a method to convert from this to a length two string in
 * the format:
 * <rank><suit>
 * where rank = A, K, Q, J, T, 9, 8, ... 2 and suit = C, D, H, S
 *
 * Thus, AC is the "Ace of clubs" (card 12), TD is the "ten of diamonds" (card
 * 21), 2H is the "two of hearts" (card 26)
 *
 * This two character string is not actually used by ESTHER but is helpful when
 * debugging code.
 *
 * This also contains a method to go from the two character string back to the
 * integer
 */
public class EstherTools {

    //Used to return an error state
    public static BestHand invalid = new BestHand(-1, 0);

    /**
     * Takes in an integer representation of a card and returns a length 2
     * String in the format <rank><suit>
     *
     * Rank is 2,3,4,5,6,7,8,9,T,J,Q,K,A Suit is C,D,H,S
     *
     * @param card
     * @return string
     */
    public static String intCardToStringCard(int card) {
        int rank = card % 13;
        int suit = card / 13;

        String translation;

        if (rank == 12) {
            translation = "A";
        } else if (rank == 11) {
            translation = "K";
        } else if (rank == 10) {
            translation = "Q";
        } else if (rank == 9) {
            translation = "J";
        } else if (rank == 8) {
            translation = "T";
        } else {
            translation = "" + (char) (rank + 50);
        }

        if (suit == 0) {
            translation += "C";
        } else if (suit == 1) {
            translation += "D";
        } else if (suit == 2) {
            translation += "H";
        } else {
            translation += "S";
        }
        return translation;
    }

    /**
     * Takes in a length 2 string in the <rank><suit> format and converts it
     * back to it's integer card value.
     *
     * @param card
     * @return int
     */
    public static int stringCardToIntCard(String card) {
        char rank = card.charAt(0);
        char suit = card.charAt(1);

        int translation;

        if (rank == 'A') {
            translation = 12;
        } else if (rank == 'K') {
            translation = 11;
        } else if (rank == 'Q') {
            translation = 10;
        } else if (rank == 'J') {
            translation = 9;
        } else if (rank == 'T') {
            translation = 8;
        } else if ('2' <= rank && rank <= '9') {
            translation = (int) (rank) - 50;
        } else {
            System.out.println("Rank char does not translate");
            return -1;
        }

        if (suit == 'D') {
            translation += 13;
        } else if (suit == 'H') {
            translation += 26;
        } else if (suit == 'S') {
            translation += 39;
        } else if (suit != 'C') {
            System.out.println("Rank char does not translate");
            return -1;
        }

        return translation;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsStraightFlush(int[] pocket, int[] board) {
        return containsStraightFlush(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsStraightFlush(int[] allCards) {
        BestHand results = getStraightFlush(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsFlush(int[] pocket, int[] board) {
        return containsFlush(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsFlush(int[] allCards) {
        BestHand results = getFlush(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsFourOfAKind(int[] pocket, int[] board) {
        return containsFourOfAKind(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsFourOfAKind(int[] allCards) {
        BestHand results = getFourOfAKind(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsFullHouse(int[] pocket, int[] board) {
        return containsFullHouse(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsFullHouse(int[] allCards) {
        BestHand results = getFullHouse(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsStraight(int[] pocket, int[] board) {
        return containsStraight(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsStraight(int[] allCards) {
        BestHand results = getStraight(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsThreeOfAKind(int[] pocket, int[] board) {
        return containsThreeOfAKind(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsThreeOfAKind(int[] allCards) {
        BestHand results = getThreeOfAKind(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsTwoPair(int[] pocket, int[] board) {
        return containsTwoPair(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsTwoPair(int[] allCards) {
        BestHand results = getTwoPair(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsOnePair(int[] pocket, int[] board) {
        return containsOnePair(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsOnePair(int[] allCards) {
        BestHand results = getOnePair(allCards);
        return results.getCombo() >= 0;
    }

    /**
     * Does what it says. Takes in the pocket and the board and will return true
     * false based on whether those cards contain the named hand type.
     *
     * @param pocket
     * @param board
     * @return boolean
     */
    public static boolean containsNoPair(int[] pocket, int[] board) {
        return containsNoPair(merge(pocket, board));
    }

    /**
     * Does what it says. Takes in a single listing of cards and will return
     * true false based on whether those cards contain the named hand type.
     *
     * @param allCards
     * @return boolean
     */
    public static boolean containsNoPair(int[] allCards) {
        BestHand results = getNoPair(allCards);
        return results.getCombo() >= 0;
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getStraightFlush(int[] pocket, int[] board) {
        return getStraightFlush(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getStraightFlush(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);

        //Begin by considering five consecutive cards
        //  that are all of the same suit
        for (int x = 0; x < allCards.length - 4; x++) {
            if ((allCards[x] == allCards[x + 4] + 4)
                    && (allCards[x] / 13 == allCards[x + 4] / 13)) {
                int cards = 0;
                for (int y = x; y < x + 5; y++) {
                    cards = cards * 100;
                    cards = cards + allCards[y] % 13;
                }
                return new BestHand(8, cards);
            }
        }
        //then look for the special case 5-4-3-2-A
        for (int x = 0; x < allCards.length - 4; x++) {
            if (allCards[x] % 13 == 12) //if you have an ace
            {
                //and "after" that you can find a 5-4-3-2
                boolean[] fourcards = new boolean[4];
                for (int y = 0; y < 4; y++) {
                    fourcards[y] = false;
                }
                // of the same suit
                int suit = allCards[x] / 13;

                for (int y = x + 1; y < allCards.length; y++) {
                    int localrank = allCards[y] % 13;
                    if (localrank < fourcards.length && allCards[y] / 13 == suit) {
                        fourcards[localrank] = true;
                    }
                }
                if (fourcards[0] && fourcards[1] && fourcards[2] && fourcards[3]) //No need to build because we know the sequence
                {
                    return new BestHand(8, 302010012);
                }
            }
        }
        return invalid;
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getFlush(int[] pocket, int[] board) {
        return getFlush(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getFlush(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        for (int x = 0; x < allCards.length - 4; x++) {
            if (allCards[x] / 13 == allCards[x + 4] / 13) {
                int cards = 0;
                for (int y = x; y < x + 5; y++) {
                    cards = cards * 100;
                    cards = cards + allCards[y] % 13;
                }
                return new BestHand(5, cards);
            }
        }
        return invalid;
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getFourOfAKind(int[] pocket, int[] board) {
        return getFourOfAKind(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getFourOfAKind(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);

        int mainslot = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] == 4) {
                mainslot = x;
                break;
            }
        }
        if (mainslot == -1) {
            return invalid;
        }
        int cards = 0;
        for (int y = 0; y < 4; y++) {
            cards = cards * 100;
            cards = cards + mainslot;
        }

        int kicker = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] > 0 && x != mainslot) {
                kicker = x;
                break;
            }
        }
        if (kicker == -1) {
            //error state.  Shouldn't hit here
            System.out.println("ERROR when detecting 4 of a kind");
            return invalid;
        }
        cards = cards * 100 + kicker;
        return new BestHand(7, cards);
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getFullHouse(int[] pocket, int[] board) {
        return getFullHouse(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getFullHouse(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);

        int mainslot = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] == 3) {
                mainslot = x;
                break;
            }
        }
        if (mainslot == -1) {
            return invalid;
        }
        int kicker = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] >= 2 && x != mainslot) {
                if (rankCounts[x] == 4) {
                    //error state.  Shouldn't hit here
                    System.out.println("ERROR -found four inside of FullHouse");
                    return invalid;
                }
                kicker = x;
                break;
            }
        }
        if (kicker != -1) {
            int cards = 0;
            for (int y = 0; y < 3; y++) {
                cards = cards * 100;
                cards = cards + mainslot;
            }
            for (int y = 0; y < 2; y++) {
                cards = cards * 100;
                cards = cards + kicker;
            }
            return new BestHand(6, cards);
        }
        return invalid;
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getStraight(int[] pocket, int[] board) {
        return getStraight(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getStraight(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);

        int startSlot = -1;
        int consecutive = 0;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] >= 1) {
                //System.out.println("st check in "+x);
                if (consecutive == 0) {
                    startSlot = x;
                }
                consecutive++;
                if (consecutive == 5) {
                    break;
                }
            } else {
                consecutive = 0;
            }
        }
        if (consecutive == 5) {
            int cards = 0;
            for (int x = 0; x < 5; x++) {
                cards *= 100;
                cards += (startSlot - x);
            }
            return new BestHand(4, cards);
        }
        //Look for special 5-4-3-2-A
        if (rankCounts[3] >= 1 && rankCounts[2] >= 1 && rankCounts[1] >= 1
                && rankCounts[0] >= 1 && rankCounts[12] >= 1) {
            return new BestHand(4, 302010012);
        }
        return invalid;
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getThreeOfAKind(int[] pocket, int[] board) {
        return getThreeOfAKind(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getThreeOfAKind(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);

        int mainslot = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] == 3) {
                mainslot = x;
                break;
            }
        }
        if (mainslot == -1) {
            return invalid;
        }
        int kicker1 = -1;
        int kicker2 = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] >= 1 && x != mainslot) {
                if (rankCounts[x] >= 2) {
                    //error state.  Shouldn't hit here
                    System.out.println("ERROR - 3 of a kind found a fullhouse");
                    return invalid;
                }
                if (kicker1 == -1) {
                    kicker1 = x;
                } else {
                    kicker2 = x;
                    break;
                }
            }
        }
        int cards = 0;
        for (int y = 0; y < 3; y++) {
            cards = cards * 100;
            cards = cards + mainslot;
        }
        cards = cards * 100;
        cards = cards + kicker1;
        cards = cards * 100;
        cards = cards + kicker2;

        return new BestHand(3, cards);
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getTwoPair(int[] pocket, int[] board) {
        return getTwoPair(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getTwoPair(int[] allCards) {

        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);

        int pair1 = -1;
        int pair2 = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] > 2) {
                //error state.  Shouldn't hit here
                System.out.println("ERROR Found more than two of one card in two pair");
                return invalid;
            }
            if (rankCounts[x] == 2) {

                if (pair1 == -1) {
                    pair1 = x;
                } else {
                    pair2 = x;
                    break;
                }
            }
        }
        if (pair2 == -1) {
            return invalid;
        }
        int kicker = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] >= 1 && x != pair1 && x != pair2) {
                kicker = x;
                break;
            }
        }
        int cards = 0;
        for (int y = 0; y < 2; y++) {
            cards = cards * 100;
            cards = cards + pair1;
        }
        for (int y = 0; y < 2; y++) {
            cards = cards * 100;
            cards = cards + pair2;
        }
        cards = cards * 100;
        cards = cards + kicker;
        return new BestHand(2, cards);
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getOnePair(int[] pocket, int[] board) {
        return getOnePair(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getOnePair(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);
        int pair = -1;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] == 2) {
                pair = x;
                break;
            }
        }
        if (pair == -1) {
            return invalid;
        }
        int kickerCount = 0;
        int cards = 0;
        for (int y = 0; y < 2; y++) {
            cards = cards * 100;
            cards = cards + pair;
        }
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] >= 1 && x != pair) {
                cards = cards * 100;
                cards = cards + x;
                kickerCount++;
                if (kickerCount == 3) {
                    break;
                }
            }
        }
        if (kickerCount == 3) {
            return new BestHand(1, cards);
        } else {
            System.out.println("Error in processing one pair.  Couldn't find kickers");
            return invalid;
        }
    }

    /**
     *
     * Takes in a pocket and a board listing of cards and returns a BestHand
     * instance representing the BestHand available with that hand type. Returns
     * a BestHand with a score of -1 if the hand isn't possible.
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getNoPair(int[] pocket, int[] board) {
        return getNoPair(merge(pocket, board));
    }

    /**
     * Takes in a single listing of cards and returns a BestHand instance
     * representing the BestHand available with that hand type. Returns a
     * BestHand with a score of -1 if the hand isn't possible.
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getNoPair(int[] allCards) {

        if (allCards.length < 5) {
            return invalid;
        }

        sort(allCards);
        int[] rankCounts = getRankCounts(allCards);

        //this is mostly here to detect that there are errors elsewhere
        //the dectection of a 2 count is a problem
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] >= 2) {
                System.out.println("ERROR - found 2 or more in no-pair");
                return invalid;
            }
        }

        int cards = 0;
        int cardCount = 0;
        for (int x = rankCounts.length - 1; x >= 0; x--) {
            if (rankCounts[x] == 1) {
                cards *= 100;
                cards += x;
                cardCount++;
                if (cardCount == 5) {
                    break;
                }
            }
        }
        if (cardCount == 5) {
            return new BestHand(0, cards);
        } else {
            System.out.println("ERROR - issues with final kickers in no-pair");
            return invalid;
        }
    }

    /**
     * Used to merge two lists of ints into a single list
     *
     * @param pocket
     * @param board
     * @return
     */
    public static int[] merge(int[] pocket, int[] board) {
        int[] allCards = new int[pocket.length + board.length];
        int slot = 0;

        for (int x = 0; x < pocket.length; x++) {
            allCards[slot] = pocket[x];
            slot += 1;
        }
        for (int x = 0; x < board.length; x++) {
            allCards[slot] = board[x];
            slot += 1;
        }
        return allCards;
    }

    /**
     * Sorts a single list of int cards into reverse order (largest first)
     *
     * @param cards
     */
    public static void sort(int[] cards) {
        for (int place = 0; place < cards.length - 1; place++) {
            int maxPlace = place;
            for (int index = place; index < cards.length; index++) {
                if (cards[maxPlace] < cards[index]) {
                    maxPlace = index;
                }
            }
            int temp = cards[maxPlace];
            cards[maxPlace] = cards[place];
            cards[place] = temp;
        }
    }

    /**
     * Takes in a list of cards in a hand and returns a length 13 list of how
     * many of each rank are in the hand Index 0=2, Index 1 = 3 .... Index
     * 12=Ace
     *
     * @param allCards
     * @return
     */
    public static int[] getRankCounts(int[] allCards) {
        int[] rankCounts = new int[13];
        for (int x = 0; x < rankCounts.length; x++) {
            rankCounts[x] = 0;
        }
        for (int x = 0; x < allCards.length; x++) {
            rankCounts[allCards[x] % 13]++;
        }
        return rankCounts;
    }

    /**
     * Takes in a set of cards and returns the best hand that can be made from
     * the set
     *
     * @param pocket
     * @param board
     * @return BestHand
     */
    public static BestHand getBestHand(int[] pocket, int[] board) {
        return getBestHand(merge(pocket, board));
    }

    /**
     * Takes in a set of cards and returns the best hand that can be made from
     * the set
     *
     * @param allCards
     * @return BestHand
     */
    public static BestHand getBestHand(int[] allCards) {
        if (allCards.length < 5) {
            return invalid;
        }
        sort(allCards);

        BestHand results = getStraightFlush(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getFourOfAKind(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getFullHouse(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getFlush(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getStraight(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getThreeOfAKind(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getTwoPair(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getOnePair(allCards);
        if (results.getCombo() >= 0) {
            return results;
        }
        results = getNoPair(allCards);
        if (results.getCombo() >= 0) {
            return results;
        } else {
            System.out.println("Found a situation where NO combo hit (INVALID!!!)");
            for (int x = 0; x < allCards.length; x++) {
                System.out.println(allCards[x]);
            }
            return invalid;
        }
    }

    //Not really intended for general use. 
    //Was used in the debugging stages and is left here for future help
    public static BestHand getBestHand(String cardRep) {
        int boardsize = (cardRep.length()+1)/3;
        int[] pocket = new int[2];
        int[] board = new int[boardsize-2];

        for (int x = 0; x < pocket.length; x++) {
            int start = (x * 3);
            pocket[x] = stringCardToIntCard(cardRep.substring(start, start + 2));
        }
        for (int x = 0; x < board.length; x++) {
            int start = (pocket.length + x) * 3;
            board[x] = stringCardToIntCard(cardRep.substring(start, start + 2));
        }
        //System.out.println("orig="+cardRep);
        //System.out.println("trans="+pocket[0]+" "+pocket[1]+" "+board[0]+" "+board[1]+" "+board[2]+" "+board[3]+" "+board[4]);
        return getBestHand(pocket, board);
    }
}
