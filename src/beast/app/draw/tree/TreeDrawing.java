package beast.app.draw.tree;

import beast.core.Description;
import beast.core.Input;
import beast.core.Plugin;
import beast.evolution.tree.Tree;
import beast.evolution.tree.coalescent.TreeIntervals;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Alexei Drummond
 */

@Description("Encapsulates a tree and options to draw it within the context of a TikzTreeFigure.")
public class TreeDrawing extends Plugin {

    enum TreeOrientation {up, down, left, right}

    enum TreeBranchStyle {triangle, square, vertical, unrooted}

    enum LeafShape {circle, square, triangle}


    public Input<Tree> treeInput = new Input<Tree>("tree", "a phylogenetic tree", Input.Validate.REQUIRED);
    public Input<Double> lineThicknessInput = new Input<Double>("lineThickness", "indicates the thickness of the lines", 1.0);
    public Input<Double> labelOffsetInput = new Input<Double>("labelOffset", "indicates the distance from leaf node to its label in pts", 5.0);
    public Input<Boolean> showLeafLabelsInput = new Input<Boolean>("showLeafLabels", "if true then the taxa labels are displayed", true);
    public Input<String> branchLabelsInput = new Input<String>("branchLabels", "the attribute name of values to display on the branches, or empty string if no branch labels to be displayed", "");
    public Input<Boolean> showInternalNodeTimes = new Input<Boolean>("showInternalNodeTimes", "if true then dotted lines are displayed at each internal node height.", false);
    public Input<Boolean> showLeafTimes = new Input<Boolean>("showLeafTimes", "if true then dotted lines are displayed at each unique leaf height.", true);
    public Input<TreeOrientation> treeOrientationInput = new Input<TreeOrientation>("orientation", "The orientation of the tree. Valid values are " +
            Arrays.toString(TreeOrientation.values()) + " (default 'right')", TreeOrientation.right, TreeOrientation.values());
    public Input<TreeBranchStyle> treeBranchStyleInput = new Input<TreeBranchStyle>("branchStyle", "The style to draw branches in. Valid values are " +
            Arrays.toString(TreeBranchStyle.values()) + " (default 'square')", TreeBranchStyle.square, TreeBranchStyle.values());
    public Input<LeafShape> leafShapeInput = new Input<LeafShape>("leafShape", "The shape to draw leaf nodes. Valid values are " +
            Arrays.toString(LeafShape.values()) + " (default 'square')", LeafShape.circle, LeafShape.values());
    public Input<String> leafColorInput = new Input<String>("leafColor", "The color to draw the shape", "255,255,255");
    public Input<Double> leafSizeInput = new Input<Double>("leafSize", "The size in points of the shape drawn at each leaf node", 5.0);


    private TreeIntervals treeIntervals;
    TreeComponent treeComponent;

    public TreeDrawing() {
    }

    public TreeDrawing(Tree tree) throws Exception {
        init(tree);
    }

    public TreeComponent getComponent() {
        return treeComponent;
    }

    public void initAndValidate() throws Exception {
        if (showInternalNodeTimes.get() || showLeafTimes.get()) {
            treeIntervals = new TreeIntervals(treeInput.get());
        }
        treeComponent = new TreeComponent(this, true);
        switch (treeOrientationInput.get()) {
            case up:
                treeComponent.transform = TreeDrawingTransform.UP;
                break;
            case down:
                treeComponent.transform = TreeDrawingTransform.DOWN;
                break;
            case left:
                treeComponent.transform = TreeDrawingTransform.LEFT;
                break;
            case right:
                treeComponent.transform = TreeDrawingTransform.RIGHT;
                break;
        }
        treeComponent.setLeafShape(leafShapeInput.get());

        String[] colorString = leafColorInput.get().split(",");
        if (colorString.length != 3)
            throw new Exception("The color string should be three values between 0-255 separated by commas");
        int red = Integer.parseInt(colorString[0]);
        int green = Integer.parseInt(colorString[0]);
        int blue = Integer.parseInt(colorString[0]);
        treeComponent.setLeafColor(new Color(red, green, blue));
        treeComponent.setLeafSize(leafSizeInput.get());

    }

    public Tree getTree() {
        return treeInput.get();
    }

    public TreeIntervals getTreeIntervals() {
        return treeIntervals;
    }

    public String getBranchLabels() {
        return branchLabelsInput.get();
    }

    public double getLabelOffset() {
        return labelOffsetInput.get();
    }

    public boolean showInternalNodeTimes() {
        return showInternalNodeTimes.get();
    }

    public boolean showLeafTimes() {
        return showLeafTimes.get();
    }

    public boolean showLeafNodes() {
        return showLeafLabelsInput.get();
    }

    public double getLineThickness() {
        return lineThicknessInput.get();
    }
}
