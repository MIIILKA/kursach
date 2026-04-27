package ua.lpnu.editor;
import java.awt.*;
public class MyText extends Shape {
    private String text;
    private int fontSize;
    public MyText(int x, int y, Color color, String text, int fontSize) {
        super(x, y, color, 1f, false);
        this.text = text;
        this.fontSize = fontSize;
    }
    @Override public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        this.width = fm.stringWidth(text);
        this.height = fm.getHeight();
        g2d.drawString(text, x, y + fm.getAscent());
        drawSelectionHandles(g2d);
    }
    public String getText() { return text; }
    @Override public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
