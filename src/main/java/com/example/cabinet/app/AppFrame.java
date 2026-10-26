package com.example.cabinet.app;

import com.example.cabinet.data.DemoDataRepository;
import com.example.cabinet.model.Folder;
import com.example.cabinet.settings.AppSettings;
import com.example.cabinet.settings.SettingsStore;
import com.example.cabinet.ui.DocumentsPanel;
import com.example.cabinet.ui.cabinet.Cabinet;
import com.example.cabinet.ui.cabinet.PanelCabinet;
import com.example.cabinet.ui.cabinet.TreeCabinet;
import com.example.cabinet.ui.chrome.CardPanel;
import com.example.cabinet.ui.chrome.ColorTheme;
import com.example.cabinet.ui.chrome.UiDensity;
import com.example.cabinet.ui.chrome.UiStyle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * Оболочка окна. При смене TREE/PANELS левый кабинет создаётся заново
 * и заново получает папки и слушатель выбора.
 */
public final class AppFrame extends JFrame {
    private final DemoDataRepository repository = new DemoDataRepository();
    private final AppSettings settings;
    private final CardPanel cabinetCard = new CardPanel();
    private final CardPanel documentsCard = new CardPanel();
    private final JPanel content = new JPanel(new BorderLayout());
    private final JMenuBar menuBar = new JMenuBar();
    private final EnumMap<AppSettings.CabinetMode, JRadioButtonMenuItem> modeItems =
            new EnumMap<>(AppSettings.CabinetMode.class);
    private final EnumMap<UiDensity, JRadioButtonMenuItem> densityItems = new EnumMap<>(UiDensity.class);
    private final EnumMap<ColorTheme, JRadioButtonMenuItem> themeItems = new EnumMap<>(ColorTheme.class);

    private UiStyle style;
    private DocumentsPanel documentsPanel;
    private JSplitPane split;
    private Cabinet currentCabinet;
    private Folder selectedFolder;
    private boolean updatingMenus;
    private boolean uiReady;

    public AppFrame(AppSettings settings) {
        super("Рабочий кабинет");
        this.settings = settings;
        this.style = new UiStyle(settings.getDensity(), settings.getTheme());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(880, 560));
        build();
        installCabinet();
        applyChrome();
        syncMenuChecks();
        setLocationRelativeTo(null);
    }

    private void build() {
        documentsCard.setDrawBorder(false);
        documentsPanel = new DocumentsPanel(style, this::refreshAll, this::openSettings);
        documentsCard.add(documentsPanel, BorderLayout.CENTER);

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cabinetCard, documentsCard);
        split.setUI(flatSplitUi());
        split.setContinuousLayout(true);
        split.setResizeWeight(0);
        split.setDividerSize(12);
        split.setBorder(null);
        split.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, event -> rememberDivider());

        content.setOpaque(true);
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.add(split, BorderLayout.CENTER);
        setContentPane(content);
        setJMenuBar(buildMenuBar());

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                refreshAll();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                SwingUtilities.invokeLater(() -> {
                    applySavedDivider();
                    SwingUtilities.invokeLater(() -> {
                        applySavedDivider();
                        uiReady = true;
                    });
                });
            }

            @Override
            public void windowClosing(WindowEvent event) {
                int location = split.getDividerLocation();
                if (location >= 120) {
                    settings.setDividerLocation(location);
                }
                SettingsStore.save(settings);
            }
        });
    }

    private JMenuBar buildMenuBar() {
        JMenu file = new JMenu("Файл");
        JMenuItem refresh = new JMenuItem("Обновить");
        refresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refresh.addActionListener(event -> refreshAll());
        JMenuItem exit = new JMenuItem("Выход");
        exit.addActionListener(event -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        file.add(refresh);
        file.addSeparator();
        file.add(exit);

        JMenu view = new JMenu("Вид");
        view.add(radioMenu("Режим кабинета", AppSettings.CabinetMode.values(), settings.getCabinetMode(), AppSettings.CabinetMode::title, modeItems, this::onMode));
        view.add(radioMenu("Плотность", UiDensity.values(), settings.getDensity(), UiDensity::title, densityItems, this::onDensity));
        view.add(radioMenu("Тема", ColorTheme.values(), settings.getTheme(), ColorTheme::title, themeItems, this::onTheme));
        view.addSeparator();
        JMenuItem settingsItem = new JMenuItem("Настройки…");
        settingsItem.addActionListener(event -> openSettings());
        view.add(settingsItem);

        menuBar.add(file);
        menuBar.add(view);
        return menuBar;
    }

    private <E extends Enum<E>> JMenu radioMenu(
            String title,
            E[] values,
            E selected,
            java.util.function.Function<E, String> caption,
            EnumMap<E, JRadioButtonMenuItem> target,
            java.util.function.Consumer<E> action
    ) {
        JMenu menu = new JMenu(title);
        ButtonGroup group = new ButtonGroup();
        for (E value : values) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(caption.apply(value), value == selected);
            group.add(item);
            item.addActionListener(event -> {
                if (!updatingMenus) {
                    action.accept(value);
                }
            });
            target.put(value, item);
            menu.add(item);
        }
        return menu;
    }

    private void onMode(AppSettings.CabinetMode mode) {
        if (settings.getCabinetMode() != mode) {
            settings.setCabinetMode(mode);
            installCabinet();
        }
        applyChrome();
        syncMenuChecks();
        persist();
    }

    private void onDensity(UiDensity density) {
        settings.setDensity(density);
        style = new UiStyle(settings.getDensity(), settings.getTheme());
        applyChrome();
        syncMenuChecks();
        persist();
    }

    private void onTheme(ColorTheme theme) {
        settings.setTheme(theme);
        style = new UiStyle(settings.getDensity(), settings.getTheme());
        applyChrome();
        syncMenuChecks();
        persist();
    }

    private void openSettings() {
        if (!SettingsDialog.edit(this, settings, style)) {
            return;
        }
        style = new UiStyle(settings.getDensity(), settings.getTheme());
        installCabinet();
        applyChrome();
        syncMenuChecks();
        persist();
    }

    private void installCabinet() {
        String keepId = selectedFolder == null ? null : selectedFolder.id();
        if (settings.getCabinetMode() == AppSettings.CabinetMode.PANELS && keepId != null) {
            keepId = repository.topLevelFolder(keepId).map(Folder::id).orElse(keepId);
        }

        cabinetCard.removeAll();
        currentCabinet = settings.getCabinetMode() == AppSettings.CabinetMode.PANELS
                ? new PanelCabinet()
                : new TreeCabinet();
        currentCabinet.applyStyle(style);
        currentCabinet.setSelectionListener(this::onFolderSelected);
        currentCabinet.setFolders(repository.folders());
        cabinetCard.add(currentCabinet.getComponent(), BorderLayout.CENTER);
        cabinetCard.setDrawBorder(settings.getCabinetMode() == AppSettings.CabinetMode.TREE);

        if (keepId == null || repository.find(keepId).isEmpty()) {
            keepId = repository.folders().isEmpty() ? null : repository.folders().get(0).id();
        }
        if (keepId != null) {
            currentCabinet.selectFolder(keepId);
        }
        cabinetCard.revalidate();
        cabinetCard.repaint();
    }

    private void onFolderSelected(Folder folder) {
        selectedFolder = folder;
        documentsPanel.showDocuments(folder.title(), repository.documentsOf(folder.id()));
    }

    private void refreshAll() {
        if (currentCabinet == null) {
            return;
        }
        currentCabinet.refresh();
        if (selectedFolder != null) {
            currentCabinet.selectFolder(selectedFolder.id());
        }
    }

    private void applyChrome() {
        Color window = style.windowBackground();
        content.setBackground(window);
        split.setBackground(window);
        cabinetCard.applyStyle(style);
        documentsCard.applyStyle(style);
        cabinetCard.setDrawBorder(settings.getCabinetMode() == AppSettings.CabinetMode.TREE);
        documentsCard.setDrawBorder(false);
        documentsPanel.applyStyle(style);
        if (currentCabinet != null) {
            currentCabinet.applyStyle(style);
        }
        styleMenu();
        applyWindowIcon();
        revalidate();
        repaint();
    }

    private void styleMenu() {
        Font font = style.font();
        menuBar.setFont(font);
        menuBar.setOpaque(true);
        menuBar.setBackground(style.cardBackground());
        menuBar.setBorder(new MatteBorder(0, 0, 1, 0, style.cardBorder()));
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            styleMenu(menuBar.getMenu(i), font);
        }
    }

    private void styleMenu(JMenu menu, Font font) {
        if (menu == null) {
            return;
        }
        menu.setFont(font);
        menu.setForeground(style.textPrimary());
        for (Component child : menu.getMenuComponents()) {
            child.setFont(font);
            if (child instanceof JComponent item) {
                item.setForeground(style.textPrimary());
            }
            if (child instanceof JMenu nested) {
                styleMenu(nested, font);
            }
        }
    }

    private void syncMenuChecks() {
        updatingMenus = true;
        try {
            modeItems.get(settings.getCabinetMode()).setSelected(true);
            densityItems.get(settings.getDensity()).setSelected(true);
            themeItems.get(settings.getTheme()).setSelected(true);
        } finally {
            updatingMenus = false;
        }
    }

    private BasicSplitPaneUI flatSplitUi() {
        return new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(style.windowBackground());
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        };
    }

    private void applySavedDivider() {
        if (split.getWidth() < 480) {
            return;
        }
        int max = Math.max(180, split.getWidth() - 320);
        int location = Math.min(Math.max(settings.getDividerLocation(), 180), max);
        split.setDividerLocation(location);
    }

    private void rememberDivider() {
        if (!uiReady) {
            return;
        }
        int location = split.getDividerLocation();
        if (location < 120 || location == settings.getDividerLocation()) {
            return;
        }
        settings.setDividerLocation(location);
        persist();
    }

    private void persist() {
        SettingsStore.save(settings);
    }

    private void applyWindowIcon() {
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D canvas = image.createGraphics();
        try {
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setColor(style.accent());
            canvas.fillRoundRect(1, 1, 30, 30, 8, 8);
            canvas.setColor(Color.WHITE);
            canvas.fillRoundRect(8, 9, 16, 3, 2, 2);
            canvas.fillRoundRect(8, 15, 12, 3, 2, 2);
            canvas.fillRoundRect(8, 21, 14, 3, 2, 2);
        } finally {
            canvas.dispose();
        }
        setIconImage(image);
    }
}
