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
    private JButton canvasBgBtn;
    private DrawingPanel panel;
    public String selectedTool = "Rectangle";

    public EditorFrame() {
        setTitle("MyEditor Pro — Графічний редактор");
        setSize(1350, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel status = new JLabel("  Готовий | Ctrl+Z — undo | G — сітка | Del — видалити");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        status.setBorder(new EmptyBorder(6, 12, 6, 12));
        status.setOpaque(true);
        status.setBackground(new Color(240, 242, 245));

        panel = new DrawingPanel(status);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(90, 0));
        sidebar.setBackground(new Color(28, 33, 40));
        sidebar.setBorder(new EmptyBorder(15, 12, 15, 12));

        JLabel toolsLabel = new JLabel("TOOLS");
        toolsLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        toolsLabel.setForeground(new Color(110, 120, 135));
        toolsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(toolsLabel);
        sidebar.add(Box.createVerticalStrut(10));

        String[][] tools = {
                {"Select", "↖", "S"}, {"Rectangle", "▭", "R"}, {"Circle", "○", "C"},
                {"Triangle", "△", "T"}, {"Line", "╱", "L"}, {"Arrow", "→", "A"},
                {"Pencil", "✎", "P"}, {"Eraser", "◻", "E"}, {"Text", "𝐓", "X"}, {"Picker", "⧩", "Color"}
        };

        for (String[] t : tools) {
            JButton btn = createSidebarButton(t[1], t[0], t[2]);
            toolButtons.put(t[0], btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
        }

        updateHighlight("Rectangle");

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(225, 230, 235)));

        colorPreviewBtn = createHeaderButton("● Колір об'єкта", Color.BLACK);
        colorPreviewBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Колір", panel.getCurrentColor());
            if (chosen != null) updateUIColors(chosen);
        });
        topBar.add(colorPreviewBtn);

        canvasBgBtn = createHeaderButton("🖼 Колір фону", Color.WHITE);
        canvasBgBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Фон", panel.getBackground());
            if (chosen != null) { panel.setBackground(chosen); canvasBgBtn.setBackground(chosen); }
        });
        topBar.add(canvasBgBtn);

        addSep(topBar);

        // ІЄРАРХІЧНЕ МЕНЮ ШАБЛОНІВ
        topBar.add(styledLabel("Шаблони:"));
        JMenuBar templateBar = new JMenuBar();
        templateBar.setBackground(Color.WHITE);
        JMenu templateMenu = new JMenu(" — Виберіть категорію — ");

        String[] cats = {"Візитка", "Постер", "Блок-схема", "Логотип", "Рамка"};
        for (String cat : cats) {
            JMenu sub = new JMenu(cat);
            for (int i = 1; i <= 5; i++) {
                final int v = i;
                JMenuItem item = new JMenuItem("Варіант " + i);
                item.addActionListener(e -> panel.loadSpecificTemplate(cat, v));
                sub.add(item);
            }
            templateMenu.add(sub);
        }
        templateBar.add(templateMenu);
        topBar.add(templateBar);

        topBar.add(Box.createHorizontalGlue());

        JButton saveBtn = createTopButton("💾 Зберегти", new Color(46, 204, 113));
        saveBtn.addActionListener(e -> panel.saveToFile());
        topBar.add(saveBtn);

        JButton clearBtn = createTopButton("🗑 Очистити", new Color(231, 76, 60));
        clearBtn.addActionListener(e -> panel.clear());
        topBar.add(clearBtn);

        add(sidebar, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    private JButton createHeaderButton(String t, Color bg) {
        JButton b = new JButton(t); b.setBackground(bg); b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(new CompoundBorder(new LineBorder(new Color(200, 205, 210)), new EmptyBorder(6, 12, 6, 12)));
        return b;
    }

    private JLabel styledLabel(String t) { return new JLabel(t); }

    private void addSep(JPanel p) {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(1, 26)); p.add(s);
    }

    private JButton createTopButton(String t, Color bg) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setBorder(new EmptyBorder(7, 15, 7, 15));
        return b;
    }

    private JButton createSidebarButton(String icon, String tool, String tip) {
        JButton b = new JButton(icon); b.setToolTipText(tip);
        b.setMaximumSize(new Dimension(66, 56)); b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        b.addActionListener(e -> { panel.setTool(tool); updateHighlight(tool); });
        return b;
    }

    private void updateHighlight(String tool) {
        toolButtons.forEach((name, btn) -> {
            btn.setBackground(name.equals(tool) ? accentColor : new Color(45, 52, 60));
            btn.setForeground(Color.WHITE);
        });
    }

    public void updateUIColors(Color c) {
        panel.setCurrentColor(c); colorPreviewBtn.setBackground(c); accentColor = c; updateHighlight(panel.selectedTool);
    }
}