package be.vives.ti.dao;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FietsDAO {

    /**
     * Voegt een fiets toe aan de db. Het registratienummer wordt automatisch gegenereerd door
     * de DAO.
     *
     * @param fiets die toegevoegd moet worden
     * @return gegeneerd registratienummer van de fiets die net werd toegevoegd of null
     * indien geen fiets werd opgegeven.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public Integer toevoegenFiets(Fiets fiets) throws DBException {
        if (fiets != null) {
            Integer primaryKey = null;
            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into fiets(status"
                                + " , standplaats"
                                + " , opmerkingen"
                                + " ) values(?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, Status.actief.toString());
                    stmt.setString(2, fiets.getStandplaats().toString());
                    stmt.setString(3, fiets.getOpmerking());
                    stmt.execute();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        primaryKey = generatedKeys.getInt(1);
                    }

                    System.out.println("Nieuwe fiets toegevoegd met registratienummer: " + primaryKey);

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenFiets "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in toevoegenFiets "
                        + "- connection" + sqlEx);
            }
            return primaryKey;
        } else {
            return null;
        }
        }

    public void wijzigenToestandFiets(int regnr, Status status, String opmerking) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    /**
     * Zoekt adhv een registratienummer een fiets op. Er wordt null teruggegeven
     * indien geen fiets werd gevonden.
     * @param regnr registratienummer van de fiets die gezocht wordt (kan null zijn)
     * @return fiets die gezocht wordt, null indien geen fiets gevonden
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public Fiets zoekFiets(Integer regnr) throws DBException {
        if (regnr != null) {
            Fiets returnFiets = null;

            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select registratienummer"
                                + " , status"
                                + " , standplaats"
                                + " , opmerkingen"
                                + " from fiets "
                                + " where registratienummer = ?")) {

                    //parameter invullen in query
                    stmt.setInt(1, regnr);
                    stmt.execute();

                    try (ResultSet r = stmt.getResultSet()) {
                        //van de fiets uit de DAO een Fiets-object maken
                        if (r.next()) {
                            returnFiets = getFietsUitDatabase(r);
                        }
                        return returnFiets;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekFiets " +
                                "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekFiets " +
                            "- statment" + sqlEx);

                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekFiets " +
                        "- statment" + sqlEx);
            }
        }
        return null;
    }

    /**
     * Geeft een lijst terug van alle fietsen met de status ACTIEF en die momenteel geen openstaande rit hebben.
     * @return een lijst van alle beschikbare fietsen
     * @throws DBException
     */
    public List<Fiets> zoekAlleBeschikbareFietsen() throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }


    /**
     * zet een fiets uit de database-resultset om in een object van type Fiets
     * @param r de resultset van de query
     * @return
     * @throws SQLException Exception die duidt op een verkeerde
     *                      installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
    private Fiets getFietsUitDatabase(ResultSet r) throws SQLException {
        Fiets fiets = new Fiets();

        fiets.setRegistratienummer(r.getInt("registratienummer"));
        fiets.setStatus(Status.valueOf(r.getString("status")));
        fiets.setStandplaats(Standplaats.valueOf(r.getString("standplaats")));
        fiets.setOpmerking(r.getString("opmerkingen"));

        return fiets;
    }

    private ArrayList<Fiets> getFietsenUitDatabase(ResultSet r) throws SQLException {
        ArrayList<Fiets> fietsen = new ArrayList<>();
        while (r.next()) {
            Fiets fiets = getFietsUitDatabase(r);
            fietsen.add(fiets);
        }
        return fietsen;
    }
}