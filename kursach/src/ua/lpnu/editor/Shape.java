package ua.lpnu.editor;

import java.awt.*;

public abstract class Shape {
    protected int x, y, width, height;
    protected Color color;
    protected double angle = 0;
    protected float strokeWidth = 2.0f;
    protected boolean filled = false;

    public Shape(int x, int y, Color color, float strokeWidth, boolean filled) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.filled = filled;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setAngle(double degrees) {
        this.angle = Math.toRadians(degrees);
    }

    public abstract void draw(Graphics2D g2d);
}