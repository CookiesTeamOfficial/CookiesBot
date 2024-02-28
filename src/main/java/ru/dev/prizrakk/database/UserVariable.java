package ru.dev.prizrakk.database;

//GDBV - Global DataBase Variable
public class UserVariable {

    public UserVariable(String name, int balance, int warn_count, int level, int xp, int ban, String UUID) {
        this.name = name;
        this.balance = balance;
        this.warn_count = warn_count;
        this.level = level;
        this.xp = xp;
        this.ban = ban;
        this.UUID = UUID;
    }

    private final String name;
    private int balance;
    private int warn_count;
    private int level;
    private int xp;
    private int ban;
    private String UUID;

    public String getName() {
        return name;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
    }

    public String getUUID() {
        return UUID;
    }



}
