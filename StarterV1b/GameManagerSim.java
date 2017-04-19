/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

import java.util.ArrayList;

/**
 *
 * @author schafer
 *
 * Things to note: Internally, player numbers are 0 through n-1 Externally they
 * are 1 through n. Careful consideration needs to be given to adding 1 when
 * reporting player numbers The button value is the internal player # who has
 * the button
 *
 */
public class GameManagerSim {

    //parameters needed at construction
    private final Player[] players;
    private final Dealer dealer;
    private final boolean debug;
    private final int[] bets;
    private final int raiseLimit;
    private final int hands;

    //Game level things set up by default (may be based on values of above)
    private int button;
    private int handNumber;
    private int[] bank;

    //Hand level things not set up by constructor but by the hand process
    private int lastToCall;
    private boolean[] stillIn;
    private int pot;
    private int activePlayers;
    private int[] playerStakes;
    private int tableStakes;
    private int round;
    private int raisesLeft;
    private int currentBettor;
    private final ArrayList<String>[] handActions = (ArrayList<String>[])new ArrayList[5];



    /*
     The main constructor for this class requires that ten things be provided
     */

    public GameManagerSim(Player[] players, Dealer dealer, boolean debug,
                          int[] bets, int raiseLimit, int hands, boolean[] whosIn,
                          int[] playerStakesIn, int[] bankIn, TableData data) {
        this.players = players;
        this.dealer = dealer;
        this.debug = debug;

        this.bets = bets;
        this.raiseLimit = raiseLimit;
        this.hands = hands;

        gameLevelSetup(whosIn, playerStakesIn, bankIn, data);
    }

    public int[] playGame(TableData data) {
        System.out.println("Playing Game from GameManagerSim");

        //Loop for each hand
        for (handNumber = 1; handNumber < hands + 1; handNumber++) {
            handLevelSetup(data);

            if (debug) {
                System.out.println("DEBUG is on.");
                System.out.println("Button with player " + (button));
            }

            //Everyone ante
            for (int x = 0; x < bank.length; x++) {
                pot += bets[0];
                bank[x] -= bets[1];
                handActions[0].add((x+1)+",ante");
                if (debug) {
                    System.out.print((x) + " ");
                    System.out.print("" + EstherTools.intCardToStringCard(dealer.getPocket(x)[0]));
                    System.out.println(" " + EstherTools.intCardToStringCard(dealer.getPocket(x)[1]));
                }
            }

            if (debug) {
                int[] b = dealer.getBoard(4);
                System.out.print("" + EstherTools.intCardToStringCard(b[0]));
                System.out.print(" " + EstherTools.intCardToStringCard(b[1]));
                System.out.print(" " + EstherTools.intCardToStringCard(b[2]));
                System.out.print(" " + EstherTools.intCardToStringCard(b[3]));
                System.out.println(" " + EstherTools.intCardToStringCard(b[4]));
            }

            //Play the hand
            while (round < 4 && activePlayers > 1) {
                manageBettingRound(data);
            }

            //Figure out winner and send a reveal message
            determinePotWinner();
            if (debug) {
                printBankTotals();
            }
        }

        return bank;
    }

    //private helper methods
    private void gameLevelSetup(boolean[] whosIn, int[] playerStakesIn, int[] bankIn, TableData data) {
        button = data.getButton() - 1;
        stillIn = whosIn;
        playerStakes = playerStakesIn;
        bank = bankIn;
    }

    private void handLevelSetup(TableData data) {
        pot = data.getTotalPot();
        activePlayers = stillIn.length;
        round = data.getBettingRound() - 1;

        for (int x = 0; x < 5; x++) {
            handActions[x] = new ArrayList<>();
        }

    }

    private void manageBettingRound(TableData data) {
        if (debug) {
            System.out.println("Entering manageBettingRound for round " + data.getBettingRound() + "(GameManagerSim)");
            //System.out.println("Changing round to: " + (round + 1));
        }

        String response;
        round++;
        //raisesLeft = raiseLimit + 1;
        raisesLeft = data.getRaisesLeft();
        int actionsNeeded = activePlayers;
        currentBettor = data.getCurrentBettor();
        tableStakes = data.getTablePot();
        for (int x = 0; x < playerStakes.length; x++) {
            playerStakes[x] = 0;
        }

        while (activePlayers > 1 && actionsNeeded > 0) {
            System.out.println("Action to "+ currentBettor + " (GameManagerSim)");
            if (stillIn[currentBettor]) {
                String valid = "fold,";
                if (raisesLeft > raiseLimit) {
                    valid += "check,bet";
                } else if (raisesLeft == 0) {
                    valid += "call";
                } else {
                    valid += "call,raise";
                }

                TableData td = new TableData(players.length,
                        hands - handNumber,
                        handNumber,
                        currentBettor + 1,
                        button + 1,
                        round,
                        bets,
                        stillIn,
                        bank,
                        pot,
                        tableStakes,
                        playerStakes[currentBettor],
                        raisesLeft,
                        dealer.getPocket(currentBettor),
                        dealer.getBoard(round),
                        handActions,
                        valid,
                        players,
                        dealer,
                        playerStakes,
                        currentBettor
                );

                response = players[currentBettor].getAction(td);
                System.out.println("GameManagerSim got response from " + players[currentBettor].getScreenName()
                    + "(" + currentBettor + ")");
                System.out.println("Response was : " + response + "\n");
                players[currentBettor].getScreenName();

                if (!valid.contains(response)) {
                    System.out.println("ERROR");
                    System.out.println("Player " + (currentBettor + 1) + " force fold.");
                    System.out.println("Gave " + response);
                    System.out.println("But only valid was" + valid);
                    response = "fold";
                }

                switch (response) {
                    case "fold":
                        activePlayers--;
                        actionsNeeded--;
                        stillIn[currentBettor] = false;
                        break;
                    case "check":
                        actionsNeeded--;
                        lastToCall = currentBettor;
                        break;
                    case "call":
                        actionsNeeded--;
                        lastToCall = currentBettor;
                        adjustStakes(currentBettor, false);
                        break;
                    case "bet":
                        raisesLeft--;
                        actionsNeeded = activePlayers - 1;
                        adjustStakes(currentBettor, true);
                        break;
                    case "raise":
                        raisesLeft--;
                        actionsNeeded = activePlayers - 1;
                        adjustStakes(currentBettor, true);
                        break;
                    default:
                        System.out.println("ERROR: THIS IS SUPPOSED TO BE IMPOSSIBLE"
                                + response);
                        response = "fold";
                        activePlayers--;
                        stillIn[currentBettor] = false;
                        break;
                }
            } else {
                response = "out";
            }

            //handActions[round].add("(" + (currentBettor + 1) + "," + response + ")");
            handActions[round].add("(" + (currentBettor) + "," + response + ")");
            currentBettor = (currentBettor + 1) % players.length;

        }

        //round++;        
    }

    private void determinePotWinner() {
        //Setup data for the Results object to pass along
        int[] payouts = new int[players.length];
        int[][] pocketCards = new int[players.length][];
        BestHand[] outcomes = new BestHand[players.length];

        //analyze each person's hand and set up data for later
        for (int x = 0; x < players.length; x++) {
            //if the player is in then analyze their hand.  Otherwise, give -1
            if (stillIn[x]) {
                outcomes[x] = EstherTools.getBestHand(dealer.getPocket(x),
                        dealer.getBoard(4));
                //Shouldn't be -1 unless I set it to this
                if (outcomes[x].getCombo() == -1) {
                    System.out.println("ERROR: Error interpreting the hand of player "
                            + (x + 1));
                }
                if (debug) {
                    System.out.println("Player " + x + " has "
                            + outcomes[x].getCombo() + " "
                            + outcomes[x].getCards());
                }
            } else {
                outcomes[x] = new BestHand(-1, 0);
            }
            payouts[x] = 0;
            pocketCards[x] = null;
        }

        ArrayList<Integer> winners = new ArrayList<>();

        //If only one active player than mark them as the winner
        // and proceed to payout with no reveals
        if (activePlayers == 1) {
            if (debug) {
                System.out.println("Only one person still in.  Automatic win");
            }
            for (int x = 0; x < players.length; x++) {
                if (outcomes[x].getCombo() != -1) {
                    winners.add(new Integer(x));
                }
            }
            if (winners.size() != 1) {
                System.out.println("ERROR: Only supposed to be one person in");
                System.out.println("But we had a count of " + winners.size());
            }
        } else {
            //If there are multiple players in the game we must go through reveals

            //First, the mustShowFirst is the first player in to the
            //left of the lastToCall
            int remaining = players.length - 1;
            int mustShowFirst = (lastToCall + 1) % players.length;
            while (!stillIn[mustShowFirst]) {
                remaining--;
                mustShowFirst = (mustShowFirst + 1) % players.length;
            }

            //mustShowFirst MUST reveal and temporarily has the best hand
            pocketCards[mustShowFirst] = dealer.getPocket(mustShowFirst);
            winners.add(new Integer(mustShowFirst));

            //For each additional player...
            for (int x = 1; x <= remaining; x++) {
                int nextPlayer = (mustShowFirst + x) % players.length;

                //If the player has a better or hand they must reveal
                int compareVal = outcomes[nextPlayer].compareTo(
                        outcomes[winners.get(0)]);
                if (compareVal > 0) {
                    pocketCards[nextPlayer] = dealer.getPocket(nextPlayer);
                    //If better than reset besthands to this new betterhand
                    if (compareVal == 1) {
                        winners = new ArrayList<>();
                    }
                    winners.add(new Integer(nextPlayer));
                }
            }
        }

        //Now it's time to make the winner announcement and distribute the pot
        if (debug) {
            if (winners.size() == 1) {
                System.out.println("Several in.  One winner");
            } else {
                System.out.println("Several in.  several winners");
            }
        }
        int baseSplit = pot / winners.size();
        for (int x = 0; x < winners.size(); x++) {
            payouts[winners.get(x)] = baseSplit;
        }

        //and if this doesn't divide evenly than you need to assign the
        //leftovers to the "first" winner(s) after the button
        int leftovers = pot - baseSplit * winners.size();
        if (winners.size() > leftovers) {
            for (int z = 0; z < leftovers; z++) {
                payouts[winners.get(z)]++;
            }
        } else {
            System.out.println("ERROR: The leftovers exceed the winner list");
        }

        int sum = 0;
        for (int x = 0; x < players.length; x++) {
            bank[x] += payouts[x];
            sum += payouts[x];
        }
        if (sum != pot) {
            System.out.println("ERROR: Payout not match pot");
        }

        for (Player player : players) {
            Results r = new Results(payouts, pocketCards, outcomes, winners);
            player.handResults(r);
        }
    }

    private int zeroOutBesthand(int[] besthand, int index) {
        for (int x = 0; x < besthand.length; x++) {
            besthand[x] = -1;
        }
        besthand[0] = index;
        return 1;
    }

    private void printBankTotals() {
        int sum = 0;
        for (int x = 0; x < bank.length; x++) {
            if (debug) {
                System.out.println(x + " has  $" + bank[x]);
            }
            sum += bank[x];
        }
        if (sum != 0) {
            System.out.println("ERROR: Sum doesn't add up");
            System.exit(0);
        }
    }

    private void adjustStakes(int currentBettor, boolean increase) {
        if (increase) {
            tableStakes++;
        }
        int amount = (tableStakes - playerStakes[currentBettor]) * bets[round];
        bank[currentBettor] -= amount;
        pot += amount;
        playerStakes[currentBettor] = tableStakes;
    }
}
