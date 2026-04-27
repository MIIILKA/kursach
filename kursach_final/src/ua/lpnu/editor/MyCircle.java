package ua.lpnu.editor;
import java.awt.*;
public class MyCircle extends Shape {
    public MyCircle(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
    }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        java.awt.geom.AffineTransform old = g2d.getTransform();
        g2d.rotate(angle, x + width / 2.0, y + height / 2.0);
        if (filled) g2d.fillOval(x, y, width, height);
        else g2d.drawOval(x, y, width, height);
        g2d.setTransform(old);
        drawSelectionHandles(g2d);
    }
    @Override public boolean contains(int px, int py) {
        int cx = x + width/2, cy = y + height/2;
        int rx = Math.abs(width/2), ry = Math.abs(height/2);
        if (rx == 0 || ry == 0) return false;
        double nx = (double)(px - cx) / rx;
        double ny = (double)(py - cy) / ry;
        return nx*nx + ny*ny <= 1.0;
    }
}
