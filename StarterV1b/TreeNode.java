//import sun.reflect.generics.tree.Tree;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 3/22/2017.
 */


public class TreeNode<T> { //} implements Iterable<TreeNode<T>> {

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    private int visits;
    private int betWins;
    private int foldWins;
    private int callWins;

    private int[] pocket;
    private int[] board;

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public TreeNode(int[] aPocket, int[] aBoard) {
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

    public List<TreeNode<T>> getChildren(){
        return this.children;
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

