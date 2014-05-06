package beast.app.draw.tree;

import beast.core.Input;
import beast.core.BEASTObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Drummond
 */
public class ColorTable extends BEASTObject {
    
    public Input<List<ColorPlugin>> colorsInput = new Input<List<ColorPlugin>>("color","A list of colors in the color table", new ArrayList<ColorPlugin>());

    public ColorTable() {}

    public ColorTable(List<Color> colors)  {

        try {
            List<ColorPlugin> colorPlugins = new ArrayList<ColorPlugin>();
            for (Color color : colors) {
                colorPlugins.add(new ColorPlugin(color));
            }
            this.setInputValue("color", colorPlugins);
            initAndValidate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void initAndValidate() {}
    
    public Color getColor(int index) {
        return colorsInput.get().get(index).getColor();
    }
    
}
