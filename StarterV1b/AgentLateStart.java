import java.util.Random;

/**
 * Created by Nick Drafahl on 2/13/2017.
 */
public class AgentLateStart extends Player {
    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};

    public AgentLateStart (int num) {
        this.num = num;
    }


    @Override
    public String getScreenName() {
        return "Raise" + this.num;
    }

    @Override
    public String getAction(TableData data) {

        // Display what this agent's current hand is to console for testing.
        System.out.println("LateStart's pocket is :");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        // Print to console to verify that we're starting a "new" game.
        System.out.println("Starting a new game...");

        // Create a new dealer, we may not want to do this and instead use a copy of the current dealer.
        Dealer dealer = data.getDealer();

        // Get the array of Players from the tabledata.
        Player[] simPlayers = data.getPlayers();

        // Overwrite the "AgentLateStart" agent with a random player agent.  Later we need to change this to be the MCTS agent.
        // If we don't do this, we'll just recursively recreate a game each time til we run out of memory.
        simPlayers[this.num] = new AgentRandomPlayer(this.num);

        //GameManager g = new GameManager(simPlayers, dealer, false);  // Default constructor, left here for documentation.  Unlikely to be used.
        //GameManager g = new GameManager(simPlayers, dealer, false, limits, 3, 1 * simPlayers.length); //Uncomment this to run with standard GameManager

        // Play the Simulation of the game.
        GameManagerSim g = new GameManagerSim(simPlayers, dealer, false, limits, 3, 1);

        // This needs to be modified so we start from a specific point in time, rather than the first players hand that was already dealt
        int[] end = g.playGame(data.getHandsPlayed());

        // Game is over, return the totals from the round.  Likely base our fitness function from this.
        System.out.println("Final Totals");
        for (int x = 0; x < end.length; x++) {
            System.out.println((x + 1) + " "
                    + data.getPlayers()[x].getScreenName() + " had " + end[x]);
        }

        // Confirmation the game ended.
        System.out.println("Finished \"new game\"");

        // Code pulled from AgentRandomPlayer.  Return a random action so we can get on to the next round.  This will be redone to be based on whatever the
        // MCTS Agent decides returns.
        String pull = data.getValidActions();
        String[] choices = pull.split(",");
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(choices.length);
        return choices[index];
    }
}
