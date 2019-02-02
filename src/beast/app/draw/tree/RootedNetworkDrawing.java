package beast.app.draw.tree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adru001 on 1/02/19.
 */
public class RootedNetworkDrawing extends JComponent {

    public static int BORDER = 20;
    public static int NODE_RADIUS = 5;

    Node root;

    public RootedNetworkDrawing(Node root) {
        this.root = root;

        setPreferredSize(new Dimension(800,800));
    }


    public void paintComponent(Graphics g) {

        List<Node> leaves = new ArrayList<>();
        Node.collectLeaves(root, leaves);

        System.out.println("leaves.size=" + leaves.size());

        double xScale = (getWidth() - 2.0 * BORDER) / (leaves.size()-1.0);

        double yScale = (getHeight() - 2.0*BORDER) / root.getTime();

        for (int i = 0; i < leaves.size(); i++) {
            Node leaf = leaves.get(i);
            leaf.x = (i*xScale)+BORDER;
        }

        setXY(root, true, yScale);
        setXY(root, false, yScale);

        paintNode(root, g);
    }

    private void setXY(Node node, boolean ignoreParents, double yScale) {
        node.y = BORDER + (root.getTime() - node.getTime()) * yScale;
        double x = 0;
        int xCount = 0;
        for (Node child : node.getChildIterable()) {
            setXY(child, ignoreParents, yScale);
            x += child.x;
            xCount += 1;
        }
        if (!ignoreParents) {
            for (Node parent : node.getParentIterable()) {
                x += parent.x;
                xCount += 1;
            }
        }

        if (!node.isLeaf()) {
            node.x = x / (double)xCount;
        }
    }

    private void paintNode(Node node, Graphics g) {
        for (Node child : node.getChildIterable()) {
            paintNode(child, g);
            paintEdge(node, child, g);
        }

        if (node.isLeaf())   {
            g.setColor(Color.black);
        } else if (node.isNetworkNode()) {
            g.setColor(Color.green);
        } else

            g.fillArc((int)Math.round(node.x)-NODE_RADIUS,(int)Math.round(node.y)-NODE_RADIUS,NODE_RADIUS*2,NODE_RADIUS*2,0,360);


    }

    private void paintEdge(Node node, Node child, Graphics g) {
        g.drawLine(
                (int)Math.round(node.x),
                (int)Math.round(node.y),
                (int)Math.round(child.x),
                (int)Math.round(child.y)
        );
    }

    public static void main(String[] args) {

        Node root = new Node(9);
        Node i8 = new Node(8);
        Node i7 = new Node(6);
        Node i6 = new Node(7);
        Node i5 = new Node(5);
        Node i4 = new Node(4);
        Node i3 = new Node(3);
        Node i2 = new Node(2);
        Node i1 = new Node(1);
        Node e1 = new Node(0);
        Node e2 = new Node(0);
        Node e3 = new Node(0);
        Node e4 = new Node(0);

        root.addChildren(i6, i8);
        i8.addChildren(i5, i7);
        i7.addChildren(i3, i4);
        i6.addChildren(i3, i4);
        i5.addChildren(i2, e4);
        i4.addChild(i1);
        i3.addChild(e1);
        i2.addChildren(i1, e3);
        i1.addChild(e2);

        JFrame frame = new JFrame("RootedNetworkDrawing");

        RootedNetworkDrawing drawing = new RootedNetworkDrawing(root);

        frame.getContentPane().add(drawing);

        frame.pack();
        frame.setVisible(true);
    }

}


