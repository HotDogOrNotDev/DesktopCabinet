package com.example.cabinet.ui.chrome;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/** Плоская кнопка без bevel. Обычная или акцентная (кнопка «ОК»). */
public class FlatButton extends JButton {
    public enum Kind {
        NORMAL,
        ACCENT
    }

    private Kind kind = Kind.NORMAL;
    private boolean hovered;
    private UiStyle style = new UiStyle(UiDensity.NORMAL, ColorTheme.LIGHT);

    public FlatButton(String text) {
        super(text);
        setUI(new BasicButtonUI());
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hovered = false;
                repaint();
            }
        });
    }

    public void setKind(Kind kind) {
        this.kind = kind == null ? Kind.NORMAL : kind;
        applyStyle(style);
    }

    public void applyStyle(UiStyle style) {
        this.style = style;
        setFont(style.font());
        setForeground(kind == Kind.ACCENT ? Color.WHITE : style.textPrimary());
        setBorder(new EmptyBorder(style.buttonPaddingY(), style.buttonPaddingX(), style.buttonPaddingY(), style.buttonPaddingX()));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill());
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (kind == Kind.NORMAL) {
                g.setColor(style.cardBorder());
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        } finally {
            g.dispose();
        }
        super.paintComponent(graphics);
    }

    private Color fill() {
        if (kind == Kind.ACCENT) {
            return hovered ? darken(style.accent()) : style.accent();
        }
        return hovered ? style.buttonHover() : style.buttonBackground();
    }

    private static Color darken(Color color) {
        return new Color(
                Math.max(0, color.getRed() - 24),
                Math.max(0, color.getGreen() - 24),
                Math.max(0, color.getBlue() - 24)
        );
    }
}
