package ua.lpnu.editor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class MyPencil extends Shape {
    private List<Point> points = new ArrayList<>();
    public MyPencil(int x, int y, Color color, float strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
        points.add(new Point(x, y));
    }
    public void addPoint(int x, int y) { points.add(new Point(x, y)); }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size() - 1; i++)
            g2d.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
    }
    @Override public boolean contains(int px, int py) { return false; }
}
