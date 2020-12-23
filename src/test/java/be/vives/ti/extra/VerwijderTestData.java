package be.vives.ti.extra;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VerwijderTestData {

    /**
     * Verwijdert een lid adhv een rijksregisternummer.
     * Gebruik dit enkel voor testdata!
     * @param rijk het rijksregisternummer
     * @throws DBException Exception die duidt op een verkeerde
     *                      installatie van de DAO of een fout in de query.
     */
    public static void removeTestLid(Rijksregisternummer rijk) throws DBException {

        //Maak connectie met db
        try (Connection conn = ConnectionManager.getConnection()) {
            //SQL statement opstellen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from lid where rijksregisternummer = ?")) {
                //rijk.getRijksregisternummer() zet een Rijksregisternummer-datatype
                //om in een string
                stmt.setString(1, rijk.getRijksregisternummer());
                stmt.execute();
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeLid - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException("SQL-exception in removeLid - statement" + sqlEx);
        }

        }


}
