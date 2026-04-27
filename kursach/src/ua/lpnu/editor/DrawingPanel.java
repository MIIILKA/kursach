package ua.lpnu.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JPanel {
    private List<Shape> shapes = new ArrayList<>();
    private Shape currentShape = null;
    public String selectedTool = "Rectangle";
    private Color currentColor = Color.BLACK;
    private float currentStroke = 2.0f;
    private boolean isFilled = false;
    private JLabel statusLabel;

    public DrawingPanel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
        setBackground(Color.WHITE);
        setFocusable(true);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (selectedTool.equals("Picker")) {
                    pickColor(e.getX(), e.getY());
                    return;
                }
                switch (selectedTool) {
                    case "Rectangle": currentShape = new MyRectangle(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Circle": currentShape = new MyCircle(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Line": currentShape = new MyLine(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Pencil": currentShape = new MyPencil(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                }
                if (currentShape != null) shapes.add(currentShape);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentShape != null && !(currentShape instanceof MyImage)) {
                    if (currentShape instanceof MyPencil) ((MyPencil)currentShape).addPoint(e.getX(), e.getY());
                    else currentShape.setSize(e.getX() - currentShape.x, e.getY() - currentShape.y);
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                statusLabel.setText(" Координати: " + e.getX() + ", " + e.getY());
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) pasteFromClipboard();
            }
        });
    }

    private void pickColor(int x, int y) {
        try {
            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            paintAll(g);
            g.dispose();
            Color picked = new Color(img.getRGB(x, y), true);
            setCurrentColor(picked);
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof EditorFrame) ((EditorFrame) win).updateUIColors(picked);
        } catch (Exception ex) {}
    }

    private void pasteFromClipboard() {
        try {
            java.awt.datatransfer.Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (cb.isDataFlavorAvailable(java.awt.datatransfer.DataFlavor.imageFlavor)) {
                Image img = (Image) cb.getData(java.awt.datatransfer.DataFlavor.imageFlavor);
                if (img != null) {
                    BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = bimg.createGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                    shapes.add(new MyImage(50, 50, bimg));
                    repaint();
                }
            }
        } catch (Exception ex) {}
    }

    public void setTool(String t) { this.selectedTool = t; }
    public void setCurrentColor(Color c) { this.currentColor = c; }
    public Color getCurrentColor() { return currentColor; }
    public void setStrokeWidth(float w) { this.currentStroke = w; }
    public void setFilled(boolean f) { this.isFilled = f; }
    public void clear() { shapes.clear(); repaint(); }
    public void rotateLastShape(double deg) {
        if (!shapes.isEmpty()) { shapes.get(shapes.size()-1).setAngle(deg); repaint(); }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Shape s : shapes) s.draw(g2d);
    }
}