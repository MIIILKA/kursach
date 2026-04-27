package ua.lpnu.editor;

import java.awt.*;

public abstract class Shape {
    protected int x, y, width, height;
    protected Color color;
    protected double angle = 0;
    protected float strokeWidth = 2.0f;
    protected boolean filled = false;
    protected boolean selected = false;

    public Shape(int x, int y, Color color, float strokeWidth, boolean filled) {
        this.x = x; this.y = y;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.filled = filled;
    }

    public void setSize(int width, int height) { this.width = width; this.height = height; }
    public void setAngle(double degrees) { this.angle = Math.toRadians(degrees); }
    public void move(int dx, int dy) { this.x += dx; this.y += dy; }
    public void setSelected(boolean s) { this.selected = s; }

    public boolean contains(int px, int py) {
        int x1 = Math.min(x, x + width), y1 = Math.min(y, y + height);
        int x2 = Math.max(x, x + width), y2 = Math.max(y, y + height);
        return px >= x1 && px <= x2 && py >= y1 && py <= y2;
    }

    public abstract void draw(Graphics2D g2d);

    protected void drawSelectionHandles(Graphics2D g2d) {
        if (!selected) return;

        int x1 = Math.min(x, x + width), y1 = Math.min(y, y + height);
        int x2 = Math.max(x, x + width), y2 = Math.max(y, y + height);

        // Малюємо тонку рамку навколо
        g2d.setColor(new Color(53, 116, 240)); // Сучасний акцентний синій
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRect(x1 - 2, y1 - 2, (x2 - x1) + 4, (y2 - y1) + 4);

        // Малюємо вузли керування (Handles)
        int[] hx = {x1-4, (x1+x2)/2-4, x2-4, x1-4, x2-4, x1-4, (x1+x2)/2-4, x2-4};
        int[] hy = {y1-4, y1-4, y1-4, (y1+y2)/2-4, (y1+y2)/2-4, y2-4, y2-4, y2-4};

        for (int i = 0; i < hx.length; i++) {
            g2d.setColor(Color.WHITE);
            g2d.fillOval(hx[i], hy[i], 8, 8); // Круглі вузли виглядають краще
            g2d.setColor(new Color(53, 116, 240));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(hx[i], hy[i], 8, 8);
        }
    }
}