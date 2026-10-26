package com.example.cabinet.ui.cabinet;

import com.example.cabinet.model.Folder;
import com.example.cabinet.ui.chrome.ColorTheme;
import com.example.cabinet.ui.chrome.UiDensity;
import com.example.cabinet.ui.chrome.UiStyle;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.border.EmptyBorder;

/** Плоский список корневых разделов. Вложенность дерева здесь не показывается. */
public final class PanelCabinet implements Cabinet {
    private final JPanel rootPanel = new JPanel(new BorderLayout());
    private final JLabel caption = new JLabel("Разделы");
    private final StackPanel list = new StackPanel();
    private final JScrollPane scroll = new JScrollPane(list);
    private final List<PanelCabinetItem> items = new ArrayList<>();

    private UiStyle style = new UiStyle(UiDensity.NORMAL, ColorTheme.LIGHT);
    private List<Folder> folders = List.of();
    private Consumer<Folder> selectionListener = folder -> {
    };
    private String selectedId;

    public PanelCabinet() {
        rootPanel.setOpaque(false);
        caption.setBorder(new EmptyBorder(2, 12, 4, 12));
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(4, 8, 8, 8));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        rootPanel.add(caption, BorderLayout.NORTH);
        rootPanel.add(scroll, BorderLayout.CENTER);
        applyStyle(style);
    }

    @Override
    public void setFolders(List<Folder> folders) {
        this.folders = List.copyOf(folders);
        rebuild();
    }

    @Override
    public void setSelectionListener(Consumer<Folder> listener) {
        selectionListener = listener == null ? folder -> {
        } : listener;
    }

    @Override
    public void selectFolder(String folderId) {
        Folder folder = find(folderId);
        if (folder != null) {
            choose(folder);
        }
    }

    @Override
    public JComponent getComponent() {
        return rootPanel;
    }

    @Override
    public void applyStyle(UiStyle style) {
        this.style = style;
        caption.setFont(style.smallFont());
        caption.setForeground(style.textMuted());
        list.setBackground(style.cardBackground());
        scroll.getViewport().setBackground(style.cardBackground());
        for (PanelCabinetItem item : items) {
            item.applyStyle(style);
        }
        list.revalidate();
        list.repaint();
    }

    @Override
    public void refresh() {
        String keep = selectedId;
        rebuild();
        if (keep != null) {
            selectFolder(keep);
        }
    }

    private void rebuild() {
        list.removeAll();
        items.clear();
        for (Folder folder : folders) {
            PanelCabinetItem item = new PanelCabinetItem(folder);
            item.applyStyle(style);
            item.setSelected(folder.id().equals(selectedId));
            item.setOnClick(() -> choose(folder));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            items.add(item);
            list.add(item);
        }
        list.revalidate();
        list.repaint();
    }

    private void choose(Folder folder) {
        selectedId = folder.id();
        for (PanelCabinetItem item : items) {
            item.setSelected(item.folder().id().equals(selectedId));
        }
        selectionListener.accept(folder);
    }

    private Folder find(String folderId) {
        if (folderId == null) {
            return null;
        }
        for (Folder folder : folders) {
            if (folder.id().equals(folderId)) {
                return folder;
            }
        }
        return null;
    }

    /** Тянется по ширине области прокрутки, чтобы hover занимал всю строку. */
    private static final class StackPanel extends JPanel implements Scrollable {
        private StackPanel() {
            setOpaque(true);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, visibleRect.height - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
