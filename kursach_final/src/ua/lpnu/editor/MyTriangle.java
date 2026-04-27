package ua.lpnu.editor;
import java.awt.*;
public class MyTriangle extends Shape {
    public MyTriangle(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
    }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        AffineTransformSave(g2d, () -> {
            g2d.rotate(angle, x + width / 2.0, y + height / 2.0);
            int[] xs = {x + width / 2, x, x + width};
            int[] ys = {y, y + height, y + height};
            if (filled) g2d.fillPolygon(xs, ys, 3);
            else g2d.drawPolygon(xs, ys, 3);
        });
        drawSelectionHandles(g2d);
    }
    private void AffineTransformSave(Graphics2D g2d, Runnable r) {
        java.awt.geom.AffineTransform old = g2d.getTransform();
        r.run();
        g2d.setTransform(old);
    }
}
