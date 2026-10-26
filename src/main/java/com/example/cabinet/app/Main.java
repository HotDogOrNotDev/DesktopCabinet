package com.example.cabinet.app;

import com.example.cabinet.settings.SettingsStore;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        // Сборка Swing только на EDT.
        SwingUtilities.invokeLater(() -> {
            try {
                AppFrame frame = new AppFrame(SettingsStore.load());
                frame.setVisible(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Рабочий кабинет", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
