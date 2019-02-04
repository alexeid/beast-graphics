package beast.app.draw.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by adru001 on 4/02/19.
 */
public class CoalescentWithRecombination {

    public static Node simulate(int children, double coalescentRate, double recombinationRate, Random random) {

        List<Node> activeNodes = new ArrayList<>();

        for (int i = 0; i < children; i++) {
            Node active = new Node(0.0);
            active.addChild(new Node(0.0));
            activeNodes.add(active);
        }

        double time = 0;
        double dt;
        while (activeNodes.size() > 1) {
            double cRate = coalescentRate * activeNodes.size() * (activeNodes.size() - 1) / 2;
            double rRate = recombinationRate * activeNodes.size();

            double totalRate = cRate + rRate;

            dt = -Math.log(random.nextDouble()) / totalRate;

            if (random.nextDouble() < (cRate/totalRate)) {
                Node c1 = activeNodes.remove(random.nextInt(activeNodes.size()));
                Node c2 = activeNodes.remove(random.nextInt(activeNodes.size()));
                Node coalescentNode =  merge(c1,c2);
                coalescentNode.setTime(time+dt);
                Node active = new Node(time+dt);
                active.addChild(coalescentNode);
                activeNodes.add(active);

            } else {
                Node p1 = new Node(time + dt);
                Node p2 = new Node(time + dt);
                Node child = activeNodes.remove(random.nextInt(activeNodes.size()));
                child.setTime(time + dt);
                p1.addChild(child);
                p2.addChild(child);
                activeNodes.add(p1);
                activeNodes.add(p2);
            }
            time += dt;
        }
        return activeNodes.get(0);
    }

    private static Node merge(Node c1, Node c2) {

        List<Node> children = c2.removeChildren();
        for (Node child : children) {
            c1.addChild(child);
        }
        return c1;
    }
}
