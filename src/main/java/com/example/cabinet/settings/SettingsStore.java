package com.example.cabinet.settings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON рядом с рабочей директорией. Если файла нет или он повреждён — остаются значения по умолчанию.
 */
public final class SettingsStore {
    public static final Path FILE = Path.of(System.getProperty("user.dir"), "user-settings.json");

    private SettingsStore() {
    }

    public static AppSettings load() {
        AppSettings settings = new AppSettings();
        if (!Files.isRegularFile(FILE)) {
            return settings;
        }
        try {
            String json = Files.readString(FILE, StandardCharsets.UTF_8);
            settings.setCabinetMode(readEnum(json, "cabinetMode", settings.getCabinetMode()));
            settings.setDensity(readEnum(json, "density", settings.getDensity()));
            settings.setTheme(readEnum(json, "theme", settings.getTheme()));
            settings.setDividerLocation(readInt(json, "dividerLocation", settings.getDividerLocation()));
        } catch (IOException ex) {
            System.err.println("Не удалось прочитать настройки, используются значения по умолчанию: " + ex.getMessage());
        }
        return settings;
    }

    public static void save(AppSettings settings) {
        String json = """
                {
                  "cabinetMode": "%s",
                  "density": "%s",
                  "theme": "%s",
                  "dividerLocation": %d
                }
                """.formatted(
                settings.getCabinetMode().name(),
                settings.getDensity().name(),
                settings.getTheme().name(),
                settings.getDividerLocation()
        );
        try {
            Files.writeString(FILE, json, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.err.println("Не удалось сохранить настройки: " + ex.getMessage());
        }
    }

    private static <E extends Enum<E>> E readEnum(String json, String key, E fallback) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([A-Za-z0-9_]+)\"").matcher(json);
        if (!matcher.find()) {
            return fallback;
        }
        try {
            return Enum.valueOf(fallback.getDeclaringClass(), matcher.group(1));
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    private static int readInt(String json, String key, int fallback) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        if (!matcher.find()) {
            return fallback;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
