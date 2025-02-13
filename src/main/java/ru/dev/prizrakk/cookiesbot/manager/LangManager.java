package ru.dev.prizrakk.cookiesbot.manager;

import net.dv8tion.jda.api.entities.Member;
import org.yaml.snakeyaml.Yaml;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LangManager extends Utils {
    private static final String LANG_DIRECTORY = "lang/";
    private static final String DEFAULT_LANG = "Russia";
    private static final Yaml yaml = new Yaml();
    private static Map<String, Map<String, Object>> languages = new HashMap<>();

    public static void loadLanguages() {
        File langDir = new File(LANG_DIRECTORY);
        if (!langDir.exists()) {
            getLogger().warn("The folder with languages was not found, creating a new one: " + LANG_DIRECTORY);
            langDir.mkdir();
            downloadLanguage();
        }

        File[] langFiles = langDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (langFiles == null) {
            getLogger().warn("No language files found in directory: " + LANG_DIRECTORY);
            return;
        }

        for (File file : langFiles) {
            String langName = file.getName().replace(".yml", "");
            try (InputStream inputStream = new FileInputStream(file)) {
                Map<String, Object> rawData = yaml.load(inputStream);
                if (rawData == null) {
                    getLogger().warn("Empty language file: " + file.getName());
                    continue;
                }

                Map<String, Object> data = new HashMap<>();
                flattenMap("", rawData, data);

                languages.put(langName, data);
                getLogger().info("Language data loaded for: " + langName);
            } catch (IOException e) {
                getLogger().error("Error loading language data for " + langName + ": ", e);
            }
        }
    }

    /**
     * Рекурсивно превращает вложенный Map в плоский Map<String, String>
     */
    // Измененный метод flattenMap с обработкой ClassCastException и отдельными проверками для Boolean и Number
    private static void flattenMap(String prefix, Map<String, Object> source, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            try {
                if (value instanceof Map) {
                    flattenMap(key, (Map<String, Object>) value, target);
                } else {
                    target.put(key, value); // Теперь сохраняем Object
                }
            } catch (ClassCastException e) {
                getLogger().error("Error parsing key: " + key + " with value: " + value, e);
                target.put(key, String.valueOf(value));
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
    public static Map<String, Object> getLanguageProperties(String langKey) {
        return languages.getOrDefault(langKey, new HashMap<>()); // Возвращаем свойства языка или пустую Map
    }


    private static void downloadLanguage() {
        String baseUrl = "https://mirror.dev-prizrakk.ru/cookiesteam/cookiesbot/lang/";
        Path langDirectory = Paths.get(LANG_DIRECTORY);

        if (!Files.exists(langDirectory)) {
            try {
                Files.createDirectory(langDirectory);
            } catch (IOException e) {
                getLogger().error("Error creating lang folder:", e);
                return;
            }
        }

        String[] langFiles = {"Russia.yml", "English.yml"};

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
                getLogger().error("Error downloading file " + langFile, e);
            }
        }
    }

    public static String getMessage(String lang, String key) {
        try {
            Map<String, Object> properties = languages.get(lang);
            if (properties == null) {
                getLogger().warn("Language not found: " + lang);
                return key;
            }

            Object value = properties.get(key);
            if (value instanceof String) {
                return (String) value;
            } else if (value instanceof List) {
                return String.join("\n", (List<String>) value); // Склеиваем массив в строку
            } else {
                getLogger().warn("Unsupported value type for key: " + key);
                return key;
            }
        } catch (Exception error) {
            getLogger().error("Error getting message for lang: " + lang + ", key: " + key, error);
            return key;
        }
    }




    public static String getMessage(Member member, String key) {
        String lang = DEFAULT_LANG;
        return getMessage(lang, key);
    }

    public static String getMessage(String key) {
        return getMessage(DEFAULT_LANG, key);
    }
}
