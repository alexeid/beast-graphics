package beast.app.draw.tree;

import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.Sequence;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.tree.coalescent.IntervalType;
import beast.evolution.tree.coalescent.TreeIntervals;
import beast.util.TreeParser;
import org.jtikz.TikzGraphics2D;
import org.jtikz.TikzRenderingHints;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Drummond
 */
public class TreeComponent extends JComponent {

    TreeDrawing treeDrawing;
    TreeDrawingOrientation orientation = TreeDrawingOrientation.UP;
    BranchStyle branchStyle = BranchStyle.SQUARE;

    Tree tree;

    // the position of the "current" leaf node
    private double p = 0;

    NumberFormat format = NumberFormat.getInstance();

    boolean isTriangle = true;

    double rootHeightForScale;
    private boolean drawAxis = true;

    private TreeDrawing.LeafShape leafShape;
    private double leafSize = 4;
    private Color leafColor = Color.white;

    private Rectangle2D bounds = new Rectangle2D.Double(0, 0, 1, 1);

    /**
     * @param treeDrawing the  tree drawing
     * @param isTriangle  true if tree should be drawn so that outside branches form a triangle on ultrametric trees
     */
    public TreeComponent(TreeDrawing treeDrawing, boolean isTriangle) {

        format.setMaximumFractionDigits(5);

        //this.scalebar = scalebar;
        this.isTriangle = isTriangle;

        this.treeDrawing = treeDrawing;
        this.tree = treeDrawing.getTree();

        rootHeightForScale = treeDrawing.getTree().getRoot().getHeight();

    }

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
        setSize((int) bounds.getWidth(), (int) bounds.getHeight());
    }

    void setTipValues(Tree tree, Node node) {
        if (node.isLeaf()) {
            node.setMetaData("p", p);
            node.setMetaData("pmin", p);
            node.setMetaData("pmax", p);
            p += getCanonicalNodeSpacing(tree);
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

    void drawLeafNode(Point2D p, Graphics2D g) {
        Shape shape = null;
        double halfSize = leafSize / 2.0;

        switch (leafShape) {
            case circle:
                shape = new Ellipse2D.Double(p.getX() - halfSize, p.getY() - halfSize, leafSize, leafSize);
                break;
            case square:
                shape = new Rectangle2D.Double(p.getX() - halfSize, p.getY() - halfSize, leafSize, leafSize);
                break;
            case triangle:
                Path2D path = new Path2D.Double();
                path.moveTo(p.getX(), p.getY() - halfSize);
                path.lineTo(p.getX() + halfSize, p.getY() + halfSize);
                path.lineTo(p.getX() - halfSize, p.getY() + halfSize);
                path.closePath();
                shape = path;
            default:
        }
        Color oldColor = g.getColor();
        g.setColor(leafColor);
        g.fill(shape);
        g.setColor(oldColor);
        g.draw(shape);

        //g.setFont(oldFont);
    }

    void drawString(String string, double x, double y, Object anchor, Object fontSize, Graphics2D g) {
        if (string != null) {
            Object oldAnchorValue = g.getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);
            Object oldFontSize = g.getRenderingHint(TikzRenderingHints.KEY_FONT_SIZE);
            g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, anchor);
            g.setRenderingHint(TikzRenderingHints.KEY_FONT_SIZE, fontSize);

            g.drawString(string, (float) x, (float) y);

            if (oldAnchorValue != null) g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, oldAnchorValue);
            if (oldFontSize != null) g.setRenderingHint(TikzRenderingHints.KEY_FONT_SIZE, oldFontSize);
        }

    }

    private boolean isDrawingBranchLabels(TreeDrawing treeDrawing) {
        return treeDrawing.getBranchLabels() != null && !treeDrawing.getBranchLabels().equals("");
    }

    private Point2D getCanonicalNodePoint2D(Node node) {
        return new Point2D.Double(getCanonicalNodeX(node), getCanonicalNodeY(node.getTree(), node.getHeight()));
    }

    private double getCanonicalNodeX(Node node) {
        return (Double) node.getMetaData("p");
    }

    private double getCanonicalNodeY(Tree tree, double height) {
        return height / tree.getRoot().getHeight();
    }


    private double getCanonicalNodeSpacing(Tree tree) {
        return 1.0 / (tree.getLeafNodeCount() - 1);
    }

    private Point2D getTransformedNodePoint2D(Node node) {
        return getTransformedPoint2D(getCanonicalNodePoint2D(node));
    }

    private Point2D getTransformedPoint2D(Point2D canonicalPoint2D) {
        return orientation.getTransform(bounds).transform(canonicalPoint2D, null);
    }

    final void drawBranch(Node node, Node childNode, Graphics2D g) {

        Shape shape = branchStyle.getBranchShape(getCanonicalNodePoint2D(childNode), getCanonicalNodePoint2D(node));
        Shape transformed = orientation.getTransform(bounds).createTransformedShape(shape);

        g.draw(transformed);
    }


    /**
     * Draws the label of a particular branch along the branch
     *
     * @param label     the label text
     * @param tree      the tree
     * @param node      the parent node
     * @param childNode the child node
     * @param fontSize  a hint about font size
     * @param g         the graphics object to draw to
     */
    final void drawBranchLabel(String label, Tree tree, Node node, Node childNode, Object fontSize, Graphics2D g) {

        Point2D p = getTransformedPoint2D(branchStyle.getCanonicalBranchLabelPoint2D(getCanonicalNodePoint2D(childNode), getCanonicalNodePoint2D(node)));

        drawString(label, p.getX(), p.getY(), orientation.getBranchLabelAnchor(), fontSize, g);
    }

    final void drawLeafLabel(Node node, Graphics2D g) {

        Point2D nodePoint = getTransformedNodePoint2D(node);

        drawString(node.getID(), nodePoint.getX(), nodePoint.getY(), orientation.getLeafLabelAnchor(), TikzRenderingHints.VALUE_normalsize, g);
    }

    void draw(TreeDrawing treeDrawing, Node node, Graphics2D g) {

        Tree tree = treeDrawing.getTree();

        if ((treeDrawing.showInternalNodeTimes() || treeDrawing.showLeafTimes()) && node.isRoot()) {
            drawNodeTimes(treeDrawing.getTreeIntervals(), treeDrawing.showInternalNodeTimes(), treeDrawing.showLeafTimes(), g);
        }

        g.setStroke(new BasicStroke((float) treeDrawing.getLineThickness()));

        p = 0.0; // canonical positioning goes from 0 to 1.

        if (node.isRoot()) {
            setTipValues(tree, node);
        }

        if (node.isLeaf()) {
            if (treeDrawing.showLeafNodes()) {
                //drawLeafNode(getTransformedPoint2D(node), g);
                drawLeafLabel(node, g);
            }
        } else {

            double cp = 0;
            if (isTriangle) {
                if (node.isRoot()) {
                    cp = 0.5;
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

                drawBranch(node, childNode, g);
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

    private void drawNodeTimes(TreeIntervals treeIntervals, boolean showInternalNodeTimes, boolean showLeafTimes, Graphics2D g) {

        Tree tree = treeIntervals.m_tree.get();
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{1.0f, 1.0f}, 0));

        double unscaledHeight = 0.0;

        double p1 = -0.1;
        double p2 = 1.1;

        String label = format.format(unscaledHeight);
        IntervalType oldIntervalType = IntervalType.SAMPLE;
        IntervalType newIntervalType;

        drawNodeTime(label, getCanonicalNodeY(tree, unscaledHeight), p1, p2, g);
        for (int i = 0; i < treeIntervals.getIntervalCount(); i++) {

            double interval = treeIntervals.getInterval(i);
            newIntervalType = treeIntervals.getIntervalType(i);

            unscaledHeight += interval;
            if ((newIntervalType == IntervalType.SAMPLE && showLeafTimes) || (newIntervalType == IntervalType.COALESCENT && showInternalNodeTimes)) {

                if (interval > 0.0 || (newIntervalType != oldIntervalType)) {
                    label = format.format(unscaledHeight);
                    drawNodeTime(label, getCanonicalNodeY(tree, unscaledHeight), p1, p2, g);
                }
            }
            oldIntervalType = newIntervalType;
        }
        g.setStroke(s);

        if (drawAxis) {

        }
    }

    void drawNodeTime(String label, double canonicalHeight, double pos1, double pos2, Graphics2D g) {
        Point2D p1 = getTransformedPoint2D(new Point2D.Double(pos1, canonicalHeight));
        Point2D p2 = getTransformedPoint2D(new Point2D.Double(pos2, canonicalHeight));

        g.draw(new Line2D.Double(p1, p2));
        drawString(label, p2.getX(), p2.getY(), orientation.getNodeHeightLabelAnchor(), TikzRenderingHints.VALUE_scriptsize, g);
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

        TreeComponent treeComponent = new TreeComponent(new TreeDrawing(new TreeParser(alignment, newickTree)), false);

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

    public void setLeafShape(TreeDrawing.LeafShape leafShape) {
        this.leafShape = leafShape;
    }

    public void setLeafColor(Color color) {
        leafColor = color;
    }

    public void setLeafSize(double leafSize) {
        this.leafSize = leafSize;
    }
}

