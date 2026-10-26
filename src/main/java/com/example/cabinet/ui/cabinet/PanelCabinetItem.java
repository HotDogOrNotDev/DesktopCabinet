package com.example.cabinet.ui.cabinet;

import com.example.cabinet.model.Folder;
import com.example.cabinet.ui.chrome.RoundedHoverLabel;
import com.example.cabinet.ui.chrome.UiStyle;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Одна строка панельного кабинета на всю ширину списка. */
public final class PanelCabinetItem extends JPanel {
    private final Folder folder;
    private final RoundedHoverLabel label;
    private Runnable onClick = () -> {
    };

    public PanelCabinetItem(Folder folder) {
        this.folder = folder;
        setLayout(new BorderLayout());
        setOpaque(false);
        label = new RoundedHoverLabel(folder.title());
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                onClick.run();
            }
        });
        add(label, BorderLayout.CENTER);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Folder folder() {
        return folder;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick == null ? () -> {
        } : onClick;
    }

    public void setSelected(boolean selected) {
        label.setItemSelected(selected);
    }

    public void applyStyle(UiStyle style) {
        label.applyStyle(style);
        int height = style.cabinetItemHeight();
        setPreferredSize(new Dimension(10, height));
        setMinimumSize(new Dimension(0, height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        revalidate();
    }
}
