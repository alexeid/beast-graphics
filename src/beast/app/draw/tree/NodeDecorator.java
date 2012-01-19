package beast.app.draw.tree;

import beast.core.Input;
import beast.core.Plugin;
import beast.evolution.tree.Node;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Alexei Drummond
 */
public class NodeDecorator extends Plugin {

    public Input<String> nodeColorInput = new Input<String>("nodeColor", "The color to fill the node", "255,255,0");
    public Input<Double> nodeSizeInput = new Input<Double>("nodeSize", "The size in points of the shape drawn to represent node", 5.0);
    public Input<NodeShape> nodeShapeInput = new Input<NodeShape>("nodeShape", "The shape to draw the node. Valid values are " +
            Arrays.toString(NodeShape.values()) + " (default 'circle')", NodeShape.circle, NodeShape.values());

    enum NodeShape {circle, square, triangle}
    Color nodeColor;

    public NodeDecorator() {}

    public NodeDecorator(Node node, String colorLabel, String sizeLabel, String shapeLabel) throws Exception {
        nodeColorInput.setValue(node.getMetaData(colorLabel),this);
        nodeSizeInput.setValue(node.getMetaData(sizeLabel),this);
        nodeShapeInput.setValue(node.getMetaData(shapeLabel),this);
        initAndValidate();
    }
    
    public void initAndValidate() throws Exception {
        nodeColor = getColorFromString(nodeColorInput.get());
    }
    
    private Color getColorFromString(String colorString) throws Exception {
        String[] colors = colorString.split(",");
        if (colors.length != 3)
            throw new Exception("The color string should be three values between 0-255 separated by commas");
        int red = Integer.parseInt(colors[0]);
        int green = Integer.parseInt(colors[1]);
        int blue = Integer.parseInt(colors[2]);
        return new Color(red, green, blue);
    }
    
    public Color getNodeColor() { return nodeColor;}
    public double getNodeSize() { return nodeSizeInput.get();}
    public NodeShape getNodeShape() { return nodeShapeInput.get();}
}
