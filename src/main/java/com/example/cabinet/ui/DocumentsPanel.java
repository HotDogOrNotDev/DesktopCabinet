package com.example.cabinet.ui;

import com.example.cabinet.model.DocumentItem;
import com.example.cabinet.ui.chrome.FlatButton;
import com.example.cabinet.ui.chrome.UiStyle;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/** Правая часть окна: заголовок раздела, кнопки и таблица документов. */
public final class DocumentsPanel extends JPanel {
    private static final DateTimeFormatter DATES = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final DefaultTableModel model;
    private final JTable table;
    private final JScrollPane scroll;
    private final CardLayout cards = new CardLayout();
    private final JPanel stack = new JPanel(cards);
    private final JPanel header = new JPanel(new BorderLayout(12, 0));
    private final JLabel titleLabel = new JLabel("Документы");
    private final JLabel countLabel = new JLabel(" ");
    private final JLabel emptyLabel = new JLabel("Выберите папку");
    private final FlatButton refreshButton = new FlatButton("Обновить");
    private final FlatButton settingsButton = new FlatButton("Настройки");

    private UiStyle style;
    private boolean columnsTuned;

    public DocumentsPanel(UiStyle style, Runnable onRefresh, Runnable onSettings) {
        super(new BorderLayout());
        this.style = style;
        setOpaque(false);

        model = new DefaultTableModel(new Object[] {"№", "Название", "Дата", "Статус"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = createTable();
        scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(titleLabel);
        titles.add(Box.createVerticalStrut(2));
        titles.add(countLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        refreshButton.setToolTipText("Перечитать демонстрационные данные (F5)");
        settingsButton.setToolTipText("Режим кабинета, плотность и тема");
        refreshButton.addActionListener(event -> onRefresh.run());
        settingsButton.addActionListener(event -> onSettings.run());
        buttons.add(refreshButton);
        buttons.add(settingsButton);

        header.setOpaque(false);
        header.add(titles, BorderLayout.CENTER);
        header.add(buttons, BorderLayout.EAST);

        JPanel empty = new JPanel(new GridBagLayout());
        empty.setOpaque(false);
        empty.add(emptyLabel);

        stack.setOpaque(false);
        stack.add(empty, "empty");
        stack.add(scroll, "table");

        add(header, BorderLayout.NORTH);
        add(stack, BorderLayout.CENTER);
        applyStyle(style);
        showEmpty();
    }

    public void showDocuments(String folderTitle, List<DocumentItem> documents) {
        titleLabel.setText(folderTitle);
        countLabel.setText(countText(documents.size()));
        model.setRowCount(0);
        for (DocumentItem item : documents) {
            model.addRow(new Object[] {
                    item.number(),
                    item.title(),
                    DATES.format(item.date()),
                    item.status()
            });
        }
        tuneColumns();
        cards.show(stack, "table");
    }

    public void showEmpty() {
        titleLabel.setText("Документы");
        countLabel.setText("Нет выбранного раздела");
        cards.show(stack, "empty");
    }

    public void applyStyle(UiStyle style) {
        this.style = style;
        titleLabel.setFont(style.titleFont());
        titleLabel.setForeground(style.textPrimary());
        countLabel.setFont(style.smallFont());
        countLabel.setForeground(style.textMuted());
        emptyLabel.setFont(style.titleFont());
        emptyLabel.setForeground(style.textMuted());
        refreshButton.applyStyle(style);
        settingsButton.applyStyle(style);
        table.setFont(style.font());
        table.setRowHeight(style.tableRowHeight());
        table.setBackground(style.cardBackground());
        table.setForeground(style.textPrimary());
        table.setSelectionBackground(style.selectedBackground());
        table.setSelectionForeground(style.textPrimary());
        table.setGridColor(style.grid());
        table.getTableHeader().setFont(style.boldFont());
        table.getTableHeader().setBackground(style.headerBackground());
        table.getTableHeader().setForeground(style.textMuted());
        scroll.getViewport().setBackground(style.cardBackground());
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, style.grid()),
                new EmptyBorder(4, 8, 10, 8)
        ));
        revalidate();
        repaint();
    }

    private JTable createTable() {
        JTable grid = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                component.setFont(style.font());
                if (isRowSelected(row)) {
                    component.setBackground(style.selectedBackground());
                } else {
                    component.setBackground(style.cardBackground());
                }
                component.setForeground(style.textPrimary());
                if (component instanceof JComponent cell) {
                    cell.setOpaque(true);
                    cell.setBorder(new EmptyBorder(0, 8, 0, 8));
                }
                return component;
            }
        };
        grid.setFillsViewportHeight(true);
        grid.setShowVerticalLines(false);
        grid.setShowHorizontalLines(true);
        grid.setRowSelectionAllowed(true);
        grid.setColumnSelectionAllowed(false);
        grid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grid.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        grid.getTableHeader().setReorderingAllowed(false);
        grid.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        return grid;
    }

    private void tuneColumns() {
        if (columnsTuned || table.getColumnModel().getColumnCount() < 4) {
            return;
        }
        columnsTuned = true;
        var columns = table.getColumnModel();
        columns.getColumn(0).setMinWidth(96);
        columns.getColumn(0).setPreferredWidth(120);
        columns.getColumn(0).setMaxWidth(150);
        columns.getColumn(1).setMinWidth(180);
        columns.getColumn(1).setPreferredWidth(420);
        columns.getColumn(2).setMinWidth(108);
        columns.getColumn(2).setPreferredWidth(124);
        columns.getColumn(2).setMaxWidth(150);
        columns.getColumn(3).setMinWidth(200);
        columns.getColumn(3).setPreferredWidth(230);
    }

    private static String countText(int count) {
        int mod10 = count % 10;
        int mod100 = count % 100;
        String word;
        if (mod10 == 1 && mod100 != 11) {
            word = "документ";
        } else if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
            word = "документа";
        } else {
            word = "документов";
        }
        return count + " " + word;
    }

    private final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable source,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(source, value, false, false, row, column);
            label.setFont(style.boldFont());
            label.setOpaque(true);
            label.setBackground(style.headerBackground());
            label.setForeground(style.textMuted());
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, style.grid()),
                    new EmptyBorder(0, 8, 0, 8)
            ));
            return label;
        }
    }
}
