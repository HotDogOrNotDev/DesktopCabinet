package com.example.cabinet.ui.cabinet;

import com.example.cabinet.model.Folder;
import com.example.cabinet.ui.chrome.UiStyle;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * Левая колонка кабинета. Реализации подменяют друг друга,
 * правая таблица документов от режима не зависит.
 */
public interface Cabinet {
    void setFolders(List<Folder> folders);

    void setSelectionListener(Consumer<Folder> listener);

    void selectFolder(String folderId);

    JComponent getComponent();

    void applyStyle(UiStyle style);

    void refresh();
}
