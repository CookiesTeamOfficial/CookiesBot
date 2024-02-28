package ru.dev.prizrakk.database;

import ru.dev.prizrakk.Main;
import ru.dev.prizrakk.manager.ConfigManager;
import ru.dev.prizrakk.manager.LoggerManager;

import java.sql.*;


public class Database {
    private static Main main;
    public Database(Main main) {
        Database.main = main;
    }

    private static Connection connection;


    LoggerManager log = new LoggerManager();

    public Connection getConnection() throws  SQLException{

        if(connection != null){
            return connection;
        }

        ConfigManager config = new ConfigManager();
        //String url = config.getProperty("jdbc");
        //String user = config.getProperty("login");
        //String password = config.getProperty("password");

        String url = "jdbc:sqlite:database.db";


        connection = DriverManager.getConnection(url);
        log.info("Подключение успешно произведено!");

        return connection;
    }

    public void initializeDatabase() throws SQLException{
        Statement statement = getConnection().createStatement();
        // SQL Запрос
        log.info("Проверка таблицы user_info");
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
        log.info("Проверка таблицы settings");
        String settings = "CREATE TABLE IF NOT EXISTS `settings` (\n" +
                "\t`dev` VARCHAR(128),\n" +
                "\t`owner` VARCHAR(128),\n" +
                "\t`audit_message` VARCHAR(128),\n" +
                "\t`audit_manager` VARCHAR(128),\n" +
                "\t`audit_blacklist` VARCHAR(128),\n" +
                "\t`balance` VARCHAR(128)\n" +
                ");";
        statement.execute(settings);
        log.info("Проверка таблицы mutes");
        String mutes = "CREATE TABLE IF NOT EXISTS mutes (\n" +
                "\t`id` INTEGER PRIMARY KEY,\n" +
                "\t`userId` BIGINT,\n" +
                "\t`endTime` TEXT\n" +
                "\t);";
        statement.execute(mutes);

        statement.close();
        log.info("Проверка базы данных прошло успешно!");
    }
    public UserVariable findPlayerStatsByNICK(String UUID) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM user_info WHERE UUID = ?");
        statement.setInt(1, Integer.parseInt(UUID));

        ResultSet resultSet = statement.executeQuery();

        UserVariable UserVariable;
        if(resultSet.next()){

            //GDBV = new GDBV(resultSet.getString("nick"), resultSet.getInt(), resultSet.getString("prefix"), resultSet.getInt("rep"), resultSet.getInt("deaths"), resultSet.getInt("kills"), resultSet.getLong("blocks_broken"), resultSet.getDouble("balance"), resultSet.getDate("last_login"), resultSet.getDate("last_logout"));
            UserVariable = new UserVariable(resultSet.getString("name"), resultSet.getInt("balance"), resultSet.getInt("warn_count"), resultSet.getInt("level"), resultSet.getInt("xp"), resultSet.getInt("ban") ,resultSet.getString("UUID"));
            //String name, int id, int balance, int warn_count, int level, int xp, int ban, int UUID
            statement.close();

            return UserVariable;
        }


        statement.close();

        return null;
    }

    /*
                "\t`id` INT,\n" +
                "\t`UUID` INT,\n" +
                "\t`name` VARCHAR(128) CHARACTER SET utf8 COLLATE utf8_general_ci,\n" +
                "\t`balance` INT,\n" +
                "\t`xp` INT,\n" +
                "\t`level` INT,\n" +
                "\t`warn_count` INT,\n" +
                "\t`ban` INT\n" +
                ");";
     */
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