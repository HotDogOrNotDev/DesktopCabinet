package com.example.cabinet.app;

import com.example.cabinet.settings.AppSettings;
import com.example.cabinet.ui.chrome.ColorTheme;
import com.example.cabinet.ui.chrome.FlatButton;
import com.example.cabinet.ui.chrome.UiDensity;
import com.example.cabinet.ui.chrome.UiStyle;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/** Модальный выбор режима кабинета, плотности и темы. */
public final class SettingsDialog extends JDialog {
    private boolean accepted;

    private SettingsDialog(Window owner, AppSettings settings, UiStyle style) {
        super(owner, "Настройки", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JComboBox<AppSettings.CabinetMode> modeBox = combo(
                AppSettings.CabinetMode.values(),
                settings.getCabinetMode(),
                AppSettings.CabinetMode::title,
                style
        );
        JComboBox<UiDensity> densityBox = combo(
                UiDensity.values(),
                settings.getDensity(),
                UiDensity::title,
                style
        );
        JComboBox<ColorTheme> themeBox = combo(
                ColorTheme.values(),
                settings.getTheme(),
                ColorTheme::title,
                style
        );

        JLabel hint = new JLabel("<html>Сохраняется в user-settings.json рядом с запуском.<br>После перезапуска режим, плотность и тема восстановятся.</html>");
        hint.setFont(style.smallFont());
        hint.setForeground(style.textMuted());

        FlatButton ok = new FlatButton("ОК");
        ok.setKind(FlatButton.Kind.ACCENT);
        ok.applyStyle(style);
        FlatButton cancel = new FlatButton("Отмена");
        cancel.applyStyle(style);
        ok.addActionListener(event -> {
            settings.setCabinetMode(modeBox.getItemAt(modeBox.getSelectedIndex()));
            settings.setDensity(densityBox.getItemAt(densityBox.getSelectedIndex()));
            settings.setTheme(themeBox.getItemAt(themeBox.getSelectedIndex()));
            accepted = true;
            dispose();
        });
        cancel.addActionListener(event -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(ok);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(style.windowBackground());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        addRow(form, 0, "Режим кабинета", modeBox, style);
        addRow(form, 1, "Плотность", densityBox, style);
        addRow(form, 2, "Тема", themeBox, style);

        GridBagConstraints hintConstraints = constraints(0, 3, 2);
        hintConstraints.insets = new Insets(10, 0, 8, 0);
        form.add(hint, hintConstraints);
        GridBagConstraints buttonConstraints = constraints(0, 4, 2);
        buttonConstraints.anchor = GridBagConstraints.EAST;
        form.add(buttons, buttonConstraints);

        setContentPane(form);
        getRootPane().setDefaultButton(ok);
        getRootPane().registerKeyboardAction(
                event -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    public static boolean edit(Window owner, AppSettings settings, UiStyle style) {
        SettingsDialog dialog = new SettingsDialog(owner, settings, style);
        dialog.setVisible(true);
        return dialog.accepted;
    }

    private static void addRow(JPanel form, int row, String caption, JComponent field, UiStyle style) {
        JLabel label = new JLabel(caption);
        label.setFont(style.font());
        label.setForeground(style.textPrimary());
        field.setFont(style.font());
        GridBagConstraints labelConstraints = constraints(0, row, 1);
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(6, 0, 6, 12);
        form.add(label, labelConstraints);
        GridBagConstraints fieldConstraints = constraints(1, row, 1);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1;
        fieldConstraints.insets = new Insets(6, 0, 6, 0);
        form.add(field, fieldConstraints);
    }

    private static GridBagConstraints constraints(int x, int y, int width) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        return constraints;
    }

    private static <E> JComboBox<E> combo(E[] values, E selected, Function<E, String> title, UiStyle style) {
        JComboBox<E> box = new JComboBox<>(values);
        box.setSelectedItem(selected);
        box.setFont(style.font());
        box.setPreferredSize(new Dimension(240, box.getPreferredSize().height));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                @SuppressWarnings("unchecked")
                E item = (E) value;
                setText(item == null ? "" : title.apply(item));
                setFont(style.font());
                return component;
            }
        });
        return box;
    }
}
