package beast.app.draw.tree;

import beast.core.Description;
import beast.core.Input;
import beast.evolution.tree.Tree;
import org.jtikz.TikzGraphics2D;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Drummond
 */
@Description("Generates the tree figure in grid format, from input trees into Tikz/PGF format for addition to LaTeX document.")
public class TikzTreeGrid extends beast.core.Runnable {

    public Input<List<TreeDrawing>> treeDrawing = new Input<List<TreeDrawing>>("treeDrawing", "a tree drawing", new ArrayList<TreeDrawing>());
    public Input<Integer> strideLength = new Input<Integer>("strideLength", "The number of trees to display in a 'stride' before wrapping to the next stride", 2);
    public Input<Boolean> isHorizontalStride = new Input<Boolean>("isHorizontalStride", "if true then strides are displayed horizontally, else vertically", true);
    public Input<Integer> width = new Input<Integer>("width", "the width of a grid cell in the figure", 150);
    public Input<Integer> height = new Input<Integer>("height", "the height of a grid cell in the figure", 150);
    public Input<Boolean> oneScale = new Input<Boolean>("oneScale", "All trees are scaled to a single scale for node heights", false);
    public Input<String> fileName = new Input<String>("fileName", "the name of the file to write Tikz code to", "");
    public Input<String> pdflatexPath = new Input<String>("pdflatexPath", "the path to pdflatex; if provided then will be run automatically", "");

    private double xSpacer = 20;
    private double ySpacer = 10;

    /**
     * The maximum root height of trees
     */
    private double maxRootHeight;

    public void initAndValidate() {

        maxRootHeight = 0;
        for (TreeDrawing drawing : treeDrawing.get()) {

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

        int count = 0;
        for (TreeDrawing drawing : treeDrawing.get()) {

            TreeComponent component = drawing.getComponent();

            if (oneScale.get()) component.setTreeHeightScaleFactor(width.get() / maxRootHeight);
            component.setTreeHeight(height.get());
            component.setTreeWidth(width.get());

            component.paint(g);
            count += 1;
            if (count % strideLength.get() == 0) {
                // next stride
                if (!isHorizontalStride.get()) {
                    g.translate(height.get() + xSpacer, -(strideLength.get() - 1) * (width.get() + ySpacer));
                } else {
                    g.translate(-(strideLength.get() - 1) * (height.get() + xSpacer), width.get() + ySpacer);
                }
            } else {
                if (!isHorizontalStride.get()) {
                    g.translate(0, width.get() + ySpacer);
                } else {
                    g.translate(height.get() + xSpacer, 0);
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
