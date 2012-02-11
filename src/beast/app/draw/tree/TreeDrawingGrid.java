package beast.app.draw.tree;

import beast.core.Description;
import beast.core.Input;
import beast.evolution.tree.Tree;
import org.jtikz.TikzGraphics2D;
import org.jtikz.TikzRenderingHints;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexei Drummond
 */
@Description("Generates the tree figure in grid format, from input trees into Tikz/PGF format for addition to LaTeX document.")
public class TreeDrawingGrid extends beast.core.Runnable {

    enum CaptionLocation {east, west, north, south}


    public Input<List<AbstractTreeDrawing>> treeDrawing = new Input<List<AbstractTreeDrawing>>("treeDrawing", "a tree drawing", new ArrayList<AbstractTreeDrawing>());
    public Input<Integer> strideLength = new Input<Integer>("strideLength", "The number of trees to display in a 'stride' before wrapping to the next stride", 2);
    public Input<Boolean> isHorizontalStride = new Input<Boolean>("isHorizontalStride", "if true then strides are displayed horizontally, else vertically", true);
    public Input<Integer> width = new Input<Integer>("width", "the width of a grid cell in the figure", 150);
    public Input<Integer> height = new Input<Integer>("height", "the height of a grid cell in the figure", 150);
    public Input<Boolean> oneScale = new Input<Boolean>("oneScale", "All trees are scaled to a single scale for node heights", false);
    public Input<Integer> columnSpacer = new Input<Integer>("columnSpacer", "The space in points between columns", 10);
    public Input<Integer> rowSpacer = new Input<Integer>("rowSpacer", "The space in points between rows", 10);
    public Input<String> fileName = new Input<String>("fileName", "the name of the file to write Tikz code to", "");
    public Input<Boolean> debug = new Input<Boolean>("debug", "if true then gray guidelines are drawn to assist debugging", false);
    public Input<String> pdflatexPath = new Input<String>("pdflatexPath", "the path to pdflatex; if provided then will be run automatically", "");
    public Input<CaptionLocation> captionLocationInput = new Input<CaptionLocation>("captionPosition", "the position of caption relative to tree bounds. Valid values are "
            + Arrays.toString(CaptionLocation.values()) + " (default 'south')", CaptionLocation.south, CaptionLocation.values());

    /**
     * The maximum root height of trees
     */
    private double maxRootHeight = 0;

    public void initAndValidate() {

        maxRootHeight = -Double.MAX_VALUE;
        for (AbstractTreeDrawing drawing : treeDrawing.get()) {

            Tree tree = drawing.getTree();
            if (tree.getRoot().getHeight() > maxRootHeight) {
                maxRootHeight = tree.getRoot().getHeight();
            }
        }
    }

    /**
     * Paints the subcomponents in a grid to the given graphics object
     */
    public void paint(Graphics2D g) {

        int cSpacer = columnSpacer.get();
        int rSpacer = rowSpacer.get();

        int count = 0;

        double x = 0;
        double y = 0;
        double w = width.get();
        double h = height.get();
        int s = strideLength.get();

        Rectangle2D bounds;

        for (AbstractTreeDrawing drawing : treeDrawing.get()) {

            //TreeComponent component = drawing.getComponent();
            bounds = new Rectangle2D.Double(x, y, w, h);

            drawing.setBounds(bounds);
            if (oneScale.get()) {
                drawing.setRootHeightForCanonicalScaling(maxRootHeight);
            }

            if (debug.get()) {
                g.setStroke(new BasicStroke(0.5f));
                g.setColor(Color.gray);
                g.draw(bounds);
                g.drawString(count + "", (float) bounds.getX(), (float) bounds.getY());
            }

            drawing.paintTree(g);
            String caption = drawing.getCaption();

            if (caption != null) {
                Object oldAnchorValue = g.getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);
                Object oldFontSize = g.getRenderingHint(TikzRenderingHints.KEY_FONT_SIZE);
                g.setRenderingHint(TikzRenderingHints.KEY_FONT_SIZE, TikzRenderingHints.VALUE_normalsize);

                g.setColor(Color.black);

                switch (captionLocationInput.get()) {
                    case east:
                        g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, TikzRenderingHints.VALUE_WEST);
                        g.drawString(caption, (float) bounds.getMaxX(), (float) bounds.getCenterY());
                        break;
                    case west:
                        g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, TikzRenderingHints.VALUE_EAST);
                        g.drawString(caption, (float) bounds.getMinX(), (float) bounds.getCenterY());
                        break;
                    case north:
                        g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, TikzRenderingHints.VALUE_CENTER);
                        g.drawString(caption, (float) bounds.getCenterX(), (float) (bounds.getMinY() - rowSpacer.get() * 0.45));
                        break;
                    case south:
                    default:
                        g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, TikzRenderingHints.VALUE_CENTER);
                        g.drawString(caption, (float) bounds.getCenterX(), (float) (bounds.getMaxY() + rowSpacer.get() * 0.45));
                }

                if (oldAnchorValue != null) g.setRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR, oldAnchorValue);
                if (oldFontSize != null) g.setRenderingHint(TikzRenderingHints.KEY_FONT_SIZE, oldFontSize);
            }


            count += 1;
            if (count % strideLength.get() == 0) {
                // next stride
                if (!isHorizontalStride.get()) {
                    x += w + cSpacer;
                    y -= (s - 1) * (h + rSpacer);
                } else {
                    x -= (s - 1) * (w + cSpacer);
                    y += h + rSpacer;
                }
            } else {
                if (!isHorizontalStride.get()) {
                    y += h + rSpacer;

                } else {
                    x += w + cSpacer;
                }
            }
        }
    }

    public void run() throws IOException, InterruptedException {

        String fileName = this.fileName.get();
        TikzGraphics2D tikzGraphics2D;

        PrintStream out = System.out;

        if (!fileName.equals("")) {
            out = new PrintStream(new FileOutputStream(fileName));
        }
        out.println("\\documentclass[12pt]{article}");
        out.println("\\usepackage{tikz,pgf}");
        out.println("\\begin{document}");

        tikzGraphics2D = new TikzGraphics2D(out);
        paint(tikzGraphics2D);
        tikzGraphics2D.flush();

        out.println("\\end{document}");
        out.flush();
        if (out != System.out) {
            out.close();

            String pdflatexPathString = pdflatexPath.get();
            if (!pdflatexPathString.equals("")) {
                String pdfFileName = fileName.substring(0, fileName.length() - 3) + "pdf";

                Process p = Runtime.getRuntime().exec(pdflatexPathString + " " + fileName);
                p.waitFor();
                Process p2 = Runtime.getRuntime().exec("open " + pdfFileName);
                p2.waitFor();
            }
        }
    }

}
