package beast.app.draw.tree;

import beast.evolution.tree.Node;

/**
 * @author Alexei Drummond
 */
public interface NodePositioningRule {

    enum TraversalOrder {PRE_ORDER, POST_ORDER}

    ;

    public TraversalOrder getTraversalOrder();

    void setPosition(Node node, String positionLabel);

    public NodePositioningRule AVERAGE_OF_CHILDREN = new NodePositioningRule() {
        @Override
        public TraversalOrder getTraversalOrder() {
            return TraversalOrder.POST_ORDER;
        }

        @Override
        public void setPosition(Node node, String positionLabel) {

            double averagePos = 0;
            int count = 0;
            for (Node child : node.getChildren()) {
                averagePos += (Double) child.getMetaData(positionLabel);
                count += 1;
            }

            averagePos /= (double) count;
            node.setMetaData(positionLabel, averagePos);
        }
    };

    public NodePositioningRule FIRST_CHILD = new NodePositioningRule() {
        @Override
        public TraversalOrder getTraversalOrder() {
            return TraversalOrder.POST_ORDER;
        }

        @Override
        public void setPosition(Node node, String positionLabel) {

            node.setMetaData(positionLabel, node.getChildren().get(0).getMetaData(positionLabel));
        }
    };


    public NodePositioningRule TRIANGULATED = new NodePositioningRule() {
        @Override
        public TraversalOrder getTraversalOrder() {
            return TraversalOrder.PRE_ORDER;
        }

        @Override
        public void setPosition(Node node, String positionLabel) {

            double py;
            if (node.isRoot()) {
                py = 0.5;
            } else {

                Node parent = node.getParent();

                double ppy = (Double) parent.getMetaData(positionLabel);
                double ph = parent.getHeight();
                double h = node.getHeight();

                double ymin = (Double) node.getMetaData(positionLabel + "_min");
                double ymax = (Double) node.getMetaData(positionLabel + "_max");

                double yminDist = Math.abs(ppy - ymin);
                double ymaxDist = Math.abs(ppy - ymax);

                if (yminDist > ymaxDist) {
                    py = ((ppy * h) + (ymin * (ph - h))) / ph;
                } else {
                    py = ((ppy * h) + (ymax * (ph - h))) / ph;
                }
            }
            node.setMetaData(positionLabel, py);
        }
    };
}
