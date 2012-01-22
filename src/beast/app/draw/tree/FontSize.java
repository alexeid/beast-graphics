package beast.app.draw.tree;

import org.jtikz.TikzRenderingHints;

/**
 * @author Alexei Drummond
 */
public enum FontSize {
    normalsize, footnotesize, scriptsize;


    public Object getTikzRenderingHint() {
        switch (this) {
            case scriptsize:
                return TikzRenderingHints.VALUE_scriptsize;
            case footnotesize:
                return TikzRenderingHints.VALUE_footnotesize;
            case normalsize:
            default:
                return TikzRenderingHints.VALUE_normalsize;
        }
    }
}
