package beast.app.draw.tree;

import beast.evolution.tree.Node;

/**
 * @author Alexei Drummond
 */
public interface NodePositioningRule {
    
    void setPosition(Node node, String positionLabel);

    public NodePositioningRule AVERAGE_OF_CHILDREN = new NodePositioningRule() {
        @Override
        public void setPosition(Node node, String positionLabel) {

            double averagePos = 0;
            int count = 0;
            for (Node child : node.getChildren()) {
                averagePos += (Double)child.getMetaData(positionLabel);
                count += 1;
            }
            
            averagePos /= (double)count;
            node.setMetaData(positionLabel, averagePos);
        }
    };
}
