package beast.app.draw.tree;

import org.jtikz.TikzRenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * @author Alexei Drummond
 */
public interface TreeDrawingTransform {

    AffineTransform getTransform(Rectangle2D bounds);

    Object getLeafLabelAnchor();

    Object getBranchLabelAnchor();

    TreeDrawingTransform UP = new TreeDrawingTransform() {

        @Override
        public AffineTransform getTransform(Rectangle2D bounds) {
            //canonical coordinates are node positions:left->right heights:top->bottom
            // for horizontal left-to-right we want:
            // node positions: top->bottom
            // heights: right->left

            AffineTransform scale = AffineTransform.getScaleInstance(bounds.getWidth(), bounds.getHeight());
            AffineTransform finalTranslate = AffineTransform.getTranslateInstance(bounds.getMinX(), bounds.getMinY());

            finalTranslate.concatenate(scale);
            return finalTranslate;
        }

        @Override
        public Object getLeafLabelAnchor() {
            return TikzRenderingHints.VALUE_SOUTH;
        }

        @Override
        public Object getBranchLabelAnchor() {
            return TikzRenderingHints.VALUE_EAST;
        }
    };

    TreeDrawingTransform RIGHT = new TreeDrawingTransform() {

        @Override
        public AffineTransform getTransform(Rectangle2D bounds) {
            //canonical coordinates are node positions:left->right heights:top->bottom
            // for horizontal left-to-right we want:
            // node positions: top->bottom
            // heights: right->left

            AffineTransform translate = AffineTransform.getTranslateInstance(-0.5, -0.5);
            AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI / 2.0);
            AffineTransform scale = AffineTransform.getScaleInstance(bounds.getWidth(), bounds.getHeight());
            AffineTransform finalTranslate = AffineTransform.getTranslateInstance(bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + bounds.getHeight() / 2.0);

            finalTranslate.concatenate(rotation);
            finalTranslate.concatenate(scale);
            finalTranslate.concatenate(translate);
            return finalTranslate;
        }

        @Override
        public Object getLeafLabelAnchor() {
            return TikzRenderingHints.VALUE_WEST;
        }

        @Override
        public Object getBranchLabelAnchor() {
            return TikzRenderingHints.VALUE_SOUTH;
        }
    };

    TreeDrawingTransform LEFT = new TreeDrawingTransform() {
        @Override
        public AffineTransform getTransform(Rectangle2D bounds) {

            AffineTransform translate = AffineTransform.getTranslateInstance(-0.5, -0.5);
            AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2.0);
            AffineTransform flipY = AffineTransform.getScaleInstance(1, -1);
            AffineTransform scale = AffineTransform.getScaleInstance(bounds.getWidth(), bounds.getHeight());
            AffineTransform finalTranslate = AffineTransform.getTranslateInstance(bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + bounds.getHeight() / 2.0);

            finalTranslate.concatenate(scale);
            finalTranslate.concatenate(flipY);
            finalTranslate.concatenate(rotation);
            finalTranslate.concatenate(translate);

            return finalTranslate;
        }

        @Override
        public Object getLeafLabelAnchor() {
            return TikzRenderingHints.VALUE_EAST;
        }

        @Override
        public Object getBranchLabelAnchor() {
            return TikzRenderingHints.VALUE_SOUTH;
        }
    };

    TreeDrawingTransform DOWN = new TreeDrawingTransform() {

        @Override
        public AffineTransform getTransform(Rectangle2D bounds) {
            //canonical coordinates are node positions:left->right heights:top->bottom
            // for horizontal left-to-right we want:
            // node positions: top->bottom
            // heights: right->left

            AffineTransform translate = AffineTransform.getTranslateInstance(-0.5, -0.5);
            AffineTransform flipY = AffineTransform.getScaleInstance(1, -1);
            AffineTransform scale = AffineTransform.getScaleInstance(bounds.getWidth(), bounds.getHeight());
            AffineTransform finalTranslate = AffineTransform.getTranslateInstance(bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + bounds.getHeight() / 2.0);
            finalTranslate.concatenate(scale);
            finalTranslate.concatenate(flipY);
            finalTranslate.concatenate(translate);

            return finalTranslate;
        }

        @Override
        public Object getLeafLabelAnchor() {
            return TikzRenderingHints.VALUE_NORTH;
        }

        @Override
        public Object getBranchLabelAnchor() {
            return TikzRenderingHints.VALUE_EAST;
        }

    };
}
