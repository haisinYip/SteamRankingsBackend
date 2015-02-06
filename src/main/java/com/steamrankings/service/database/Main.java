package com.steamrankings.service.database;

import java.sql.SQLException;
import java.util.HashMap;

public class Main {
	
	public static void main(String[] args) throws SQLException{
		DBConnector conn = new DBConnector();
		String[] columns = {"name", "icon_url"};
		String[][] data = {{"2211313","3","34","324"},{"311111","3","34","324"},{"4232122","3","34","324"}};
		HashMap<String,String> list = new HashMap<String,String>();
		list.put("id", "322222");
		list.put("name", "3");
		list.put("icon_url", "icon_urlSFSFSFSFSdsfsdf");
		//conn.updateEntry("games", list, "id", "9");
		//conn.addEntryToDB("achievements", new String[]{"324423","3","34","324","df","df"});
		//conn.addEntryToDB("games", list);
		//conn.getEntry("games", "id", "2");
		conn.burstAddToDB("games", data);
		conn.printLastQuery();
		//conn.getData("games", new String[]{"name", "id"});
		//conn.print(new String[]{"id", "name"});
		System.out.println(conn.getCount("games", "name", "3"));
		//System.out.println(conn.primaryKeyExists("games", "id", "dls"));
		
	}
}
