import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.*;
import static java.lang.Math.*;

/**
 * Created by Nick Drafahl on 2/13/2017.
 */
public class AgentLateStart extends Player {
    private final boolean SERIALIZE = true;

    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};

    // Local TreeNodes
    private LinkedList<TreeNode> queue;
    private TreeNode root;
    private TreeNode currentNode;
    private TreeNode simNode;
    private TreeNode lastSimNode;
    private TreeNode lastNode;
    private TreeNode simStartNode;

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
    private int lastRealBoardSize;

    private int nodeTotal;

    private LinkedList<String> actionQueue;

    private TableData localData;


    public AgentLateStart (int num) {
        this.num = num;

        if (SERIALIZE) {
            System.out.println("Serialize is True, reading in root");
            this.readTree();
            System.out.println("After reading in treenode.ser, nodeTotal is: " + nodeTotal);
            System.out.println("After reading in treenode.ser, root's name is: " + root.getName());
            System.out.println("After reading in treenode.ser, root's number of children is: " + root.getNumOfChildren());
        } else {
            root = new TreeNode("root");
            nodeTotal = 1;
        }

        simulate = true;
        this.queue = new LinkedList<TreeNode>();
        this.actionQueue = new LinkedList<String>();

        lastSimNode = root;
        lastNode = root;
        currentNode = root;
        lastBoardSize = 0;
        lastRealBoardSize = 0;
    }

    @Override
    public String getScreenName() {
        return "LateStart" + this.num;
    }

    @Override
    public String getAction(TableData data) {
        //TODO:  Need to retain the currentNode if we are being called from the same round in the game, rather than switching tempNode into currentNodei

        if (simulate) {
            System.out.println("Entering getAction for AgentLateStart where we are not running a simulation");
        } else {
            System.out.println("Entering getAction for AgentLateStart where we are running a simulation");
        }

        generateLocalData(data);
        
        if(simulate) {
            boolean nodeFound = false;
            int choice;

            String pull = data.getValidActions();
            String[] choices = pull.split(",");
            Arrays.sort(choices);

            TreeNode sTempNode;

            if(data.getBoard().length == 0) {
                sTempNode = root.findChild(tempPocket, tempBoard, root.isRoot());
            } else {
                if(lastRealBoardSize == data.getBoard().length) {
                    sTempNode = lastSimNode;
                } else {
                    sTempNode = lastSimNode.findChild(tempPocket, tempBoard, lastSimNode.isRoot());
                }
            }

            //TreeNode tempNode = lastSimNode.findChild(tempPocket, tempBoard, lastSimNode.isRoot());

            if(sTempNode == null) {
                nodeFound = false;
                sTempNode = new TreeNode(tempPocket, tempBoard);
                lastSimNode.addChild(sTempNode);
                nodeTotal++;
                simulate = false;
                System.out.println("in simulate, added new node to lastSimNode: " + lastSimNode.getDepth() + " " + sTempNode.getDepth());
            } else {
                nodeFound = true;
            }

            currentNode = sTempNode;



            if(nodeFound) {
                choice = makeDecision(currentNode, choices);
                lastSimNode = currentNode;
            } else {
                choice = -1;
            }

            if(choice == -1) {
                simulate = false;

                simStartNode = currentNode;

                int simulationsRan = 0;

                for(long stop = System.nanoTime()+TimeUnit.SECONDS.toNanos(1); stop > System.nanoTime(); ) {

                    generateLocalData( (TableData) data.clone());        

                    System.out.println("Simluations ran so far: " + simulationsRan);
                    
                    /*GameManagerSim g = new GameManagerSim(simPlayers, dealer, true, limits, 3, 1, simWhosIn,
                        simPlayerStakes, simBank, localData);*/

                    GameManagerSim g = new GameManagerSim(data.getPlayers(), dealer, false, limits, 3, 1, simWhosIn,
                        simPlayerStakes, simBank, localData);


                    int[] end = g.playGame(data);

                    simulationsRan++;

                    /*System.out.println("Final Totals (AgentLateStart)");
                    for (int x = 0; x < end.length; x++) {
                    System.out.println((x + 1) + " "
                        + data.getPlayers()[x].getScreenName() + " had " + end[x]);
                    }*/

                    // Confirmation the game ended.
                    System.out.println("Finished \"new game\" where beginning simulation had an initial board size of: " + lastBoardSize);
                    //System.out.println("Beginning back propogation of nodes visited.");

                } //end for(long stop...

                System.out.println("Total simulations ran: " + simulationsRan);

                //simulate = true;

                choice = makeDecision(simStartNode, choices);
                simulate = true;
                lastSimNode = simStartNode;
                lastRealBoardSize = data.getBoard().length;
            } /*else {
                return choices[choice];
            } */

            simulate = true;
            System.out.println("MCTS Agent is returning " + choices[choice] + " to GameManager");
            return choices[choice];

        } else {
            // Code pulled from AgentRandomPlayer.  Return a random action so we can get on to the next round.  This will be updated to be based on whatever the
            // MCTS Agent decides to return based on the Algorithm.
            
            boolean lastNodeUsed = false;     

            if(data.getBoard().length == 0) {
                System.out.println("Searching root");
                currentNode = root;
                //TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot());
            } else {
                System.out.println("Searching not root");
                if(queue.size() == 0) {
                    //currentNode = lastSimNode;
                    currentNode = simStartNode;
                } else {
                    if(lastBoardSize == data.getBoard().length) {
                        lastNodeUsed = true;
                        currentNode = lastNode;                       
                        System.out.println("board length was same as last run, using lastNode: " + currentNode.getDepth());
                    } else {
                        currentNode = queue.getLast();
                        System.out.println("board length not the same as last run, using end of queue: " + currentNode.getDepth());

                    }
                }
                //currentNode = queue.getLast();
                //TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot());
            }

            if(!lastNodeUsed) {      
                TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot()); 

                if(tempNode == null) {
                    System.out.println("No node found from node at depth " + currentNode.getDepth() + ", creating new node.");
                    tempNode = new TreeNode(tempPocket, tempBoard);
                    //root.addChild(tempNode);
                    currentNode.addChild(tempNode);
                    nodeTotal++;
                    System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
                } else {
                    System.out.println("Node found under currentNode!");
                    System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
                }

                currentNode = tempNode;
                lastNode = tempNode;
                lastBoardSize = data.getBoard().length;
            }

            System.out.println("Emplacing into queue (else) with depth of: " + currentNode.getDepth());
            queue.add(currentNode);

            String pull = data.getValidActions();
            String[] choices = pull.split(",");
            Arrays.sort(choices);

            int decisionMade = makeDecision(currentNode, choices);
            
            actionQueue.add(choices[decisionMade]);
            System.out.println("Returning " + choices[decisionMade] + " to GameManagerSim from AgentLateStart");

            System.out.println("nodeTotal = " + this.nodeTotal);
            
            return choices[decisionMade];

        }
    }

    private void generateLocalData(TableData data) {
        // Create tempBoard and tempPocket data from the board and pocket in TableData.
        // Sorting to more easily search/create nodes in the Tree.
        tempPocket = Arrays.copyOf(data.getPocket(), data.getPocket().length);        
        tempBoard = Arrays.copyOf(data.getBoard(), data.getBoard().length);

        Arrays.sort(tempPocket);
        Arrays.sort(tempBoard);

        //dealer = data.getDealer();
        
        /*try {
            dealer = (Dealer)data.getDealer().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }*/

        dealer = (Dealer) data.getDealer().clone();

        localData = (TableData) data.clone();
 

        simPlayers = Arrays.copyOf(data.getPlayers(), data.getPlayers().length);
        simPlayers[this.num] = this;
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

    private void backPropagate(boolean simulationWon) {
        System.out.println("entering backPropagate with a boolean of " + simulationWon);
        TreeNode backNode;
        String backAction;

        //currentNode = queue.getFirst();    

        lastSimNode = queue.getFirst();

        System.out.println("queue's size is: " + queue.size());
        System.out.println("actionQueue's size is: " + actionQueue.size());

        System.out.println("\nPrinting nodes visited:");
        while(!queue.isEmpty()) {
            backNode = queue.getLast();
            backAction = actionQueue.getLast();
            //System.out.println("Depth : " + backNode.getDepth());
            System.out.println("Depth : " + backNode.getDepth() + " Action to get here: " + backAction);

            System.out.println("Stats for this node before update (plays, wins, visits): " + backNode.getActionPlays(backAction) + " " + backNode.getActionWins(backAction) + " " + backNode.getVisitCount());
            backNode.updateNodeStats(simulationWon, backAction);
            System.out.println("Stats after update (plays, wins, visits): " + backNode.getActionPlays(backAction) + " " + backNode.getActionWins(backAction) + " " + backNode.getVisitCount());


            actionQueue.removeLast();
            queue.removeLast();
        }     
        queue.clear();
        //System.out.println("Queue size is now: " + queue.size());
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
            System.out.println("root had: " + root.getNumOfChildren() + " children.");
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
            nodeTotal = root.getTotalNodeCount();
            System.out.println("root has: " + root.getNumOfChildren() + " children.");
            //root.printChildrenDepths();
        }catch(IOException i) {
            root = new TreeNode("root");
            nodeTotal = 1;
            //i.printStackTrace();
            System.out.println("treenode.ser not found, creating new root node");
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
    private double mctsAlg(int w, int n, int t) {
        System.out.println("w, n, t : " + w +  " " + n + " " + t);
        double c = sqrt(2);

        double nodeValue = (w / n);

        double nodeData = sqrt(log(t) / n);

        double biasValue = (c * nodeData);

        return nodeValue + biasValue;
        //return ((w/n) + (c * (sqrt(log(t) / n))));
    }
    /*private double mctsAlg(int w, int n, int t){
        double c = sqrt(2);

        return ((w/n) + (c * (sqrt(log(t) / n))));
    }*/

    private int makeDecision(TreeNode inputNode, String[] choices) {
        System.out.println("Entering makeDecision");

        int totalPlays = 0;
        int indexToReturn = -1;
        double mctsValue = -1;
        String currentChoice;

        for(int j = 0; j < choices.length; j++) {        
            totalPlays = totalPlays + inputNode.getActionPlays(choices[j]);
        }


        // For each available action, let's pull data from the currentNode we're at to make a decision
        // on what we're going to return for our "choice"
        for(int i = 0; i < choices.length; i++) {
            currentChoice = choices[i];
            System.out.println("currentChoice = " + currentChoice);

            // if we've never played any of the current choices we have before, we want to make sure to play it at least once, so we're going to return it.
            if(inputNode.getActionPlays(currentChoice) == 0) {
                System.out.println("makeDecision is returning early because we had no data for the choice: " + currentChoice);

                if(simulate) { return -1; }
                else { return i; }
                //return choices[i];
            // else, we have data for the current choice, let's calculate the MCTS "value" for the stats we have for that action at this state of the game
            } else {
                //double newMCTSValue = mctsAlg(inputNode.getActionWins(currentChoice), inputNode.getActionPlays(currentChoice), inputNode.getVisitCount());
                System.out.println("inputNode.getActionsWins(" + currentChoice + ") = " + inputNode.getActionWins(currentChoice));
                double newMCTSValue = mctsAlg(inputNode.getActionWins(currentChoice), inputNode.getActionPlays(currentChoice), totalPlays);

                /*if(currentChoice.equals("fold")) {
                    System.out.println("fold, adjusting newmctsvalue");
                    newMCTSValue = newMCTSValue - .25;
                }*/

                /*System.out.println("newMCTSValue for choice " + currentChoice + " is " + newMCTSValue + " from the data (Wins, Plays, VisitCount) "
                        + inputNode.getActionWins(currentChoice) + " " + inputNode.getActionPlays(currentChoice) + " " + inputNode.getVisitCount());*/

                System.out.println("newMCTSValue for choice " + currentChoice + " is " + newMCTSValue + " from the data (Wins, Plays, totalPlays) "
                        + inputNode.getActionWins(currentChoice) + " " + inputNode.getActionPlays(currentChoice) + " " + totalPlays);


                // if the newMCTSValue is greater than the previous one we've set (or the intialized value) let's set that we're going to return that action to the game
                if (newMCTSValue > mctsValue) {
                    System.out.println("Replacing mctsValue with the choice of " + currentChoice + " " + newMCTSValue +  " > " + mctsValue);
                    mctsValue = newMCTSValue;
                    indexToReturn = i;
                } else {
                    System.out.println("newMCTSValue was not greater than mctsValue " + newMCTSValue + " " + " < " + mctsValue);
                }
            }
        }


        return indexToReturn;
        
    } 
    

    private int getNodeTotal() {
        return this.nodeTotal;
    }

    @Override
    public void handResults(Results r) {
        boolean simulationWon = false;

        System.out.println();
        System.out.println("Hand is over.");
        System.out.print("Winner(s):");
        int winningHand = -1;
        for (Integer i : r.getWhoWon()) {
            if(i == this.num) {
                System.out.println("AgentLateStart won!");
                simulationWon = true;
            }

            System.out.print(" " + (i));
            winningHand = i;
        }
        System.out.println();

        if(!simulate) {
            backPropagate(simulationWon);
        }

        System.out.println("backPropagation is complete.  Should be seeing game ending totals now");

        /*System.out.println("Winning hand was a "
                + r.getOutcomes()[winningHand].toString());

        System.out.println();
        System.out.println("Starting next round of betting.");*/
    }

}
