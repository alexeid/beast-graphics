package beast.app.draw.tree;

import beast.core.Description;
import beast.core.Input;
import beast.core.Runnable;
import org.jtikz.TikzGraphics2D;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Drummond
 */
@Description("Generates the tree figure in grid format, from input trees into Tikz/PGF format for addition to LaTeX document.")
public class TikzTreeFigure extends Runnable {

    public Input<List<TreeDrawing>> treeDrawing = new Input<List<TreeDrawing>>("treeDrawing", "a tree drawing", new ArrayList<TreeDrawing>());
    public Input<Integer> strideLength = new Input<Integer>("strideLength", "The number of trees to display in a 'stride' before wrapping to the next stride", 2);
    public Input<Boolean> isHorizontalStride = new Input<Boolean>("isHorizontalStride", "if true then strides are displayed horizontally, else vertically", true);
    public Input<Integer> width = new Input<Integer>("width", "the width of a grid cell in the figure", 150);
    public Input<Integer> height = new Input<Integer>("height", "the height of a grid cell in the figure", 150);
    public Input<String> fileName = new Input<String>("fileName", "the name of the file to write Tikz code to", "");
    public Input<String> pdflatexPath = new Input<String>("pdflatexPath", "the path to pdflatex; if provided then will be run automatically", "");

    public void initAndValidate() {
    }

    public void run() throws IOException, InterruptedException {
        TreeComponent treeComponent = new SquareTreeComponent(treeDrawing.get());
        treeComponent.setTreeWidth(width.get());
        treeComponent.setTreeHeight(height.get());
        treeComponent.strideLength = strideLength.get();
        treeComponent.isHorizontalStride = isHorizontalStride.get();

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
        treeComponent.paint(tikzGraphics2D);
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
