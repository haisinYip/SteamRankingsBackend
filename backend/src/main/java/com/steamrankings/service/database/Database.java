package com.steamrankings.service.database;

import org.javalite.activejdbc.Base;
import com.steamrankings.service.core.Initialization;

public class Database {

    public Database() {
    }

    public static void openDBConnection() {
        if (Base.hasConnection() == false) {
            Base.open("com.mysql.jdbc.Driver",
                    "jdbc:mysql://" + Initialization.CONFIG.getProperty("server")
                    + ":" + Initialization.CONFIG.getProperty("mysql_port")
                    + "/" + Initialization.CONFIG.getProperty("mysql_database")
                    + "?characterEncoding=utf8",
                    Initialization.CONFIG.getProperty("mysql_username"),
                    Initialization.CONFIG.getProperty("mysql_password"));
        }
    }

    public static void closeDBConnection() {
        if (Base.hasConnection()) {
            Base.close();
        }
    }
}
