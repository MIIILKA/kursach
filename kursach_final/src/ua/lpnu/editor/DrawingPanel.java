package ua.lpnu.editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DrawingPanel extends JPanel {
    private List<Shape> shapes = new ArrayList<>();
    private Deque<List<Shape>> undoStack = new ArrayDeque<>();
    private Deque<List<Shape>> redoStack = new ArrayDeque<>();
    private Shape currentShape = null;
    public String selectedTool = "Rectangle";
    private Color currentColor = Color.BLACK;
    private float currentStroke = 2.0f;
    private boolean isFilled = false;
    private JLabel statusLabel;
    private Shape selectedShape = null;
    private int dragStartX, dragStartY;
    private boolean showGrid = false;
    private int textFontSize = 18;

    public DrawingPanel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
        setBackground(Color.WHITE);
        setFocusable(true);

        MouseAdapter adapter = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                dragStartX = e.getX(); dragStartY = e.getY();

                if (selectedTool.equals("Picker")) { pickColor(e.getX(), e.getY()); return; }
                if (selectedTool.equals("Select")) { doSelect(e.getX(), e.getY()); return; }
                if (selectedTool.equals("Text")) { doAddText(e.getX(), e.getY()); return; }

                saveUndo();
                switch (selectedTool) {
                    case "Rectangle": currentShape = new MyRectangle(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Circle":    currentShape = new MyCircle(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Line":      currentShape = new MyLine(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Pencil":    currentShape = new MyPencil(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Triangle":  currentShape = new MyTriangle(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Arrow":     currentShape = new MyArrow(e.getX(), e.getY(), currentColor, currentStroke, isFilled); break;
                    case "Eraser":    currentShape = new MyEraser(e.getX(), e.getY(), (int)currentStroke * 4 + 8); break;
                }
                if (currentShape != null) shapes.add(currentShape);
            }

            @Override public void mouseDragged(MouseEvent e) {
                if (selectedTool.equals("Select") && selectedShape != null) {
                    selectedShape.move(e.getX() - dragStartX, e.getY() - dragStartY);
                    dragStartX = e.getX(); dragStartY = e.getY();
                    repaint(); return;
                }
                if (currentShape != null) {
                    if (currentShape instanceof MyPencil) ((MyPencil) currentShape).addPoint(e.getX(), e.getY());
                    else if (currentShape instanceof MyEraser) ((MyEraser) currentShape).addPoint(e.getX(), e.getY());
                    else {
                        int w = e.getX() - currentShape.x;
                        int h = e.getY() - currentShape.y;
                        if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
                            int side = Math.max(Math.abs(w), Math.abs(h));
                            w = w < 0 ? -side : side;
                            h = h < 0 ? -side : side;
                        }
                        currentShape.setSize(w, h);
                    }
                    repaint();
                }
            }
            @Override public void mouseReleased(MouseEvent e) { currentShape = null; }
            @Override public void mouseMoved(MouseEvent e) {
                statusLabel.setText("  Координати: " + e.getX() + ", " + e.getY() + "   |   фігур: " + shapes.size());
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) pasteFromClipboard();
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) undo();
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) redo();
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) saveToFile();
                if (e.getKeyCode() == KeyEvent.VK_DELETE && selectedShape != null) deleteSelected();
                if (e.getKeyCode() == KeyEvent.VK_G) { showGrid = !showGrid; repaint(); }
            }
        });
    }

    private void doSelect(int x, int y) {
        if (selectedShape != null) selectedShape.selected = false;
        selectedShape = null;
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                selectedShape = shapes.get(i);
                selectedShape.selected = true;
                break;
            }
        }
        repaint();
    }

    private void doAddText(int x, int y) {
        String input = JOptionPane.showInputDialog(this, "Введіть текст:", "Текстовий інструмент", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            saveUndo();
            shapes.add(new MyText(x, y, currentColor, input.trim(), textFontSize));
            repaint();
        }
    }

    private void deleteSelected() {
        if (selectedShape == null) return;
        saveUndo();
        shapes.remove(selectedShape);
        selectedShape = null;
        repaint();
    }

    private void saveUndo() {
        undoStack.push(new ArrayList<>(shapes));
        redoStack.clear();
        if (undoStack.size() > 50) undoStack.pollLast();
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(new ArrayList<>(shapes));
        shapes = undoStack.pop();
        selectedShape = null;
        repaint();
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(new ArrayList<>(shapes));
        shapes = redoStack.pop();
        repaint();
    }

    public void saveToFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                paint(g); g.dispose();
                ImageIO.write(img, "png", fc.getSelectedFile());
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public void openImageFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fc.getSelectedFile());
                if (img != null) { saveUndo(); shapes.add(new MyImage(30, 30, img)); repaint(); }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void pickColor(int x, int y) {
        try {
            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            paintAll(g); g.dispose();
            Color picked = new Color(img.getRGB(x, y), true);
            setCurrentColor(picked);
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof EditorFrame) ((EditorFrame) win).updateUIColors(picked);
        } catch (Exception ex) {}
    }

    public void pasteFromClipboard() {
        try {
            java.awt.datatransfer.Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (cb.isDataFlavorAvailable(java.awt.datatransfer.DataFlavor.imageFlavor)) {
                Image img = (Image) cb.getData(java.awt.datatransfer.DataFlavor.imageFlavor);
                if (img != null) {
                    BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = bimg.createGraphics();
                    g.drawImage(img, 0, 0, null); g.dispose();
                    saveUndo();
                    shapes.add(new MyImage(50, 50, bimg));
                    repaint();
                }
            }
        } catch (Exception ex) {}
    }

    // НОВИЙ МЕТОД ДЛЯ МЕНЮ
    public void loadSpecificTemplate(String name, int variant) {
        saveUndo();
        shapes.clear();
        shapes.addAll(TemplateManager.createSpecificTemplate(name, variant));
        repaint();
    }

    public void setTool(String t) { this.selectedTool = t; if (selectedShape != null) { selectedShape.selected = false; selectedShape = null; repaint(); } }
    public void setCurrentColor(Color c) { this.currentColor = c; }
    public Color getCurrentColor() { return currentColor; }
    public void setStrokeWidth(float w) { this.currentStroke = w; }
    public void setFilled(boolean f) { this.isFilled = f; }
    public void setTextFontSize(int sz) { this.textFontSize = sz; }
    public void clear() { saveUndo(); shapes.clear(); selectedShape = null; repaint(); }
    public void toggleGrid() { showGrid = !showGrid; repaint(); }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (showGrid) drawGrid(g2d);
        for (Shape s : shapes) s.draw(g2d);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(220, 225, 235));
        int step = 25;
        for (int x = 0; x < getWidth(); x += step) g2d.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += step) g2d.drawLine(0, y, getWidth(), y);
    }
}