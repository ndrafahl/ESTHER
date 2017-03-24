import java.util.Arrays;
import java.util.Random;

/**
 * Created by Nick Drafahl on 2/13/2017.
 */
public class AgentLateStart extends Player {
    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};
    private TreeNode root;

    public AgentLateStart (int num) {
        this.num = num;
    }

    @Override
    public String getScreenName() {
        return "LateStart" + this.num;
    }

    @Override
    public String getAction(TableData data) {

        // Display what this agent's current hand is to console for testing.
        System.out.println("LateStart's pocket is :");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        // Print to console to verify that we're starting a "new" game.
        System.out.println("Starting a new game from round " + data.getBettingRound());

        // Create a new dealer, we may not want to do this and instead use a copy of the current dealer.
        Dealer dealer = data.getDealer();

        // We need to deep copy the arrays from TableData so that we do not affect it.
        Player[] simPlayers = Arrays.copyOf(data.getPlayers(), data.getPlayers().length);
        boolean[] simWhosIn = Arrays.copyOf(data.getWhosIn(), data.getWhosIn().length);
        int[] simPlayerStakes = Arrays.copyOf(data.getPlayerStakes(), data.getPlayerStakes().length);
        int[] simBank = Arrays.copyOf(data.getCashBalances(), data.getCashBalances().length);

        // Overwrite the "AgentLateStart" agent with a random player agent.  Later we need to change this to be the MCTS agent.
        // If we don't do this, we'll just recursively recreate a game each time til we run out of memory.
        //simPlayers[this.num] = new AgentRandomPlayer(this.num);

        // Play the Simulation of the game.
        GameManagerSim g = new GameManagerSim(simPlayers, dealer, true, limits, 3, 1,
                simWhosIn, simPlayerStakes, simBank, data);

        // Play the simulated game, only one "hand"
        int[] end = g.playGame(data);

        // Game is over, return the totals from the round.  Likely base our fitness function from this.
        System.out.println("Final Totals");
        for (int x = 0; x < end.length; x++) {
            System.out.println((x + 1) + " "
                    + data.getPlayers()[x].getScreenName() + " had " + end[x]);
        }

        // Confirmation the game ended.
        System.out.println("Finished \"new game\"");

        // Code pulled from AgentRandomPlayer.  Return a random action so we can get on to the next round.  This will be updated to be based on whatever the
        // MCTS Agent decides to return based on the Algorithm.
        String pull = data.getValidActions();
        String[] choices = pull.split(",");
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(choices.length);
        return choices[index];
    }
}
