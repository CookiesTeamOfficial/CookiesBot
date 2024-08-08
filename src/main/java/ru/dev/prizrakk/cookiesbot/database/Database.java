package ru.dev.prizrakk.cookiesbot.database;

import ru.dev.prizrakk.cookiesbot.manager.ConfigManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;

import java.sql.*;


public class Database extends Utils {

    private static Connection connection;

    public Connection getConnection() throws  SQLException{

        if(connection != null){
            return connection;
        }
        getLogger().debug("===================");
        getLogger().debug("Loading the Database");
        getLogger().debug("===================");
        ConfigManager config = new ConfigManager();
        //String url = config.getProperty("jdbc");
        //String user = config.getProperty("login");
        //String password = config.getProperty("password");

        String url = "jdbc:sqlite:database.db";


        connection = DriverManager.getConnection(url);
        getLogger().debug("Connection successful!");
        return connection;
    }

    public void initializeDatabase() throws SQLException{
        Statement statement = getConnection().createStatement();
        // SQL Запрос
        getLogger().debug("Checking the user_info table");
        String user_info = "CREATE TABLE IF NOT EXISTS `user_info` (\n" +
                "\t`id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`UUID` INT,\n" +
                "\t`name` VARCHAR(128),\n" +
                "\t`staff` VARCHAR(128),\n" +
                "\t`staff_key` VARCHAR(128),\n" +
                "\t`balance` INT,\n" +
                "\t`xp` INT,\n" +
                "\t`level` INT,\n" +
                "\t`warn_count` INT,\n" +
                "\t`ban` INT\n" +
                ");";
        statement.execute(user_info);
        getLogger().debug("Checking the guild_settings table");
        String settings = "CREATE TABLE IF NOT EXISTS `guild_settings` (\n" +
                "\t`id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`UUID` INT,\n" +
                "\t`dev` VARCHAR(128),\n" +
                "\t`owner` VARCHAR(128),\n" +
                "\t`lang` VARCHAR(128),\n" +
                "\t`audit_message` VARCHAR(128),\n" +
                "\t`audit_manager` VARCHAR(128),\n" +
                "\t`audit_blacklist` VARCHAR(128),\n" +
                "\t`balance` VARCHAR(128),\n" +
                "\t`ban` INT\n" +
                ");";
        statement.execute(settings);
        getLogger().debug("Checking the user_rank table");
        String user_rank = "CREATE TABLE IF NOT EXISTS `user_rank` (\n" +
                "\t`user_id` INTEGER,\n" +
                "\t`guild_id` INTEGER,\n" +
                "\t`exp` INT,\n" +
                "\t`maxExp` INT,\n" +
                "\t`level` INT,\n" +
                "\tPRIMARY KEY (`user_id`, `guild_id`)\n" +
                ");";
        statement.execute(user_rank);
        getLogger().debug("Check mutes table");
        String mutes = "CREATE TABLE IF NOT EXISTS mutes (\n" +
                "\t`id` INTEGER PRIMARY KEY,\n" +
                "\t`userId` BIGINT,\n" +
                "\t`endTime` TEXT\n" +
                "\t);";
        statement.execute(mutes);

        statement.close();
        getLogger().debug("Verification/Creation of database was successful!");
    }
    public GuildVariable findGuild(String UUID) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM guild_settings WHERE UUID = ?");
        statement.setString(1, UUID);
        ResultSet resultSet = statement.executeQuery();
        GuildVariable guildVariable;
        if (resultSet.next()) {
            guildVariable = new GuildVariable(
                    resultSet.getString("UUID"),
                    resultSet.getString("dev"),
                    resultSet.getString("owner"),
                    resultSet.getString("lang"),
                    resultSet.getString("audit_message"),
                    resultSet.getString("audit_manager"),
                    resultSet.getString("audit_blacklist"),
                    resultSet.getString("balance"),
                    resultSet.getInt("ban")
            );
            statement.close();
            return guildVariable;
        }
        statement.close();
        return null;
    }
    public UserVariable findPlayerStatsByNICK(String UUID) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM user_info WHERE UUID = ?");
        statement.setInt(1, Integer.parseInt(UUID));
        ResultSet resultSet = statement.executeQuery();
        UserVariable UserVariable;
        if(resultSet.next()){
            UserVariable = new UserVariable(
                    resultSet.getString("name"),
                    resultSet.getInt("balance"),
                    resultSet.getInt("warn_count"),
                    resultSet.getInt("level"),
                    resultSet.getInt("xp"),
                    resultSet.getInt("ban"),
                    resultSet.getString("UUID")
            );
            statement.close();

            return UserVariable;
        }


        statement.close();

        return null;
    }
    public ExpVariable findExpStatsByNICK(String userID, String guildID) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM user_rank WHERE user_id = ? AND guild_id = ?");
        statement.setString(1, userID);
        statement.setString(2, guildID);

        ResultSet resultSet = statement.executeQuery();

        ExpVariable expVariable;
        if(resultSet.next()){
            expVariable = new ExpVariable(
                    resultSet.getString("user_id"),
                    resultSet.getString("guild_id"),
                    resultSet.getInt("exp"),
                    resultSet.getInt("maxExp"),
                    resultSet.getInt("level")
            );

            statement.close();

            return expVariable;
        }


        statement.close();

        return null;
    }
    public void createUserExpStats(ExpVariable expVariable) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO user_rank(user_id, guild_id, exp, maxExp, level) VALUES (?, ?,?, ?, ?)");
        statement.setString(1, expVariable.getUserID());
        statement.setString(2, expVariable.getGuildID());
        statement.setDouble(3, expVariable.getExp());
        statement.setInt(4, expVariable.getMaxExp());
        statement.setInt(5, expVariable.getLevel());

        statement.executeUpdate();

        statement.close();

    }

    public void createUserStats(UserVariable UserVariable) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_stats( UUID, name, balance, xp, level, warn_count, ban) VALUES (?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, UserVariable.getUUID());
        statement.setString(2, UserVariable.getName());
        statement.setDouble(3, UserVariable.getBalance());
        statement.setInt(4, UserVariable.getXp());
        statement.setInt(5, UserVariable.getLevel());
        statement.setInt(6, UserVariable.getWarn_count());
        statement.setInt(7, UserVariable.getBan());

        statement.executeUpdate();

        statement.close();

    }
    public void createGuildStats(GuildVariable guildVariable) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO guild_settings(UUID, dev, owner, lang, audit_message, audit_manager, audit_blacklist, balance, ban) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, guildVariable.getUUID());
        statement.setString(2, guildVariable.getDev());
        statement.setString(3, guildVariable.getOwner());
        statement.setString(4, guildVariable.getLang());
        statement.setString(5, guildVariable.getAuditMessage());
        statement.setString(6, guildVariable.getAuditManager());
        statement.setString(7, guildVariable.getAuditBlacklist());
        statement.setString(8, guildVariable.getBalance());
        statement.setInt(9, guildVariable.getBan());

        statement.executeUpdate();
        statement.close();
    }

    public void updateGuildStats(GuildVariable guildVariable) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE guild_settings SET dev = ?, owner = ?, lang = ?, audit_message = ?, audit_manager = ?, audit_blacklist = ?, balance = ?, ban = ? WHERE UUID = ?");
        statement.setString(1, guildVariable.getDev());
        statement.setString(2, guildVariable.getOwner());
        statement.setString(3, guildVariable.getLang());
        statement.setString(4, guildVariable.getAuditMessage());
        statement.setString(5, guildVariable.getAuditManager());
        statement.setString(6, guildVariable.getAuditBlacklist());
        statement.setString(7, guildVariable.getBalance());
        statement.setInt(8, guildVariable.getBan());
        statement.setString(9, guildVariable.getUUID());

        statement.executeUpdate();
        statement.close();
    }
    public void updateUserExpStats(ExpVariable expVariable) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE user_rank SET exp = ?, maxExp = ?, level = ? WHERE user_id = ? AND guild_id = ?");
        statement.setDouble(1, expVariable.getExp());
        statement.setInt(2, expVariable.getMaxExp());
        statement.setInt(3, expVariable.getLevel());
        statement.setString(4, expVariable.getUserID());
        statement.setString(5, expVariable.getGuildID());

        statement.executeUpdate();

        statement.close();

    }
    public void updateUserStats(UserVariable UserVariable) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE user_info SET warn_count = ?, xp = ?, level = ?, name = ?, balance = ?, ban = ? WHERE UUID = ?");
        statement.setInt(1, UserVariable.getWarn_count());
        statement.setInt(2, UserVariable.getXp());
        statement.setInt(3, UserVariable.getLevel());
        statement.setString(4, UserVariable.getName());
        statement.setDouble(5, UserVariable.getBalance());
        statement.setInt(6, UserVariable.getBan());
        statement.setString(7, UserVariable.getUUID());

        statement.executeUpdate();

        statement.close();

    }
}