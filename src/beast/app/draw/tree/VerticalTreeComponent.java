package beast.app.draw.tree;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import org.jtikz.TikzRenderingHints;

import java.awt.*;
import java.util.List;

/**
 * @author Alexei Drummond
 */
public class VerticalTreeComponent extends TreeComponent {

    public VerticalTreeComponent(List<TreeDrawing> treeDrawings) {

        super(treeDrawings, 0, 0, true);
    }


    public VerticalTreeComponent(List<TreeDrawing> treeDrawings, double nodeHeightScale, double nodeSpacingScale) {

        super(treeDrawings, nodeHeightScale, nodeSpacingScale, true);
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

        draw(childPosition, childHeight, position, height, g);
    }

    @Override
    void drawBranchLabel(String branchLabel, Tree tree, Node node, Node childNode, Object anchor, double fontSize, Graphics2D g) {
        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());
        double pos = getNodePosition(node);
        double childPos = getNodePosition(childNode);

        drawNode(branchLabel, (pos + childPos) / 2, (height + childHeight) / 2, TikzRenderingHints.VALUE_SOUTH, 9.0, g);
    }

    @Override
    void drawInternodeInterval(double nodeHeight, double p1, double p2, Graphics2D g) {
        draw(p1, nodeHeight, p2, nodeHeight, g);
    }

    static String ladderTree(int size) {

        String tree = "(1:1, 2:1)";

        for (int i = 2; i < size; i++) {
            tree = "(" + tree + ":1," + (i + 1) + ":" + i + ")";
        }

        return tree + ";";
    }
}
