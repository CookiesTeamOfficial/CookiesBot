package ru.dev.prizrakk.cookiesbot.database;

public class GuildVariable {
    private int id;
    private String UUID;
    private String dev;
    private String owner;
    private String lang;
    private String auditMessage;
    private String auditManager;
    private String auditBlacklist;
    private String balance;
    private int ban;

    public GuildVariable(String UUID, String dev, String owner, String lang, String auditMessage, String auditManager, String auditBlacklist, String balance, int ban) {
        this.UUID = UUID;
        this.dev = dev;
        this.owner = owner;
        this.lang = lang;
        this.auditMessage = auditMessage;
        this.auditManager = auditManager;
        this.auditBlacklist = auditBlacklist;
        this.balance = balance;
        this.ban = ban;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }

    public String getAuditManager() {
        return auditManager;
    }

    public void setAuditManager(String auditManager) {
        this.auditManager = auditManager;
    }

    public String getAuditBlacklist() {
        return auditBlacklist;
    }

    public void setAuditBlacklist(String auditBlacklist) {
        this.auditBlacklist = auditBlacklist;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
    }
}
