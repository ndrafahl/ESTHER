
import java.util.ArrayList;
import java.util.List;

 /**
 * Created by Alex on 3/3/2017.
 */

public class MyTeeeNode<T>{
    private T data = null;
    private List<MyTeeeNode> children = new ArrayList<>();
    private MyTeeeNode parent = null;
    private int visited = 0;
    private int winner = 0;
    private String lastAction = "";

    public MyTeeeNode(T data) {
        this.data = data;
    }

    public void addChild(MyTeeeNode child) {
        child.setParent(this);
        this.children.add(child);
 }

    public void addChild(T data) {
        MyTeeeNode<T> newChild = new MyTeeeNode<>(data);
        newChild.setParent(this);
        children.add(newChild);
    }

    public void addChildren(List<MyTeeeNode> children) {
        for(MyTeeeNode t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<MyTeeeNode> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(MyTeeeNode parent) {
        this.parent = parent;
    }

    public MyTeeeNode getParent() {
        return parent;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
