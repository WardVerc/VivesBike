package be.vives.ti.dao.connect;

import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	/**
	 * Levert een connectie naar de be.vives.DAO. Leest hiervoor uit het bestand
	 * DB.properties
	 *
	 * @return connectie-object naar de be.vives.DAO
	 * @throws DBException wanneer de be.vives.DAO niet toegankelijk is
	 */
	public static Connection getConnection() throws DBException {

		try {
			Class.forName(DBProp.getDriver());
			return DriverManager.getConnection(DBProp.getDbUrl(), DBProp.getProp());
		} catch (ClassNotFoundException | SQLException ex) {
			throw new DBException("Connectie met de be.vives.DAO mislukt: " + ex);
		}
	}
}
