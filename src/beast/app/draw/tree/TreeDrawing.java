package beast.app.draw.tree;

import beast.core.Description;
import beast.core.Input;
import beast.core.Plugin;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeUtils;
import beast.evolution.tree.coalescent.TreeIntervals;

import java.util.Arrays;

/**
 * @author Alexei Drummond
 */

@Description("Encapsulates a tree and options to draw it within the context of a TikzTreeFigure.")
public class TreeDrawing extends Plugin {

    enum TreeOrientation {up, down, left, right}

    enum TreeBranchStyle {line, square}

    enum NodePosition {average, triangulated}

    enum RotateTree {ladderizeLeft, ladderizeRight}

    public Input<Tree> treeInput = new Input<Tree>("tree", "a phylogenetic tree", Input.Validate.REQUIRED);
    public Input<Double> lineThicknessInput = new Input<Double>("lineThickness", "indicates the thickness of the lines", 1.0);
    public Input<Boolean> showLeafLabelsInput = new Input<Boolean>("showLeafLabels", "if true then the leaf labels are shown.", true);

    public Input<Double> leafLabelOffsetInput = new Input<Double>("leafLabelOffset", "indicates the distance from leaf node to its label in pts", 5.0);
    public Input<String> branchLabelsInput = new Input<String>("branchLabels", "the attribute name of values to display on the branches, or empty string if no branch labels to be displayed", "");
    public Input<TreeOrientation> treeOrientationInput = new Input<TreeOrientation>("orientation", "The orientation of the tree. Valid values are " +
            Arrays.toString(TreeOrientation.values()) + " (default 'right')", TreeOrientation.right, TreeOrientation.values());
    public Input<TreeBranchStyle> treeBranchStyleInput = new Input<TreeBranchStyle>("branchStyle", "The style to draw branches in. Valid values are " +
            Arrays.toString(TreeBranchStyle.values()) + " (default 'square')", TreeBranchStyle.square, TreeBranchStyle.values());
    public Input<NodePosition> nodePositionInput = new Input<NodePosition>("nodePositioning", "option for node positioning. Valid values are " +
            Arrays.toString(NodePosition.values()) + " (default 'average')", NodePosition.average, NodePosition.values());

    public Input<NodeDecorator> leafDecorator = new Input<NodeDecorator>("leafDecorator", "options for how to draw the leaf nodes");
    public Input<NodeDecorator> internalNodeDecorator = new Input<NodeDecorator>("internalNodeDecorator", "options for how to draw the internal nodes");

    public Input<NodeTimesDecorator> leafTimesDecorator = new Input<NodeTimesDecorator>("leafTimesDecorator", "options for how to display leaf times");
    public Input<NodeTimesDecorator> internalNodeTimesDecorator = new Input<NodeTimesDecorator>("internalNodeTimesDecorator", "options for how to display internal node times");

    public Input<RotateTree> rotateTreeInput = new Input<RotateTree>("rotateTree", "if true then tree nodes are rotated by given criteria. Valid values are "
            + Arrays.toString(RotateTree.values()), RotateTree.ladderizeLeft, RotateTree.values());
    public Input<String> captionInput = new Input<String>("caption", "caption for tree figure", "");

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
        if (leafTimesDecorator.get() != null || internalNodeTimesDecorator.get() != null) {
            treeIntervals = new TreeIntervals(treeInput.get());
        }
        treeComponent = new TreeComponent(this);
        switch (treeOrientationInput.get()) {
            case up:
                treeComponent.orientation = TreeDrawingOrientation.UP;
                break;
            case down:
                treeComponent.orientation = TreeDrawingOrientation.DOWN;
                break;
            case left:
                treeComponent.orientation = TreeDrawingOrientation.LEFT;
                break;
            case right:
                treeComponent.orientation = TreeDrawingOrientation.RIGHT;
                break;
        }

        switch (treeBranchStyleInput.get()) {
            case square:
                treeComponent.branchStyle = BranchStyle.SQUARE;
                break;
            case line:
                treeComponent.branchStyle = BranchStyle.LINE;
                break;
        }

        switch (nodePositionInput.get()) {
            case average:
                treeComponent.positioningRule = NodePositioningRule.AVERAGE_OF_CHILDREN;
                break;
            case triangulated:
                treeComponent.positioningRule = NodePositioningRule.TRIANGULATED;
                break;
        }

        treeComponent.leafDecorator = leafDecorator.get();
        treeComponent.internalNodeDecorator = internalNodeDecorator.get();

        if (leafTimesDecorator.get() != null) {
            treeComponent.leafTimesDecorator = leafTimesDecorator.get();
        }
        if (internalNodeTimesDecorator.get() != null) {
            treeComponent.internalNodeTimesDecorator = internalNodeTimesDecorator.get();
        }

        if (rotateTreeInput.get() != null) {
            if (rotateTreeInput.get() == RotateTree.ladderizeLeft) {
                TreeUtils.rotateNodeByComparator(treeComponent.tree.getRoot(), TreeUtils.createReverseNodeDensityMinNodeHeightComparator());
            } else if (rotateTreeInput.get() == RotateTree.ladderizeRight) {
                TreeUtils.rotateNodeByComparator(treeComponent.tree.getRoot(), TreeUtils.createNodeDensityMinNodeHeightComparator());
            }
        }

        String caption = captionInput.get();
        if (caption != null && !caption.equals("")) {
            treeComponent.setCaption(caption);
        }
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

    public boolean showLeafLabels() {
        return showLeafLabelsInput.get();
    }


    public double getLineThickness() {
        return lineThicknessInput.get();
    }
}
