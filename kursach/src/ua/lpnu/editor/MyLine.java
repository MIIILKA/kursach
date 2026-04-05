package ua.lpnu.editor;
import java.awt.*;
public class MyLine extends Shape {
    public MyLine(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
    }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawLine(x, y, x + width, y + height);
    }
}