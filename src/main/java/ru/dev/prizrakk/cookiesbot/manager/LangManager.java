package ru.dev.prizrakk.cookiesbot.manager;

import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LangManager extends Utils {
    private static final String LANG_DIRECTORY = "lang/";
    private static final String DEFAULT_LANG = "Russia"; // Язык по умолчанию

    private static Map<String, Properties> languages = new HashMap<>();
    public static Map<String, Properties> getLanguages() {
        return languages;
    }

    public static void loadLanguages() {
        File langDir = new File(LANG_DIRECTORY);
        if (!langDir.exists()) {
            getLogger().warn("The folder with languages was not found, I am creating a new one: " + LANG_DIRECTORY);
            langDir.mkdir();
            downloadLanguage();
        }

        File[] langFiles = langDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".lang"));
        if (langFiles == null) {
            getLogger().warn("There are no language files in the directory: " + LANG_DIRECTORY);
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
                getLogger().info("Language data loaded for language: " + langName);
            } catch (IOException e) {
                getLogger().error("Error loading language data for language " + langName + ": ", e);
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

    private static void downloadLanguage() {
        // URL, откуда будут загружаться файлы
        String baseUrl = "https://mirror.dev-prizrakk.ru/cookiesteam/cookiesbot/lang/";

        // Папка, куда будут сохранены файлы
        Path langDirectory = Paths.get("lang");

        // Создаем папку, если она не существует
        if (!Files.exists(langDirectory)) {
            try {
                Files.createDirectory(langDirectory);
            } catch (IOException e) {
                getLogger().error("Error when creating lang folder:", e);
                return;
            }
        }

        // Список имен файлов для загрузки
        String[] langFiles = new String[]{
                "Russia.lang",
                "English.lang"
                // Добавь здесь другие файлы, если нужно
        };

        for (String langFile : langFiles) {
            try (BufferedInputStream in = new BufferedInputStream(new URL(baseUrl + langFile).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(langDirectory.resolve(langFile).toFile())) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                getLogger().info(langFile + " successfully downloaded and saved in the lang folder.");
            } catch (IOException e) {
                getLogger().error("Error uploading file " + langFile, e);
            }
        }
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
                    getLogger().info("File created: " + fileName);
                }
            }
        } catch (IOException e) {
            getLogger().error("Error creating file " + fileName + ": ", e);
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
            getLogger().error("Language file not found for language: " + lang);
        }
        return key; // Вернуть ключ, если сообщение не найдено
    }

    public static String getMessage(String key) {
        return getMessage(DEFAULT_LANG, key); // Получить сообщение на языке по умолчанию
    }
}
