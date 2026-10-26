package com.example.cabinet.data;

import com.example.cabinet.model.DocumentItem;
import com.example.cabinet.model.Folder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Фиксированный набор папок и документов. Сети нет: «Обновить» перечитывает этот же мок.
 */
public final class DemoDataRepository {
    private static final String NEW = "Новый";
    private static final String REVIEW = "На согласовании";
    private static final String SIGNED = "Подписан";
    private static final String REJECTED = "Отклонён";
    private static final String ARCHIVED = "В архиве";
    private static final String CONTROL = "На контроле";

    private final List<Folder> roots;
    private final Map<String, List<DocumentItem>> documents;

    public DemoDataRepository() {
        roots = buildFolders();
        documents = buildDocuments();
    }

    public List<Folder> folders() {
        return roots;
    }

    public List<DocumentItem> documentsOf(String folderId) {
        return documents.getOrDefault(folderId, List.of());
    }

    public Optional<Folder> find(String folderId) {
        if (folderId == null) {
            return Optional.empty();
        }
        for (Folder root : roots) {
            Optional<Folder> found = findIn(root, folderId);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    /** Для режима панелей: вложенный раздел сворачивается к корневой папке. */
    public Optional<Folder> topLevelFolder(String folderId) {
        for (Folder root : roots) {
            if (findIn(root, folderId).isPresent()) {
                return Optional.of(root);
            }
        }
        return Optional.empty();
    }

    private static Optional<Folder> findIn(Folder folder, String folderId) {
        if (folder.id().equals(folderId)) {
            return Optional.of(folder);
        }
        for (Folder child : folder.children()) {
            Optional<Folder> found = findIn(child, folderId);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    private static List<Folder> buildFolders() {
        return List.of(
                new Folder("inbox", "Входящие", List.of(
                        new Folder("inbox-unread", "Непрочитанные"),
                        new Folder("inbox-today", "За сегодня")
                )),
                new Folder("control", "На контроле"),
                new Folder("approval", "На согласование", List.of(
                        new Folder("approval-urgent", "Срочные")
                )),
                new Folder("outbox", "Исходящие"),
                new Folder("archive", "Архив", List.of(
                        new Folder("archive-2025", "2025 год"),
                        new Folder("archive-2026", "2026 год")
                ))
        );
    }

    private static Map<String, List<DocumentItem>> buildDocuments() {
        Map<String, List<DocumentItem>> map = new LinkedHashMap<>();
        map.put("inbox", rows(new String[][]{
                {"ВХ-1204", "Заявка на закупку бумаги", "2026-10-01", NEW},
                {"ВХ-1211", "Письмо о переносе поставки", "2026-10-02", NEW},
                {"ВХ-1218", "Акт сверки за сентябрь", "2026-10-03", REVIEW},
                {"ВХ-1225", "Служебная записка об отпуске", "2026-10-06", REVIEW},
                {"ВХ-1232", "Счёт на оплату № 448", "2026-10-07", NEW},
                {"ВХ-1239", "Протокол разногласий к договору", "2026-10-08", REJECTED},
                {"ВХ-1246", "Уведомление о планёрке", "2026-10-09", SIGNED},
                {"ВХ-1253", "Отчёт курьера за неделю", "2026-10-10", NEW}
        }));
        map.put("inbox-unread", rows(new String[][]{
                {"ВХ-1301", "Запрос коммерческого предложения", "2026-10-20", NEW},
                {"ВХ-1308", "Претензия по срокам монтажа", "2026-10-21", NEW},
                {"ВХ-1315", "Список участников совещания", "2026-10-21", NEW},
                {"ВХ-1322", "Скан доверенности", "2026-10-22", NEW},
                {"ВХ-1329", "Письмо из бухгалтерии", "2026-10-22", NEW}
        }));
        map.put("inbox-today", rows(new String[][]{
                {"ВХ-1402", "Заявка на пропуск посетителя", "2026-10-26", NEW},
                {"ВХ-1409", "График дежурств на ноябрь", "2026-10-26", REVIEW},
                {"ВХ-1416", "Накладная на канцтовары", "2026-10-26", NEW},
                {"ВХ-1423", "Памятка по охране труда", "2026-10-26", SIGNED},
                {"ВХ-1430", "Письмо о смене реквизитов", "2026-10-26", NEW}
        }));
        map.put("control", rows(new String[][]{
                {"КН-2104", "Поручение: ответить контрагенту", "2026-09-18", CONTROL},
                {"КН-2112", "Поручение: сверить спецификацию", "2026-09-22", CONTROL},
                {"КН-2119", "Контроль срока договора аренды", "2026-09-28", CONTROL},
                {"КН-2126", "Поручение: подготовить смету", "2026-10-02", REVIEW},
                {"КН-2133", "Контроль исполнения заявки № 77", "2026-10-08", CONTROL},
                {"КН-2140", "Поручение: закрыть акт", "2026-10-14", SIGNED},
                {"КН-2147", "Напоминание о дедлайне отчёта", "2026-10-19", CONTROL}
        }));
        map.put("approval", rows(new String[][]{
                {"СГ-3011", "Договор поставки мебели", "2026-09-12", REVIEW},
                {"СГ-3018", "Приказ о командировке", "2026-09-16", REVIEW},
                {"СГ-3025", "Смета ремонта переговорной", "2026-09-20", REJECTED},
                {"СГ-3032", "Положение о премировании", "2026-09-25", REVIEW},
                {"СГ-3039", "Дополнительное соглашение № 3", "2026-10-01", SIGNED},
                {"СГ-3046", "Заявка на обучение сотрудников", "2026-10-05", REVIEW},
                {"СГ-3053", "Регламент приёма корреспонденции", "2026-10-09", REVIEW},
                {"СГ-3060", "Счёт-договор на связь", "2026-10-13", NEW},
                {"СГ-3067", "Лист согласования бюджета", "2026-10-17", REVIEW}
        }));
        map.put("approval-urgent", rows(new String[][]{
                {"СГ-3104", "Срочно: акт разногласий", "2026-10-23", REVIEW},
                {"СГ-3111", "Срочно: доверенность на получение", "2026-10-24", REVIEW},
                {"СГ-3118", "Срочно: письмо в банк", "2026-10-24", NEW},
                {"СГ-3125", "Срочно: спецификация к тендеру", "2026-10-25", REVIEW},
                {"СГ-3132", "Срочно: приказ о замещении", "2026-10-25", REJECTED},
                {"СГ-3139", "Срочно: протокол закупки", "2026-10-26", REVIEW}
        }));
        map.put("outbox", rows(new String[][]{
                {"ИС-4102", "Ответ на претензию поставщика", "2026-09-30", SIGNED},
                {"ИС-4109", "Сопроводительное письмо к акту", "2026-10-03", SIGNED},
                {"ИС-4116", "Запрос закрывающих документов", "2026-10-06", NEW},
                {"ИС-4123", "Приглашение на встречу", "2026-10-08", SIGNED},
                {"ИС-4130", "Уведомление об изменении графика", "2026-10-11", REVIEW},
                {"ИС-4137", "Гарантийное письмо", "2026-10-15", SIGNED},
                {"ИС-4144", "Отказ от дополнительных работ", "2026-10-18", REJECTED},
                {"ИС-4151", "Благодарность подрядчику", "2026-10-21", SIGNED}
        }));
        map.put("archive", rows(new String[][]{
                {"АР-5001", "Годовой отчёт канцелярии", "2025-12-20", ARCHIVED},
                {"АР-5008", "Реестр договоров", "2026-01-15", ARCHIVED},
                {"АР-5015", "Опись дел за квартал", "2026-04-02", ARCHIVED},
                {"АР-5022", "Журнал входящих", "2026-06-18", ARCHIVED},
                {"АР-5029", "Номенклатура дел", "2026-07-01", ARCHIVED},
                {"АР-5036", "Акт об уничтожении черновиков", "2026-08-12", ARCHIVED}
        }));
        map.put("archive-2025", rows(new String[][]{
                {"АР-5510", "План закупок 2025", "2025-02-11", ARCHIVED},
                {"АР-5517", "Итоги инвентаризации", "2025-05-19", ARCHIVED},
                {"АР-5524", "Штатное расписание", "2025-07-01", ARCHIVED},
                {"АР-5531", "Отчёт по командировкам", "2025-09-14", ARCHIVED},
                {"АР-5538", "Реестр доверенностей", "2025-11-03", ARCHIVED},
                {"АР-5545", "Закрытие года: пояснения", "2025-12-28", ARCHIVED}
        }));
        map.put("archive-2026", rows(new String[][]{
                {"АР-5603", "Январская опись входящих", "2026-01-31", ARCHIVED},
                {"АР-5610", "Протокол планёрки февраля", "2026-02-27", ARCHIVED},
                {"АР-5617", "Квартальный акт сверки", "2026-04-04", ARCHIVED},
                {"АР-5624", "Реестр исходящих, II квартал", "2026-06-30", ARCHIVED},
                {"АР-5631", "Отчёт по контролю поручений", "2026-08-15", ARCHIVED},
                {"АР-5638", "Сводка согласований, сентябрь", "2026-09-30", ARCHIVED},
                {"АР-5645", "Черновик описи за октябрь", "2026-10-20", REVIEW}
        }));
        return Map.copyOf(map);
    }

    private static List<DocumentItem> rows(String[][] raw) {
        List<DocumentItem> items = new ArrayList<>();
        for (String[] row : raw) {
            items.add(new DocumentItem(row[0], row[1], LocalDate.parse(row[2]), row[3]));
        }
        return List.copyOf(items);
    }
}
