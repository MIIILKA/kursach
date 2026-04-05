package ua.lpnu.editor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MyImage extends Shape {
    private BufferedImage img;

    public MyImage(int x, int y, BufferedImage img) {
        super(x, y, Color.BLACK, 1, false);
        this.img = img;
        if (img != null) {
            this.width = img.getWidth();
            this.height = img.getHeight();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (img != null) {
            java.awt.geom.AffineTransform old = g2d.getTransform();
            g2d.rotate(angle, x + width / 2.0, y + height / 2.0);
            g2d.drawImage(img, x, y, null);
            g2d.setTransform(old);
        }
    }
}