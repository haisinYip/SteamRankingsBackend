package com.steamrankings.service.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

public class DBConnector {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement safeStatement = null;
	private ResultSet results = null;
	private String lastQuery = null;

	// configs
	private String serverName = null;
	private String port = null;
	private String databaseName = null;
	private String username = null;
	private String password = null;

	// table names
	final public String TABLE_NAME_ACHIEVEMENTS = "achievements";
	final public String TABLE_NAME_GAMES = "games";
	final public String TABLE_NAME_PROFILES = "profiles";
	final public String TABLE_NAME_PROFILES_ACHIEVEMENTS = "profiles_has_achievements";
	final public String TABLE_NAME_PROFILES_GAMES = "profiles_has_games";

	// table columns
	final public int TABLE_ACHIEVEMENTS_COL_ID_INDEX = 1;
	final public int TABLE_ACHIEVEMENTS_COL_GAMES_ID_INDEX = 2;
	final public int TABLE_ACHIEVEMENTS_COL_NAMES_INDEX = 3;
	final public int TABLE_ACHIEVEMENTS_COL_DESCRIPTION_INDEX = 4;
	final public int TABLE_ACHIEVEMENTS_COL_UNLOCKED_ICON_URL_INDEX = 5;
	final public int TABLE_ACHIEVEMENTS_COL_LOCKED_ICON_URL_INDEX = 6;
	final public int TABLE_ACHIEVEMENTS_COL_COUNT = TABLE_ACHIEVEMENTS_COL_LOCKED_ICON_URL_INDEX;

	final public String TABLE_ACHIEVEMENTS_COL_ID_LABEL = "id";
	final public String TABLE_ACHIEVEMENTS_COL_GAMES_ID_LABEL = "games_id";
	final public String TABLE_ACHIEVEMENTS_COL_NAMES_LABEL = "name";
	final public String TABLE_ACHIEVEMENTS_COL_DESCRIPTION_LABEL = "description";
	final public String TABLE_ACHIEVEMENTS_COL_UNLOCKED_ICON_URL_LABEL = "unlocked_icon_url";
	final public String TABLE_ACHIEVEMENTS_COL_LOCKED_ICON_URL_LABEL = "locked_icon_url";

	final public int TABLE_GAMES_COL_ID_INDEX = 1;
	final public int TABLE_GAMES_COL_NAME_INDEX = 2;
	final public int TABLE_GAMES_COL_ICON_URL_INDEX = 3;
	final public int TABLE_GAMES_COL_LOGO_URL_INDEX = 4;
	final public int TABLE_GAMES_COL_COUNT = TABLE_GAMES_COL_LOGO_URL_INDEX;

	final public String TABLE_GAMES_COL_ID_LABEL = "id";
	final public String TABLE_GAMES_COL_NAME_LABEL = "name";
	final public String TABLE_GAMES_COL_ICON_URL_LABEL = "icon_url";
	final public String TABLE_GAMES_COL_LOGO_URL_LABEL = "logo_url";

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
	final public int TABLE_PROFILES_COL_COUNT = TABLE_PROFILES_COL_AVATAR_ICON_URL_INDEX;

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
	final public int TABLE_PROFILES_ACHIEVEMENTS_COL_COUNT = 4;
	
	final public String TABLE_PROFILES_ACHIEVEMENTS_COL_PROFILES_ID_LABEL = "profiles_id";
	final public String TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_ID_LABEL = "achievements_id";
	final public String TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_GAMES_ID_LABEL = "achievements_games_id";
	final public String TABLE_PROFILES_ACHIEVEMENTS_COL_UNLOCKED_TIMESTAMP_LABEL = "unlocked_timestamp";

	final public int TABLE_PROFILES_GAMES_COL_PROFILES_ID_INDEX = 1;
	final public int TABLE_PROFILES_GAMES_COL_GAMES_ID_INDEX = 2;
	final public int TABLE_PROFILES_GAMES_COL_TOTAL_PLAY_TIME_INDEX = 3;
	final public int TABLE_PROFILES_GAMES_COL_COUNT = 3;
	
	final public String TABLE_PROFILES_GAMES_COL_PROFILES_ID_LABEL = "profiles_id";
	final public String TABLE_PROFILES_GAMES_COL_GAMES_ID_LABEL = "games_id";
	final public String TABLE_PROFILES_GAMES_COL_TOTAL_PLAY_TIME_LABEL = "total_play_time";

	public DBConnector() {

		this.loadFromConfig();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);
			statement = connection.createStatement();

		} catch (Exception ex) {
			System.out.println("Error instantiating database connection : " + ex.getMessage());
		}
	}

	private void loadFromConfig() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
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
					System.out.println(e.getMessage());
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
			for (int i=0; i<data.length; i++) {
				String insert = "INSERT INTO " + table + " (";
				query = insert;

				if (table.equals("achievements")) {
					for (int j=1; j<=TABLE_ACHIEVEMENTS_COL_COUNT; j++) {
						switch (j) {
							case TABLE_ACHIEVEMENTS_COL_ID_INDEX:
								query = query + TABLE_ACHIEVEMENTS_COL_ID_LABEL;
								break;
							case TABLE_ACHIEVEMENTS_COL_GAMES_ID_INDEX: 
								query = query + TABLE_ACHIEVEMENTS_COL_GAMES_ID_LABEL;
								break;
							case TABLE_ACHIEVEMENTS_COL_NAMES_INDEX:
								query = query + TABLE_ACHIEVEMENTS_COL_NAMES_LABEL;
								break;
							case TABLE_ACHIEVEMENTS_COL_DESCRIPTION_INDEX:
								query = query + TABLE_ACHIEVEMENTS_COL_DESCRIPTION_LABEL;
								break;
							case TABLE_ACHIEVEMENTS_COL_UNLOCKED_ICON_URL_INDEX:
								query = query + TABLE_ACHIEVEMENTS_COL_UNLOCKED_ICON_URL_LABEL;
								break;
							case TABLE_ACHIEVEMENTS_COL_LOCKED_ICON_URL_INDEX: 
								query = query + TABLE_ACHIEVEMENTS_COL_LOCKED_ICON_URL_LABEL;
								break;
						}
						query = query + ",";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ") VALUES (";

					for (int k=0; k<TABLE_ACHIEVEMENTS_COL_COUNT; k++) {
						query = query + "?,";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ")";
					//System.out.println(query);
					this.safeStatement = this.connection.prepareStatement(query);

					for (int m=1; m<=TABLE_ACHIEVEMENTS_COL_COUNT; m++) {
						this.safeStatement.setString(m, data[i][m-1]);
					}
				}

				else if (table.equals("games")) {
					for (int j=1; j<=TABLE_GAMES_COL_COUNT; j++) {
						switch (j) {
							case TABLE_GAMES_COL_ID_INDEX:
								query = query + TABLE_GAMES_COL_ID_LABEL;
								break;
							case TABLE_GAMES_COL_NAME_INDEX: 
								query = query + TABLE_GAMES_COL_NAME_LABEL;
								break;
							case TABLE_GAMES_COL_ICON_URL_INDEX:
								query = query + TABLE_GAMES_COL_ICON_URL_LABEL;
								break;
							case TABLE_GAMES_COL_LOGO_URL_INDEX:
								query = query + TABLE_GAMES_COL_LOGO_URL_LABEL;
								break;
						}
						query = query + ",";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ") VALUES (";

					for (int k=0; k<TABLE_GAMES_COL_COUNT; k++) {
						query = query + "?,";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ")";
					//System.out.println(query);
					this.safeStatement = this.connection.prepareStatement(query);

					for (int m=1; m<=TABLE_GAMES_COL_COUNT; m++) {
						this.safeStatement.setString(m, data[i][m-1]);
					}
				}

				else if (table.equals("profiles")) {
					for (int j=1; j<=TABLE_PROFILES_COL_COUNT; j++) {
						switch (j) {
							case TABLE_PROFILES_COL_ID_INDEX:
								query = query + TABLE_PROFILES_COL_ID_LABEL;
								break;
							case TABLE_PROFILES_COL_COMMUNITY_ID_INDEX: 
								query = query + TABLE_PROFILES_COL_COMMUNITY_ID_LABEL;
								break;
							case TABLE_PROFILES_COL_PERSONA_NAME_INDEX:
								query = query + TABLE_PROFILES_COL_PERSONA_NAME_LABEL;
								break;
							case TABLE_PROFILES_COL_REAL_NAME_INDEX:
								query = query + TABLE_PROFILES_COL_REAL_NAME_LABEL;
								break;
							case TABLE_PROFILES_COL_LOC_COUNTRY_INDEX:
								query = query + TABLE_PROFILES_COL_LOC_COUNTRY_LABEL;
								break;
							case TABLE_PROFILES_COL_LOC_PROVINCE_INDEX: 
								query = query + TABLE_PROFILES_COL_LOC_PROVINCE_LABEL;
								break;
							case TABLE_PROFILES_COL_LOC_CITY_INDEX:
								query = query + TABLE_PROFILES_COL_LOC_CITY_LABEL;
								break;
							case TABLE_PROFILES_COL_AVATAR_FULL_URL_INDEX:
								query = query + TABLE_PROFILES_COL_AVATAR_FULL_URL_LABEL;
								break;
							case TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_INDEX:
								query = query + TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_LABEL;
								break;
							case TABLE_PROFILES_COL_AVATAR_ICON_URL_INDEX:
								query = query + TABLE_PROFILES_COL_AVATAR_ICON_URL_LABEL;
								break;
						}
						query = query + ",";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ") VALUES (";

					for (int k=0; k<TABLE_PROFILES_COL_COUNT; k++) {
						query = query + "?,";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ")";
					//System.out.println(query);
					this.safeStatement = this.connection.prepareStatement(query);

					for (int m=1; m<=TABLE_PROFILES_COL_COUNT; m++) {
						this.safeStatement.setString(m, data[i][m-1]);
					}
				}
				
				else if (table.equals("TABLE_PROFILES_GAMES_COL_COUNT")) {
					for (int j=1; j<=TABLE_GAMES_COL_COUNT; j++) {
						switch (j) {
							case TABLE_PROFILES_GAMES_COL_PROFILES_ID_INDEX:
								query = query + TABLE_PROFILES_GAMES_COL_PROFILES_ID_LABEL;
								break;
							case TABLE_PROFILES_GAMES_COL_GAMES_ID_INDEX: 
								query = query + TABLE_PROFILES_GAMES_COL_GAMES_ID_LABEL;
								break;
							case TABLE_PROFILES_GAMES_COL_TOTAL_PLAY_TIME_INDEX:
								query = query + TABLE_PROFILES_GAMES_COL_TOTAL_PLAY_TIME_LABEL;
								break;
						}
						query = query + ",";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ") VALUES (";

					for (int k=0; k<TABLE_PROFILES_GAMES_COL_COUNT; k++) {
						query = query + "?,";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ")";
					//System.out.println(query);
					this.safeStatement = this.connection.prepareStatement(query);

					for (int m=1; m<=TABLE_PROFILES_GAMES_COL_COUNT; m++) {
						this.safeStatement.setString(m, data[i][m-1]);
					}
				}
				
				else if (table.equals("profiles_has_achievements")) {
					for (int j=1; j<=TABLE_PROFILES_ACHIEVEMENTS_COL_COUNT; j++) {
						switch (j) {
							case TABLE_PROFILES_ACHIEVEMENTS_COL_PROFILES_ID_INDEX:
								query = query + TABLE_PROFILES_ACHIEVEMENTS_COL_PROFILES_ID_LABEL;
								break;
							case TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_ID_INDEX: 
								query = query + TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_ID_LABEL;
								break;
							case TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_GAMES_ID_INDEX:
								query = query + TABLE_PROFILES_ACHIEVEMENTS_COL_ACHIEVEMENTS_GAMES_ID_LABEL;
								break;
							case TABLE_PROFILES_ACHIEVEMENTS_COL_UNLOCKED_TIMESTAMP_INDEX:
								query = query + TABLE_PROFILES_ACHIEVEMENTS_COL_UNLOCKED_TIMESTAMP_LABEL;
								break;
						}
						query = query + ",";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ") VALUES (";

					for (int k=0; k<TABLE_PROFILES_ACHIEVEMENTS_COL_COUNT; k++) {
						query = query + "?,";
					}

					query = query.substring(0, query.length() - 1);
					query = query + ")";
					//System.out.println(query);
					this.safeStatement = this.connection.prepareStatement(query);

					for (int m=1; m<=TABLE_PROFILES_ACHIEVEMENTS_COL_COUNT; m++) {
						this.safeStatement.setString(m, data[i][m-1]);
					}
				}

				this.lastQuery = query;
				safeStatement.executeUpdate();
			}
		} catch (Exception ex) {
			System.out.println("Failed to burst add to database");
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}

	// make sure column count matches
	public void addEntryToDB(String table, String[] row) {

		String query = "INSERT INTO " + table + " " + "VALUES (";
		for (int i=0; i<row.length; i++) {
			//fix
			query = query + "\"" + row[i] + "\"" + ",";
		}
		query = query.substring(0, query.length() - 1);
		query = query + ");";

		try {
			this.lastQuery = query;
			statement.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Add entry failed");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void addEntryToDB(String table, HashMap<String, String> row) {

		String query = "INSERT INTO " + table + " " + "SET ";
		for (Entry<String, String> entry : row.entrySet()) {
			//fix
			//query = query + entry.getKey() + "=" + "\"" + entryVal + "\"" + ",";
		}
		query = query.substring(0, query.length() - 1);
		query = query + ";";

		try {
			this.lastQuery = query;
			statement.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Add entry failed");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void updateEntry(String table, HashMap<String, String> list, String pkey, String pval) throws SQLException {

		String query = "UPDATE " + table + " SET ";
		for (Entry<String, String> entry : list.entrySet()) {
			//fix
			//query = query + entry.getKey() + "=" + "\"" + entryVal + "\"" + ",";
		}
		query = query.substring(0, query.length() - 1);
		//fix
		query = query + " WHERE " + pkey + "=" + "\"" + pval + "\";";

		this.lastQuery = query;
		System.out.println(query);
		statement.executeUpdate(query);
	}

	public boolean primaryKeyExists(String table, String colIndex, String pkey) throws SQLException {
		//fix
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

	public void printLastQueryContent(String[] columnIndexes) throws SQLException {

		if (results!=null && results.first()) {
			for (int i = 0; i < columnIndexes.length; i++)
				System.out.print(columnIndexes[i] + " | ");

			System.out.println();

			for (int i = 0; i < columnIndexes.length; i++)
				System.out.print(results.getString(columnIndexes[i]) + " | ");

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
		//fix
		String query = "SELECT * FROM " + table + " WHERE " + columnIndex + "=" + "\"" + primaryKey + "\";";
		return this.queryDB(query);
	}

	public int getCount(String table, HashMap<String,String> conditions) throws SQLException {

		int count = 0;
		String query = "SELECT COUNT(*) AS res FROM " + table + " WHERE (";
		for (Entry<String, String> entry : conditions.entrySet()) {
			//fix
			//query = query + entry.getKey() + "=" + "\"" + entryVal + "\"" + " AND ";
		}
		query = query.substring(0, query.length() - 4);
		query = query + ")";
		this.queryDB(query);

		if(this.results!=null && this.results.first())
			count = this.results.getInt(1);

		return count;
	}

	public int getNumberOfEntries(String table) throws SQLException {

		int count = 0;

		String query = "SELECT COUNT(*)" + " FROM " + table;
		this.queryDB(query);
		if(this.results!=null && this.results.first())
			count = this.results.getInt(1);

		return count;
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
			System.out.println(ex.getMessage());
		}
	}

	// deprecated
	public int getCount(String table, String columnIndex, String primaryKey) throws SQLException {
		int count = 0;

		String query = "SELECT COUNT(" + columnIndex + ") AS res FROM " + table + " WHERE " + columnIndex + "=" + "\"" + primaryKey + "\";";
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