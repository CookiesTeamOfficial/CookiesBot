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

import java.util.Scanner;

public class PreLoad {
    ConfigManager configManager = new ConfigManager();
    private static final LoggerManager loggerManager = new LoggerManager();
    private JDA jda;
    public boolean preLoadToken() {
        loggerManager.log("info", "Token verification!");
        boolean ldCheck;
        if(configManager.getProperty("token").isEmpty()) {
            loggerManager.log("error","Token is not empty please edit config.properties");
            loggerManager.log("info" , "Do you want to fill out the token right now? (true/false)");
            Scanner scanner = new Scanner(System.in);
            String check = scanner.nextLine();
            if(check.equals("true")) {
                loggerManager.log("info", "Enter the bot's discord token:");
                String tokenC = scanner.nextLine();
                if(tokenC.isEmpty()) {
                    loggerManager.log("error", "The field for entering the discord bot token is empty. Please enter the token in config.properties!");
                    loggerManager.log("error", "Verification completed, the token has not been detected!");
                    ldCheck = false;
                }
                loggerManager.log("info", "A token has been detected, an entry in the configuration!");
                configManager.setProperty("token", tokenC);
                configManager.saveConfig();
                loggerManager.log("info", "The token has been recorded! Launching the Discord bot!");
                loggerManager.log("info", "The verification has been completed successfully!");
                ldCheck = true;

            }else{
                loggerManager.log("info", "You have chosen false, please enter the token in config.properties!");
                loggerManager.log("error", "Token not detected program crash!");
                loggerManager.log("error", "Verification completed, the token has not been detected!");
                ldCheck = false;
            }
        } else {
            loggerManager.log("info", "The verification has been completed successfully!");
            ldCheck = true;
        }
        return ldCheck;
    }
    public boolean preLoadDataBase() {
        //TODO
        return true;
    }
    public boolean preLoadOther() {
        //TODO
        return true;
    }
    public void jda() {
        String token = "";
        if (preLoadToken()) {
           token = configManager.getProperty("token");
        } else {
            System.exit(512);
        }
        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("BebraBotTV"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .build();
        CommandManager manager = new CommandManager();
        /* music command */
        //TODO

        /*Fun Command **/
        manager.add(new Calc());
        manager.add(new Dogs());
        manager.add(new HttpCat());
        manager.add(new Tyan());

        /*Предложения**/
        manager.add(new Suggest());
        jda.addEventListener(new SuggestModals());
        /* Ембеды **/
        manager.add(new Embed());
        jda.addEventListener(new EmbedModal());

        /* Информационые **/
        manager.add(new Help());
        manager.add(new ServerInfo());
        manager.add(new About());
        jda.addEventListener(manager);
    }
}
