package ru.dev.prizrakk;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.dev.prizrakk.command.prefix.fun.Dogs;
import ru.dev.prizrakk.command.prefix.fun.Kawaii;
import ru.dev.prizrakk.command.prefix.system.Api;
import ru.dev.prizrakk.command.slash.music.*;
import ru.dev.prizrakk.command.slash.server.Avatar;
import ru.dev.prizrakk.command.slash.server.ServerInfo;
import ru.dev.prizrakk.command.slash.server.UserInfo;
import ru.dev.prizrakk.command.slash.server.moderation.Mute;
import ru.dev.prizrakk.database.Database;
import ru.dev.prizrakk.events.OnJoin;
import ru.dev.prizrakk.events.OnLeft;
import ru.dev.prizrakk.events.logs.MessageAudit;
import ru.dev.prizrakk.manager.CommandManager;
import ru.dev.prizrakk.manager.ConsoleManager;
import ru.dev.prizrakk.manager.LoggerManager;
import ru.dev.prizrakk.util.Config;

import java.sql.SQLException;

public class Main {
    static JDA jda;
    LoggerManager log = new LoggerManager();
    public static void main(String[] args) {

        Main main = new Main();
        main.jda();
        ConsoleManager consoleManager = new ConsoleManager();
        consoleManager.console();

        /* =============== */
        /* DataBase Loader */
        /* =============== */
        main.JDBCConnect();

    }
    public void jda() {
        jda = JDABuilder.createDefault(Config.token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("PornHub | Eve Elfi в лесу с парнем 4k"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                .build();

        /* ======================= */
        /* Slash Command           */
        /* ======================= */
        CommandManager commandManager = new CommandManager();
        /* music command */
        commandManager.add(new Play());
        commandManager.add(new Skip());
        commandManager.add(new Stop());
        commandManager.add(new NowPlaying());
        commandManager.add(new Queue());
        commandManager.add(new Repeat());
        /* server */
        commandManager.add(new ServerInfo());
        commandManager.add(new UserInfo());
        commandManager.add(new Avatar());
        /* moderation */
        commandManager.add(new Mute());

        jda.addEventListener(commandManager);

        /* ======================= */
        /* Prefix Command          */
        /* ======================= */
        /* fun */
        jda.addEventListener(new Dogs());
        jda.addEventListener(new Kawaii());
        /* system */
        jda.addEventListener(new Api());

        /* ======================= */
        /* Event                   */
        /* ======================= */
        /* logger */
        jda.addEventListener(new MessageAudit());
        jda.addEventListener(new OnLeft());
        jda.addEventListener(new OnJoin());
    }
    public void JDBCConnect() {
        try {
            Database database = new Database(this);
            database.initializeDatabase();
        } catch (SQLException ex) {
            log.error("Error Database!");
            ex.printStackTrace();
        }
    }
}