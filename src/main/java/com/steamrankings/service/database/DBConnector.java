package com.steamrankings.service.database;

import java.sql.*;

public class DBConnector {
	private Connection connection;
	private Statement statement;
	private ResultSet results;
	
	private final String serverName = "mikemontreal.ignorelist.com";
	private final String port = "17862";
	private final String databaseName = "steamrankings_db";
	private final String username = "steamroller";	
	private final String password = "ecse4282015*";	
	
	public DBConnector(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			//connection = DriverManager.getConnection("jdbc:mysql://mikemontreal.ignorelist.com:17862/steamrankings_db","steamroller","ecse4282015*");
			connection = DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);;
			statement = connection.createStatement();
			
		} catch(Exception ex){
			System.out.println("Connection failed");
			System.out.println(ex.getMessage());
		}
	}
	
	public ResultSet getData(String table, String[] columns){
		try{
			String query = "select * from " + table;
			results = statement.executeQuery(query);
			
			for(int i=0; i<columns.length; i++)
				System.out.print(columns[i] + " | ");
			
			System.out.println();
			
			while(results.next()){
				String name = results.getString("name");
				String id = results.getString("id");
				
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
}
