package ua.lpnu.editor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class EditorFrame extends JFrame {

    // ── Палітра ───────────────────────────────────────────────────────────────
    static final Color BG_DEEP    = new Color(6,   8,   16);
    static final Color BG_PANEL   = new Color(10,  13,  24);
    static final Color BG_SURFACE = new Color(15,  19,  35);
    static final Color BG_CARD    = new Color(20,  26,  46);
    static final Color CYAN       = new Color(0,   220, 255);
    static final Color VIOLET     = new Color(120, 40,  220);
    static final Color PINK       = new Color(255, 50,  180);
    static final Color GREEN      = new Color(0,   255, 150);
    static final Color TEXT_HI    = new Color(210, 225, 255);
    static final Color TEXT_LO    = new Color(70,  90,  130);
    static final Color BORDER     = new Color(30,  40,  70);

    private final Map<String, ToolBtn> toolButtons = new LinkedHashMap<>();
    private JButton colorDot;
    DrawingPanel panel;
    public String selectedTool = "Rectangle";

    // drag undecorated window
    private int dragX, dragY;

    public EditorFrame() {
        setUndecorated(true);
        setSize(1480, 960);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        applyTheme();

        JLabel status = buildStatus();
        panel = new DrawingPanel(status);
        panel.setBackground(BG_DEEP);

        AnimatedCanvas aCanvas = new AnimatedCanvas(panel);
        JPanel sidebar  = buildSidebar();
        JPanel titlebar = buildTitlebar();
        JPanel floatbar = buildFloatBar();

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_DEEP);
        center.add(floatbar, BorderLayout.NORTH);
        center.add(aCanvas,  BorderLayout.CENTER);
        center.add(status,   BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DEEP);
        root.setBorder(new LineBorder(new Color(0, 220, 255, 55), 1));
        root.add(titlebar, BorderLayout.NORTH);
        root.add(sidebar,  BorderLayout.WEST);
        root.add(center,   BorderLayout.CENTER);
        setContentPane(root);

        // drag вікна
        titlebar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragX = e.getX(); dragY = e.getY(); }
        });
        titlebar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - dragX, getY() + e.getY() - dragY);
            }
        });
    }

    // ── Глобальна тема ────────────────────────────────────────────────────────
    private void applyTheme() {
        UIManager.put("PopupMenu.background",           BG_SURFACE);
        UIManager.put("Menu.background",                BG_SURFACE);
        UIManager.put("MenuItem.background",            BG_CARD);
        UIManager.put("PopupMenu.foreground",           TEXT_HI);
        UIManager.put("Menu.foreground",                TEXT_HI);
        UIManager.put("MenuItem.foreground",            TEXT_HI);
        UIManager.put("MenuItem.selectionBackground",   new Color(0, 220, 255, 35));
        UIManager.put("MenuItem.selectionForeground",   CYAN);
        UIManager.put("Menu.selectionBackground",       new Color(0, 220, 255, 35));
        UIManager.put("Menu.selectionForeground",       CYAN);
        UIManager.put("MenuBar.background",             BG_SURFACE);
        UIManager.put("MenuBar.foreground",             TEXT_HI);
        UIManager.put("MenuBar.border",                 BorderFactory.createEmptyBorder());
        UIManager.put("TextField.background",           BG_CARD);
        UIManager.put("TextField.foreground",           TEXT_HI);
        UIManager.put("TextField.caretForeground",      CYAN);
        UIManager.put("OptionPane.background",          BG_SURFACE);
        UIManager.put("Panel.background",               BG_SURFACE);
        UIManager.put("OptionPane.messageForeground",   TEXT_HI);
    }

    // ── Titlebar ──────────────────────────────────────────────────────────────
    private JPanel buildTitlebar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_PANEL);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // cyan glow лінія знизу — розходиться від центру
                int cx = getWidth() / 2;
                g2.setPaint(new GradientPaint(0, getHeight()-2, new Color(0,220,255,0),
                        cx, getHeight()-2, new Color(0,220,255,180)));
                g2.fillRect(0, getHeight()-2, cx, 2);
                g2.setPaint(new GradientPaint(cx, getHeight()-2, new Color(0,220,255,180),
                        getWidth(), getHeight()-2, new Color(0,220,255,0)));
                g2.fillRect(cx, getHeight()-2, cx, 2);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 40));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        left.setOpaque(false);
        JLabel logo = new JLabel("VECTOR·STUDIO");
        logo.setFont(new Font("Consolas", Font.BOLD, 14));
        logo.setForeground(CYAN);
        JLabel ed = new JLabel("/ 2030 EDITION");
        ed.setFont(new Font("Consolas", Font.PLAIN, 11));
        ed.setForeground(TEXT_LO);
        left.add(logo); left.add(ed);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 7));
        right.setOpaque(false);
        right.add(winBtn("–", new Color(255,200,0),  e -> setState(ICONIFIED)));
        right.add(winBtn("□", new Color(0,200,120),  e -> setExtendedState(
                getExtendedState() == MAXIMIZED_BOTH ? NORMAL : MAXIMIZED_BOTH)));
        right.add(winBtn("✕", new Color(255,60,80),  e -> System.exit(0)));

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton winBtn(String icon, Color col, ActionListener al) {
        JButton b = new JButton(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isRollover()
                        ? new Color(col.getRed(), col.getGreen(), col.getBlue(), 200)
                        : new Color(col.getRed(), col.getGreen(), col.getBlue(), 55);
                g2.setColor(fill);
                g2.fillOval(2, 2, getWidth()-4, getHeight()-4);
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 140));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(2, 2, getWidth()-5, getHeight()-5);
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(col.darker());
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(26, 26));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(Color.WHITE); b.repaint(); }
            public void mouseExited(MouseEvent e)  { b.setForeground(col.darker()); b.repaint(); }
        });
        return b;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_PANEL);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER);
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.setPaint(new GradientPaint(0,0,new Color(0,220,255,90),0,getHeight(),new Color(120,40,220,40)));
                g2.fillRect(0, 0, 2, getHeight());
            }
        };
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(68, 0));
        sb.setBorder(new EmptyBorder(12, 6, 12, 6));

        sb.add(sectionLbl("DRAW"));
        String[][] draw = {
                {"Select","⊹"},{"Rectangle","▭"},{"Circle","◯"},
                {"Triangle","△"},{"Line","╱"},{"Arrow","↗"},
                {"Pencil","✏"},{"Eraser","⌫"}
        };
        for (String[] t : draw) {
            ToolBtn btn = new ToolBtn(t[1], t[0], this);
            toolButtons.put(t[0], btn);
            sb.add(btn); sb.add(Box.createVerticalStrut(4));
        }
        sb.add(Box.createVerticalStrut(8));
        sb.add(sectionLbl("MORE"));
        String[][] more = {{"Text","T"},{"Picker","⧩"}};
        for (String[] t : more) {
            ToolBtn btn = new ToolBtn(t[1], t[0], this);
            toolButtons.put(t[0], btn);
            sb.add(btn); sb.add(Box.createVerticalStrut(4));
        }
        updateHighlight("Rectangle");
        return sb;
    }

    private JLabel sectionLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Consolas", Font.BOLD, 9));
        l.setForeground(TEXT_LO);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(6,0,8,0));
        return l;
    }

    // ── Floating toolbar ──────────────────────────────────────────────────────
    private JPanel buildFloatBar() {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 19, 40, 235));
                g2.fillRoundRect(8, 4, getWidth()-16, getHeight()-8, 16, 16);
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(0, 220, 255, 60));
                g2.drawRoundRect(8, 4, getWidth()-17, getHeight()-9, 16, 16);
                g2.setPaint(new GradientPaint(8,4,new Color(0,220,255,14),8,getHeight(),new Color(120,40,220,7)));
                g2.fillRoundRect(8, 4, getWidth()-16, getHeight()-8, 16, 16);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 12));

        // Колір об'єкта
        colorDot = dotBtn(CYAN);
        colorDot.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Колір об'єкта", panel.getCurrentColor());
            if (c != null) updateUIColors(c);
        });
        bar.add(colorDot); bar.add(barLbl("Колір"));
        bar.add(sep());

        // Фон
        JButton bgBtn = dotBtn(BG_DEEP);
        bgBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Колір фону", panel.getBackground());
            if (c != null) { panel.setBackground(c); bgBtn.setBackground(c); bgBtn.repaint(); }
        });
        bar.add(bgBtn); bar.add(barLbl("Фон"));
        bar.add(sep());

        // Товщина
        bar.add(barLbl("Товщина:"));
        JComboBox<String> sw = combo(new String[]{"1px","2px","3px","5px","8px","12px"});
        sw.setSelectedIndex(1);
        sw.addActionListener(e -> panel.setStrokeWidth(Float.parseFloat(
                ((String)sw.getSelectedItem()).replace("px",""))));
        bar.add(sw);
        bar.add(sep());

        // Заливка
        JCheckBox fill = check("Заливка");
        fill.addActionListener(e -> panel.setFilled(fill.isSelected()));
        bar.add(fill);
        bar.add(sep());

        // Шаблони
        bar.add(barLbl("Шаблони:"));
        bar.add(buildTemplates());
        bar.add(sep());

        // Ghost кнопки
        bar.add(ghost("⊞ Grid",  e -> panel.toggleGrid()));
        bar.add(ghost("↩ Undo",  e -> panel.undo()));
        bar.add(ghost("↪ Redo",  e -> panel.redo()));
        bar.add(sep());

        // Action кнопки
        bar.add(glow("⬇ Зберегти", GREEN,                 e -> panel.saveToFile()));
        bar.add(glow("✕ Очистити", new Color(255,50,80),  e -> panel.clear()));

        return bar;
    }

    private JButton dotBtn(Color col) {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getBackground();
                g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),55));
                g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(c);
                g2.fillOval(3,3,getWidth()-6,getHeight()-6);
                g2.setStroke(new BasicStroke(1.4f));
                g2.setColor(new Color(255,255,255,80));
                g2.drawOval(3,3,getWidth()-7,getHeight()-7);
            }
        };
        b.setBackground(col);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(28,28));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JMenuBar buildTemplates() {
        JMenuBar mb = new JMenuBar();
        mb.setOpaque(false);
        mb.setBorder(BorderFactory.createEmptyBorder());
        JMenu m = new JMenu("▾ Вибрати");
        m.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        m.setForeground(TEXT_HI);
        m.setOpaque(false);
        for (String cat : new String[]{"Візитка","Постер","Блок-схема","Логотип","Рамка"}) {
            JMenu sub = new JMenu(cat);
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sub.setForeground(TEXT_HI);
            for (int i = 1; i <= 5; i++) {
                final int v = i;
                JMenuItem item = new JMenuItem("Варіант " + i);
                item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                item.addActionListener(e -> panel.loadSpecificTemplate(cat, v));
                sub.add(item);
            }
            m.add(sub);
        }
        mb.add(m);
        return mb;
    }

    private JLabel barLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(TEXT_LO);
        return l;
    }

    private JSeparator sep() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(1, 22));
        s.setForeground(BORDER);
        return s;
    }

    private JCheckBox check(String t) {
        JCheckBox cb = new JCheckBox(t);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setForeground(TEXT_HI);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_HI);
        cb.setFocusable(false);
        cb.setPreferredSize(new Dimension(68, 28));
        cb.setBorder(BorderFactory.createLineBorder(BORDER));
        return cb;
    }

    private JButton ghost(String t, ActionListener al) {
        JButton b = new JButton(t) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0,220,255,22));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(new Color(0,220,255,70));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                }
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(TEXT_HI);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(3,10,3,10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.repaint(); }
            public void mouseExited(MouseEvent e)  { b.repaint(); }
        });
        return b;
    }

    private JButton glow(String t, Color col, ActionListener al) {
        JButton b = new JButton(t) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hot = getModel().isRollover();
                if (hot) {
                    g2.setColor(new Color(col.getRed(),col.getGreen(),col.getBlue(),40));
                    g2.fillRoundRect(-4,-4,getWidth()+8,getHeight()+8,16,16);
                }
                GradientPaint gp = new GradientPaint(0,0,
                        new Color(col.getRed(),col.getGreen(),col.getBlue(), hot?230:175),
                        getWidth(),getHeight(),
                        new Color(Math.min(col.getRed()+40,255),col.getGreen(),Math.min(col.getBlue()+40,255), hot?230:175));
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(5,14,5,14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.repaint(); }
            public void mouseExited(MouseEvent e)  { b.repaint(); }
        });
        return b;
    }

    // ── Status bar ────────────────────────────────────────────────────────────
    private JLabel buildStatus() {
        JLabel l = new JLabel("  READY  |  Ctrl+Z undo  |  Ctrl+Y redo  |  G grid  |  Del remove  |  Ctrl+S save");
        l.setFont(new Font("Consolas", Font.PLAIN, 11));
        l.setForeground(TEXT_LO);
        l.setOpaque(true);
        l.setBackground(BG_PANEL);
        l.setBorder(new CompoundBorder(new MatteBorder(1,0,0,0,BORDER), new EmptyBorder(6,16,6,16)));
        return l;
    }

    void updateHighlight(String tool) {
        selectedTool = tool;
        toolButtons.forEach((n,b) -> b.repaint());
    }

    public void updateUIColors(Color c) {
        panel.setCurrentColor(c);
        colorDot.setBackground(c);
        colorDot.repaint();
        updateHighlight(panel.selectedTool);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ToolBtn — анімована кнопка інструменту
// ═══════════════════════════════════════════════════════════════════════════════
class ToolBtn extends JButton {
    private final String tool;
    private final EditorFrame frame;
    private float hover = 0f;
    private Timer anim;

    ToolBtn(String icon, String tool, EditorFrame frame) {
        super(icon);
        this.tool = tool; this.frame = frame;
        setFont(new Font("Segoe UI Symbol", Font.PLAIN, 19));
        setForeground(EditorFrame.TEXT_HI);
        setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
        setMaximumSize(new Dimension(56,48)); setPreferredSize(new Dimension(56,48));
        setAlignmentX(CENTER_ALIGNMENT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(tool);
        addActionListener(e -> { frame.panel.setTool(tool); frame.updateHighlight(tool); });
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { animate(true); }
            public void mouseExited(MouseEvent e)  { animate(false); }
        });
    }

    private void animate(boolean in) {
        if (anim != null) anim.stop();
        anim = new Timer(14, null);
        anim.addActionListener(e -> {
            hover += in ? 0.15f : -0.15f;
            if (hover >= 1f) { hover = 1f; anim.stop(); }
            if (hover <= 0f) { hover = 0f; anim.stop(); }
            repaint();
        });
        anim.start();
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean active = tool.equals(frame.selectedTool);

        if (active) {
            GradientPaint gp = new GradientPaint(0,0,new Color(0,220,255,60),
                    getWidth(),getHeight(),new Color(120,40,220,60));
            g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
            g2.setColor(new Color(0,220,255,160));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            // outer glow
            g2.setColor(new Color(0,220,255,28));
            g2.setStroke(new BasicStroke(5f));
            g2.drawRoundRect(3,3,getWidth()-6,getHeight()-6,10,10);
        } else {
            g2.setColor(EditorFrame.BG_CARD);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
            if (hover > 0f) {
                g2.setColor(new Color(0,220,255,(int)(hover*28)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(new Color(0,220,255,(int)(hover*110)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            } else {
                g2.setColor(EditorFrame.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        }
        super.paintComponent(g);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// AnimatedCanvas — dot-grid + particle overlay
// ═══════════════════════════════════════════════════════════════════════════════
class AnimatedCanvas extends JLayeredPane {
    private final DrawingPanel dp;
    private final ParticleOverlay overlay;

    AnimatedCanvas(DrawingPanel dp) {
        this.dp = dp;
        overlay = new ParticleOverlay();
        setLayout(null);
        add(dp,      JLayeredPane.DEFAULT_LAYER);
        add(overlay, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                dp.setBounds(0,0,getWidth(),getHeight());
                overlay.setBounds(0,0,getWidth(),getHeight());
            }
        });

        // Пробрасуємо всі mouse-події крізь overlay на DrawingPanel
        overlay.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                overlay.burst(e.getX(), e.getY());
                dp.dispatchEvent(SwingUtilities.convertMouseEvent(overlay,e,dp));
                dp.requestFocusInWindow();
            }
            public void mouseReleased(MouseEvent e) {
                dp.dispatchEvent(SwingUtilities.convertMouseEvent(overlay,e,dp));
            }
        });
        overlay.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                dp.dispatchEvent(SwingUtilities.convertMouseEvent(overlay,e,dp));
            }
            public void mouseMoved(MouseEvent e) {
                dp.dispatchEvent(SwingUtilities.convertMouseEvent(overlay,e,dp));
            }
        });
    }
}

// ── Dot-grid + Particle system ────────────────────────────────────────────────
class ParticleOverlay extends JPanel {
    private static final int GRID = 32;
    private final float[] phases;
    private final List<Particle> particles = new ArrayList<>();
    private final Random rng = new Random();
    private long tick = 0;

    ParticleOverlay() {
        setOpaque(false);
        phases = new float[300 * 300];
        for (int i = 0; i < phases.length; i++) phases[i] = rng.nextFloat() * 6.28f;

        new Timer(28, e -> { tick++; particles.removeIf(p -> p.life <= 0); particles.forEach(Particle::update); repaint(); }).start();
    }

    void burst(int cx, int cy) {
        int n = 16 + rng.nextInt(12);
        for (int i = 0; i < n; i++) particles.add(new Particle(cx, cy, rng));
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cols = getWidth()  / GRID + 1;
        int rows = getHeight() / GRID + 1;
        double t = tick * 0.038;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = Math.min(r * cols + c, phases.length - 1);
                float pulse = (float)(Math.sin(t + phases[idx]) * 0.5 + 0.5);
                int alpha = (int)(pulse * 52 + 8);
                float sz  = pulse * 1.8f + 0.5f;
                float ratio = (float) c / Math.max(cols-1, 1);
                int red   = (int)(0   + 120 * ratio);
                int green = (int)(220 - 180 * ratio);
                int blue  = (int)(255 -  35 * ratio);
                g2.setColor(new Color(red, green, blue, alpha));
                g2.fillOval((int)(c*GRID - sz/2), (int)(r*GRID - sz/2), (int)sz, (int)sz);
            }
        }

        for (Particle p : particles) {
            float a = p.life / (float) p.maxLife;
            g2.setColor(new Color(p.col.getRed(),p.col.getGreen(),p.col.getBlue(),(int)(a*210)));
            float sz = a * p.size;
            g2.setStroke(new BasicStroke(sz*0.55f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int)(p.x-p.vx*2),(int)(p.y-p.vy*2),(int)p.x,(int)p.y);
            g2.fillOval((int)(p.x-sz/2),(int)(p.y-sz/2),(int)sz,(int)sz);
        }
    }
}

class Particle {
    float x, y, vx, vy, size;
    int life, maxLife;
    Color col;
    static final Color[] PAL = {
            new Color(0,220,255), new Color(120,40,220),
            new Color(255,50,180), new Color(0,255,150), new Color(255,200,0)
    };
    Particle(int cx, int cy, Random rng) {
        x = cx; y = cy;
        double a = rng.nextDouble() * Math.PI * 2;
        float  s = 1.5f + rng.nextFloat() * 5.5f;
        vx = (float)(Math.cos(a)*s); vy = (float)(Math.sin(a)*s);
        size = 3f + rng.nextFloat() * 5f;
        maxLife = 18 + rng.nextInt(22); life = maxLife;
        col = PAL[rng.nextInt(PAL.length)];
    }
    void update() { x+=vx; y+=vy; vx*=0.92f; vy*=0.92f; vy+=0.09f; life--; }
}