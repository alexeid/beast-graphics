package beast.app.draw.tree;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import org.jtikz.TikzRenderingHints;

import java.awt.*;

/**
 * @author Alexei Drummond
 */
public class UnrootedTreeComponent extends TreeComponent {

    double startingTheta = 0.0;

    public UnrootedTreeComponent(java.util.List<TreeDrawing> treeDrawings) {
        super(treeDrawings, false);
    }

    public void setTheta(double theta) {
        startingTheta = theta;
    }

    void draw(TreeDrawing treeDrawing, Node node, Graphics2D g) {

        Tree tree = treeDrawing.getTree();

        if (node.isRoot()) {
            setTipValues(tree, node);
        }

        if (node.isLeaf()) {
            drawLabel(treeDrawing, node, g);
        } else {

            for (Node childNode : node.getChildren()) {
                draw(treeDrawing, childNode, g);
            }

            for (Node childNode : node.getChildren()) {

                drawBranch(tree, node, childNode, g);
            }
        }
    }


    void setTipValues(Tree tree, Node node) {

        double scale = this.getNodeHeightScale(tree);

        double fullAngle = Math.PI / 2.0;
        double theta = Math.PI + startingTheta;
        if (node.isRoot()) {
            fullAngle = 2 * Math.PI;

            node.setMetaData("x1", treeWidth / 2);
            node.setMetaData("y1", treeHeight / 2);

        } else {

            Node parent = node.getParent();

            theta = (Double) node.getMetaData("theta");

            double length = node.getLength();


            double x = (Double) parent.getMetaData("x1") + length * scale * Math.cos(theta);
            double y = (Double) parent.getMetaData("y1") + length * scale * Math.sin(theta);

            node.setMetaData("x1", x);
            node.setMetaData("y1", y);

            theta -= fullAngle / 2.0;
        }


        int children = node.getChildCount();
        if (!node.isRoot()) children -= 1;

        //System.out.println("parent.theta=" + node.getAttribute("theta"));
        for (Node childNode : node.getChildren()) {

            //System.out.println("  child.theta=" + theta);
            childNode.setMetaData("theta", theta);
            theta += fullAngle / (double) children;
        }

        for (Node childNode : node.getChildren()) {
            setTipValues(tree, childNode);
        }
    }

    void drawBranch(Tree tree, Node node, Node childNode, Graphics2D g) {

        double x1 = (Double) node.getMetaData("x1");
        double y1 = (Double) node.getMetaData("y1");

        double x2 = (Double) childNode.getMetaData("x1");
        double y2 = (Double) childNode.getMetaData("y1");

        draw(x1, y1, x2, y2, g);
    }

    void drawLabel(TreeDrawing treeDrawing, Node node, Graphics2D g) {

        double x = (Double) node.getMetaData("x1");
        double y = (Double) node.getMetaData("y1");

        double theta = (Double) node.getMetaData("theta");

        double x1 = x + treeDrawing.getLabelOffset() * Math.cos(theta);
        double y1 = y + treeDrawing.getLabelOffset() * Math.sin(theta);

        drawNode(node.getID(), x1, y1, TikzRenderingHints.VALUE_CENTER, 9, g);
    }
}
