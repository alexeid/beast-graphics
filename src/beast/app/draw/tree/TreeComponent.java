package beast.app.draw.tree;

import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.Sequence;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.tree.coalescent.TreeIntervals;
import beast.util.TreeParser;
import org.jtikz.TikzGraphics2D;
import org.jtikz.TikzRenderingHints;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Drummond
 */
public class TreeComponent extends JComponent {

    TreeDrawing treeDrawing;

    // the position of the "current" leaf node
    private double p = 0;

    NumberFormat format = NumberFormat.getInstance();

    boolean isTriangle = true;

    /**
     * The scaling of the node heights. If the scale is 0 then scale is automatically calculated from component size
     */
    double nhs = 0;

    /**
     * The scaling of the spacing between the nodes (The number of pixels between adjacent tip nodes). If the spacing is 0 then it is automatically calculated from component size
     */
    double ns = 0;

    double rootHeightForScale;
    private boolean drawAxis = true;

    /**
     * @param treeDrawing the  tree drawing
     * @param isTriangle  true if tree should be drawn so that outside branches form a triangle on ultrametric trees
     */
    public TreeComponent(TreeDrawing treeDrawing, boolean isTriangle) {

        this(treeDrawing, 0, 0, isTriangle);

    }

    public TreeComponent(TreeDrawing treeDrawing, double nodeHeightScale, double nodeSpacing, boolean isTriangle) {

        format.setMaximumFractionDigits(5);

        //this.scalebar = scalebar;
        this.isTriangle = isTriangle;

        this.nhs = nodeHeightScale;
        this.ns = nodeSpacing;

        this.treeDrawing = treeDrawing;

        rootHeightForScale = treeDrawing.getTree().getRoot().getHeight();
    }

    /**
     * @param node the node
     * @return the position of this node in perpendicular to root-to-tip direction once scaled and labelOffset
     */
    double getNodePosition(Node node) {
        return (Double) node.getMetaData("p");
    }

    /**
     * @return the distance from the edge of component to the first leaf when traveling across the leaves
     */
    double firstLeafNodePosition(Tree tree) {
        return getNodeSpacing(tree) / 2;
    }

    /**
     * @return the distance from edge of component to root when traveling towards the leaves
     */
    final double rootOffset() {
        return 0.05 * getScaledTreeHeight();
    }

    /**
     * @param tree
     * @return the ratio of the available pixels for tree to the rootHeightForScale parameter
     */
    final double getNodeHeightScale(Tree tree) {

        if (nhs == 0) return getScaledTreeHeight() / rootHeightForScale;
        return nhs;
    }

    /**
     * The total pixels available for the tree.
     */
    double getScaledTreeHeight() {
        return 0.9 * getWidth();
    }

    /**
     * @param unscaledNodeHeight the node height to scale
     * @return the position of this node in root-to-tip direction once scaled and labelOffset for component
     */
    double getScaledOffsetNodeHeight(Tree tree, double unscaledNodeHeight) {
        return getScaledTreeHeight() - unscaledNodeHeight * getNodeHeightScale(tree) + rootOffset();
    }


    double getTotalSizeForNodeSpacing() {
        return getHeight();
    }

    /**
     * The spacing between the nodes (The number of pixels between adjacent leaf nodes)
     */
    final double getNodeSpacing(Tree tree) {
        if (ns == 0) return getTotalSizeForNodeSpacing() / tree.getLeafNodeCount();
        return ns;
    }

    void setTipValues(Tree tree, Node node) {
        if (node.isLeaf()) {
            node.setMetaData("p", p);
            node.setMetaData("pmin", p);
            node.setMetaData("pmax", p);
            p += getNodeSpacing(tree);
        } else {

            double pmin = Double.MAX_VALUE;
            double pmax = Double.MIN_VALUE;
            for (Node childNode : node.getChildren()) {
                setTipValues(tree, childNode);

                double cpmin = (Double) childNode.getMetaData("pmin");
                double cpmax = (Double) childNode.getMetaData("pmax");

                if (cpmin < pmin) pmin = cpmin;
                if (cpmax > pmax) pmax = cpmax;
            }
            node.setMetaData("pmin", pmin);
            node.setMetaData("pmax", pmax);
        }
    }

//    void drawScaleBar(Tree tree, StringBuilder builder) {
//
//        double sby = y;// - nodeSpacing - 1;
//        double width = scalebar.size * nodeHeightScale;
//
//        double sbx1 = (tree.getHeight(tree.getRootNode()) * nodeHeightScale + -width) / 2.0;
//        double sbx2 = sbx1 + width;
//
//        double sbx3 = (sbx1 + sbx2) / 2;
//
//        draw(sbx1, sby, sbx2, sby, builder);
//
//        label(sbx3, sby + labelOffset, "" + scalebar.size, builder);
//    }

//    void label(double x, double y, String label, Object anchor, Graphics2D g) {
//
//        if (label != null) {
//            Object oldHintValue = g.getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);
//            g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, TikzRenderingHints.VALUE_CENTER);
//            g.drawString(label, (float) x, (float) y);
//            if (oldHintValue != null) g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, oldHintValue);
//        }
//    }

    void draw(double x1, double y1, double x2, double y2, Graphics2D g) {

        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    void drawNode(String label, double x, double y, Object anchor, Object fontSize, Graphics2D g) {
        Object oldAnchorValue = g.getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);
        Object oldFontSize = g.getRenderingHint(TikzRenderingHints.KEY_FONT_SIZE);
        g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, anchor);
        g.setRenderingHint(TikzRenderingHints.KEY_FONT_SIZE, fontSize);
        //Font oldFont = g.getFont();
        //g.setFont(oldFont.deriveFont((float) fontSize));

        g.drawString(label, (float) x, (float) y);
        if (oldAnchorValue != null) g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, oldAnchorValue);
        if (oldFontSize != null) g.setRenderingHint(TikzRenderingHints.KEY_FONT_SIZE, oldFontSize);
        //g.setFont(oldFont);
    }

    private boolean isDrawingBranchLabels(TreeDrawing treeDrawing) {
        return treeDrawing.getBranchLabels() != null && !treeDrawing.getBranchLabels().equals("");
    }

    void drawBranch(Tree tree, Node node, Node childNode, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());

        double position = getNodePosition(node);
        double childPosition = getNodePosition(childNode);

        draw(height, position, childHeight, childPosition, g);
    }

    void drawBranchLabel(String label, Tree tree, Node node, Node childNode, Object fontSize, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());

        double position = getNodePosition(node);
        double childPosition = getNodePosition(childNode);

        drawNode(label, (height + childHeight) / 2, (position + childPosition) / 2, TikzRenderingHints.VALUE_SOUTH, fontSize, g);
    }

    void drawLabel(TreeDrawing treeDrawing, Node node, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(treeDrawing.getTree(), node.getHeight());
        double position = getNodePosition(node);

        drawNode(node.getID(), height + treeDrawing.getLabelOffset(), position, TikzRenderingHints.VALUE_CENTER, TikzRenderingHints.VALUE_normalsize, g);
    }

    void draw(TreeDrawing treeDrawing, Node node, Graphics2D g) {

        Tree tree = treeDrawing.getTree();

        if (treeDrawing.showInternodeIntervals() && node.isRoot()) {
            drawInternodeIntervals(treeDrawing.getTreeIntervals(), g);
        }

        g.setStroke(new BasicStroke((float) treeDrawing.getLineThickness()));

        p = firstLeafNodePosition(tree);

        if (node.isRoot()) {
            setTipValues(tree, node);
        }

        if (node.isLeaf() && treeDrawing.showLeafNodes()) {
            drawLabel(treeDrawing, node, g);
        } else {

            double cp = 0;
            if (isTriangle) {
                if (node.isRoot()) {

                    int tipCount = tree.getLeafNodeCount();

                    cp = ((tipCount - 1) * getNodeSpacing(tree)) / 2.0 + firstLeafNodePosition(tree);
                } else {

                    Node parent = node.getParent();

                    double pp = (Double) parent.getMetaData("p");
                    double ph = parent.getHeight();
                    double h = node.getHeight();

                    double pmin = (Double) node.getMetaData("pmin");
                    double pmax = (Double) node.getMetaData("pmax");

                    double pminDist = Math.abs(pp - pmin);
                    double pmaxDist = Math.abs(pp - pmax);

                    if (pminDist > pmaxDist) {
                        cp = ((pp * h) + (pmin * (ph - h))) / ph;
                    } else {
                        cp = ((pp * h) + (pmax * (ph - h))) / ph;
                    }
                }
                node.setMetaData("p", cp);
            }

            int count = 0;
            for (Node childNode : node.getChildren()) {
                draw(treeDrawing, childNode, g);
                cp += (Double) childNode.getMetaData("p");
                count += 1;
            }
            cp /= count;
            if (!isTriangle) node.setMetaData("p", cp);

            for (Node childNode : node.getChildren()) {

                drawBranch(tree, node, childNode, g);
                if (isDrawingBranchLabels(treeDrawing)) {
                    Object metaData = childNode.getMetaData(treeDrawing.getBranchLabels());
                    String branchLabel;
                    if (metaData instanceof Number) {
                        branchLabel = format.format(metaData);
                    } else {
                        branchLabel = metaData.toString();
                    }
                    drawBranchLabel(branchLabel, tree, node, childNode, TikzRenderingHints.VALUE_scriptsize, g);
                }
            }
        }
    }

    private void drawInternodeIntervals(TreeIntervals treeIntervals, Graphics2D g) {

        Tree tree = treeIntervals.m_tree.get();
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{1.0f, 1.0f}, 0));

        double[] intervals = treeIntervals.getIntervals(null);

        double unscaledHeight = 0.0;

        double p1 = firstLeafNodePosition(tree) / 2;
        double p2 = getTotalSizeForNodeSpacing() - p1;

        String label = format.format(unscaledHeight);

        drawInternodeInterval(label, getScaledOffsetNodeHeight(tree, unscaledHeight), p1, p2, g);
        for (double interval : intervals) {

            if (interval > 0.0) {
                unscaledHeight += interval;
                label = format.format(unscaledHeight);
                double scaledHeight = getScaledOffsetNodeHeight(tree, unscaledHeight);
                drawInternodeInterval(label, scaledHeight, p1, p2, g);
            }
        }
        g.setStroke(s);

        if (drawAxis) {

        }
    }

    void drawInternodeInterval(String label, double scaledNodeHeight, double p1, double p2, Graphics2D g) {
        draw(scaledNodeHeight, p1, scaledNodeHeight, p2, g);
        if (label != null)
            drawNode(label, scaledNodeHeight, p2, TikzRenderingHints.VALUE_NORTH, TikzRenderingHints.VALUE_scriptsize, g);
    }


    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Tree tree = treeDrawing.getTree();

        draw(treeDrawing, tree.getRoot(), g2d);
    }

    public static void main(String[] args) throws Exception {

        String newickTree = "((((1:0.1,2:0.1):0.1,3:0.2):0.1,4:0.3):0.1,5:0.4);";

        List<Sequence> sequences = new ArrayList<Sequence>();
        sequences.add(new Sequence("A", "A"));
        sequences.add(new Sequence("B", "A"));
        sequences.add(new Sequence("C", "A"));
        sequences.add(new Sequence("D", "A"));
        sequences.add(new Sequence("E", "A"));

        Alignment alignment = new Alignment(sequences, 4, "nucleotide");

        TreeComponent treeComponent = new SquareTreeComponent(new TreeDrawing(new TreeParser(alignment, newickTree)));

        TikzGraphics2D tikzGraphics2D = new TikzGraphics2D();
        treeComponent.setSize(new Dimension(100, 100));
        treeComponent.paintComponent(tikzGraphics2D);
        tikzGraphics2D.flush();

        //System.out.println(tikzGraphics2D.toString());
        //tikzGraphics2D.paintComponent(treeComponent);

//        JFrame frame = new JFrame("TreeComponent");
//        frame.getContentPane().add(treeComponent, BorderLayout.CENTER);
//        frame.setSize(new Dimension(800, 600));
//        frame.setVisible(true);
    }

    public void setTreeHeightScaleFactor(double scaleFactor) {
        nhs = scaleFactor;
    }
}

