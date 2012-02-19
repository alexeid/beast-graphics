package beast.app.draw.tree;

import beast.core.Description;
import beast.core.Input;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeUtils;
import beast.evolution.tree.coalescent.TreeIntervals;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * @author Alexei Drummond
 */

@Description("Encapsulates a rooted tree and options to draw it within the context of a TreeDrawingGrid.")
public class RootedTreeDrawing extends AbstractTreeDrawing {

    enum TreeOrientation {up, down, left, right}
    enum TreeBranchStyle {line, square}
    enum NodePosition {average, triangulated, firstChild}

    public Input<TreeOrientation> treeOrientationInput = new Input<TreeOrientation>("orientation", "The orientation of the tree. Valid values are " +
            Arrays.toString(TreeOrientation.values()) + " (default 'right')", TreeOrientation.right, TreeOrientation.values());
    public Input<TreeBranchStyle> treeBranchStyleInput = new Input<TreeBranchStyle>("branchStyle", "The style to draw branches in. Valid values are " +
            Arrays.toString(TreeBranchStyle.values()) + " (default 'square')", TreeBranchStyle.square, TreeBranchStyle.values());
    public Input<NodePosition> nodePositionInput = new Input<NodePosition>("nodePositioning", "option for node positioning. Valid values are " +
            Arrays.toString(NodePosition.values()) + " (default 'average')", NodePosition.average, NodePosition.values());

    public Input<NodeTimesDecorator> leafTimesDecorator = new Input<NodeTimesDecorator>("leafTimesDecorator", "options for how to display leaf times");
    public Input<NodeTimesDecorator> internalNodeTimesDecorator = new Input<NodeTimesDecorator>("internalNodeTimesDecorator", "options for how to display internal node times");

    public Input<String> colorByTrait = new Input<String>("colorTrait", "The trait name to use for color index");
    public Input<ColorTable> traitColorTable = new Input<ColorTable>("traitColorTable", "The color table to map colorTrait index to colors");

    public Input<Boolean> rootAlignedInput = new Input<Boolean>("isRootAligned", "true if the trees should be aligned by root instead of tips (default is false).", false);
    
    private TreeIntervals treeIntervals;
    TreeComponent treeComponent;

    public RootedTreeDrawing() {
    }

    public RootedTreeDrawing(Tree tree) throws Exception {
        treeInput.setValue(tree, this);
        initAndValidate();
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
            case firstChild:
                treeComponent.positioningRule = NodePositioningRule.FIRST_CHILD;
        }

        treeComponent.leafDecorator = leafDecorator.get();
        treeComponent.internalNodeDecorator = internalNodeDecorator.get();

        if (leafTimesDecorator.get() != null) {
            treeComponent.leafTimesDecorator = leafTimesDecorator.get();
        }
        if (internalNodeTimesDecorator.get() != null) {
            treeComponent.internalNodeTimesDecorator = internalNodeTimesDecorator.get();
        }

        if (rotateTreeInput.get() == RotateTree.ladderizeLeft) {
            TreeUtils.rotateNodeByComparator(treeComponent.tree.getRoot(), TreeUtils.createReverseNodeDensityMinNodeHeightComparator());
        } else if (rotateTreeInput.get() == RotateTree.ladderizeRight) {
            TreeUtils.rotateNodeByComparator(treeComponent.tree.getRoot(), TreeUtils.createNodeDensityMinNodeHeightComparator());
        }

        String caption = captionInput.get();
        if (caption != null && !caption.equals("")) {
            treeComponent.setCaption(caption);
        }

        // escape underscores unless the name has a $ on either side
        for (Node child : getTree().getExternalNodes()) {

            String id = child.getID();

            if (id.contains("$") && id.substring(id.indexOf("$") + 1).contains("$")) {
                // DO NOTHING -- THERE IS AT LEAST ONE MATH REGION IN THE NAME
            } else {
                //escape the underscores as the name is not in a math context
                child.setID(child.getID().replace("_", "\\_"));
            }
        }

        if (colorByTrait.get() != null) {
            treeComponent.setColorTraitName(colorByTrait.get());
        }
        if (traitColorTable.get() != null) {
            treeComponent.setTraitColorTable(traitColorTable.get());
        }
    }

    @Override
    public void setBounds(Rectangle2D bounds) {
        treeComponent.setBounds(bounds);
    }

    @Override
    public void paintTree(Graphics2D g) {
        treeComponent.paint(g);
    }

    @Override
    public void setRootHeightForCanonicalScaling(double height) {
        treeComponent.rootHeightForScale = height;
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

    public boolean isRootAligned() {
        return rootAlignedInput.get();
    }

}
