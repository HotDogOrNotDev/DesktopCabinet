package com.example.cabinet.ui.chrome;

import java.awt.Color;
import java.awt.Font;

/** Снимок плотности и темы. Компоненты берут размеры и цвета только отсюда. */
public final class UiStyle {
    private static final int BASE_FONT = 15;

    private final UiDensity density;
    private final ColorTheme theme;

    public UiStyle(UiDensity density, ColorTheme theme) {
        this.density = density == null ? UiDensity.NORMAL : density;
        this.theme = theme == null ? ColorTheme.LIGHT : theme;
    }

    public UiDensity density() {
        return density;
    }

    public ColorTheme theme() {
        return theme;
    }

    public Font font() {
        int size = BASE_FONT + density.fontDelta();
        Font segoe = new Font("Segoe UI", Font.PLAIN, size);
        if ("Segoe UI".equalsIgnoreCase(segoe.getFamily())) {
            return segoe;
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }

    public Font boldFont() {
        return font().deriveFont(Font.BOLD);
    }

    public Font titleFont() {
        return font().deriveFont(Font.BOLD, font().getSize2D() + 4f);
    }

    public Font smallFont() {
        return font().deriveFont(Math.max(11f, font().getSize2D() - 1f));
    }

    public int tableRowHeight() {
        return density.scale(32);
    }

    public int cabinetItemHeight() {
        return density.scale(46);
    }

    public int treeRowHeight() {
        return density.scale(30);
    }

    public int buttonPaddingY() {
        return density.scale(7);
    }

    public int buttonPaddingX() {
        return density.scale(14);
    }

    public Color windowBackground() {
        return theme.windowBackground();
    }

    public Color cardBackground() {
        return theme.cardBackground();
    }

    public Color cardBorder() {
        return theme.cardBorder();
    }

    public Color hoverBackground() {
        return theme.hoverBackground();
    }

    public Color selectedBackground() {
        return theme.selectedBackground();
    }

    public Color textPrimary() {
        return theme.textPrimary();
    }

    public Color textMuted() {
        return theme.textMuted();
    }

    public Color accent() {
        return theme.accent();
    }

    public Color headerBackground() {
        return theme.headerBackground();
    }

    public Color buttonBackground() {
        return theme.buttonBackground();
    }

    public Color buttonHover() {
        return theme.buttonHover();
    }

    public Color grid() {
        return theme.grid();
    }
}
