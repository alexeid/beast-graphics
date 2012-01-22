package beast.app.draw.tree;

import beast.core.Input;
import beast.core.Plugin;
import beast.evolution.tree.Tree;
import org.jtikz.TikzRenderingHints;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * @author Alexei Drummond
 */
public abstract class AbstractTreeDrawing extends Plugin {

    enum RotateTree {none, ladderizeLeft, ladderizeRight}

    public Input<Tree> treeInput = new Input<Tree>("tree", "a phylogenetic tree", Input.Validate.REQUIRED);
    public Input<Double> lineThicknessInput = new Input<Double>("lineThickness", "indicates the thickness of the lines", 1.0);
    public Input<Boolean> showLeafLabelsInput = new Input<Boolean>("showLeafLabels", "if true then the leaf labels are shown.", true);

    public Input<Double> leafLabelOffsetInput = new Input<Double>("leafLabelOffset", "indicates the distance from leaf node to its label in pts", 5.0);
    public Input<String> branchLabelsInput = new Input<String>("branchLabels", "the attribute name of values to display on the branches, or empty string if no branch labels to be displayed", "");

    public Input<NodeDecorator> leafDecorator = new Input<NodeDecorator>("leafDecorator", "options for how to draw the leaf nodes");
    public Input<NodeDecorator> internalNodeDecorator = new Input<NodeDecorator>("internalNodeDecorator", "options for how to draw the internal nodes");

    public Input<RotateTree> rotateTreeInput = new Input<RotateTree>("rotateTree", "policy for rotating tree nodes before drawing. Valid values are "
            + Arrays.toString(RotateTree.values()), RotateTree.none, RotateTree.values());
    public Input<String> captionInput = new Input<String>("caption", "caption for tree figure", "");

    public final Tree getTree() {
        return treeInput.get();
    }

    public final double getLeafLabelOffset() {
        return leafLabelOffsetInput.get();
    }

    public final double getLineThickness() {
        return lineThicknessInput.get();
    }

    public final String getCaption() {
        return captionInput.get();
    }

    public final void drawString(String string, double x, double y, Object anchor, Object fontSize, Graphics2D g) {
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

    public abstract void setBounds(Rectangle2D bounds);

    public abstract void paintTree(Graphics2D g);

    public abstract void setRootHeightForCanonicalScaling(double maxRootHeight);


}
