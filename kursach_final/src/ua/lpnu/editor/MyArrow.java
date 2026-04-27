package ua.lpnu.editor;
import java.awt.*;
import java.awt.geom.*;
public class MyArrow extends Shape {
    public MyArrow(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
    }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        // лінія стрілки
        g2d.drawLine(x, y, x + width, y + height);
        // голівка стрілки
        double angle = Math.atan2(height, width);
        int headLen = 18;
        double spread = Math.toRadians(25);
        int tx = x + width, ty = y + height;
        int ax1 = (int)(tx - headLen * Math.cos(angle - spread));
        int ay1 = (int)(ty - headLen * Math.sin(angle - spread));
        int ax2 = (int)(tx - headLen * Math.cos(angle + spread));
        int ay2 = (int)(ty - headLen * Math.sin(angle + spread));
        g2d.drawLine(tx, ty, ax1, ay1);
        g2d.drawLine(tx, ty, ax2, ay2);
        drawSelectionHandles(g2d);
    }
    @Override public boolean contains(int px, int py) {
        double dist = Line2D.ptSegDist(x, y, x+width, y+height, px, py);
        return dist < 8;
    }
}
