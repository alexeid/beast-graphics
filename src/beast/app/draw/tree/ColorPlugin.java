package beast.app.draw.tree;

import beast.core.Input;
import beast.core.BEASTObject;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Alexei Drummond
 */
public class ColorPlugin extends BEASTObject {

    public Input<String> colorInput = new Input<String>("color", "The color. Valid values are " + Arrays.toString(ColorName.values()) + " (default 'black'). Comma-delimited triplets of 0-255 are also permitted (e.g '128,128,0')", "black");

    private Color color = Color.black;

    enum ColorName {

        black, blue, cyan, darkGray, gray, green, lightGray, magenta, orange, pink, red, white, yellow;

        public Color getColor() {
            switch (this) {
                case red:
                    return Color.red;
                case green:
                    return Color.green;
                case blue:
                    return Color.blue;
                case yellow:
                    return Color.yellow;
                case orange:
                    return Color.orange;
                case white:
                    return Color.white;
                case gray:
                    return Color.gray;
                case darkGray:
                    return Color.darkGray;
                case lightGray:
                    return Color.lightGray;
                case magenta:
                    return Color.magenta;
                case cyan:
                    return Color.cyan;
                case pink:
                    return Color.pink;
                case black:
                default:
                    return Color.black;
            }
        }
    }

    public ColorPlugin() {}

    public ColorPlugin(Color color) {
        this.color = color;
    }

    public void initAndValidate() throws Exception {
        color = getColorFromString(colorInput.get());
    }

    private Color getColorFromString(String colorString) throws Exception {
        String[] colors = colorString.split(",");
        if (colors.length != 3) {
            // try to interpret as ColorName
            return ColorName.valueOf(colorString).getColor();
        } else {
            int red = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue = Integer.parseInt(colors[2]);
            return new Color(red, green, blue);
        }
    }

    public Color getColor() {
        return color;
    }
}
