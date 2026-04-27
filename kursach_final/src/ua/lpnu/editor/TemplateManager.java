package ua.lpnu.editor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateManager {

    public static List<Shape> createSpecificTemplate(String name, int variant) {
        switch (name) {
            case "Візитка":    return businessCard(variant);
            case "Постер":     return poster(variant);
            case "Блок-схема": return flowchart(variant);
            case "Логотип":    return logoTemplate(variant);
            case "Рамка":      return photoFrame(variant);
            default:           return new ArrayList<>();
        }
    }

    // ─── БЛОК-СХЕМИ (5 різних логічних структур) ───────────────────────────
    private static List<Shape> flowchart(int variant) {
        List<Shape> s = new ArrayList<>();
        Color g = new Color(46, 204, 113), b = new Color(53, 116, 240), o = new Color(230, 126, 34), r = Color.RED;
        int cx = 400, cy = 60;

        switch (variant) {
            case 1: // Вертикальна лінійна
                for(int i=0; i<3; i++) {
                    MyArrow a = new MyArrow(cx+60, cy+50+(i*120), Color.GRAY, 2, false); a.setSize(0, 70); s.add(a);
                }
                addNode(s, cx, cy, g, "START", 1); addNode(s, cx, cy+120, b, "INPUT", 2);
                addNode(s, cx, cy+240, o, "PROCESS", 2); addNode(s, cx, cy+360, r, "END", 1);
                break;
            case 2: // Розгалуження (Diamond/Decision)
                addNode(s, cx, cy, g, "START", 1);
                MyArrow a1 = new MyArrow(cx+60, cy+50, Color.GRAY, 2, false); a1.setSize(0, 50); s.add(a1);
                addNode(s, cx, cy+100, o, "IF X > 0", 2);
                MyArrow left = new MyArrow(cx, cy+130, Color.GRAY, 2, false); left.setSize(-100, 50); s.add(left);
                MyArrow right = new MyArrow(cx+120, cy+130, Color.GRAY, 2, false); right.setSize(100, 50); s.add(right);
                addNode(s, cx-150, cy+180, b, "TRUE", 2); addNode(s, cx+150, cy+180, r, "FALSE", 2);
                break;
            case 3: // Паралельні процеси
                addNode(s, cx, cy, g, "BEGIN", 1);
                MyLine l = new MyLine(cx-100, cy+70, Color.GRAY, 2, false); l.setSize(320, 0); s.add(l);
                for(int i=0; i<3; i++) {
                    MyArrow a = new MyArrow(cx-100+(i*160), cy+70, Color.GRAY, 2, false); a.setSize(0, 50); s.add(a);
                    addNode(s, cx-160+(i*160), cy+120, b, "TASK "+(i+1), 2);
                }
                break;
            case 4: // Цикл (Loop)
                addNode(s, cx, cy, b, "DATA", 2);
                MyArrow aToDec = new MyArrow(cx+60, cy+60, Color.GRAY, 2, false); aToDec.setSize(0, 60); s.add(aToDec);
                addNode(s, cx, cy+120, o, "REPEAT?", 1);
                MyArrow aLoop = new MyArrow(cx, cy+145, Color.GRAY, 2, false); aLoop.setSize(-100, -60); s.add(aLoop);
                s.add(new MyText(cx-80, cy+110, Color.DARK_GRAY, "YES", 12));
                break;
            default: // Компактна
                addNode(s, cx, cy, g, "INIT", 1); addNode(s, cx+150, cy, b, "RUN", 2);
                MyArrow a = new MyArrow(cx+120, cy+25, Color.GRAY, 2, false); a.setSize(30, 0); s.add(a);
                break;
        }
        return s;
    }

    // ─── ВІЗИТКИ (5 різних композицій) ──────────────────────────────────────
    private static List<Shape> businessCard(int v) {
        List<Shape> s = new ArrayList<>();
        Color acc = getVariantColor(v), dark = new Color(35, 38, 45);
        MyRectangle bg = new MyRectangle(100, 100, (v%2==0 ? dark : Color.WHITE), 0, true); bg.setSize(500, 280); s.add(bg);

        if (v == 1) { // Ліва смуга
            MyRectangle side = new MyRectangle(100, 100, acc, 0, true); side.setSize(20, 280); s.add(side);
        } else if (v == 2) { // Кутовий зріз
            MyTriangle corn = new MyTriangle(500, 100, acc, 0, true); corn.setSize(100, 100); s.add(corn);
        } else if (v == 3) { // Центральна лінія
            MyRectangle line = new MyRectangle(100, 230, acc, 0, true); line.setSize(500, 5); s.add(line);
        } else if (v == 4) { // Два кола
            MyCircle c1 = new MyCircle(500, 100, acc, 0, true); c1.setSize(80, 80); s.add(c1);
            MyCircle c2 = new MyCircle(530, 130, acc.darker(), 0, true); c2.setSize(60, 60); s.add(c2);
        } else { // Рамка всередині
            MyRectangle r = new MyRectangle(120, 120, acc, 2, false); r.setSize(460, 240); s.add(r);
        }
        s.add(new MyText(150, 160, (v%2==0 ? Color.WHITE : Color.BLACK), "MYKOLA BALYUK", 22));
        s.add(new MyText(150, 195, acc, "Software Architect v"+v, 14));
        return s;
    }

    // ─── ПОСТЕРИ (Різна сітка) ──────────────────────────────────────────────
    private static List<Shape> poster(int v) {
        List<Shape> s = new ArrayList<>();
        Color acc = getVariantColor(v);
        MyRectangle f = new MyRectangle(50, 50, (v==5 ? Color.BLACK : Color.WHITE), 1, true); f.setSize(450, 650); s.add(f);

        if (v == 1) { // Шапка
            MyRectangle h = new MyRectangle(50, 50, acc, 0, true); h.setSize(450, 120); s.add(h);
        } else if (v == 2) { // Велике коло по центру
            MyCircle c = new MyCircle(125, 200, acc, 0, true); c.setSize(300, 300); s.add(c);
        } else if (v == 3) { // Три діагональні лінії
            for(int i=0; i<3; i++) { MyLine l = new MyLine(50, 100+(i*40), acc, 15, false); l.setSize(450, 300); s.add(l); }
        } else if (v == 4) { // Сітка квадратів
            for(int i=0; i<4; i++) { MyRectangle r = new MyRectangle(80+(i*100), 500, acc, 0, true); r.setSize(80, 80); s.add(r); }
        } else { // Мінімалізм з великим текстом
            MyRectangle border = new MyRectangle(70, 70, acc, 5, false); border.setSize(410, 610); s.add(border);
        }
        s.add(new MyText(80, 100, (v==1||v==5 ? Color.WHITE : Color.BLACK), "FESTIVAL "+v, 34));
        return s;
    }

    // ─── РАМКИ (Унікальний декор) ───────────────────────────────────────────
    private static List<Shape> photoFrame(int v) {
        List<Shape> s = new ArrayList<>();
        Color gold = new Color(212, 175, 55), wood = new Color(101, 67, 33);
        Color c = (v%2==0 ? gold : wood);

        MyRectangle out = new MyRectangle(80, 80, c, 12, false); out.setSize(520, 420); s.add(out);
        if (v == 1) { // Заклепки по кутах
            int[][] pts = {{80,80}, {588,80}, {80,488}, {588,488}};
            for(int[] p : pts) { MyCircle dot = new MyCircle(p[0]-6, p[1]-6, c, 0, true); dot.setSize(12,12); s.add(dot); }
        } else if (v == 2) { // Подвійна лінія
            MyRectangle in = new MyRectangle(100, 100, c, 2, false); in.setSize(480, 380); s.add(in);
        } else if (v == 3) { // Орнамент зверху
            MyTriangle t = new MyTriangle(315, 65, c, 0, true); t.setSize(50, 30); s.add(t);
        } else if (v == 4) { // Широка нижня панель
            MyRectangle panel = new MyRectangle(80, 440, c, 0, true); panel.setSize(520, 60); s.add(panel);
        } else { // Тінь (напівпрозорий прямокутник)
            MyRectangle shadow = new MyRectangle(95, 95, new Color(0,0,0,50), 0, true); shadow.setSize(520, 420); s.add(shadow);
        }
        s.add(new MyText(260, 535, c, "Photo Variant "+v, 18));
        return s;
    }

    // ─── ЛОГОТИПИ (Вже були гарні, залишаю унікальну логіку) ─────────────────
    private static List<Shape> logoTemplate(int variant) {
        List<Shape> s = new ArrayList<>();
        Color acc = getVariantColor(variant);
        int cx = 350, cy = 250;
        switch (variant) {
            case 1:
                MyCircle r = new MyCircle(cx-80, cy-80, new Color(44, 62, 80), 8, false); r.setSize(160, 160); s.add(r);
                MyCircle c = new MyCircle(cx-30, cy-30, acc, 0, true); c.setSize(60, 60); s.add(c);
                break;
            case 2:
                MyTriangle t = new MyTriangle(cx-60, cy-80, acc, 0, true); t.setSize(120, 100); s.add(t);
                MyRectangle b = new MyRectangle(cx-60, cy+20, acc, 0, true); b.setSize(120, 20); s.add(b);
                break;
            case 3:
                MyRectangle r1 = new MyRectangle(cx-70, cy-70, Color.DARK_GRAY, 4, false); r1.setSize(140, 140); s.add(r1);
                MyRectangle r2 = new MyRectangle(cx-40, cy-40, acc, 0, true); r2.setSize(80, 80); s.add(r2);
                break;
            case 4:
                MyArrow arr = new MyArrow(cx, cy+60, acc, 12, false); arr.setSize(0, -120); s.add(arr);
                break;
            default:
                for(int i=0; i<3; i++) {
                    MyTriangle st = new MyTriangle(cx-80+(i*20), cy-60+(i*30), acc, 2, false); st.setSize(160-(i*40), 80); s.add(st);
                }
                break;
        }
        s.add(new MyText(cx-80, cy+130, Color.BLACK, "LOGO V"+variant, 22));
        return s;
    }

    private static void addNode(List<Shape> s, int x, int y, Color c, String txt, int type) {
        if(type == 1) { MyCircle node = new MyCircle(x, y, c, 0, true); node.setSize(120, 50); s.add(node); }
        else { MyRectangle node = new MyRectangle(x, y, c, 0, true); node.setSize(120, 60); s.add(node); }
        s.add(new MyText(x+25, y+20, Color.WHITE, txt, 13));
    }

    private static Color getVariantColor(int v) {
        Color[] p = { new Color(53, 116, 240), new Color(231, 76, 60), new Color(155, 89, 182), new Color(26, 188, 156), new Color(241, 196, 15) };
        return p[(v - 1) % 5];
    }
}