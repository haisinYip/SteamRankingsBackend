package com.steamrankings.service.database;

import org.javalite.activejdbc.Base;

import com.steamrankings.service.core.Application;

public class Database {
	public static void openDBConnection() {
		Base.open("com.mysql.jdbc.Driver",
				"jdbc:mysql://" + Application.CONFIG.getProperty("server")
						+ ":" + Application.CONFIG.getProperty("mysql_port")
						+ "/" + Application.CONFIG.getProperty("mysql_database")
						+ "?characterEncoding=utf8",
				Application.CONFIG.getProperty("mysql_username"),
				Application.CONFIG.getProperty("mysql_password"));

	}

	public static void closeDBConnection() {
		Base.close();
	}
}
