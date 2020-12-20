package be.vives.ti.dao.connect;

import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TEST_DB {
    public static void main(String args[]) {
        try (Connection conn = ConnectionManager.getConnection();) {
// preparedStatement opstellen (en automtisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select * from lid");) {
// execute voert het SQL-statement uit
                stmt.execute();
// result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    while (r.next()) {
                        System.out.println(r.getString("voornaam") + " " +
                                r.getString("naam") + " " +
                                        r.getString("rijksregisternummer") + " " +
                                r.getString("emailadres")
                                );
                    }
                } catch (SQLException sqlEx) {
                    System.out.println("SQL-exception - resultset");
                }
            } catch (SQLException sqlEx) {
                System.out.println("SQL-exception - statement");
            }
        } catch (SQLException sqlEx) {
            System.out.println("SQL-exception - connection");
        } catch (DBException db) {
            System.out.println(db.getMessage());
        }
    }
}
