package be.vives.ti.dao;


import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LidDAO {

    public Integer toevoegenLid(Lid lid) throws DBException {
        if (lid != null) {
            Integer primaryKey = null;

            try (Connection conn = ConnectionManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into lid(rijksregisternummer"
                                + " , voornaam"
                                + " , naam"
                                + " , emailadres"
                                + " , start_lidmaatschap"
                                + " , einde_lidmaatschap"
                                + " , opmerking"
                                + " ) values(?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, lid.getRijksregisternummer());
                    stmt.setString(2, lid.getVoornaam());
                    stmt.setString(3, lid.getNaam());
                    stmt.setString(4, lid.getEmailadres());
                    stmt.setString(5, lid.getStart_lidmaatschap().toString());
                    stmt.setString(6, lid.getEinde_lidmaatschap().toString());
                    stmt.setString(7, lid.getOpmerking());
                    stmt.execute();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();

                    if (generatedKeys.next()) {
                        primaryKey = generatedKeys.getInt(1);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenLid "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in toevoegenLid "
                        + "- connection" + sqlEx);
            }
            return primaryKey;
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

    public Lid zoekLid(Rijksregisternummer rijksregisternummer) throws DBException, ApplicationException {
        if (rijksregisternummer != null) {
            Lid returnLid = null;
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
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
                        // er werd een lid gevonden
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

    public List<Lid> zoekAlleLeden() throws DBException, ApplicationException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private Lid getLidUitDatabase(ResultSet r) throws SQLException, ApplicationException {
        Lid lid = new Lid();
        Rijksregisternummer rijk = new Rijksregisternummer(r.getString("rijksregisternummer"));
        LocalDate start = LocalDate.parse(r.getString("start_lidmaatschap"));
        LocalDate einde = LocalDate.parse(r.getString("einde_lidmaatschap"));

        lid.setVoornaam(r.getString("voornaam"));
        lid.setNaam(r.getString("naam"));
        lid.setEmailadres(r.getString("emailadres"));
        lid.setStart_lidmaatschap(start);
        lid.setEinde_lidmaatschap(einde);
        lid.setOpmerking(r.getString("opmerking"));
        lid.setRijksregisternummer(rijk);

        return lid;
    }
}
