package com.example.cabinet.ui.cabinet;

import com.example.cabinet.model.Folder;
import com.example.cabinet.ui.chrome.ColorTheme;
import com.example.cabinet.ui.chrome.UiDensity;
import com.example.cabinet.ui.chrome.UiStyle;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/** Кабинет на JTree. У демо-папок один уровень вложенности. */
public final class TreeCabinet implements Cabinet {
    private final JPanel rootPanel = new JPanel(new BorderLayout());
    private final JLabel caption = new JLabel("Разделы");
    private final JTree tree = new JTree();
    private final JScrollPane scroll = new JScrollPane(tree);
    private final DefaultTreeCellRenderer renderer = new FolderRenderer();

    private UiStyle style = new UiStyle(UiDensity.NORMAL, ColorTheme.LIGHT);
    private List<Folder> folders = List.of();
    private Consumer<Folder> selectionListener = folder -> {
    };
    private String selectedId;
    private boolean adjusting;

    public TreeCabinet() {
        rootPanel.setOpaque(false);
        caption.setBorder(new EmptyBorder(2, 8, 6, 8));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(renderer);
        tree.setToggleClickCount(2);
        tree.addTreeSelectionListener(this::onSelected);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
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
        TreePath path = findPath(folderId);
        if (path == null) {
            return;
        }
        selectedId = folderId;
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
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
        tree.setFont(style.font());
        tree.setRowHeight(style.treeRowHeight());
        tree.setBackground(style.cardBackground());
        scroll.getViewport().setBackground(style.cardBackground());
        tree.repaint();
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
        adjusting = true;
        try {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
            for (Folder folder : folders) {
                root.add(toNode(folder));
            }
            tree.setModel(new DefaultTreeModel(root));
            expandAll();
        } finally {
            adjusting = false;
        }
    }

    private static DefaultMutableTreeNode toNode(Folder folder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        for (Folder child : folder.children()) {
            node.add(toNode(child));
        }
        return node;
    }

    private void expandAll() {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    private void onSelected(TreeSelectionEvent event) {
        if (adjusting) {
            return;
        }
        Object selected = tree.getLastSelectedPathComponent();
        if (!(selected instanceof DefaultMutableTreeNode node)) {
            return;
        }
        if (!(node.getUserObject() instanceof Folder folder)) {
            return;
        }
        selectedId = folder.id();
        selectionListener.accept(folder);
    }

    private TreePath findPath(String folderId) {
        if (folderId == null || !(tree.getModel().getRoot() instanceof DefaultMutableTreeNode root)) {
            return null;
        }
        Enumeration<?> nodes = root.depthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            if (node.getUserObject() instanceof Folder folder && folder.id().equals(folderId)) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    private final class FolderRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(
                JTree source,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus
        ) {
            Component component = super.getTreeCellRendererComponent(source, value, selected, expanded, leaf, row, hasFocus);
            setOpaque(true);
            setIcon(null);
            setLeafIcon(null);
            setOpenIcon(null);
            setClosedIcon(null);
            setFont(style.font());
            setTextNonSelectionColor(style.textPrimary());
            setTextSelectionColor(style.textPrimary());
            setBackgroundNonSelectionColor(style.cardBackground());
            setBackgroundSelectionColor(style.selectedBackground());
            setBorderSelectionColor(style.selectedBackground());
            setBackground(selected ? style.selectedBackground() : style.cardBackground());
            setForeground(style.textPrimary());
            int padY = Math.max(1, (style.treeRowHeight() - style.font().getSize()) / 4);
            setBorder(new EmptyBorder(padY, 4, padY, 4));
            return component;
        }
    }
}
