package org.example.server;

public final class ServerConfig {

    private ServerConfig() { }

    public static final String SERVICE_NAME = "AnnuaireService";
    public static final int RMI_PORT = 1099;


    public static final String ADMIN_PASSWORD = "000";

    //SQLite database URL
    public static final String DB_URL = "jdbc:sqlite:annuaire.db";
    public static final String DB_NAME = "annuaire.db";

}
