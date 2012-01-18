package beast.app.draw.tree;

/**
 * @author Alexei Drummond
 */
public class VerticalTreeComponent extends TreeComponent {

    public VerticalTreeComponent(TreeDrawing treeDrawing) {

        super(treeDrawing, 0, 0, true);
    }


    public VerticalTreeComponent(TreeDrawing treeDrawing, double nodeHeightScale, double nodeSpacingScale) {

        super(treeDrawing, nodeHeightScale, nodeSpacingScale, true);
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

    static String ladderTree(int size) {

        String tree = "(1:1, 2:1)";

        for (int i = 2; i < size; i++) {
            tree = "(" + tree + ":1," + (i + 1) + ":" + i + ")";
        }

        return tree + ";";
    }
}
