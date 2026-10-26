package com.example.cabinet.model;

import java.time.LocalDate;
import java.util.Objects;

/** Строка списка документов. Данные демонстрационные, только для UI. */
public record DocumentItem(String number, String title, LocalDate date, String status) {

    public DocumentItem {
        number = requireText(number, "номер");
        title = requireText(title, "название");
        status = requireText(status, "статус");
        Objects.requireNonNull(date, "дата");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Пустое поле документа: " + field);
        }
        return value;
    }
}
