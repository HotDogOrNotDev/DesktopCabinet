package com.example.cabinet.model;

import java.util.List;

/**
 * Раздел кабинета. Дерево показывает children (один уровень в демо),
 * панели — только корневые папки.
 */
public record Folder(String id, String title, List<Folder> children) {

    public Folder {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Пустой id папки");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Пустое название папки");
        }
        children = List.copyOf(children == null ? List.of() : children);
    }

    public Folder(String id, String title) {
        this(id, title, List.of());
    }

    @Override
    public String toString() {
        return title;
    }
}
