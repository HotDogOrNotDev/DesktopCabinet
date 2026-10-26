package com.example.cabinet.ui.chrome;

import java.awt.Color;

/** Палитра окна. Цвета живут здесь, а не литералами в компонентах. */
public enum ColorTheme {
    LIGHT(
            "Светлая",
            hex("EEF0F4"),
            hex("FFFFFF"),
            hex("D5D8E0"),
            hex("D8DEEA"),
            hex("C5CEDF"),
            hex("1E2430"),
            hex("5E6778"),
            hex("2F5D9F"),
            hex("F6F7FA"),
            hex("FFFFFF"),
            hex("EEF1F7"),
            hex("E1E4EC")
    ),
    SOFT_GRAY(
            "Мягкий серый",
            hex("E4E1DA"),
            hex("F7F6F3"),
            hex("CFCBC3"),
            hex("D9D3C8"),
            hex("C8C0B2"),
            hex("2C2925"),
            hex("6A645C"),
            hex("6E5B3E"),
            hex("EFEDE7"),
            hex("F7F6F3"),
            hex("EBE7DF"),
            hex("DDD8CE")
    );

    private final String title;
    private final Color windowBackground;
    private final Color cardBackground;
    private final Color cardBorder;
    private final Color hoverBackground;
    private final Color selectedBackground;
    private final Color textPrimary;
    private final Color textMuted;
    private final Color accent;
    private final Color headerBackground;
    private final Color buttonBackground;
    private final Color buttonHover;
    private final Color grid;

    ColorTheme(
            String title,
            Color windowBackground,
            Color cardBackground,
            Color cardBorder,
            Color hoverBackground,
            Color selectedBackground,
            Color textPrimary,
            Color textMuted,
            Color accent,
            Color headerBackground,
            Color buttonBackground,
            Color buttonHover,
            Color grid
    ) {
        this.title = title;
        this.windowBackground = windowBackground;
        this.cardBackground = cardBackground;
        this.cardBorder = cardBorder;
        this.hoverBackground = hoverBackground;
        this.selectedBackground = selectedBackground;
        this.textPrimary = textPrimary;
        this.textMuted = textMuted;
        this.accent = accent;
        this.headerBackground = headerBackground;
        this.buttonBackground = buttonBackground;
        this.buttonHover = buttonHover;
        this.grid = grid;
    }

    public String title() {
        return title;
    }

    public Color windowBackground() {
        return windowBackground;
    }

    public Color cardBackground() {
        return cardBackground;
    }

    public Color cardBorder() {
        return cardBorder;
    }

    public Color hoverBackground() {
        return hoverBackground;
    }

    public Color selectedBackground() {
        return selectedBackground;
    }

    public Color textPrimary() {
        return textPrimary;
    }

    public Color textMuted() {
        return textMuted;
    }

    public Color accent() {
        return accent;
    }

    public Color headerBackground() {
        return headerBackground;
    }

    public Color buttonBackground() {
        return buttonBackground;
    }

    public Color buttonHover() {
        return buttonHover;
    }

    public Color grid() {
        return grid;
    }

    private static Color hex(String rgb) {
        return new Color(Integer.parseInt(rgb, 16));
    }
}
