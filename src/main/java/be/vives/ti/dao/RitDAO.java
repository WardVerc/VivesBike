package be.vives.ti.dao;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RitDAO {

    /**
     * Voegt een rit toe aan de db. Het id wordt automatisch gegenereerd door
     * de DAO.
     *
     * @param rit dat toegevoegd moet worden
     * @return gegeneerd id van de rit die net werd toegevoegd of null
     * indien geen rit werd opgegeven.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public Integer toevoegenRit(Rit rit) throws DBException {
        if (rit != null) {
            Integer primaryKey = null;
            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into rit(starttijd"
                                + " , eindtijd"
                                + " , prijs"
                                + " , lid_rijksregisternummer"
                                + " , fiets_registratienummer"
                                + " ) values(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setTimestamp(1, java.sql.Timestamp.valueOf(rit.getStarttijd()));

                    if (rit.getEindtijd() == null) {
                        stmt.setNull(2, Types.DATE);
                    } else {
                        stmt.setTimestamp(1, java.sql.Timestamp.valueOf(rit.getEindtijd()));
                    }
                    if (rit.getPrijs() == null) {
                        stmt.setNull(3, Types.DECIMAL);
                    } else {
                        stmt.setString(3, rit.getPrijs().toString());
                    }
                    stmt.setString(4, rit.getLidRijksregisternummer());
                    stmt.setInt(5, rit.getFietsRegistratienummer());
                    stmt.execute();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        primaryKey = generatedKeys.getInt(1);
                    }

                    System.out.println("Nieuwe rit toegevoegd met id: " + primaryKey);

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenRit "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in toevoegenRit "
                        + "- connection" + sqlEx);
            }
            return primaryKey;
        } else {
            return null;
        }
    }

    public void afsluitenRit(Rit rit) throws DBException {
        if (rit != null) {
            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "update rit "
                                + " set eindtijd = ?"
                                + " , prijs = ?"
                                + " where id = ?")) {

                    if (rit.getEindtijd() == null) {
                        throw new NullPointerException("Nullpointer-exception in afsluitenRit "
                        + "- rit.getEindtijd() is null");
                    } else {
                        stmt.setTimestamp(1, java.sql.Timestamp.valueOf(rit.getEindtijd()));
                    }
                    if (rit.getPrijs() == null) {
                        throw new NullPointerException("Nullpointer-exception in afsluitenRit "
                                + "- rit.getPrijs() is null");
                    } else {
                        stmt.setBigDecimal(2, rit.getPrijs());
                    }
                    stmt.setInt(3, rit.getId());
                    stmt.execute();

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in afsluitenRit "
                            + "- statement" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in afsluitenRit "
                        + "- connection" + sqlEx);
            }
        }
    }

    public Rit zoekRit(Integer ritID) throws DBException, ApplicationException {
        if (ritID != null) {
            Rit returnRit = null;

            //Maak connectie met db
            try (Connection conn = ConnectionManager.getConnection()) {
                //SQL statement opstellen
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select id"
                                + " , starttijd"
                                + " , eindtijd"
                                + " , prijs"
                                + " , lid_rijksregisternummer"
                                + " , fiets_registratienummer"
                                + " from rit "
                                + " where id = ?")) {

                    //parameter invullen in query
                    stmt.setInt(1, ritID);
                    stmt.execute();

                    try (ResultSet r = stmt.getResultSet()) {
                        //van de rit uit de DAO een Rit-object maken
                        if (r.next()) {
                            returnRit = getRitUitDatabase(r);
                        }
                        return returnRit;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekRit " +
                                "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekRit " +
                            "- statment" + sqlEx);

                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekRit " +
                        "- statment" + sqlEx);
            }
        }
        return null;
    }

    public int zoekEersteRitVanLid(String rr) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }


    public ArrayList<Rit> zoekActieveRittenVanLid(String rr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException("Not implemented yet!");

    }

    public ArrayList<Rit> zoekActieveRittenVanFiets(int regnr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Rit getRitUitDatabase(ResultSet r) throws SQLException, ApplicationException {
        Rit rit = new Rit();

        rit.setId(r.getInt("id"));

        //date time formatter is nodig om string om te zetten naar LocalDateTime
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (r.getString("starttijd") == null) {
            rit.setStarttijd(null);
        } else {
            LocalDateTime starttijdFormatted = LocalDateTime.parse(r.getString("starttijd"), format);
            rit.setStarttijd(starttijdFormatted);
        }

        if (r.getString("eindtijd") == null) {
            rit.setEindtijd(null);
        } else {
            LocalDateTime eindtijdFormatted = LocalDateTime.parse(r.getString("eindtijd"), format);
            rit.setEindtijd(eindtijdFormatted);
        }

        rit.setPrijs(r.getBigDecimal("prijs"));

        //rijksregisternummer-string uit db omzetten naar een rijksregisternr-object
        Rijksregisternummer rijks = new Rijksregisternummer(r.getString("lid_rijksregisternummer"));
        rit.setLidRijksregisternummer(rijks);

        rit.setFietsRegistratienummer(r.getInt("fiets_registratienummer"));

        return rit;
    }

}