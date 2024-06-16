package ru.dev.prizrakk.cookiesbot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    private final Database database;
    public DatabaseUtils(Database database) {
        this.database = database;
    }
    public ExpVariable getPlayerStatsFromDatabase(String userID, String guildID) throws SQLException {
        ExpVariable expVariable = database.findExpStatsByNICK(userID, guildID);
        if (expVariable == null) {
            expVariable = new ExpVariable(userID, guildID, 0, 175, 0);
            database.createUserExpStats(expVariable);
        }
        return expVariable;
    }
    // Вставка нового метода в DatabaseUtils


}
