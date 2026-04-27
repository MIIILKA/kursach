package ua.lpnu.editor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class MyEraser extends Shape {
    private List<Point> points = new ArrayList<>();
    private int size;
    public MyEraser(int x, int y, int size) {
        super(x, y, Color.WHITE, size, true);
        this.size = size;
        points.add(new Point(x, y));
    }
    public void addPoint(int x, int y) { points.add(new Point(x, y)); }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size() - 1; i++)
            g2d.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
    }
}
