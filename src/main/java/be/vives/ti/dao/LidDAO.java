package be.vives.ti.dao;


import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;
import com.mysql.cj.protocol.Resultset;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LidDAO {

    /**
     * Voegt een lid toe.
     * @param lid dat toegevoegd wordt.
     * @return rijksregisternummer van lid dat net werd toegevoegd of null indien geen lid
     * @throws DBException Exception die duidt op een verkeerde
     *                      installatie van de DAO of een fout in de query.
     */
    public String toevoegenLid(Lid lid) throws DBException {
        if (lid != null) {
            String toegevoegdLid = null;

            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into lid(rijksregisternummer"
                                + " , voornaam"
                                + " , naam"
                                + " , emailadres"
                                + " , start_lidmaatschap"
                                + " , opmerking"
                                + " ) values(?,?,?,?,?,?)")) {
                    stmt.setString(1, lid.getRijksregisternummer());
                    stmt.setString(2, lid.getVoornaam());
                    stmt.setString(3, lid.getNaam());
                    stmt.setString(4, lid.getEmailadres());
                    stmt.setString(5, lid.getStart_lidmaatschap().toString());
                    stmt.setString(6, lid.getOpmerking());
                    stmt.execute();

                    toegevoegdLid = lid.getRijksregisternummer();
                    System.out.println("Nieuw lid toegevoegd met rijksnr: " + toegevoegdLid);

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenLid "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in toevoegenLid "
                        + "- connection" + sqlEx);
            }
            return toegevoegdLid;
        } else {
            return null;
        }
        }

    public void wijzigenLid(Lid lid) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void uitschrijvenLid(String rr) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    /**
     * Haal lid uit database adhv rijksregisternummer
     * @param rijksregisternummer
     * @return null indien niets gevonden, anders de klant die gezocht wordt.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     * @throws ApplicationException Exception die duidt op een fout in getLidUitDatabase()
     */
    public Lid zoekLid(Rijksregisternummer rijksregisternummer) throws DBException, ApplicationException {
        if (rijksregisternummer != null) {
            Lid returnLid = null;

            try (Connection conn = ConnectionManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select rijksregisternummer"
                                + " , voornaam"
                                + " , naam"
                                + " , emailadres"
                                + " , start_lidmaatschap"
                                + " , einde_lidmaatschap"
                                + " , opmerking "
                                + " from lid "
                                + " where rijksregisternummer = ?")) {

                    // parameters invullen in query
                    stmt.setString(1, rijksregisternummer.getRijksregisternummer());

                    // execute voert het SQL-statement uit
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // van het lid uit de DAO een Lid-object maken
                         if (r.next()) {
                            returnLid = getLidUitDatabase(r);
                        }
                        return returnLid;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekLid "
                                + "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekLid "
                            + "- statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekLid "
                        + "- connection" + sqlEx);
            }
        }
        return null;
    }

    public ArrayList<Lid> zoekAlleLeden() throws DBException, ApplicationException {
        //Maak connectie met db
        try (Connection conn = ConnectionManager.getConnection()) {
            //SQL statement opstellen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select rijksregisternummer"
                            + " , voornaam"
                            + " , naam"
                            + " , emailadres"
                            + " , start_lidmaatschap"
                            + " , einde_lidmaatschap"
                            + " , opmerking"
                            + " from lid "
                            + " order by naam"
                            + "         , voornaam")) {
                stmt.execute();

                try (ResultSet r = stmt.getResultSet()) {
                    return getLedenUitDatabase(r);
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekAlleLeden - resultset" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekAlleLeden "
                        + "- statement" + sqlEx);
            }

        } catch (SQLException sqlEx) {
            throw new DBException("SQL-exception in zoekAlleLeden "
                    + "- connection" + sqlEx);
        }


}

    /**
     * zet een lid uit de database-resultset om in een object van type Lid
     * @param r de resultset van de query
     * @return
     * @throws SQLException Exception die duidt op een verkeerde
     *                      installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
    private Lid getLidUitDatabase(ResultSet r) throws SQLException, ApplicationException {
        Lid lid = new Lid();
        Rijksregisternummer rijk = new Rijksregisternummer(r.getString("rijksregisternummer"));
        LocalDate start = LocalDate.parse(r.getString("start_lidmaatschap"));
        LocalDate einde = null;

        if (r.getString("einde_lidmaatschap") == null) {
            einde = null;
        } else {
            einde = LocalDate.parse(r.getString("einde_lidmaatschap"));
        }

        lid.setVoornaam(r.getString("voornaam"));
        lid.setNaam(r.getString("naam"));
        lid.setEmailadres(r.getString("emailadres"));
        lid.setStart_lidmaatschap(start);
        lid.setEinde_lidmaatschap(einde);
        lid.setOpmerking(r.getString("opmerking"));
        lid.setRijksregisternummer(rijk);

        return lid;
    }

    private ArrayList<Lid> getLedenUitDatabase(ResultSet r) throws SQLException, ApplicationException {
        ArrayList<Lid> leden = new ArrayList<>();
        while (r.next()) {
            Lid lid = getLidUitDatabase(r);
            leden.add(lid);
        }
        return leden;
    }

}
