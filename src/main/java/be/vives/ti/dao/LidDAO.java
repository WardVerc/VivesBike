package be.vives.ti.dao;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Bevat alle functionaliteit op de DAO-tabel lid:
 * toevoegen lid, uitschrijven lid, wijzigen lid,
 * zoek lid adhv rijksregisternummer, zoek alle leden
 * gesorteerd op naam, voornaam.
 */

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
            String toegevoegdLid;

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


    /**
     * Wijzigt voornaam, naam, email en opmerking van een lid
     * @param lid dat gewijzigd wordt
     * @throws DBException Exception die duidt op een verkeerde
     *                      installatie van de DAO of een fout in de query.
     */
    public void wijzigenLid(Lid lid) throws DBException {
        if (lid != null) {
            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "update lid "
                                + " set voornaam = ?"
                                + " , naam = ?"
                                + " , emailadres = ?"
                                + " , start_lidmaatschap = ?"
                                + " , opmerking = ?"
                                + " where rijksregisternummer = ?")) {

                    stmt.setString(1, lid.getVoornaam());
                    stmt.setString(2, lid.getNaam());
                    stmt.setString(3, lid.getEmailadres());
                    stmt.setString(4, lid.getStart_lidmaatschap().toString());
                    stmt.setString(5, lid.getOpmerking());
                    stmt.setString(6, lid.getRijksregisternummer());
                    stmt.execute();

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in wijzigenLid "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in wijzigenLid "
                        + "- connection" + sqlEx);
            }
        }
    }

    /**
     * Schrijft het lid met meegegeven rijksregisternummer uit (wordt niet verwijderd).
     * @param rr rijksregisternummer
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public void uitschrijvenLid(Rijksregisternummer rr) throws DBException {
        if (rr != null) {
            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "update lid "
                                + " set einde_lidmaatschap = ?"
                                + " where rijksregisternummer = ?")) {

                    stmt.setString(1, LocalDate.now().toString());
                    stmt.setString(2, rr.getRijksregisternummer());

                    stmt.execute();

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in uitschrijvenLid "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in uitschrijvenLid "
                        + "- connection" + sqlEx);
            }
        }
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

    /**
     * Zoekt alle leden en geeft ze terug in een lijst, gesorteerd op naam, voornaam.
     * @return lijst van leden
     * @throws DBException Exception die duidt op een verkeerde installatie van de
     * be.vives.DAO of een fout in de query
     * @throws ApplicationException
     */
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
