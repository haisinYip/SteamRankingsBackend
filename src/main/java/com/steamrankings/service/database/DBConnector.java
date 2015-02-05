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

	public DBConnector() {

		this.loadFromConfig();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);
			statement = connection.createStatement();

		} catch(Exception ex) {
			System.out.println("Connection failed");
			System.out.println(ex.getMessage());
		}
	}

	private void loadFromConfig() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			String tmpdir = System.getProperty("java.io.tmpdir");
			input = new FileInputStream(tmpdir + "//config.properties");

			prop.load(input);

			serverName = prop.getProperty("server");
			port = prop.getProperty("port");
			databaseName = prop.getProperty("database");
			username = prop.getProperty("username");	
			password = prop.getProperty("password");

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
		} catch(Exception ex) {
			ex.printStackTrace();
		} 
		return results;
	}

	public ResultSet getTable(String table) {
		return this.queryDB("SELECT * FROM " + table);
	}

	// make sure to set up data array matches schema
	public void burstAddToDB(String table, String[][] data) {
		String query = "";
		try{
			for(int i=0; i<data.length; i++)
			{
				String insert = "INSERT INTO " + table + " " + "VALUES (";
				query = insert;
				for(int j=0; j<data[i].length; j++)
				{
					if(j==data[i].length-1)
					{
						query = query + "'" + data[i][j] + "')";
						break;
					}
					query =  query + "'" + data[i][j] + "'" + ",";

				}
				statement.executeUpdate(query);
			}
		} catch(Exception ex) {
			System.out.println("Failed to burst add to database");
			ex.printStackTrace();
		} 
	}

	// make sure column count matches
	public void addEntryToDB(String table, String[] row) {
		String query = "INSERT INTO " + table + " " + "VALUES (";
		for(int i=0; i<row.length; i++) 
			query = query + "\"" + row[i] + "\"" + ",";

		query = query.substring(0, query.length()-1); 
		query = query + ")";
		try {
			this.lastQuery = query;
			statement.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Add entry failed");
			e.printStackTrace();
		}
	}

	// unresolved bug 
	// query is correct and was manually tested
	public void addEntryToDB(String table, HashMap<String,String> row) {
		String query = "INSERT INTO " + table + " " + "SET ";
		for (Entry<String, String> entry : row.entrySet()) 
			query = query + entry.getKey() + "=" + "\"" + entry.getValue() + "\"" + ",";
		query = query.substring(0, query.length()-1); 
		try {
			this.lastQuery = query;
			System.out.println(query);
			statement.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Add entry failed");
			e.printStackTrace();
		}
	}
	
	public void updateEntry(String table, HashMap<String,String> list, String pkey, String pval) throws SQLException {
		String query = "UPDATE " + table + " SET ";
		for (Entry<String, String> entry : list.entrySet()) 
			query = query + entry.getKey() + "=" + "\"" + entry.getValue() + "\"" + ",";

		query = query.substring(0, query.length()-1);
		query = query + " WHERE " + pkey + "=" + "\"" + pval + "\"";
		this.lastQuery = query;
		statement.executeUpdate(query);
	}

	public boolean primaryKeyExists(String table, String colIndex, String pkey) throws SQLException {
		String query = "SELECT * FROM " + table + " WHERE " + colIndex + "=" + "\"" + pkey + "\"";
		this.queryDB(query);
		// check if first row exists
		return results.first();
	}

	public ResultSet getData(String table, String[] columnIndexes) {
		String query = "SELECT ";
		for(int i=0; i<columnIndexes.length; i++) 
			query = query + columnIndexes[i] + ",";

		query = query.substring(0, query.length()-1);
		query = query + " FROM " + table;
		this.lastQuery = query;
		return this.queryDB(query);
	}	

	public void closeConnection() {
		try {
			if(connection != null){
				connection.close();
				statement.close();
				results.close();
			}
		} catch(Exception ex) {
			System.out.println("Failed to close connection");
			ex.printStackTrace();
		}
	}

	public void print(String[] columnIndexes) throws SQLException {

		if (results.first())
		{
			System.out.println("Query : " + this.lastQuery);
			for(int i=0; i<columnIndexes.length; i++)
				System.out.print(columnIndexes[i] + " | ");

			System.out.println();

			while(results.next()){
				for(int i=0; i<columnIndexes.length; i++)
					System.out.print(results.getString(columnIndexes[i]) + " | ");

				System.out.println();
			}
		}
		else 
			System.out.println("ResultSet is empty");
	}

	// deprecated
	public ResultSet readData(String table, String[] columnIndexes){
		try{
			String query = "SELECT * FROM " + table;
			results = statement.executeQuery(query);

		} catch(Exception ex){
			ex.printStackTrace();
		} 
		return results;
	}

	// deprecated
	public void writeData(String table, String[][] data){
		String query = "";
		try{

			for(int i=0; i<data.length; i++)
			{
				String insert = "INSERT INTO " + table + " " + "VALUES (";
				query = insert;
				for(int j=0; j<data[i].length; j++)
				{
					if(j==data[i].length-1)
					{
						query = query + "'" + data[i][j] + "'";
						break;
					}
					query =  query + "'" + data[i][j] + "'" + ",";

				}
				query = query + ")";
				statement.executeUpdate(query);
			}
		} catch(Exception ex){
			System.out.println("Failed to update database");
			ex.printStackTrace();
		} 
	}
}
