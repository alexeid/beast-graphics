package beast.app.draw.tree;

import beast.core.Input;
import beast.core.BEASTObject;

import java.util.Arrays;

/**
 * @author Alexei Drummond
 */
public class NodeTimesDecorator extends BEASTObject {

    public Input<Boolean> showNodeTimeLinesInput = new Input<Boolean>("showLines", "if true then dotted lines are displayed at each unique node height.", true);
    public Input<Boolean> showNodeTimeLabelsInput = new Input<Boolean>("showLabels", "if true then height is used to label each unique node height.", true);
    public Input<String> nodeTimeLabelsInput = new Input<String>("labels", "labels used to override default node time labels, comma-delimited");
    public Input<FontSize> nodeTimeLabelsFontSizeInput =
            new Input<FontSize>("fontSize", "font size of node time labels. Valid values are " +
                    Arrays.toString(FontSize.values()), FontSize.normalsize, FontSize.values());

    private int currentIndex = 0;


    public String getCurrentLabel(String defaultLabel) {
        if (nodeTimeLabelsInput.get() != null) return getNodeTimeLabels()[currentIndex];
        return defaultLabel;
    }

    public void resetCurrent() {
        currentIndex = 0;
    }

    public void incrementCurrent() {
        currentIndex += 1;
    }


    public void initAndValidate() {
    }

    public boolean showNodeTimeLines() {
        return showNodeTimeLinesInput.get();
    }

    public boolean showNodeTimeLabels() {
        return showNodeTimeLabelsInput.get();
    }

    public String[] getNodeTimeLabels() {
        String nodeTimeLabelString = nodeTimeLabelsInput.get();
        if (nodeTimeLabelString == null) return null;
        return nodeTimeLabelString.split(",");
    }

    public FontSize getNodeTimeLabelFontSize() {
        return nodeTimeLabelsFontSizeInput.get();
    }
}
