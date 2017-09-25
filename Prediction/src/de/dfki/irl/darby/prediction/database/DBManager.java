package de.dfki.irl.darby.prediction.database;

import java.sql.SQLException;
import java.util.HashMap;

public class DBManager {
	public enum DatabaseType {
		MYSQL, HANA, POSTGRESQL  //added POSTGRES
	};

	private static HashMap<DatabaseType, Database> dbCache = new HashMap<DatabaseType, Database>();

	public static Database getDatabase(DatabaseType type) {
		Database ret = dbCache.get(type);

		if (ret == null) {
			ret = createDB(type);
			dbCache.put(type, ret);
		}

		return ret;
	}

	private static Database createDB(DatabaseType type) {
		try {

			switch (type) {
			case HANA:
				return new HanaDatabase();
			case MYSQL:
				return new MySQLDatabase();
			
			// added case POSTGRES	
			case POSTGRESQL:
				return new PostGreSqlDatabase();	
			default:
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
