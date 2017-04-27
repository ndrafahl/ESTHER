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
public class AgentMCTSBot extends Player {
    // SERIALIZE is a constant that determines whether or not we are going to write/read data from a serialized Tree from previous runs of ESTHER
    private final boolean SERIALIZE;

    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};

    // Local TreeNodes
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

    // simulate is true only when GameManager is calling getAction(), otherwise it is false when we're running a simulation.  this is to maintain
    // a simulation running for a definite period of time
    private boolean simulate;

    // Variables to maintain previous board sizes, for keeping track of whether or not we can reuse the previous "node" in our new simulation
    private int lastBoardSize;
    private int lastRealBoardSize;

    // Variable to keep track of how many nodes we've created this run
    private int nodeTotal;

    // actionQueue maintains a LIFO queue of what actions we've taken in a given simulation, so we know which actions need to be updated when
    // we backPropagate at the end of a simulation
    private LinkedList<String> actionQueue;

    // queue is similar to actionQueue, except that this is a LIFO queue that maintains which nodes we've visited in a simulation.  
    // Will be the same size as actionQueue
    private LinkedList<TreeNode> queue;
    
    // localData is where we store the cloned version of the TableData that gets passed into getAction()
    private TableData localData;

    // our only constructor, this initializes the local treenodes stored in this object, as well as handling the call to readTree() if SERIALIZE is true
    public AgentMCTSBot (int num, boolean serialization) {
        this.num = num;
        this.SERIALIZE = serialization;

        // if SERIALIZE is true, let's read in treenode.ser if it's available.  else, let's default root to a node with no children
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

        // local variable initialization that will be maintained as the game progresses
        simulate = true;
        this.queue = new LinkedList<TreeNode>();
        this.actionQueue = new LinkedList<String>();

        lastSimNode = root;
        lastNode = root;
        currentNode = root;
        lastBoardSize = 0;
        lastRealBoardSize = 0;
    }

    // Function call to return the number that was assigned to this bot when ESTHER is first ran
    @Override
    public String getScreenName() {
        return "MCTS Bot" + this.num;
    }

    // getAction() is called from either GameManager or GameManagerSim to get which action our bot would like to take in the current state of the game
    @Override
    public String getAction(TableData data) {

        // debugging information
        if (simulate) {
            System.out.println("MCTS Bot: Not running simulation, being prompted for action (call from GameManager).\n");
        } else {
            System.out.println("MCTS Bot: Running a simulation, being prompted for action (call from GameManagerSim).\n");
        }

        // generate localData for this bot from the tableData that was passed into getAction(), as we do not want to modify that data as it's being used by 
        // GameManager to maintain a fully functioning hand
        generateLocalData(data);
        
        // if GameManager is calling getAction() (simulate is true), let's determine if we need to run simulations, or if we can return an action with no simulations
        if(simulate) {

            // default nodeFound to false, let's assume we won't find one.
            boolean nodeFound = false;
            int choice;

            // get the actions that are available to us at this point in the game, and put them into a sorted array
            String pull = data.getValidActions();
            String[] choices = pull.split(",");
            Arrays.sort(choices);

            TreeNode sTempNode;

            // if the current board length is 0 (no cards on the table), then we can assume we're searching under root as that is where all the two card hands are contained
            if(data.getBoard().length == 0) {
                sTempNode = root.findChild(tempPocket, tempBoard, root.isRoot());
            // else, there are cards on the table, but if the number of cards on the table matches the same number of cards as the last time we saw getAction(), then that means that
            // we are still at the same state as before, just with different potential actions to take
            } else {
                if(lastRealBoardSize == data.getBoard().length) {
                    sTempNode = lastSimNode;
                } else {
                    sTempNode = lastSimNode.findChild(tempPocket, tempBoard, lastSimNode.isRoot());
                }
            }

            // if sTempNode is null, that means we didn't find our current state under the previous state of the game.
            // this means we need to create a new TreeNode to contain the current state and emplace it under our previous state
            if(sTempNode == null) {
                nodeFound = false;
                sTempNode = new TreeNode(tempPocket, tempBoard);
                lastSimNode.addChild(sTempNode);
                nodeTotal++;
            // else, we found our current state under the previous state
            } else {
                nodeFound = true;
            }

            // we want to make our current state to be what we base our choice on, so let's switch our currentNode to that state
            currentNode = sTempNode;

            // if we found the node, let's see if we have enough data about our available actions to return a decision to GameManager
            if(nodeFound) {
                choice = makeDecision(currentNode, choices); // will return -1 if one of our available choices has no data (i.e never been played)
                lastSimNode = currentNode;
            // else, we have no valid choice to make
            } else {
                choice = -1;
            }

            // if makeDecision returned -1 (not enough data for all actions), or choice was set to -1 because we never found a node, we need to run simulations from our
            // current state until we have enough data to make a proper decision
            if(choice == -1) {

                System.out.println("MCTS Bot: Not enough data available, beginning Simulation.\n");

                // set simulate to false, as we want to hit the else statement (where GameManagerSim will be calling getAction())
                simulate = false;

                // set simStartNode to currentNode, as after our simulations are done we want to make sure to return to that state to return an action
                simStartNode = currentNode;

                // keep track of the total number of simulations we've ran from this point
                int simulationsRan = 0;

                // for an alloted time, we're going to continuously run simulations from our state to gather data
                // right now, we have this set to 1 second
                for(long stop = System.nanoTime()+TimeUnit.SECONDS.toNanos(1); stop > System.nanoTime(); ) {

                    // generate a clone of the TableData we were given, so we do not modify it
                    generateLocalData( (TableData) data.clone());        

                    System.out.println("Simulations ran so far: " + simulationsRan);
                    
                    // each time we come back to the beginning of the for loop, we need to create a new GameManagerSim to run our simulation to the end of the hand
                    GameManagerSim g = new GameManagerSim(data.getPlayers(), dealer, false, limits, 3, 1, simWhosIn,
                        simPlayerStakes, simBank, localData);

                    // play the simulation
                    int[] end = g.playGame(data);

                    simulationsRan++;

                    // Confirmation the game ended.
                    System.out.println("Finished simulation where we started from a board size of: " + lastBoardSize);
                    //System.out.println("Beginning back propogation of nodes visited.");

                } //end for(long stop...

                System.out.println("Total simulations ran: " + simulationsRan);

                // we should now have enough data to make an 'intellectual' decision on what action we should take at our current state
                // choice is the index value of choices[] that we want to return
                choice = makeDecision(simStartNode, choices);

                // set simulate to true, as next time getAction() is called, it'll be from GameManager with a new state of the game
                simulate = true;

                // set lastSimNode to the state we were just at:  this is due to the fact that we know the next state of the game (if it's not a new hand) will be directly below
                // the current state we're at (i.e we'll still have the same 2 cards in our hand, the same 3 cards will be on the board, plus a new card on the board)
                lastSimNode = simStartNode;

                // set lastRealBoardSize to the current board length, that way if we get called from the same state (perhaps we have the chance to raise/bet a second time) we know to reuse this state
                lastRealBoardSize = data.getBoard().length;
            } // end (if choice == -1)

            // return our choice to GameManager, this occurs whether or not we had to simulate
            System.out.println("MCTS Bot: Returning " + choices[choice] + " to GameManager.");
            return choices[choice];

        // else only occurs if getAction() is called from GameManagerSim, which means we're in the middle of a simulation
        } else {
            
            // set lastNodeUsed to false.  this is going to determine whether or not we'll reuse the state we were previously at in the simulation (i.e last getAction() had 2 cards in hand, 3 cards on the board, 
            // and we're still at the same state on this call to getAction())            
            boolean lastNodeUsed = false;     

            // again, if board size is 0, we're searching root
            if(data.getBoard().length == 0) {
                //System.out.println("Searching root");
                currentNode = root;
            } else {
                //System.out.println("Searching not root");
                // if queue.size() == 0, this means we've hit the first run of the simulation.  that means we're still at the state we began the simulation on, so let's use that to search for children below us
                if(queue.size() == 0) {
                    currentNode = simStartNode;
                } else {
                    // if lastBoardSize == this board's size, then we're still at the same state.  reuse the lastNode we visited in the simulation to gather data
                    if(lastBoardSize == data.getBoard().length) {
                        lastNodeUsed = true;
                        currentNode = lastNode;                       
                        //System.out.println("board length was same as last run, using lastNode: " + currentNode.getDepth());
                    // else, we're at a new state, so we need to search the previous state for children
                    } else {
                        currentNode = queue.getLast();
                        //System.out.println("board length not the same as last run, using end of queue: " + currentNode.getDepth());
                    }
                }
            }

            // if we're not using the lastNode we were at, due to being at the same state of the hand, then we need to search to find if we've seen this new state we're at before
            if(!lastNodeUsed) {      
                TreeNode tempNode = currentNode.findChild(tempPocket, tempBoard, currentNode.isRoot()); 

                if(tempNode == null) {
                    //System.out.println("No node found from node at depth " + currentNode.getDepth() + ", creating new node.");
                    tempNode = new TreeNode(tempPocket, tempBoard);
                    currentNode.addChild(tempNode);
                    nodeTotal++;
                    //System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
                } else {
                    //System.out.println("Node found under currentNode!");
                    //System.out.println("currentNodes depth is: " + currentNode.getDepth() + " | tempNodes depth is: " + tempNode.getDepth());
                }

                // reassign which nodes to keep track of, as well as this current board size for the next getAction() call
                currentNode = tempNode;
                lastNode = tempNode;
                lastBoardSize = data.getBoard().length;
            }

            //System.out.println("Emplacing into queue (else) with depth of: " + currentNode.getDepth());
            
            // add our currentState to the queue to backPropagate back through later to update our data
            queue.add(currentNode);

            // get what actions we can take, and put them into an array
            String pull = data.getValidActions();
            String[] choices = pull.split(",");
            Arrays.sort(choices);

            // use the data we have at our currentNodes state to make a decision on what to do.  if we have no data for one of the potential actions, we'll automatically use that as we need to build data for it
            int decisionMade = makeDecision(currentNode, choices);
            
            // add the action we're taking to a queue, so we can use in backPropagate later
            actionQueue.add(choices[decisionMade]);
            System.out.println("MCTS Bot: Returning " + choices[decisionMade] + " to GameManagerSim.\n");

            //System.out.println("nodeTotal = " + this.nodeTotal);
            
            // return the choice we've made back to GameManagerSim
            return choices[decisionMade];

        }
    }

    // generateLocalData makes what are essentially "clones" of the data that is given from TableData when getAction() is called, and stores them locally in this object.
    // we do this as we do not want to modify the data that the *real* tableData contains, else we're injecting bad data into the game
    private void generateLocalData(TableData data) {
        // Create tempBoard and tempPocket data from the board and pocket in TableData.
        // Sorting to more easily search/create nodes in the Tree.
        tempPocket = Arrays.copyOf(data.getPocket(), data.getPocket().length);        
        tempBoard = Arrays.copyOf(data.getBoard(), data.getBoard().length);

        Arrays.sort(tempPocket);
        Arrays.sort(tempBoard);

        dealer = (Dealer) data.getDealer().clone();

        localData = (TableData) data.clone();
 

        simPlayers = Arrays.copyOf(data.getPlayers(), data.getPlayers().length);
        simPlayers[this.num] = this;
        simWhosIn = Arrays.copyOf(data.getWhosIn(), data.getWhosIn().length);
        simPlayerStakes = Arrays.copyOf(data.getPlayerStakes(), data.getPlayerStakes().length);
        simBank = Arrays.copyOf(data.getCashBalances(), data.getCashBalances().length);
    }

    // helper function to print our hand, not used, but kept in the event we need it
    private void printHand(TableData data) {
        // Display what this agent's current hand is to console for testing.
        System.out.println("MCTS Bot: Pocket is :");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        System.out.print("MCTS Bot: Pocket in Int is: ");
        System.out.println(data.getPocket()[0] + " " + data.getPocket()[1]);

        System.out.print("MCTS Bot: Sorted pocket in Int is: ");
        System.out.println(tempPocket[0] + " " + tempPocket[1]);
    }

    // backPropagate is called when GameManagerSim calls handResults() on our bot.  we know that the game is over, and we need to backPropagate back through each of the nodes we've visited in this simulation
    // if we won the game, we'll update the Win values for the action we took at that state, as well as updating the visit counts
    private void backPropagate(boolean simulationWon) {

        System.out.println("MCTS Bot: Back propagating through states visited, updating node values.");

        // local variables for storage
        TreeNode backNode;
        String backAction;

        //lastSimNode = queue.getFirst();

        // begin going through the nodes that we've stored in the queue, and update those nodes based on the action we took at that node.
        System.out.println("\nPrinting nodes visited:");
        while(!queue.isEmpty()) {
            backNode = queue.getLast();
            backAction = actionQueue.getLast();

            System.out.println("Action to get here: " + backAction);

            System.out.println("Stats for this node before update (plays, wins, visits): " + backNode.getActionPlays(backAction) + " " + backNode.getActionWins(backAction) + " " + backNode.getVisitCount());
            backNode.updateNodeStats(simulationWon, backAction);
            System.out.println("Stats after update (plays, wins, visits): " + backNode.getActionPlays(backAction) + " " + backNode.getActionWins(backAction) + " " + backNode.getVisitCount());

            actionQueue.removeLast();
            queue.removeLast();
        }

        // this shouldn't be needed, but we're going to be safe and clear the queues for the next time they're used
        queue.clear();
        actionQueue.clear();
        System.out.println("");
    }

    // writeTree() can be called to write out the root node (as it contains links to all its children, who then contain links to their children, etc) to a file for reuse later
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

    // readTree() can read back in the root node that we may have written from a previous run of ESTHER, if it exists, so we can reuse data from previous hands and perhaps skip simulating state that we already
    // have data on
    public void readTree(){
        try {
            FileInputStream fileIn = new FileInputStream("treenode.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            root = (TreeNode) in.readObject(); //assigns root as previous root
            in.close();
            fileIn.close();
            nodeTotal = root.getTotalNodeCount();
            //System.out.println("root has: " + root.getNumOfChildren() + " children.");
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

    // mctsAlg is the meat of this bot, it takes the number of times we've won for a given action at a state of the game, the total number of times we've run that action in total,
    // as well as the total number of times we've used *each* action at that state (that's currently available to us) as well as a constant to increase/decrease exploration/exploitation to determine which action we will
    // return to GameManager/GameManagerSim
    // W = num of wins after move
    // N = num of simulations after move
    // C = exploration paramter. sqrt(2) is good first guess. Adjust it to fit
    // T = total num of simulations. N of parent node
    private double mctsAlg(int w, int n, int t) {
        //System.out.println("w, n, t : " + w +  " " + n + " " + t);

        // exploration constant
        double c = sqrt(2);

        // nodeValue = number of wins / number of total plays for a given action
        double nodeValue = (w / n);

        // nodeData = sqrt(log(total number of times we've played each valid action at our state) / total number of times we've visited the state in total)
        double nodeData = sqrt(log(t) / n);

        // biasValue = constant * nodeData
        double biasValue = (c * nodeData);

        // sum NodeValue + biasValue, and return a double which is the calculated weight of that action
        return nodeValue + biasValue;

    }

    // function makeDecision is passed a TreeNode and the choices that are currently available for that state, and will use the mctsAlg on each of those choices to attempt to determine
    // what choice is the best to make
    private int makeDecision(TreeNode inputNode, String[] choices) {
        System.out.println("MCTS Bot: Going to make a decision...");

        // Initialize local variables
        int totalPlays = 0;
        int indexToReturn = -1;
        double mctsValue = -1;
        String currentChoice;

        // for each of the choices we have available, we need to sum the amount of times we've used each of those actions at this state for the mctsAlg
        for(int j = 0; j < choices.length; j++) {        
            totalPlays = totalPlays + inputNode.getActionPlays(choices[j]);
        }

        // For each available action, let's pull data from the currentNode we're at to make a decision
        // on what we're going to return for our "choice"
        for(int i = 0; i < choices.length; i++) {
            currentChoice = choices[i];
            System.out.println("MCTS Bot: Examining the action: " + currentChoice);

            // if we've never playedthe current choice in our array of choices, we want to make sure to play it at least once, so we're going to return it.
            if(inputNode.getActionPlays(currentChoice) == 0) {
                System.out.println("MCTS Bot: Returning early because we had no data for the choice: " + currentChoice);

                if(simulate) { return -1; } // we return -1 if we're currently not running a simulation, as that tells the bot we need to run a simulation of the game
                else { return i; } // else, we just return the index that we *need* to play to the running simulation

            // else, we have data for the current choice, let's calculate the MCTS "value" from the stats we have for that action at this state of the game
            } else {
                //System.out.println("inputNode.getActionsWins(" + currentChoice + ") = " + inputNode.getActionWins(currentChoice));
                double newMCTSValue = mctsAlg(inputNode.getActionWins(currentChoice), inputNode.getActionPlays(currentChoice), totalPlays);

                System.out.println("MCTS Bot: Has determined the value of " + currentChoice + " to be " + newMCTSValue + " from the data (Wins, Plays, totalPlays) "
                        + inputNode.getActionWins(currentChoice) + " " + inputNode.getActionPlays(currentChoice) + " " + totalPlays);


                // if the newMCTSValue is greater than the previous one we've set (or the intialized value) let's set that we're going to return that action to the game
                if (newMCTSValue > mctsValue) {
                    System.out.println("MCTS Bot: Found that the choice " + currentChoice + " is the best choice so far, with a value of: " + newMCTSValue);
                    mctsValue = newMCTSValue;
                    indexToReturn = i;
                }/* else {
                    System.out.println("newMCTSValue was not greater than mctsValue " + newMCTSValue + " " + " < " + mctsValue);
                }*/
            }
        }

        // return the index we've picked in the array of choices 
        return indexToReturn;
        
    } 
    
    // helper method to get the total number of Nodes we've created so far
    private int getNodeTotal() {
        return this.nodeTotal;
    }

    // handResults is called anytime a hand is finished, either by GameManager or GameManagerSim
    @Override
    public void handResults(Results r) {
        boolean simulationWon = false; // by default, assume we're running a simulation and we've lost that simulation

        System.out.println();
        System.out.println("MCTS Bot: Simulation of the hand is over.");
        System.out.print("MCTS Bot:  Simulation Winner(s):");
        int winningHand = -1;
        for (Integer i : r.getWhoWon()) {
            if(i == this.num) {
                //System.out.println("MCTSBot: Won this simulation.");
                simulationWon = true;
            }

            System.out.print(" " + (i));
            winningHand = i;
        }
        if (simulationWon) {
            System.out.println("\nMCTS Bot: Won this simulation.");
        }

        System.out.println();

        // if we're running a simulation (!simulate), then let's backPropagate and update our states visited
        // if we've won, let's make sure we update our "won" variables in those states for the action taken at that state
        if(!simulate) {
            backPropagate(simulationWon);
        }
    } // end handResults

} // end bot
