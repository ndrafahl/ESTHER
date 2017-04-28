//import sun.reflect.generics.tree.Tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by Alex on 3/22/2017.
 */


public class TreeNode implements java.io.Serializable {

    private TreeNode parent;
    private List<TreeNode> children;

    private String name;
    private int depth;

    private int visits;

    // local variables used for the MCTS Algorithm
    private int betWins;
    private int betPlays;
    private int raiseWins;
    private int raisePlays;
    private int checkWins;
    private int checkPlays;
    private int foldWins;
    private int foldPlays;
    private int callWins;
    private int callPlays;

    private int[] pocket;
    private int[] board;

    // used only as a sanity check for when we write and read the root node to a serialized file
    private int totalNodeCount;

    // this constructor is only ever used by root, as it's the only node that gets a name
    public TreeNode(String aName) {
	    this.name = aName;
        this.depth = 0;
        this.children = new LinkedList<TreeNode>();
    }

    public TreeNode(int[] aBoard) {
        this.name = null;
	    this.board = aBoard;
	    this.visits = 0;
	    this.betWins = 0;
	    this.foldWins = 0;
	    this.callWins = 0;
	    this.children = new LinkedList<TreeNode>();
    }

    public TreeNode(int[] aPocket, int[] aBoard) {
        this.name = null;
        this.pocket = aPocket;
        this.board = aBoard;
        this.visits = 0;
        this.betWins = 0;
        this.foldWins = 0;
        this.callWins = 0;
        this.children = new LinkedList<TreeNode>();
    }

    public void addChild(TreeNode child){
        child.setParent(this);
        this.children.add(child);
        child.setDepth(this.depth);
    }

    private void setParent(TreeNode aParent){
        this.parent = aParent;
    }

    public TreeNode getParent(){
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public int getVisitCount() {
        return this.visits;
    }

    // returns integer value of how many times we've won with a given action at this node
    // used by the MCTS Algorithm
    public int getActionWins(String action) {
        if(action.equals("bet")) {
            return this.betWins;
        } else if(action.equals("fold")) {
            return this.foldWins;
        } else if(action.equals("call")) {
            return this.callWins;
        } else if(action.equals("raise")) {
            return this.raiseWins;
        } else if(action.equals("check")) {
            return this.checkWins;
        } else {
            System.out.println("ERROR:  getActionWins recieved invalid action taken, quitting");
            System.exit(1);
        }

        return 0;
    }

    // returns integer value of how many times we've played a given action at this node
    // used by the MCTS Algorithm
    public int getActionPlays(String action) {
        if(action.equals("bet")) {
            return this.betPlays;
        } else if(action.equals("fold")) {
            return this.foldPlays;
        } else if(action.equals("call")) {
            return this.callPlays;
        } else if(action.equals("raise")) {
            return this.raisePlays;
        } else if(action.equals("check")) {
            return this.checkPlays;
        } else {
            System.out.println("ERROR:  getActionPlays recieved invalid action taken, quitting");
            System.exit(1);
        }

        return 0;
    }

    public int[] getBoard() {
        return this.board;
    }

    public int[] getPocket() {
        return this.pocket;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int aDepth) {
        this.depth = aDepth + 1;
    }

    public List<TreeNode> getChildren(){
        return this.children;
    }

    public int getNumOfChildren(){
        return this.children.size();
    }

    public boolean isRoot() {
        return (name == "root");
    }

    // findChild will taken in the current Pocket, Board, and whether or not the Node is root and return if it was able to find a given child
    // under the node with the Pocket/Board combo given
    public TreeNode findChild(int[] inPocket, int[] inBoard, boolean isRoot) {

        // if we have no children, we aren't going to find any, return null
        if(this.children.size() == 0) {           
            return null;
        }

        for(TreeNode t : this.children) {
            // if this node is root, we just need to compare the inboard pocket cards to our pocket cards
            if(isRoot) {
                if(Arrays.equals(t.getPocket(), inPocket)) {
                    return t;
                } else {
                    return null;
                }
            // else, this node isn't root, and we need to compare the board to our board
            } else {
                if(Arrays.equals(t.getBoard(), inBoard)) {
                    return this;
                } else {
                    return null;
                }
            }
        }

        // should never occur, but return null if somehow it does
        System.out.println("findChild is returning because something went wrong!");
        return null;
    }

    // sets root's totalNodeCount prior to writing root to a serialized file
    public void setTotalNodeCount(int nodeCount) {
        this.totalNodeCount = nodeCount;
    }

    // used only by root, get's the total node count after we've read root in from a serialized file
    public int getTotalNodeCount() {
        return this.totalNodeCount;
    }

    public boolean hasChildren() { return !children.isEmpty();}

    // updateNodeStats is only ever called when backPropagate is called in AgentMCTSBot
    // we're going to update the nodes stats based on what the action was taken for that state of the game, as well
    // as whether or not we won that game
    public void updateNodeStats(boolean gameWon, String action) {
        System.out.println("updatedNodeStats called with gameWon = " + gameWon);

        if(action.equals("fold")) {
            this.foldPlays++;
            this.visits++;
            if(gameWon) { this.foldWins++; }
        } else if (action.equals("bet")) {
            this.betPlays++;
            this.visits++;
            if(gameWon) { this.betWins++; }
        } else if (action.equals("call")) {
            this.callPlays++;
            this.visits++;
            if(gameWon) { this.callWins++; }
        } else if (action.equals("raise")) {
            this.raisePlays++;
            this.visits++;
            if(gameWon) { this.raiseWins++; }
        } else if (action.equals("check")) {
            this.checkPlays++;
            this.visits++;
            if(gameWon) { this.checkWins++; }
        } else {
            System.out.println("updateNodeStats got an invalid action, please review.");
            System.out.println("gameWon was: " + gameWon + " and action was: " + action);
        }
    }
}

