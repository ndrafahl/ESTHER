//import sun.reflect.generics.tree.Tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by Alex on 3/22/2017.
 */


public class TreeNode<T> implements java.io.Serializable {

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    private TreeNode<T> serialChild;

    private String name;
    private int depth;

    private int visits;
    private int betWins;
    private int foldWins;
    private int callWins;

    private int[] pocket;
    private int[] board;

    private int totalNodeCount;

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public TreeNode(String aName) {
	    this.name = aName;
        this.depth = 0;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public TreeNode(int[] aBoard) {
        this.name = null;
	    this.board = aBoard;
	    this.visits = 0;
	    this.betWins = 0;
	    this.foldWins = 0;
	    this.callWins = 0;
	    this.children = new LinkedList<TreeNode<T>>();
    }

    public TreeNode(int[] aPocket, int[] aBoard) {
        this.name = null;
        this.pocket = aPocket;
        this.board = aBoard;
        this.visits = 0;
        this.betWins = 0;
        this.foldWins = 0;
        this.callWins = 0;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public void addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child);
        childNode.setParent(this);
        this.children.add(childNode);
        // return childNode;             //want to return child node?
    }
    public void addChild(TreeNode child){
        child.setParent(this);
        this.children.add(child);
        child.setDepth(this.depth);
    }

    private void setParent(TreeNode<T> parent){
        this.parent = parent;
    }

    public TreeNode<T> getParent(){
        return this.parent;
    }

    public T getData(){
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public int getVisited() {
        return this.visits;
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

    public List<TreeNode<T>> getChildren(){
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

    public TreeNode<T> findChild(int[] intArray) {
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
    }

    public TreeNode<T> findChild(int[] inPocket, int[] inBoard, boolean isRoot) {

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
       
    public TreeNode<T> getSerialChild() {
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

    public void readChildren(TreeNode<T> node){
        for (TreeNode child : node.getChildren()) {
            System.out.println(child.getData());
            if(child.hasChildren()){
                System.out.println("Has grandchild!");
                readChildren(child);
            }
        }

    }

}

