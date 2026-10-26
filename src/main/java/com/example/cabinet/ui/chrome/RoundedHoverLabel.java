package com.example.cabinet.ui.chrome;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/** Подпись пункта панельного кабинета: hover и выбор рисуются скруглённым фоном. */
public class RoundedHoverLabel extends JLabel {
    private static final int ARC = 14;
    private static final int INSET_X = 4;
    private static final int INSET_Y = 3;

    private boolean hovered;
    private boolean selected;
    private UiStyle style = new UiStyle(UiDensity.NORMAL, ColorTheme.LIGHT);

    public RoundedHoverLabel(String text) {
        super(text);
        setOpaque(false);
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

    public void setItemSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void applyStyle(UiStyle style) {
        this.style = style;
        setFont(style.font());
        setForeground(style.textPrimary());
        setBorder(new EmptyBorder(0, 16, 0, 12));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (selected || hovered) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(selected ? style.selectedBackground() : style.hoverBackground());
                g.fillRoundRect(
                        INSET_X,
                        INSET_Y,
                        Math.max(0, getWidth() - INSET_X * 2),
                        Math.max(0, getHeight() - INSET_Y * 2),
                        ARC,
                        ARC
                );
            } finally {
                g.dispose();
            }
        }
        super.paintComponent(graphics);
    }
}
