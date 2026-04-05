package ua.lpnu.editor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EditorFrame extends JFrame {
    private Color accentColor = new Color(52, 152, 219);
    private Map<String, JButton> toolButtons = new HashMap<>();
    private JButton colorPreviewBtn;
    private DrawingPanel panel;
    public String selectedTool = "Rectangle";

    public EditorFrame() {
        setTitle("MyEditor Pro - Микола Балюк");
        setSize(1250, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Налаштування швидкості появи підказок
        ToolTipManager.sharedInstance().setInitialDelay(200);

        JLabel status = new JLabel(" Готовий | Ctrl+V - вставка | Піпетка - вибір кольору");
        panel = new DrawingPanel(status);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(85, 0));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setBorder(new EmptyBorder(15, 10, 15, 10));

        // Масив: {ID інструменту, Символ, Текст підказки}
        String[][] tools = {
                {"Rectangle", "⬜", "Прямокутник"},
                {"Circle", "⭕", "Коло"},
                {"Line", "╱", "Лінія"},
                {"Pencil", "✎", "Олівець"},
                {"Picker", "⧩", "Піпетка (Вибір кольору)"}
        };

        for (String[] t : tools) {
            JButton btn = createSidebarButton(t[1], t[0], t[2]);
            toolButtons.put(t[0], btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        updateHighlight("Rectangle");

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(225, 225, 225)));

        topBar.add(new JLabel("Товщина:"));
        JSpinner strokeSpin = new JSpinner(new SpinnerNumberModel(2, 1, 30, 1));
        strokeSpin.addChangeListener(e -> panel.setStrokeWidth(((Number)strokeSpin.getValue()).floatValue()));
        topBar.add(strokeSpin);

        JCheckBox fillBox = new JCheckBox("Заливка");
        fillBox.addActionListener(e -> panel.setFilled(fillBox.isSelected()));
        topBar.add(fillBox);

        colorPreviewBtn = new JButton("Колір");
        colorPreviewBtn.setBackground(Color.BLACK);
        colorPreviewBtn.setForeground(Color.WHITE);
        colorPreviewBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Палітра", panel.getCurrentColor());
            if (chosen != null) updateUIColors(chosen);
        });
        topBar.add(colorPreviewBtn);

        JButton clearBtn = new JButton("Очистити 🗑");
        clearBtn.addActionListener(e -> panel.clear());
        topBar.add(Box.createHorizontalGlue());
        topBar.add(clearBtn);

        add(sidebar, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    private JButton createSidebarButton(String iconText, String tool, String tooltip) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(tooltip); // Підказка при наведенні
        btn.setMaximumSize(new Dimension(60, 60));
        btn.setPreferredSize(new Dimension(60, 60));
        btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            panel.setTool(tool);
            updateHighlight(tool);
        });
        return btn;
    }

    private void updateHighlight(String tool) {
        toolButtons.forEach((name, btn) -> {
            boolean sel = name.equals(tool);
            if (sel) {
                btn.setBackground(new Color(60, 70, 80));
                btn.setForeground(Color.WHITE);
                // Підсвітка рамки поточним вибраним кольором
                btn.setBorder(new LineBorder(accentColor, 2));
            } else {
                btn.setBackground(new Color(45, 50, 55));
                btn.setForeground(new Color(180, 180, 180));
                btn.setBorder(new LineBorder(new Color(60, 60, 60), 1));
            }
        });
    }

    public void updateUIColors(Color c) {
        panel.setCurrentColor(c);

        colorPreviewBtn.setBackground(c);
        // Адаптивний колір тексту (чорний на світлому, білий на темному)
        double bright = (c.getRed()*0.299 + c.getGreen()*0.587 + c.getBlue()*0.114)/255;
        colorPreviewBtn.setForeground(bright > 0.5 ? Color.BLACK : Color.WHITE);

        // Оновлюємо підсвітку кнопки, щоб рамка теж могла реагувати на колір (опціонально)
        updateHighlight(panel.selectedTool);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new EditorFrame().setVisible(true));
    }
}