package beastgraphics.app.draw.tree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by adru001 on 1/02/19.
 */
public class RootedNetworkDrawing extends JComponent {

    public static int BORDER = 80;
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

        double xScale = (getWidth() - 2.0 * BORDER) / (leaves.size() - 1.0);

        double yScale = (getHeight() - 2.0 * BORDER) / root.getTime();

        for (int i = 0; i < leaves.size(); i++) {
            Node leaf = leaves.get(i);
            leaf.x = (i * xScale) + BORDER;
        }

        setXY(root, true, yScale);

        //for (int i = 0; i < 100; i++) {
        //    //paintNode(root, g, Color.gray);
        //    setXY(root, false, yScale);
        //}
        paintNode(root, g, null, true, false);
        paintNode(root, g, null, false, true);
    }

    private void setXY(Node node, boolean ignoreParents, double yScale) {
        node.y = BORDER + (root.getTime() - node.getTime()) * yScale;
        List<Double> x = new ArrayList<>();
        List<Double> weight = new ArrayList<>();

        for (Node child : node.getChildIterable()) {
            setXY(child, ignoreParents, yScale);
            x.add(child.x);
            weight.add(1.0/Math.abs(node.time-child.time));
        }
        if (!ignoreParents) {
            for (Node parent : node.getParentIterable()) {
                x.add(parent.x);
                weight.add(1.0/Math.abs(node.time-parent.time));
            }
        }


        if (!node.isLeaf()) {
            node.x = weightedAverage(x,weight);
        }
        //if (node.isNetworkNode()) {
        //    node.x = node.getChild(0).x;
        //}
    }

    private double weightedAverage(List<Double> x, List<Double> weight) {
        double totalWeight = 0.0;
        for (Double w : weight) {
            totalWeight += w;
        }
        double wa = 0.0;
        for (int i = 0; i < x.size(); i++) {
            wa += x.get(i) * (weight.get(i)/totalWeight);
        }
        return wa;
    }

    private void paintNode(Node node, Graphics g, Color color, boolean paintEdges, boolean paintNodes) {
        for (Node child : node.getChildIterable()) {
            paintNode(child, g, color, paintEdges, paintNodes);
            if (paintEdges) paintEdge(node, child, g, color);
        }

        if (color != null) {
            g.setColor(color);
        } else {
            if (node.isLeaf()) {
                g.setColor(Color.red);
            } else if (node.isNetworkNode()) {
                g.setColor(Color.blue);
            } else {
                g.setColor(Color.black);
            }
        }

        if (paintNodes) g.fillArc((int)Math.round(node.x)-NODE_RADIUS,(int)Math.round(node.y)-NODE_RADIUS,NODE_RADIUS*2,NODE_RADIUS*2,0,360);
    }

    private void paintEdge(Node node, Node child, Graphics g, Color color) {
        if (color == null) color = Color.black;
        g.setColor(color);
        ((Graphics2D)g).setStroke(new BasicStroke(2.0f));

        double midx = (node.x + child.x) / 2.0;

        if (true) {
            g.drawLine(
                    (int) Math.round(node.x),
                    (int) Math.round(node.y),
                    (int) Math.round(child.x),
                    (int) Math.round(child.y)
            );

        }   else {

            if (node.isDivergenceNode() && child.isNetworkNode()) {

                if (child.getParent(0) == node && node.x > child.x) {
                    Node op = child.getParent(1);
                    midx = child.x - Math.abs((op.x - child.x) / 2.0);
                }
                if (child.getParent(1) == node && node.x < child.x) {
                    Node op = child.getParent(0);
                    midx = child.x + Math.abs((op.x - child.x) / 2.0);
                }

                g.drawLine(
                        (int) Math.round(node.x),
                        (int) Math.round(node.y),
                        (int) Math.round(midx),
                        (int) Math.round(node.y)
                );
                g.drawLine(
                        (int) Math.round(midx),
                        (int) Math.round(node.y),
                        (int) Math.round(midx),
                        (int) Math.round(child.y)
                );
                g.drawLine(
                        (int) Math.round(midx),
                        (int) Math.round(child.y),
                        (int) Math.round(child.x),
                        (int) Math.round(child.y)
                );
            } else if (node.isNetworkNode() && child.isNetworkNode()) {
                g.drawLine(
                        (int) Math.round(node.x),
                        (int) Math.round(node.y),
                        (int) Math.round(node.x),
                        (int) Math.round(child.y)
                );
                g.drawLine(
                        (int) Math.round(node.x),
                        (int) Math.round(child.y),
                        (int) Math.round(child.x),
                        (int) Math.round(child.y)
                );
            } else if (node.isDivergenceNode() && !child.isNetworkNode()) {

                g.drawLine(
                        (int) Math.round(node.x),
                        (int) Math.round(node.y),
                        (int) Math.round(child.x),
                        (int) Math.round(node.y)
                );
                g.drawLine(
                        (int) Math.round(child.x),
                        (int) Math.round(node.y),
                        (int) Math.round(child.x),
                        (int) Math.round(child.y)
                );
            } else {
                g.drawLine(
                        (int) Math.round(node.x),
                        (int) Math.round(node.y),
                        (int) Math.round(child.x),
                        (int) Math.round(child.y)
                );
            }
        }
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

        Random random = new Random(778);

        root = CoalescentWithRecombination.simulate(10,1,0.5, random);

        JFrame frame = new JFrame("RootedNetworkDrawing");

        RootedNetworkDrawing drawing = new RootedNetworkDrawing(root);

        drawing.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Node root = CoalescentWithRecombination.simulate(10, 1, 0.5, random);
                drawing.setRoot(root);
                frame.repaint();
            }
        });

        frame.getContentPane().add(drawing);

        frame.pack();
        frame.setVisible(true);
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}


