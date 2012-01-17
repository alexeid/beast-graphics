package beast.app.draw.tree;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import org.jtikz.TikzRenderingHints;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

/**
 * @author Alexei Drummond
 */
public class VerticalTreeComponent extends TreeComponent {

    public VerticalTreeComponent(TreeDrawing treeDrawing) {

        super(treeDrawing, 0, 0, true);
    }


    public VerticalTreeComponent(TreeDrawing treeDrawing, double nodeHeightScale, double nodeSpacingScale) {

        super(treeDrawing, nodeHeightScale, nodeSpacingScale, true);
    }

    @Override
    double getScaledTreeHeight() {
        return 0.9 * getHeight();
    }

    /**
     * The total amount of space for nodes (The number of pixels between adjacent leaf nodes * the number of leaf nodes)
     */
    double getTotalSizeForNodeSpacing() {
        return getWidth();
    }

    @Override
    void drawBranch(Tree tree, Node node, Node childNode, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());

        double position = getNodePosition(node);
        double childPosition = getNodePosition(childNode);

        //draw(childPosition, childHeight, position, height, g);

        Path2D path = new GeneralPath();
        path.moveTo(childPosition, childHeight);
        path.lineTo(childPosition, height);
        path.lineTo(position, height);

        g.draw(path);

    }

    @Override
    void drawBranchLabel(String branchLabel, Tree tree, Node node, Node childNode, Object fontSize, Graphics2D g) {
        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());
        double pos = getNodePosition(node);
        double childPos = getNodePosition(childNode);

        drawNode(branchLabel, childPos, (height + childHeight) / 2, TikzRenderingHints.VALUE_EAST, fontSize, g);
    }

    void drawLabel(TreeDrawing treeDrawing, Node node, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(treeDrawing.getTree(), node.getHeight());
        double position = getNodePosition(node);

        drawNode(node.getID(), position, height + treeDrawing.getLabelOffset(), TikzRenderingHints.VALUE_CENTER, TikzRenderingHints.VALUE_normalsize, g);
    }

    @Override
    void drawInternodeInterval(String label, double scaledNodeHeight, double p1, double p2, Graphics2D g) {
        draw(p1, scaledNodeHeight, p2, scaledNodeHeight, g);
        if (label != null)
            drawNode(label, p2, scaledNodeHeight, TikzRenderingHints.VALUE_WEST, TikzRenderingHints.VALUE_scriptsize, g);
    }

    static String ladderTree(int size) {

        String tree = "(1:1, 2:1)";

        for (int i = 2; i < size; i++) {
            tree = "(" + tree + ":1," + (i + 1) + ":" + i + ")";
        }

        return tree + ";";
    }
}
