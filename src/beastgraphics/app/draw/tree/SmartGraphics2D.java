package beastgraphics.app.draw.tree;

import beastgraphics.jtikz.TikzRenderingHints;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * @author Alexei Drummond
 */
public class SmartGraphics2D extends Graphics2D {
    
    Graphics2D parent;
    
    public SmartGraphics2D(java.awt.Graphics2D g) {
        this.parent = g;    
    }
    
    @Override
    public void draw(Shape shape) {
        parent.draw(shape);
    }

    @Override
    public boolean drawImage(Image image, AffineTransform affineTransform, ImageObserver imageObserver) {
        return parent.drawImage(image, affineTransform, imageObserver);
    }

    @Override
    public void drawImage(BufferedImage bufferedImage, BufferedImageOp bufferedImageOp, int x, int y) {
        parent.drawImage(bufferedImage,bufferedImageOp,x,y);
    }

    @Override
    public void drawRenderedImage(RenderedImage renderedImage, AffineTransform affineTransform) {
        parent.drawRenderedImage(renderedImage,affineTransform);
    }

    @Override
    public void drawRenderableImage(RenderableImage renderableImage, AffineTransform affineTransform) {
        parent.drawRenderableImage(renderableImage,affineTransform);
    }

    @Override
    public void drawString(String s, int x, int y) {
        
        this.drawString(s,(float)x,(float)y);
    }

    @Override
    public void drawString(String s, float x, float y) {
        Object nodeAnchorHint = getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);

        if (nodeAnchorHint != null) {
            Rectangle2D stringBounds = getFontMetrics().getStringBounds(s,parent);

            double stringWidth = stringBounds.getWidth();
            if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_SOUTH)) {
                parent.drawString(s,(float)(x - stringWidth/2.0),(float)(y - stringBounds.getMaxY()));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_SOUTH_EAST)) {
                parent.drawString(s,(float)(x - stringWidth),(float)(y - stringBounds.getMaxY()));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_WEST)) {
                parent.drawString(s,x,(float)(y - stringBounds.getMinY()/2.0));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_EAST)) {
                parent.drawString(s,(float)(x - stringWidth),(float)(y - stringBounds.getMinY()/2.0));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_NORTH)) {
                parent.drawString(s,(float)(x - stringWidth/2.0),(float)(y - stringBounds.getMinY()));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_CENTER)) {
                parent.drawString(s,(float)(x - stringWidth/2.0),(float)(y - stringBounds.getMinY()/2.0));
            }
            else {
                parent.drawString(s,x,y);
            }

        }
    }

    @Override
    public void drawString(AttributedCharacterIterator attributedCharacterIterator, int x, int y) {
        this.drawString(attributedCharacterIterator,(float)x,(float)y);
    }

    @Override
    public void drawString(AttributedCharacterIterator attributedCharacterIterator, float x, float y) {
        Object nodeAnchorHint = getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);

        if (nodeAnchorHint != null) {
            Rectangle2D stringBounds = getFontMetrics().getStringBounds(attributedCharacterIterator,(int)x,(int)y,parent);
            double stringWidth = stringBounds.getWidth();
            if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_SOUTH)) {
                parent.drawString(attributedCharacterIterator,(float)(x - stringWidth/2.0),(float)(y - stringBounds.getMaxY()));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_SOUTH_EAST)) {
                parent.drawString(attributedCharacterIterator,(float)(x - stringWidth),(float)(y - stringBounds.getMaxY()));
            }  else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_WEST)) {
                parent.drawString(attributedCharacterIterator,x,(float)(y + stringBounds.getMinY()/2.0));
            }   else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_EAST)) {
                parent.drawString(attributedCharacterIterator,(float)(x - stringWidth),(float)(y - stringBounds.getMinY()/2.0));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_NORTH)) {
                parent.drawString(attributedCharacterIterator,(float)(x - stringWidth/2.0),(float)(y - stringBounds.getMinY()));
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_CENTER)) {
                parent.drawString(attributedCharacterIterator,(float)(x - stringWidth/2.0),(float)(y - stringBounds.getMinY()/2.0));
            } else {
                parent.drawString(attributedCharacterIterator,x,y);
            }
        }
    }

    @Override
    public void drawGlyphVector(GlyphVector glyphVector, float x, float y) {
        Object nodeAnchorHint = getRenderingHint(TikzRenderingHints.KEY_NODE_ANCHOR);

        if (nodeAnchorHint != null) {
            Rectangle2D bounds = glyphVector.getVisualBounds();
            double width = bounds.getWidth();
            if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_SOUTH)) {
                parent.drawGlyphVector(glyphVector,(float)(x - width/2.0),y);
            } else if (nodeAnchorHint.equals(TikzRenderingHints.VALUE_SOUTH_EAST)) {
                parent.drawGlyphVector(glyphVector,(float)(x - width),y);
            }
        }
    }

    @Override
    public boolean drawImage(Image image, int x, int y, ImageObserver imageObserver) {
        return parent.drawImage(image,x,y,imageObserver);
    }

    @Override
    public boolean drawImage(Image image, int x, int y, int w, int h, ImageObserver imageObserver) {
        return parent.drawImage(image,x,y,w,h,imageObserver);
    }

    @Override
    public boolean drawImage(Image image, int x, int y, Color color, ImageObserver imageObserver) {
        return parent.drawImage(image,x,y,color,imageObserver);
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, Color color, ImageObserver imageObserver) {
        return parent.drawImage(image,i,i1,i2,i3,color,imageObserver);
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, ImageObserver imageObserver) {
        return parent.drawImage(image,i,i1,i2,i3,i4,i5,i6,i7,imageObserver);
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Color color, ImageObserver imageObserver) {
        return parent.drawImage(image,i,i1,i2,i3,i4,i5,i6,i7,color,imageObserver);
    }

    @Override
    public void dispose() {
        parent.dispose();
    }

    @Override
    public void fill(Shape shape) {
        parent.fill(shape);
    }

    @Override
    public boolean hit(Rectangle rectangle, Shape shape, boolean b) {
        return parent.hit(rectangle,shape,b);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return parent.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite composite) {
        parent.setComposite(composite);
    }

    @Override
    public void setPaint(Paint paint) {
        parent.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke stroke) {
        parent.setStroke(stroke);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key key, Object o) {
        parent.setRenderingHint(key,o);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key key) {
        return parent.getRenderingHint(key);
    }

    @Override
    public void setRenderingHints(Map<?, ?> map) {
        parent.setRenderingHints(map);
    }

    @Override
    public void addRenderingHints(Map<?, ?> map) {
        parent.addRenderingHints(map);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return parent.getRenderingHints();
    }

    @Override
    public Graphics create() {
        return parent.create();
    }

    @Override
    public void translate(int i, int i1) {
        parent.translate(i,i1);
    }

    @Override
    public Color getColor() {
        return parent.getColor();
    }

    @Override
    public void setColor(Color color) {
       parent.setColor(color);
    }

    @Override
    public void setPaintMode() {
        parent.setPaintMode();
    }

    @Override
    public void setXORMode(Color color) {
        parent.setXORMode(color);
    }

    @Override
    public Font getFont() {
        return parent.getFont();
    }

    @Override
    public void setFont(Font font) {
        parent.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return parent.getFontMetrics(font);
    }

    @Override
    public Rectangle getClipBounds() {
        return parent.getClipBounds();
    }

    @Override
    public void clipRect(int i, int i1, int i2, int i3) {
        parent.clipRect(i,i1,i2,i3);
    }

    @Override
    public void setClip(int i, int i1, int i2, int i3) {
        parent.setClip(i,i1,i2,i3);
    }

    @Override
    public Shape getClip() {
        return parent.getClip();
    }

    @Override
    public void setClip(Shape shape) {
        parent.setClip(shape);
    }

    @Override
    public void copyArea(int i, int i1, int i2, int i3, int i4, int i5) {
        parent.copyArea(i,i1,i2,i3,i4,i5);
    }

    @Override
    public void drawLine(int i, int i1, int i2, int i3) {
        parent.drawLine(i,i1,i2,i3);
    }

    @Override
    public void fillRect(int i, int i1, int i2, int i3) {
        parent.fillRect(i,i1,i2,i3);
    }

    @Override
    public void clearRect(int i, int i1, int i2, int i3) {
        parent.clearRect(i,i1,i2,i3);
    }

    @Override
    public void drawRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {
        parent.drawRoundRect(i,i1,i2,i3,i4,i5);
    }

    @Override
    public void fillRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {
        parent.fillRoundRect(i,i1,i2,i3,i4,i5);
    }

    @Override
    public void drawOval(int i, int i1, int i2, int i3) {
        parent.drawOval(i,i1,i2,i3);
    }

    @Override
    public void fillOval(int i, int i1, int i2, int i3) {
        parent.fillOval(i,i1,i2,i3);
    }

    @Override
    public void drawArc(int i, int i1, int i2, int i3, int i4, int i5) {
        parent.drawArc(i,i1,i2,i3,i4,i5);
    }

    @Override
    public void fillArc(int i, int i1, int i2, int i3, int i4, int i5) {
        parent.fillArc(i,i1,i2,i3,i4,i5);
    }

    @Override
    public void drawPolyline(int[] ints, int[] ints1, int i) {
        parent.drawPolyline(ints, ints1, i);
    }

    @Override
    public void drawPolygon(int[] ints, int[] ints1, int i) {
        parent.drawPolygon(ints, ints1, i);
    }

    @Override
    public void fillPolygon(int[] ints, int[] ints1, int i) {
        parent.fillPolygon(ints, ints1, i);
    }

    @Override
    public void translate(double v, double v1) {
        parent.translate(v,v1);
    }

    @Override
    public void rotate(double v) {
        parent.rotate(v);
    }

    @Override
    public void rotate(double v, double v1, double v2) {
        parent.rotate(v, v1, v2);
    }

    @Override
    public void scale(double v, double v1) {
        parent.scale(v,v1);
    }

    @Override
    public void shear(double v, double v1) {
        parent.shear(v,v1);
    }

    @Override
    public void transform(AffineTransform affineTransform) {
        parent.transform(affineTransform);
    }

    @Override
    public void setTransform(AffineTransform affineTransform) {
        parent.setTransform(affineTransform);
    }

    @Override
    public AffineTransform getTransform() {
        return parent.getTransform();
    }

    @Override
    public Paint getPaint() {
        return parent.getPaint();
    }

    @Override
    public Composite getComposite() {
        return parent.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        parent.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return parent.getBackground();
    }

    @Override
    public Stroke getStroke() {
        return parent.getStroke();
    }

    @Override
    public void clip(Shape shape) {
        parent.clip(shape);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return parent.getFontRenderContext();
    }
}
