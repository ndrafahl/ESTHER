import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.*;
import static java.lang.Math.*;

/**
 * Created by Nick Drafahl on 2/13/2017.
 */
public class AgentLateStart extends Player {
    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};

    // Local TreeNodes
    private LinkedList<TreeNode> queue;
    private TreeNode root;
    private TreeNode currentNode;
    private TreeNode simNode;
    private TreeNode lastSimNode;

    // Local Variables Created from TableData
    private int[] tempBoard;
    private int[] tempPocket; 
    private Dealer dealer;
    private Player[] simPlayers;
    private boolean[] simWhosIn;
    private int[] simPlayerStakes;
    private int[] simBank;

    private boolean simulate;
    private int lastBoardSize;

    private LinkedList<String> actionQueue;


    public AgentLateStart (int num) {
        this.num = num;
	    root = new TreeNode("root");
        currentNode = root;
        simulate = true;
        this.queue = new LinkedList<TreeNode>();
        this.actionQueue = new LinkedList<String>();
        //this.readTree();
    }

    @Override
    public String getScreenName() {
        return "LateStart" + this.num;
    }

    @Override
    public String getAction(TableData data) {
        //TODO:  Need to retain the currentNode if we are being called from the same round in the game, rather than switching tempNode into currentNode
        System.out.println("Entering getAction for AgentLateStart");
        generateLocalData(data);

        /*if(data.getBoard().length == 0) {
            System.out.println("Searching root");
            currentNode = root;
            //TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot());
        } else {
            System.out.println("Searching not root");
            if(queue.size() == 0) {
                currentNode = lastSimNode;
            } else {
                currentNode = queue.getLast();
            }
            //currentNode = queue.getLast();
            //TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot());
        }

        TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot()); 

        if(tempNode == null) {
            System.out.println("No node found from node at depth " + currentNode.getDepth() + ", creating new node.");
            tempNode = new TreeNode(tempPocket, tempBoard);
            //root.addChild(tempNode);
            currentNode.addChild(tempNode);
            System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
        } else {
            System.out.println("Node found under root!");
            System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
        } */

        // simulate should only be true if GameManager is calling getAction(), otherwise GameManagerSim is calling getAction()
        /*if(simulate) {
            System.out.println("if(simulate) putting in currentNode");
            queue.add(currentNode);
            System.out.println("if(simulate) putting in null action");
            actionQueue.add("null");
            System.out.println("Successfully emplaced null action");      
            System.out.println("Emplacing into queue with depth of: " + currentNode.getDepth());
            lastBoardSize = data.getBoard().length;
        }*/

        //printHand(data);

        //System.out.println("Board size is currently: " + data.getBoard().length);

        // Print to console to verify that we're starting a "new" game.
        //System.out.println("Starting a new game from round " + data.getBettingRound());

        //currentNode = tempNode;

        // Overwrite the "AgentLateStart" agent with a random player agent.  Later we need to change this to be the MCTS agent.
        // If we don't do this, we'll just recursively recreate a game each time til we run out of memory.
        //simPlayers[this.num] = new AgentRandomPlayer(this.num);
        
        if(simulate) {
            simulate = false;
            // Play the Simulation of the game.
            //queue.add(currentNode); 
   
            System.out.println("Calling GameManagerSim with a boardsize of: " + data.getBoard().length);

            GameManagerSim g = new GameManagerSim(simPlayers, dealer, false, limits, 3, 1,
                    simWhosIn, simPlayerStakes, simBank, data);

            // Play the simulated game, only one "hand"
            int[] end = g.playGame(data);
            backPropagate();
            simulate = true;
            

            // Game is over, return the totals from the round.  Likely base our fitness function from this.
            System.out.println("Final Totals");
            for (int x = 0; x < end.length; x++) {
                System.out.println((x + 1) + " "
                        + data.getPlayers()[x].getScreenName() + " had " + end[x]);
            }

            // Confirmation the game ended.
            System.out.println("Finished \"new game\" where beginning simulation had an initial board size of: " + lastBoardSize);
            //writeTree();
        } else {
            // Code pulled from AgentRandomPlayer.  Return a random action so we can get on to the next round.  This will be updated to be based on whatever the
            // MCTS Agent decides to return based on the Algorithm.
            
            System.out.println("Entering else statement with a boardsize of: " + data.getBoard().length);

            if(data.getBoard().length == 0) {
                System.out.println("Searching root");
                currentNode = root;
                //TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot());
            } else {
                System.out.println("Searching not root");
                if(queue.size() == 0) {
                    currentNode = lastSimNode;
                } else {
                    currentNode = queue.getLast();
                }
                //currentNode = queue.getLast();
                //TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot());
            }

            TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot()); 

            if(tempNode == null) {
                System.out.println("No node found from node at depth " + currentNode.getDepth() + ", creating new node.");
                tempNode = new TreeNode(tempPocket, tempBoard);
                //root.addChild(tempNode);
                currentNode.addChild(tempNode);
                System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
            } else {
                System.out.println("Node found under root!");
                System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
            }

            currentNode = tempNode;


            System.out.println("Emplacing into queue (else) with depth of: " + currentNode.getDepth());
            queue.add(currentNode);
            String pull = data.getValidActions();
            String[] choices = pull.split(",");
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(choices.length);

            while(choices[index].equals("fold")) {
                System.out.println("Was going to return fold, picking new index.");
                index = randomGenerator.nextInt(choices.length);
            }

            actionQueue.add(choices[index]);
            return choices[index];
        }

        // This should only occur after the simulation has completed.
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

    private void printHand(TableData data) {
        // Display what this agent's current hand is to console for testing.
        System.out.println("LateStart's pocket is :");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        System.out.print("LateStart's pocket in Int is: ");
        System.out.println(data.getPocket()[0] + " " + data.getPocket()[1]);

        System.out.print("LateStart's sorted pocket in Int is: ");
        System.out.println(tempPocket[0] + " " + tempPocket[1]);
    }

    private void backPropagate() {
        TreeNode backNode;
        String backAction;

        //currentNode = queue.getFirst();    

        lastSimNode = queue.getFirst();

        System.out.println("queue's size is: " + queue.size());
        //System.out.println("actionQueue's size is: " + actionQueue.size());

        System.out.println("\nPrinting nodes visited:");
        while(!queue.isEmpty()) {
            backNode = queue.getLast();
            //backAction = actionQueue.getLast();
            System.out.println("Depth : " + backNode.getDepth());
            //System.out.println("Depth : " + backNode.getDepth() + " Action to get here: " + backAction);
            //actionQueue.removeLast();
            queue.removeLast();
        }

        queue.clear();
        actionQueue.clear();
    }

    public void writeTree(){
        try{
            FileOutputStream fileOut = new FileOutputStream("treenode.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(root);
            out.close();
            fileOut.close();
            System.out.println("Tree has been saved in treenode.ser");
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

    public void readTree(){
        //TreeNode x = null;
        try {
            FileInputStream fileIn = new FileInputStream("treenode.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            root = (TreeNode) in.readObject(); //assigns root as previous root
            in.close();
            fileIn.close();
        }catch(IOException i) {
            i.printStackTrace();
            return;
        }catch(ClassNotFoundException e) {
            System.out.println("TreeNode class not found");
            e.printStackTrace();
            return;
        }finally {
            return;
        }
    }

    // W = num of wins after move
    // N = num of simulations after move
    // C = exploration paramter. sqrt(2) is good first guess. Adjust it to fit
    // T = total num of simulations. N of parent node
    private double mctsAlg(int w, int n, int c, int t){
        return (w/n) + c * (sqrt(log(t) / n));
    }
}
