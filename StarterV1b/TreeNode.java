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

    private TreeNode serialChild;

    private String name;
    private int depth;

    private int visits;

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

    private int totalNodeCount;

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


    public int getBetWins() {
        return this.betWins;
    }

    public int getFoldWins() {
        return this.foldWins;
    }

    public int getCallWins() {
        return this.callWins;
    }

    public int getRaiseWins() {
        return this.raiseWins;
    }

    public int getCheckWins() {
        return this.checkWins;
    }

    public int getBetPlays() {
        return this.betPlays;
    }

    public int getFoldPlays() {
        return this.foldPlays;
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
        boolean rBool = (name == "root");
        //System.out.println("rBool is: " + rBool);
        return (name == "root");
    }

    /*public TreeNode findChild(int[] intArray) {
        for(TreeNode t : this.children) {
            if(this.name == "root" || this.parent.getName() == "root") {
                if(Arrays.equals(t.getPocket(), intArray)) {
                    return t;
                } else {
                    return null;
                }
            } else {
                if(Arrays.equals(t.getBoard(), intArray)) {
                    return this;
                } else {
                    return null;
                }
            }
        }

        return null;
    }*/

    public TreeNode findChild(int[] inPocket, int[] inBoard, boolean isRoot) {

        if(this.children.size() == 0) {
            //System.out.println("findChild is returning because there are 0 children");
            return null;
        }

        for(TreeNode t : this.children) {
            if(isRoot) {
                //System.out.println("findChild evaluted that this is root");
                if(Arrays.equals(t.getPocket(), inPocket)) {
                    return t;
                } else {
                    return null;
                }
            } else {
                //System.out.println("findChild evaluated that this is not root");
                if(Arrays.equals(t.getBoard(), inBoard)) {
                    return this;
                } else {
                    return null;
                }
            }
        }

        System.out.println("findChild is returning because something went wrong!");
        return null;
    }

    public void printChildrenDepths() {
        for (TreeNode node : this.getChildren()) {
            System.out.println(node.getDepth() + " ");
        }
    }

    public void recursionToRoot() {
        if(this.name == "root" || this.parent == null) {
            System.out.println("My name is root and my depth is: " + this.depth);
            //System.out.println(this.depth);
            //return;
        } else {
            System.out.println("My name is not root and my depth is: " + this.depth);
            this.getParent().recursionToRoot();
            //recursionToRoot(this.parent);
        }

        return;
    }

    public void recursionToRoot(TreeNode t) { 
        //System.out.println(this.getDepth());
        
        if(this.name == "root" || this.parent == null) {
            System.out.println("My name is root and my depth is: " + this.depth);
            //return;
        } else {
            System.out.println("My name is not root and my depth is: " + this.depth);
            recursionToRoot(this.parent);
        }

        return;
    }

    public void setSerialChild(TreeNode t) {
        this.serialChild = t;
    }
       
    public TreeNode getSerialChild() {
        return this.serialChild;
    }

    public void setTotalNodeCount(int nodeCount) {
        this.totalNodeCount = nodeCount;
    }

    public int getTotalNodeCount() {
        return this.totalNodeCount;
    }

    public boolean hasChildren() { return !children.isEmpty();}
    // other features ...

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

