package beast.app.draw.tree;

import beast.core.Input;

import java.util.Arrays;

/**
 * @author Alexei Drummond
 */
public class UnrootedTreeDrawing extends TreeDrawing {

    public Input<Double> thetaInput = new Input<Double>("theta", "Initial direction of drawing", 0.0);

    public void initAndValidate() throws Exception {
        super.initAndValidate();
        treeComponent = new UnrootedTreeComponent(this);
        ((UnrootedTreeComponent) treeComponent).setTheta(thetaInput.get());
    }

}
