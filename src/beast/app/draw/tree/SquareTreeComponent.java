package beast.app.draw.tree;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.util.TreeParser;
import org.jtikz.TikzRenderingHints;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

/**
 * @author Alexei Drummond
 */
public class SquareTreeComponent extends TreeComponent {


    public SquareTreeComponent(TreeDrawing treeDrawing) {

        super(treeDrawing, false);
    }

    public SquareTreeComponent(TreeDrawing treeDrawing, double nodeHeightScale, double nodeSpacing) {

        super(treeDrawing, nodeHeightScale, nodeSpacing, false);
    }

    void drawBranch(Tree tree, Node node, Node childNode, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());

        double pos = getNodePosition(node);
        double childPos = getNodePosition(childNode);

        System.out.println("height=" + height + ", childHeight=" + childHeight + ", pos=" + pos + ", childPos=" + childPos);

        
        Path2D path = new GeneralPath();
        path.moveTo(childHeight, childPos);
        path.lineTo(height, childPos);
        path.lineTo(height, pos);

        g.draw(path);
    }

    void drawBranchLabel(String branchLabel, Tree tree, Node node, Node childNode, Object anchor, double fontSize, Graphics2D g) {
        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());
        double childPos = getNodePosition(childNode);

        drawNode(branchLabel, (height + childHeight) / 2, childPos, TikzRenderingHints.VALUE_SOUTH, TikzRenderingHints.VALUE_scriptsize, g);
    }

    public static void main(String[] args) throws Exception {

        String newickTree = "((((1:0.1,2:0.1):0.1,3:0.2):0.1,4:0.3):0.1,5:0.4);";

        TreeComponent treeComponent =
                new SquareTreeComponent(new TreeDrawing(new TreeParser(newickTree)));

        JFrame frame = new JFrame("SquareTreeComponent");
        frame.getContentPane().add(treeComponent, BorderLayout.CENTER);
        frame.setSize(new Dimension(800, 600));
        frame.setVisible(true);
    }


}
