package ru.dev.prizrakk.cookiesbot.manager;

import org.yaml.snakeyaml.Yaml;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class ConfigManager extends Utils {
    private static final String CONFIG_NAME = "config.yml";
    private File configFile;
    private Map<String, Object> properties;

    public ConfigManager() {
        this.configFile = new File(CONFIG_NAME);

        if (!this.configFile.exists()) {
            extractDefaultConfig();
        }

        loadConfig();
    }

    // 📌 Копирование config.yml из ресурсов, если файла нет
    private void extractDefaultConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            if (inputStream == null) {
                getLogger().error("Default config.yml not found in resources!");
                return;
            }

            Files.copy(inputStream, this.configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            getLogger().info("Config copied to: " + this.configFile.getAbsolutePath());
        } catch (IOException e) {
            getLogger().error("Failed to extract config", e);
        }
    }

    // 📌 Загрузка конфига
    private void loadConfig() {
        try (InputStream inputStream = new FileInputStream(this.configFile)) {
            Yaml yaml = new Yaml();
            this.properties = yaml.load(inputStream);
            //getLogger().debug("Config loaded: " + this.configFile.getAbsolutePath());
        } catch (IOException e) {
            getLogger().error("Failed to load config", e);
        }
    }

    // 📌 Получение свойства
    public String getProperty(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> temp = this.properties;

        for (int i = 0; i < keys.length - 1; i++) {
            temp = (Map<String, Object>) temp.get(keys[i]);
        }
        Object value = temp.get(keys[keys.length - 1]);
        return value != null ? value.toString() : null;
    }
    public Map<String, Object> getMap(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> temp = this.properties;

        for (int i = 0; i < keys.length - 1; i++) {
            temp = (Map<String, Object>) temp.get(keys[i]);
            if (temp == null) return null; // Если путь не найден
        }

        Object value = temp.get(keys[keys.length - 1]);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }


    // 📌 Установка свойства
    public void setProperty(String key, String value) {
        String[] keys = key.split("\\.");
        Map<String, Object> temp = this.properties;

        for (int i = 0; i < keys.length - 1; i++) {
            temp = (Map<String, Object>) temp.get(keys[i]);
        }
        temp.put(keys[keys.length - 1], value);
    }

    // 📌 Сохранение изменений в config.yml
    public void saveConfig() {
        try (FileWriter writer = new FileWriter(this.configFile)) {
            Yaml yaml = new Yaml();
            yaml.dump(this.properties, writer);
            getLogger().info("Config saved successfully!");
        } catch (IOException e) {
            getLogger().error("Failed to save config", e);
        }
    }
}
