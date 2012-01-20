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

    enum FontSize {normalsize, footnotesize, scriptsize}

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
    public Input<NodeDecorator> leafDecorator = new Input<NodeDecorator>("leafDecorator", "options for how to draw the leaf nodes");
    public Input<NodeDecorator> internalNodeDecorator = new Input<NodeDecorator>("internalNodeDecorator", "options for how to draw the internal nodes");
    public Input<String> leafTimeLabelsInput = new Input<String>("leafTimeLabels", "labels for leaf times, comma-delimited");
    public Input<FontSize> leafTimeLabelsFontSizeInput =
            new Input<FontSize>("leafTimeLabelsFontSize", "font size of leaf time labels. Valid values are " +
                    Arrays.toString(FontSize.values()), FontSize.normalsize, FontSize.values());
    public Input<Boolean> rotateTreeInput = new Input<Boolean>("rotateTree", "if true then tree nodes are rotated by node density", false);

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
        treeComponent.leafDecorator = leafDecorator.get();
        treeComponent.internalNodeDecorator = internalNodeDecorator.get();

        if (leafTimeLabelsInput.get() != null) {
            String[] leafTimeLabels = leafTimeLabelsInput.get().split(",");
            treeComponent.setLeafTimeLabels(leafTimeLabels);
        }
        treeComponent.setLeafTimeLabelFontSize(leafTimeLabelsFontSizeInput.get());

        if (rotateTreeInput.get()) {
            TreeUtils.rotateNodeByComparator(treeComponent.tree.getRoot(), TreeUtils.createNodeDensityMinNodeHeightComparator());
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
