import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;

/**
 * Created by Nick Drafahl on 2/13/2017.
 */
public class AgentLateStart extends Player {
    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};
    private TreeNode root;
    private TreeNode currentNode;
    private boolean simulate;
    private List<TreeNode> queue;

    // Local Variables Created from TableData
    private int[] tempBoard;
    private int[] tempPocket; 
    private Dealer dealer;
    private Player[] simPlayers;
    private boolean[] simWhosIn;
    private int[] simPlayerStakes;
    private int[] simBank;

    public AgentLateStart (int num) {
        this.num = num;
	    root = new TreeNode("root");
        currentNode = root;
        simulate = true;
        this.queue = new LinkedList<TreeNode>();
    }

    @Override
    public String getScreenName() {
        return "LateStart" + this.num;
    }

    @Override
    public String getAction(TableData data) {
        generateLocalData(data);

        // Display what this agent's current hand is to console for testing.
        System.out.println("LateStart's pocket is :");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        System.out.print("LateStart's pocket in Int is: ");
        System.out.println(data.getPocket()[0] + " " + data.getPocket()[1]);

        System.out.print("LateStart's sorted pocket in Int is: ");
        System.out.println(tempPocket[0] + " " + tempPocket[1]);

        // Print to console to verify that we're starting a "new" game.
        //System.out.println("Starting a new game from round " + data.getBettingRound());
     
    	//TreeNode tempNode = new TreeNode(data.getPocket(), data.getBoard());
        TreeNode tempNode = root.findChild(tempPocket);

        if(tempNode == null) {
            System.out.println("No node found under root.");
            tempNode = new TreeNode(tempPocket, tempBoard);
            root.addChild(tempNode);
        } else {
            System.out.println("Node found under root!:");
        }
    	//root.addChild(tempNode);
        System.out.println("roots children count: " + root.getNumOfChildren());

        // Overwrite the "AgentLateStart" agent with a random player agent.  Later we need to change this to be the MCTS agent.
        // If we don't do this, we'll just recursively recreate a game each time til we run out of memory.
        //simPlayers[this.num] = new AgentRandomPlayer(this.num);
        
        if(simulate) {
            simulate = false;
            // Play the Simulation of the game.
            GameManagerSim g = new GameManagerSim(simPlayers, dealer, true, limits, 3, 1,
                    simWhosIn, simPlayerStakes, simBank, data);

            // Play the simulated game, only one "hand"
            int[] end = g.playGame(data);
            simulate = true;
            

            // Game is over, return the totals from the round.  Likely base our fitness function from this.
            System.out.println("Final Totals");
            for (int x = 0; x < end.length; x++) {
                System.out.println((x + 1) + " "
                        + data.getPlayers()[x].getScreenName() + " had " + end[x]);
            }

            // Confirmation the game ended.
            System.out.println("Finished \"new game\"");
        } else {
            // Code pulled from AgentRandomPlayer.  Return a random action so we can get on to the next round.  This will be updated to be based on whatever the
            // MCTS Agent decides to return based on the Algorithm.
            String pull = data.getValidActions();
            String[] choices = pull.split(",");
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(choices.length);
            return choices[index];
        }

        String pull = data.getValidActions();
        String [] choices = pull.split(",");
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(choices.length);
        return choices[index];
    }

    private void generateLocalData(TableData data) {
        // Create tempBoard and tempPocket data from the board and pocket in TableData.
        // Sorting to more easily search/create nodes in the Tree.
        tempPocket = Arrays.copyOf(data.getPocket(), data.getPocket().length);        
        tempBoard = Arrays.copyOf(data.getBoard(), data.getBoard().length);

        Arrays.sort(tempPocket);
        Arrays.sort(tempBoard);

        dealer = data.getDealer();

        simPlayers = Arrays.copyOf(data.getPlayers(), data.getPlayers().length);
        simWhosIn = Arrays.copyOf(data.getWhosIn(), data.getWhosIn().length);
        simPlayerStakes = Arrays.copyOf(data.getPlayerStakes(), data.getPlayerStakes().length);
        simBank = Arrays.copyOf(data.getCashBalances(), data.getCashBalances().length);

    }
}
