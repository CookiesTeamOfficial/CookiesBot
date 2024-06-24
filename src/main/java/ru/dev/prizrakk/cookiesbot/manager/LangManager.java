package ru.dev.prizrakk.cookiesbot.manager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class LangManager {
    private static final String LANG_DIRECTORY = "lang/";
    private static final String DEFAULT_LANG = "Russia"; // Язык по умолчанию

    private static Map<String, Properties> languages = new HashMap<>();
    public static Map<String, Properties> getLanguages() {
        return languages;
    }

    public static void loadLanguages() {
        File langDir = new File(LANG_DIRECTORY);
        if (!langDir.exists()) {
            System.out.println("Папка с языками не найдена, создаю новую: " + LANG_DIRECTORY);
            langDir.mkdir();
            createDefaultLangFiles();
        }

        File[] langFiles = langDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".lang"));
        if (langFiles == null) {
            System.err.println("Нет языковых файлов в директории: " + LANG_DIRECTORY);
            return;
        }

        for (File file : langFiles) {
            String langName = file.getName().replace(".lang", "");
            Properties properties = new Properties();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                StringBuilder valueBuilder = new StringBuilder();
                String line;
                String key = null;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue; // Пропуск пустых строк и комментариев

                    if (line.contains("=")) {
                        if (key != null) {
                            properties.put(key, valueBuilder.toString().trim());
                            valueBuilder.setLength(0);
                        }
                        String[] parts = line.split("=", 2);
                        key = parts[0].trim();
                        valueBuilder.append(parts[1].trim());
                    } else if (key != null) {
                        valueBuilder.append("\n").append(line.trim());
                    }
                }
                if (key != null) {
                    properties.put(key, valueBuilder.toString().trim());
                }
                languages.put(langName, properties);
                System.out.println("Языковые данные загружены для языка: " + langName);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки языковых данных для языка " + langName + ": " + e.getMessage());
            }
        }
    }

    public static void reloadLanguages() {
        languages.clear();
        loadLanguages();
    }

    public static Set<String> getAvailableLanguages() {
        return languages.keySet();
    }

    private static void createDefaultLangFiles() {
        createLangFile("Russia.lang", new String[]{
                "welcome.message=Добро пожаловать на сервер!",
                "goodbye.message=До свидания!",
                "multiline.message=Это многострочное сообщение на русском языке.\nСледующая строка этого сообщения также должна быть учтена."
        });

        createLangFile("English.lang", new String[]{
                "welcome.message=Welcome to the server!",
                "goodbye.message=Goodbye!",
                "multiline.message=This is a multi-line message in English.\nThe next line of this message should also be considered."
        });
    }

    private static void createLangFile(String fileName, String[] content) {
        try {
            File file = new File(LANG_DIRECTORY + fileName);
            if (!file.exists()) {
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(LANG_DIRECTORY + fileName))) {
                    for (String line : content) {
                        writer.write(line);
                        writer.newLine();
                    }
                    System.out.println("Файл создан: " + fileName);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания файла " + fileName + ": " + e.getMessage());
        }
    }

    public static String getMessage(String lang, String key) {
        Properties properties = languages.get(lang);
        if (properties != null) {
            String value = properties.getProperty(key);
            if (value != null) {
                return value.replace("\\n", "\n");
            }
        } else {
            System.err.println("Языковой файл не найден для языка: " + lang);
        }
        return key; // Вернуть ключ, если сообщение не найдено
    }

    public static String getMessage(String key) {
        return getMessage(DEFAULT_LANG, key); // Получить сообщение на языке по умолчанию
    }
}
