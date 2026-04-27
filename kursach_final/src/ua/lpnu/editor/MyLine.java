package ua.lpnu.editor;
import java.awt.*;
import java.awt.geom.Line2D;
public class MyLine extends Shape {
    public MyLine(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
    }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x, y, x + width, y + height);
        drawSelectionHandles(g2d);
    }
    @Override public boolean contains(int px, int py) {
        return Line2D.ptSegDist(x, y, x+width, y+height, px, py) < 8;
    }
}
