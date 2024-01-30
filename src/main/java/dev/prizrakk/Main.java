package dev.prizrakk;

import dev.prizrakk.commands.fun.*;
import dev.prizrakk.commands.info.About;
import dev.prizrakk.commands.info.Help;
import dev.prizrakk.commands.info.ServerInfo;
import dev.prizrakk.commands.info.Suggest;
import dev.prizrakk.commands.modals.EmbedModal;
import dev.prizrakk.commands.modals.SuggestModals;
import dev.prizrakk.manager.CommandManager;
import dev.prizrakk.manager.ConfigManager;
import dev.prizrakk.manager.LoggerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.File;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static LoggerManager loggerManager = new LoggerManager();
    private static File configFile;

    static JDA jda;
    public static void main(String[] args) {
        PreLoad preLoad = new PreLoad();
        ConfigManager configManager = new ConfigManager();
        configFile = new File("config.properties");
        if (!configFile.exists()) {
            configManager.setProperty("token", "");
            configManager.setProperty("jdbc", "jdbc:");
            configManager.setProperty("password", "password");
            configManager.setProperty("login", "user");
            configManager.saveConfig();
        }
        preLoad.jda();

    }

}