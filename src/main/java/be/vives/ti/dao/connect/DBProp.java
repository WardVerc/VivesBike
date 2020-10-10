package be.vives.ti.dao.connect;

import be.vives.ti.exception.DBException;

import java.util.Properties;

public class DBProp {

	private static String dbUrl;
	private static Properties prop;
	private static String driver;

	/**
	 * Haalt de URL, driver paswoord en login uit het bestand DB.properties en
	 * vult deze in in de overeenkomstige velden
	 */
	private DBProp() throws DBException {
		prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream(
					"/database/DB.properties"));
			dbUrl = prop.getProperty("dbUrl");
			driver = prop.getProperty("driver");
			prop.setProperty("serverTimezone", java.util.TimeZone.getDefault().getID());

		} catch (java.io.IOException ex) {
			throw new DBException(
					"Bestand (DB.properties) met gegevens over DB niet gevonden.");
		}
	}

	/**
	 * @return the dbUrl
	 * @throws DBException wanneer DB.properties niet toegankelijk is
	 */
	public static String getDbUrl() throws DBException {
		if (dbUrl == null) {
			DBProp db = new DBProp();
		}
		return dbUrl;
	}

	/**
	 * @return the driver
	 * @throws DBException wanneer DB.properties niet toegankelijk is
	 */
	public static Properties getProp() throws DBException {
		if (prop == null) {
			DBProp db = new DBProp();
		}
		return prop;
	}

	/**
	 * @return the login
	 * @throws DBException wanneer DB.properties niet toegankelijk is
	 */
	public static String getDriver() throws DBException {
		if (driver == null) {
			DBProp db = new DBProp();
		}
		return driver;
	}
}
