package ua.lpnu.editor;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class MyRectangle extends Shape {
    public MyRectangle(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
    }

    @Override public void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.rotate(angle, x + width / 2.0, y + height / 2.0);

        if (filled) {
            // Додаємо м'який вертикальний градієнт
            GradientPaint gp = new GradientPaint(x, y, color, x, y + height, color.darker());
            g2d.setPaint(gp);
            g2d.fillRect(x, y, width, height);
        } else {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.drawRect(x, y, width, height);
        }

        g2d.setTransform(old);
        drawSelectionHandles(g2d);
    }
}