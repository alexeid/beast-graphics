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
                averagePos += (Double) child.getMetaData(positionLabel);
                count += 1;
            }

            averagePos /= (double) count;
            node.setMetaData(positionLabel, averagePos);
        }
    };

    public NodePositioningRule TRIANGULATED = new NodePositioningRule() {
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
