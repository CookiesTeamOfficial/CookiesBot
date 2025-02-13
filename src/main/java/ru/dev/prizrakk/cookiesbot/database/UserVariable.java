package ru.dev.prizrakk.cookiesbot.database;

//GDBV - Global DataBase Variable
public class UserVariable {
    private String UUID;
    private String staff;
    private String achievements;
    private String lang;
    private int balance;
    private int warn_count;
    private int ban;

    public UserVariable(String UUID, String staff, String achievements, String lang, int balance, int warn_count, int ban) {
        this.UUID = UUID;
        this.staff = staff;
        this.achievements = achievements;
        this.lang = lang;
        this.balance = balance;
        this.warn_count = warn_count;
        this.ban = ban;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getWarn_count() {
        return warn_count;
    }

    public void setWarn_count(int warn_count) {
        this.warn_count = warn_count;
    }

    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
    }
}
