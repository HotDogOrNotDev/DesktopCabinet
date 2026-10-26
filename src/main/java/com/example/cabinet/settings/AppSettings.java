package com.example.cabinet.settings;

import com.example.cabinet.ui.chrome.ColorTheme;
import com.example.cabinet.ui.chrome.UiDensity;

/** Локальные настройки окна. Меняются из меню и диалога, пишутся в JSON. */
public final class AppSettings {
    public enum CabinetMode {
        TREE("Дерево"),
        PANELS("Панели");

        private final String title;

        CabinetMode(String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }
    }

    private CabinetMode cabinetMode = CabinetMode.TREE;
    private UiDensity density = UiDensity.NORMAL;
    private ColorTheme theme = ColorTheme.LIGHT;
    private int dividerLocation = 280;

    public CabinetMode getCabinetMode() {
        return cabinetMode;
    }

    public void setCabinetMode(CabinetMode cabinetMode) {
        if (cabinetMode != null) {
            this.cabinetMode = cabinetMode;
        }
    }

    public UiDensity getDensity() {
        return density;
    }

    public void setDensity(UiDensity density) {
        if (density != null) {
            this.density = density;
        }
    }

    public ColorTheme getTheme() {
        return theme;
    }

    public void setTheme(ColorTheme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }

    public int getDividerLocation() {
        return dividerLocation;
    }

    public void setDividerLocation(int dividerLocation) {
        if (dividerLocation >= 120) {
            this.dividerLocation = dividerLocation;
        }
    }
}
