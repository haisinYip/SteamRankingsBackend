package com.steamrankings.service.database;

import java.sql.*;

import com.steamrankings.service.core.Application;

public class DBConnector {
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    private final String serverName = Application.CONFIG.getProperty("server");
    private final String port = Application.CONFIG.getProperty("mysql_port");
    private final String databaseName = Application.CONFIG.getProperty("mysql_database");
    private final String username = Application.CONFIG.getProperty("mysql_username");
    private final String password = Application.CONFIG.getProperty("mysql_password");

    public DBConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);
            ;
            statement = connection.createStatement();

        } catch (Exception ex) {
            System.out.println("Connection failed");
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet readData(String table, String[] columns) {
        try {
            String query = "SELECT * FROM " + table;
            results = statement.executeQuery(query);

            for (int i = 0; i < columns.length; i++)
                System.out.print(columns[i] + " | ");

            System.out.println();

            while (results.next()) {
                for (int i = 0; i < columns.length; i++)
                    System.out.print(results.getString(columns[i]) + " | ");

                System.out.println();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (connection != null) {
                    // connection.close();
                    // statement.close();
                    // results.close();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return results;
    }

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
                // System.out.println(query);
            }
        } catch (Exception ex) {
            System.out.println("Failed to update database");
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (connection != null) {
                    //connection.close();
                    //statement.close();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
