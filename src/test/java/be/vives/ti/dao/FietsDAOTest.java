package be.vives.ti.dao;

//deze hieronder is nodig voor assertThat()
import static org.assertj.core.api.Assertions.*;

import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.VerwijderTestData;
import org.junit.Test;

public class FietsDAOTest {

    private FietsDAO fietsDAO = new FietsDAO();

    //maak testdata (fiets)
    private Fiets maakFiets(Standplaats standplaats, String opmerking) {
        Fiets fiets = new Fiets();
        fiets.setStatus(Status.actief);
        fiets.setStandplaats(standplaats);
        fiets.setOpmerking(opmerking);

        return fiets;
    }

    //Checkt of de data van de toegevoegde fiets overeenkomt met de
    //opgehaalde fiets adhv het registratienummer
    @Test
    public void testToevoegenFiets() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets ophalen
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets.getRegistratienummer());

            //vergelijk opgehaalde fiets met toegevoegde fiets
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets.getRegistratienummer());
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(fiets.getStandplaats());
            assertThat(ophaalFiets.getOpmerking()).isEqualTo(fiets.getOpmerking());

        } finally {
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }

    }
}