package com.steamrankings.service.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

public class DBConnector {
    private Connection connection;
    private Statement statement;
    private ResultSet results;
    private String lastQuery;

    private String serverName = null;
    private String port = null;
    private String databaseName = null;
    private String username = null;
    private String password = null;

    final public String TABLE_NAME_ACHIEVEMENTS = "achievements";
    final public String TABLE_NAME_GAMES = "games";
    final public String TABLE_NAME_PROFILES = "profiles";
    final public String TABLE_NAME_PROFILES_ACHIEVEMENTS = "profiles_has_achievements";
    final public String TABLE_NAME_PROFILES_GAMES = "profiles_has_games";
    
    final public int TABLE_ACHIEVEMENTS_COL_ID_INDEX = 1;
    final public int TABLE_ACHIEVEMENTS_COL_GAMES_ID_INDEX = 2;
    final public int TABLE_ACHIEVEMENTS_COL_NAMES_INDEX = 3;
    final public int TABLE_ACHIEVEMENTS_COL_DESCRIPTION_INDEX = 4;
    final public int TABLE_ACHIEVEMENTS_COL_UNLOCKED_ICON_URL_INDEX = 5;
    final public int TABLE_ACHIEVEMENTS_COL_LOCKED_ICON_URL_INDEX = 6;
    
    final public int TABLE_GAMES_COL_ID_INDEX = 1;
    final public int TABLE_GAMES_COL_NAME_INDEX = 2;
    final public int TABLE_GAMES_COL_ICON_URL_INDEX = 3;
    final public int TABLE_GAMES_COL_LOGO_URL_INDEX = 4;
    
    final public int TABLE_PROFILES_COL_ID_INDEX = 1;
    final public int TABLE_PROFILES_COL_COMMUNITY_ID_INDEX = 2;
    final public int TABLE_PROFILES_COL_PERSONA_NAME_INDEX = 3;
    final public int TABLE_PROFILES_COL_REAL_NAME_INDEX = 4;
    final public int TABLE_PROFILES_COL_LOC_COUNTRY_INDEX = 5;
    final public int TABLE_PROFILES_COL_LOC_PROVINCE_INDEX = 6;
    final public int TABLE_PROFILES_COL_LOC_CITY_INDEX = 7;
    final public int TABLE_PROFILES_COL_AVATAR_FULL_URL_INDEX = 8;
    final public int TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_INDEX = 9;
    final public int TABLE_PROFILES_COL_AVATAR_ICON_URL_INDEX = 10;
    
    final public String TABLE_PROFILES_COL_ID_LABEL = "id";
    final public String TABLE_PROFILES_COL_COMMUNITY_ID_LABEL = "community_id";
    final public String TABLE_PROFILES_COL_PERSONA_NAME_LABEL = "persona_name";
    final public String TABLE_PROFILES_COL_REAL_NAME_LABEL = "real_name";
    final public String TABLE_PROFILES_COL_LOC_COUNTRY_LABEL = "location_country";
    final public String TABLE_PROFILES_COL_LOC_PROVINCE_LABEL = "location_province";
    final public String TABLE_PROFILES_COL_LOC_CITY_LABEL = "location_city";
    final public String TABLE_PROFILES_COL_AVATAR_FULL_URL_LABEL = "avatar_full_url";
    final public String TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_LABEL = "avatar_medium_url";
    final public String TABLE_PROFILES_COL_AVATAR_ICON_URL_LABEL = "avatar_icon_url";
    
    final public int TABLE_PROFILES_ACHIEVEMENTS_COL_PROFILES_ID_INDEX = 1;
    final public int TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_ID_INDEX = 2;
    final public int TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_GAMES_ID_INDEX = 3;
    final public int TABLE_PROFILES_ACHIEVEMENTS_COL_UNLOCKED_TIMESTAMP_INDEX = 4;
    
    final public int TABLE_PROFILES_GAMES_COL_PROFILES_ID_INDEX = 1;
    final public int TABLE_PROFILES_GAMES_COL_GAMES_ID_INDEX = 2;
    final public int TABLE_PROFILES_GAMES_COL_TOTAL_PLAY_TIME_INDEX = 3;
    
    public DBConnector() {

        this.loadFromConfig();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);
            statement = connection.createStatement();

        } catch (Exception ex) {
            System.out.println("Connection failed");
            System.out.println(ex.getMessage());
        }
    }

    private void loadFromConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            String tmpdir = System.getProperty("java.io.tmpdir");
            input = new FileInputStream("config.properties");

            prop.load(input);

            serverName = prop.getProperty("server");
            port = prop.getProperty("mysql_port");
            databaseName = prop.getProperty("mysql_database");
            username = prop.getProperty("mysql_username");
            password = prop.getProperty("mysql_password");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ResultSet queryDB(String query) {
        try {
            this.lastQuery = query;
            results = statement.executeQuery(query);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    public ResultSet getTable(String table) {
        return this.queryDB("SELECT * FROM " + table + ";");
    }

    // make sure to set up data array matches schema
    public void burstAddToDB(String table, String[][] data) {
        String query = "";
        try {
            for (int i = 0; i < data.length; i++) {
                String insert = "INSERT INTO " + table + " " + "VALUES (";
                query = insert;
                for (int j = 0; j < data[i].length; j++) {
                    if (j == data[i].length - 1) {
                        query = query + "\"" + data[i][j] + "\")";
                        break;
                    }
                    query = query + "\"" + data[i][j] + "\"" + ",";

                }
                query = query + ";";
                this.lastQuery = query;
                statement.executeUpdate(query);
            }
        } catch (Exception ex) {
            System.out.println("Failed to burst add to database");
            ex.printStackTrace();
        }
    }

    // make sure column count matches
    public void addEntryToDB(String table, String[] row) {
        String query = "INSERT INTO " + table + " " + "VALUES (";
        for (int i = 0; i < row.length; i++)
            query = query + "\"" + row[i] + "\"" + ",";

        query = query.substring(0, query.length() - 1);
        query = query + ");";
        try {
            this.lastQuery = query;
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Add entry failed");
            e.printStackTrace();
        }
    }

    // unresolved bug
    // query is correct and was manually tested
    public void addEntryToDB(String table, HashMap<String, String> row) {
        String query = "INSERT INTO " + table + " " + "SET ";
        for (Entry<String, String> entry : row.entrySet())
            query = query + entry.getKey() + "=" + "\"" + entry.getValue() + "\"" + ",";
        query = query.substring(0, query.length() - 1);
        query = query + ";";
        try {
            this.lastQuery = query;
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Add entry failed");
            e.printStackTrace();
        }
    }

    public void updateEntry(String table, HashMap<String, String> list, String pkey, String pval) throws SQLException {
        String query = "UPDATE " + table + " SET ";
        for (Entry<String, String> entry : list.entrySet())
            query = query + entry.getKey() + "=" + "\"" + entry.getValue() + "\"" + ",";

        query = query.substring(0, query.length() - 1);
        query = query + " WHERE " + pkey + "=" + "\"" + pval + "\";";
        this.lastQuery = query;
        statement.executeUpdate(query);
    }

    public boolean primaryKeyExists(String table, String colIndex, String pkey) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + colIndex + "=" + "\"" + pkey + "\";";
        this.queryDB(query);
        // check if first row exists
        return results.first();
    }

    public ResultSet getData(String table, String[] columnIndexes) {
        String query = "SELECT ";
        for (int i = 0; i < columnIndexes.length; i++)
            query = query + columnIndexes[i] + ",";

        query = query.substring(0, query.length() - 1);
        query = query + " FROM " + table;
        this.lastQuery = query;
        return this.queryDB(query);
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                statement.close();
                results.close();
            }
        } catch (Exception ex) {
            System.out.println("Failed to close connection");
            ex.printStackTrace();
        }
    }

    public void print(String[] columnIndexes) throws SQLException {

        if (results.first()) {
            for (int i = 0; i < columnIndexes.length; i++)
                System.out.print(columnIndexes[i] + " | ");

            System.out.println();

            while (results.next()) {
                for (int i = 0; i < columnIndexes.length; i++)
                    System.out.print(results.getString(columnIndexes[i]) + " | ");

                System.out.println();
            }
        } else
            System.out.println("ResultSet is empty");
    }

    public void printLastQuery() {
        System.out.println("Query : " + this.lastQuery);
    }

    public ResultSet getEntry(String table, String columnIndex, String primaryKey) {
        String query = "SELECT * FROM " + table + " WHERE " + columnIndex + "=" + "\"" + primaryKey + "\";";
        return this.queryDB(query);
    }

    public int getCount(String table, String columnIndex, String primaryKey) throws SQLException {
        int count = 0;

        String query = "SELECT COUNT(" + columnIndex + ") AS res FROM " + table + " WHERE " + columnIndex + "=" + "\"" + primaryKey + "\";";
        this.queryDB(query);
        this.results.first();
        count = this.results.getInt(1);

        return count;
    }

    public int getNumberOfEntries(String table) throws SQLException {
        int count = 0;

        String query = "SELECT COUNT(*)" + " FROM " + table;
        this.queryDB(query);
        this.results.first();
        count = this.results.getInt(1);

        return count;
    }

    // deprecated
    public ResultSet readData(String table, String[] columnIndexes) {
        try {
            String query = "SELECT * FROM " + table;
            results = statement.executeQuery(query);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    // deprecated
    public void writeData(String table, String[][] data) {
        String query = "";
        try {

            for (int i = 0; i < data.length; i++) {
                String insert = "INSERT INTO " + table + " " + "VALUES (";
                query = insert;
                for (int j = 0; j < data[i].length; j++) {
                    if (j == data[i].length - 1) {
                        query = query + "'" + data[i][j] + "'";
                        break;
                    }
                    query = query + "'" + data[i][j] + "'" + ",";

                }
                query = query + ")";
                statement.executeUpdate(query);
            }
        } catch (Exception ex) {
            System.out.println("Failed to update database");
            ex.printStackTrace();
        }
    }
}