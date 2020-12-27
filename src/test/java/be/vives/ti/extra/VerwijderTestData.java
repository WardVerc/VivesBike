package be.vives.ti.extra;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

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

        public static void removeTestLeden(ArrayList<Lid> leden) throws Exception {
            for (Lid l : leden) {
                Rijksregisternummer r = new Rijksregisternummer(l.getRijksregisternummer());
                removeTestLid(r);
            }

        }

        public static void removeTestFiets(Integer regnr) throws Exception {
            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "delete from fiets where registratienummer = ?")) {
                    //rijk.getRijksregisternummer() zet een Rijksregisternummer-datatype
                    //om in een string
                    stmt.setString(1, regnr.toString());
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in removeFiets - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeFiets - statement" + sqlEx);
            }

        }

    public static void removeTestFietsen(ArrayList<Fiets> fietsen) throws Exception {
        for (Fiets f : fietsen) {
            Integer regnr = f.getRegistratienummer();
            removeTestFiets(regnr);
        }
    }

    public static void removeTestRit(Integer id) throws Exception {
        //Maak connectie met db
        try (Connection conn = ConnectionManager.getConnection()) {
            //SQL statement opstellen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from rit where id = ?")) {

                stmt.setInt(1, id);
                stmt.execute();
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeRit - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException("SQL-exception in removeRit - statement" + sqlEx);
        }

    }

    public static void removeTestRitten(ArrayList<Rit> ritten) throws Exception {
        for (Rit r : ritten) {
            Integer ritID = r.getId();
            removeTestRit(ritID);
        }
    }
}
