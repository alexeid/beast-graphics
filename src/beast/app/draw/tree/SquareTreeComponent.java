package beast.app.draw.tree;

import beast.util.TreeParser;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alexei Drummond
 */
public class SquareTreeComponent extends TreeComponent {

    public SquareTreeComponent(TreeDrawing treeDrawing) {

        super(treeDrawing, false);
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
