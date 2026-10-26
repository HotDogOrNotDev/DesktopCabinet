package com.example.cabinet.ui.chrome;

/**
 * Плотность интерфейса. Все размеры считаются из rowScale и fontDelta,
 * чтобы дерево, панели, таблица и кнопки менялись вместе.
 */
public enum UiDensity {
    COMPACT("Компактная", 0.78, -3),
    NORMAL("Обычная", 1.0, 0),
    COMFORTABLE("Свободная", 1.32, 4);

    private final String title;
    private final double rowScale;
    private final int fontDelta;

    UiDensity(String title, double rowScale, int fontDelta) {
        this.title = title;
        this.rowScale = rowScale;
        this.fontDelta = fontDelta;
    }

    public String title() {
        return title;
    }

    public double rowScale() {
        return rowScale;
    }

    public int fontDelta() {
        return fontDelta;
    }

    public int scale(int basePx) {
        return Math.max(1, (int) Math.round(basePx * rowScale));
    }
}
