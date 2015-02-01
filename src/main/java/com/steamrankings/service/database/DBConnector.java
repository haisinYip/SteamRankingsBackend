package com.steamrankings.service.database;

import java.sql.*;
import java.util.HashMap;

public class DBConnector {
	private Connection connection;
	private Statement statement;
	private ResultSet results;
	
	private final String serverName = "mikemontreal.ignorelist.com";
	private final String port = null;
	private final String databaseName = "steamrankings_db";
	private final String username = null;	
	private final String password = null;	
	
	public DBConnector(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);;
			statement = connection.createStatement();
			
		} catch(Exception ex){
			System.out.println("Connection failed");
			System.out.println(ex.getMessage());
		}
	}
	
	public ResultSet readData(String table, String[] columns){
		try{
			String query = "SELECT * FROM " + table;
			results = statement.executeQuery(query);
			
			for(int i=0; i<columns.length; i++)
				System.out.print(columns[i] + " | ");
			
			System.out.println();
			
			while(results.next()){
				for(int i=0; i<columns.length; i++)
					System.out.print(results.getString(columns[i]) + " | ");
				
				System.out.println();
			}
		} catch(Exception ex){
			System.out.println(ex.getMessage());
		} finally {
			try {
				if(connection != null){
					connection.close();
					statement.close();
					results.close();
				}
			} catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		}
		return results;
	}
	
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
				//System.out.println(query);
			}
		} catch(Exception ex){
			System.out.println("Failed to update database");
			System.out.println(ex.getMessage());
		} finally {
			try {
				if(connection != null){
					connection.close();
					statement.close();
				}
			} catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		}
	}
}
