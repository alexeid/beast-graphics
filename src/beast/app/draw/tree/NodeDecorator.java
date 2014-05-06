package beast.app.draw.tree;

import beast.core.Input;
import beast.core.BEASTObject;
import beast.evolution.tree.Node;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Alexei Drummond
 */
public class NodeDecorator extends BEASTObject {

    public Input<ColorPlugin> colorInput = new Input<ColorPlugin>("fillColor", "The color to fill node");
    public Input<Double> nodeSizeInput = new Input<Double>("nodeSize", "The size in points of the shape drawn to represent node", 5.0);
    public Input<NodeShape> nodeShapeInput = new Input<NodeShape>("nodeShape", "The shape to draw the node. Valid values are " +
            Arrays.toString(NodeShape.values()) + " (default 'circle')", NodeShape.circle, NodeShape.values());

    enum NodeShape {circle, square, triangle}

    public static final NodeDecorator BLACK_DOT = new NodeDecorator() {
        public Color getNodeColor() {
            return Color.black;
        }

        public double getNodeSize() {
            return 4;
        }

        public NodeShape getNodeShape() {
            return NodeShape.circle;
        }

        public boolean drawNodeShape() {
            return false;
        }
    };


    public NodeDecorator() {
    }

    public NodeDecorator(Node node, Color color, String sizeLabel, String shapeLabel) throws Exception {
        colorInput.setValue(new ColorPlugin(color), this);
        nodeSizeInput.setValue(node.getMetaData(sizeLabel), this);
        nodeShapeInput.setValue(node.getMetaData(shapeLabel), this);
        initAndValidate();
    }

    public void initAndValidate() {}

    public Color getNodeColor() {
        return colorInput.get().getColor();
    }

    public double getNodeSize() {
        return nodeSizeInput.get();
    }

    public NodeShape getNodeShape() {
        return nodeShapeInput.get();
    }

    public boolean drawNodeShape() {
        return true;
    }
}
