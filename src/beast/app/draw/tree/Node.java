package beast.app.draw.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adru001 on 1/02/19.
 */
public class Node {

    double time;
    double x, y;

    private List<Node> parents;
    private List<Node> children;

    public  Node(double t) {
        time = t;
    }

    public boolean isLeaf() {
        return children == null || children.size() == 0;
    }

    public boolean isRoot() {
        return parents == null || parents.size() == 0;
    }

    public int getParentCount() {
        if (parents == null) return 0;
        return parents.size();
    }
    public double getChildCount() {
        if (children == null) return 0;
        return children.size();
    }

    public Iterable<Node> getChildIterable() {
        return () -> new Iterator() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return children != null && children.size() > index;
            }

            @Override
            public Object next() {
                return children.get(index++);
            }
        };
    }

    public Iterable<Node> getParentIterable() {
        return () -> new Iterator() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return parents != null && parents.size() > index;
            }

            @Override
            public Object next() {
                return parents.get(index++);
            }
        };
    }

    public boolean isNetworkNode() {
        return getParentCount() > 1;
    }

    public boolean isDivergenceNode() {
        return (isRoot() || getParentCount() == 1) && !isLeaf();
    }

    public void addChildren(Node... node) {
        for (Node n : node) {
            addChild(n);
        }
    }

    public void addChild(Node node) {
        if (children == null) children = new ArrayList<>();
        children.add(node);
        if (node.parents == null) node.parents = new ArrayList<>();
        node.parents.add(this);
    }

    public void removeChild(Node node) {
        if (children == null) throw new IllegalArgumentException("No children to remove!");
        children.remove(node);
        node.parents.remove(this);
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public static void collectLeaves(Node node, List<Node> leaves) {
        if (node.isLeaf() && !leaves.contains(node)) leaves.add(node);

        for (Node child : node.getChildIterable()) {
            collectLeaves(child, leaves);
        }
    }

    public Node getChild(int i) {
        return children.get(i);
    }

    public Node getParent(int i) {
        return parents.get(i);
    }

    public List<Node> removeChildren() {
        List<Node> c = children;
        children = null;
        for (int i = 0; i < c.size(); i++) {
            c.get(i).parents.remove(this);
        }
        return c;
    }
}
