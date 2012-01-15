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
import java.util.*;
import java.util.List;

/**
 * @author Alexei Drummond
 */
public class TreeComponent extends JComponent {

    List<Tree> trees;
    int strideLength = 2;
    boolean isHorizontalStride = true;

    boolean oneScale = true;

    //the maximum root height
    private double maxRootHeight;

    // the position of the "current" leaf node
    private double p = 0;

    // offset of leaf node labels from leaf node position
    double labelOffset;

    //String newick;
    //String options = "ultra thick";
    //ScaleBar scalebar;
    NumberFormat format = NumberFormat.getInstance();

    boolean isTriangle = true;

    private boolean showInternodeIntervals = false;
    private Map<Tree, TreeIntervals> internodeIntervals = null;

    private Boolean showLeafLabels = true;

    String branchLabels = "";

    double treeHeight = 100;
    double treeWidth = 100;

    /**
     * The scaling of the node heights. If the scale is 0 then scale is automatically calculated from component size
     */
    double nhs = 0;

    /**
     * The scaling of the spacing between the nodes (The number of pixels between adjacent tip nodes). If the spacing is 0 then it is automatically calculated from component size
     */
    double ns = 0;

    double lineThickness = 1.0;


    /**
     * @param trees       the list of trees to draw
     * @param labelOffset the pixel labelOffset of labels from leaf nodes
     * @param isTriangle  true if tree should be drawn so that outside branches form a triangle on ultrametric trees
     */
    public TreeComponent(List<Tree> trees, double labelOffset, boolean isTriangle) {

        this(trees, 0, 0, labelOffset, isTriangle, false);

    }

    public TreeComponent(List<Tree> trees, double nodeHeightScale, double nodeSpacing, double labelOffset, boolean isTriangle, boolean showInternodeIntervals) {

        format.setMaximumFractionDigits(5);

        //this.scalebar = scalebar;
        this.isTriangle = isTriangle;
        setShowInternodeIntervals(showInternodeIntervals);

        this.labelOffset = labelOffset;
        this.nhs = nodeHeightScale;
        this.ns = nodeSpacing;

        this.trees = new ArrayList<Tree>();
        this.trees.addAll(trees);

        maxRootHeight = 0;
        for (Tree tree : trees) {
            if (tree.getRoot().getHeight() > maxRootHeight) {
                maxRootHeight = tree.getRoot().getHeight();
            }
        }

        setSize(calculateComponentWidth(), calculateComponentHeight());
    }

    void setShowInternodeIntervals(boolean showInternodeIntervals) {

        this.showInternodeIntervals = showInternodeIntervals;
        if (showInternodeIntervals && internodeIntervals == null) {
            internodeIntervals = new HashMap<Tree, TreeIntervals>();
            for (Tree tree : trees) {
                try {
                    internodeIntervals.put(tree, new TreeIntervals(tree));
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

//    public void addTree(Tree tree) {
//        trees.add(tree);
//    }

    public void setTreeHeight(double height) {
        System.out.println("Setting tree height to " + height);

        this.treeHeight = height;
        setSize(calculateComponentWidth(), calculateComponentHeight());
    }

    public void setTreeWidth(double width) {
        System.out.println("Setting tree width to " + width);
        this.treeWidth = width;
        setSize(calculateComponentWidth(), calculateComponentHeight());
    }

    private int calculateComponentHeight() {

        int height = 0;

        if (!isHorizontalStride) {
            height = (int) Math.ceil(strideLength * getTotalSizeForNodeSpacing());
        } else {
            height = (int) Math.ceil(calculateStrideCount() * getTotalSizeForNodeSpacing());
        }

        System.out.println("Calculated height is " + height);
        return height;

    }

    private int calculateComponentWidth() {
        int width = 0;

        if (!isHorizontalStride) {
            width = (int) Math.ceil(calculateStrideCount() * treeHeight);
        } else {

            width = (int) Math.ceil(strideLength * treeHeight);
        }

        System.out.println("Calculated width is " + width);
        return width;

    }

    private double calculateStrideCount() {
        return trees.size() / strideLength + (trees.size() % strideLength == 0 ? 0 : 1);
    }

    /**
     * @param unscaledNodeHeight the node height to scale
     * @return the position of this node in root-to-tip direction once scaled and labelOffset for component
     */
    double getScaledOffsetNodeHeight(Tree tree, double unscaledNodeHeight) {
        return getScaledTreeHeight() - unscaledNodeHeight * getNodeHeightScale(tree) + rootOffset();
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

    final double getNodeHeightScale(Tree tree) {

        if (nhs == 0) return getScaledTreeHeight() / (oneScale ? maxRootHeight : tree.getRoot().getHeight());
        return nhs;
    }

    double getScaledTreeHeight() {
        return 0.9 * treeWidth;
    }


    double getTotalSizeForNodeSpacing() {
        return treeHeight;
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

    void label(double x, double y, String label, Graphics2D g) {

        if (label != null) {
            Object oldHintValue = g.getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);
            g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, TikzRenderingHints.VALUE_CENTER);
            g.drawString(label, (float) x, (float) y);
            if (oldHintValue != null) g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, oldHintValue);
        }
    }

    void draw(double x1, double y1, double x2, double y2, Graphics2D g) {

        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    void drawNode(String label, double x, double y, Object anchor, double fontSize, Graphics2D g) {
        Object oldHintValue = g.getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);
        g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, anchor);
        Font oldFont = g.getFont();
        g.setFont(oldFont.deriveFont((float) fontSize));
        g.drawString(label, (float) x, (float) y);
        if (oldHintValue != null) g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, oldHintValue);
        g.setFont(oldFont);
    }

    void drawBranch(Tree tree, Node node, Node childNode, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double childHeight = getScaledOffsetNodeHeight(tree, childNode.getHeight());

        double position = getNodePosition(node);
        double childPosition = getNodePosition(childNode);

        if (branchLabels != null && !branchLabels.equals("")) {
            Object metaData = childNode.getMetaData(branchLabels);
            String branchLabel;
            if (metaData instanceof Number) {
                branchLabel = format.format(metaData);
            } else {
                branchLabel = metaData.toString();
            }
            drawNode(branchLabel, (height + childHeight) / 2, (position + childPosition) / 2, TikzRenderingHints.VALUE_SOUTH, 9.0, g);
        }
        draw(height, position, childHeight, childPosition, g);
    }

    void drawLabel(Tree tree, Node node, Graphics2D g) {

        double height = getScaledOffsetNodeHeight(tree, node.getHeight());
        double position = getNodePosition(node);

        label(height + labelOffset, position, node.getID(), g);
    }

    void draw(Tree tree, Node node, Graphics2D g) {

        if (showInternodeIntervals && node.isRoot()) {
            drawInternodeIntervals(internodeIntervals.get(tree), g);
        }

        g.setStroke(new BasicStroke((float) lineThickness));

        p = firstLeafNodePosition(tree);

        if (node.isRoot()) {
            setTipValues(tree, node);
        }

        if (node.isLeaf() && showLeafLabels) {
            drawLabel(tree, node, g);
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
                draw(tree, childNode, g);
                cp += (Double) childNode.getMetaData("p");
                count += 1;
            }
            cp /= count;
            if (!isTriangle) node.setMetaData("p", cp);

            for (Node childNode : node.getChildren()) {

                drawBranch(tree, node, childNode, g);
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

        drawInternodeInterval(getScaledOffsetNodeHeight(tree, unscaledHeight), p1, p2, g);
        for (double interval : intervals) {

            if (interval > 0.0) {
                unscaledHeight += interval;
                double scaledHeight = getScaledOffsetNodeHeight(tree, unscaledHeight);
                drawInternodeInterval(scaledHeight, p1, p2, g);
            }
        }
        g.setStroke(s);
    }

    void drawInternodeInterval(double scaledNodeHeight, double p1, double p2, Graphics2D g) {
        draw(scaledNodeHeight, p1, scaledNodeHeight, p2, g);
    }


    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        int count = 0;
        //double x = 0;
        //double y = 0;

        System.out.println("Drawing " + trees.size() + " trees");

        for (Tree tree : trees) {

            System.out.println("Drawing tree " + tree);

            draw(tree, tree.getRoot(), g2d);
            count += 1;
            if (count % strideLength == 0) {
                // next stride
                if (!isHorizontalStride) {
                    g2d.translate(treeHeight, -getTotalSizeForNodeSpacing() * (strideLength - 1));
                } else {
                    g2d.translate(-treeHeight * (strideLength - 1), getTotalSizeForNodeSpacing());
                }
            } else {
                if (!isHorizontalStride) {
                    g2d.translate(0, getTotalSizeForNodeSpacing());
                } else {
                    g2d.translate(treeHeight, 0);
                }
            }
        }
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

        double labelOffset = 5;

        TreeComponent treeComponent = new SquareTreeComponent(Arrays.asList(new Tree[]{new TreeParser(alignment, newickTree)}), labelOffset, false);

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

    public void setLineThickness(double lineThickness) {
        this.lineThickness = lineThickness;
    }

    public void setBranchLabelAttribute(String branchLabels) {
        this.branchLabels = branchLabels;
    }

    public void setShowLeafLabels(Boolean showLeafLabels) {
        this.showLeafLabels = showLeafLabels;
    }
}

