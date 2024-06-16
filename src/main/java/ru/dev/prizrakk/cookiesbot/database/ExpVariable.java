package ru.dev.prizrakk.cookiesbot.database;

public class ExpVariable {
    public ExpVariable(String userID, String guildID, int exp, int maxExp, int level) {
        this.guildID = guildID;
        this.userID = userID;
        this.exp = exp;
        this.maxExp = maxExp;
        this.level = level;
    }

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public int getMaxExp(){
        return maxExp;
    }
    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    private String userID;
    private String guildID;
    private int exp;
    private int maxExp;
    private int level;
}
