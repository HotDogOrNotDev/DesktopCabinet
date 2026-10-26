package com.example.cabinet.ui.chrome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Скруглённая карточка. Фон панели прозрачный: углы не закрашиваются
 * и сквозь них виден фон окна. Отступ 8 px держит контент внутри дуги.
 */
public class CardPanel extends JPanel {
    public static final int ARC = 24;
    private static final int INSET = 8;

    private boolean drawBorder = true;
    private Color fill = Color.WHITE;
    private Color borderColor = new Color(0xD5D8E0);

    public CardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(INSET, INSET, INSET, INSET));
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        repaint();
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void applyStyle(UiStyle style) {
        fill = style.cardBackground();
        borderColor = style.cardBorder();
        setBorder(BorderFactory.createEmptyBorder(INSET, INSET, INSET, INSET));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth() - 1;
            int height = getHeight() - 1;
            g.setColor(fill);
            g.fillRoundRect(0, 0, width, height, ARC, ARC);
            if (drawBorder) {
                g.setColor(borderColor);
                g.drawRoundRect(0, 0, width, height, ARC, ARC);
            }
        } finally {
            g.dispose();
        }
    }
}
