package be.vives.ti.dao;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                        stmt.setTimestamp(2, java.sql.Timestamp.valueOf(rit.getEindtijd()));
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

    /**
     * Sluit een rit af door de prijs en eindtijd up te daten van een meegegeven rit.
     * @param rit de rit die wordt afgesloten
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
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

    /**
     * Zoek een rit op basis van een ritID
     * @param ritID de ID van de rit die gezocht wordt
     * @return de rit
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
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

    /**
     * Zoekt de eerste rit van een lid adhv het rijksregisternummer van het lid.
     * De eerste rit is de rit met de oudste starttijd.
     * @param rr rijksregisternummer van het lid
     * @return de ritID dat gevonden is
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
    public Integer zoekEersteRitVanLid(String rr) throws DBException, ApplicationException {
        if (rr != null) {
            Integer returnRitID = null;

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
                                + " where lid_rijksregisternummer = ?"
                + " order by starttijd asc")) {

                    //parameter invullen in query
                    stmt.setString(1, rr);
                    stmt.execute();

                    try (ResultSet r = stmt.getResultSet()) {
                        //van de rit uit de DAO de ID van de rit ophalen
                        if (r.next()) {
                            returnRitID = getRitUitDatabase(r).getId();
                        }
                        return returnRitID;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                                "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                            "- statment" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                        "- statment" + sqlEx);
            }
        }
        return null;
    }

    /**
     * Zoekt de actieve rit van een lid adhv het rijksregisternummer van het lid.
     * De actieve rit is de rit dat een starttijd heeft maar nog geen eindtijd.
     * Een lid kan slechts 1 actieve rit tegelijk hebben.
     * @param rr rijksregisternummer van het lid.
     * @return het ID van de rit dat gevonden is
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
    public Integer zoekActieveRitVanLid(String rr) throws DBException, ApplicationException {
        if (rr != null) {
            Integer returnRitID = null;

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
                                + " from rit"
                                + " where lid_rijksregisternummer = ?"
                                //bij null moet je IS/IS NOT gebruiken ipv = of !=
                                + " and starttijd IS NOT ?"
                                + " and eindtijd IS ?")) {

                    //parameter invullen in query
                    //rit moet gestart zijn (starttijd is niet null)
                    //rit mag nog niet geëindigd zijn (eindtijd is null)
                    //= actieve rit
                    stmt.setString(1, rr);
                    stmt.setNull(2, Types.DATE);
                    stmt.setNull(3, Types.DATE);
                    stmt.execute();

                    try (ResultSet r = stmt.getResultSet()) {
                        //van de rit uit de DAO de ID van de rit ophalen
                        if (r.next()) {
                            returnRitID = getRitUitDatabase(r).getId();
                        }
                        return returnRitID;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                                "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                            "- statment" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                        "- statment" + sqlEx);
            }
        }
        return null;

    }

    /**
     * Zoekt de actieve rit van een fiets adhv het registratienummer van de fiets.
     * De actieve rit is de rit dat een starttijd heeft maar nog geen eindtijd.
     * Een fiets kan slechts 1 actieve rit tegelijk hebben.
     * @param regnr registratienummer van de fiets.
     * @return het ID van de rit dat gevonden is
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
    public Integer zoekActieveRitVanFiets(int regnr) throws DBException, ApplicationException {
           Integer returnRitID = null;

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
                                + " from rit"
                                + " where fiets_registratienummer = ?"
                                //bij null moet je IS/IS NOT gebruiken ipv = of !=
                                + " and starttijd IS NOT ?"
                                + " and eindtijd IS ?")) {

                    //parameter invullen in query
                    //rit moet gestart zijn (starttijd is niet null)
                    //rit mag nog niet geëindigd zijn (eindtijd is null)
                    //= actieve rit
                    stmt.setInt(1, regnr);
                    stmt.setNull(2, Types.DATE);
                    stmt.setNull(3, Types.DATE);
                    stmt.execute();

                    try (ResultSet r = stmt.getResultSet()) {
                        //van de rit uit de DAO de ID van de rit ophalen
                        if (r.next()) {
                            returnRitID = getRitUitDatabase(r).getId();
                        }
                        return returnRitID;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                                "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                            "- statment" + sqlEx);
                }

            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekEersteRitVanLid " +
                        "- statment" + sqlEx);
            }
    }

    /**
     * Zet een resultset van de database om in een Rit-object.
     * @param r resultset
     * @return
     * @throws SQLException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     * @throws ApplicationException
     */
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