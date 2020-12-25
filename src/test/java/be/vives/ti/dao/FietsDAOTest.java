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

    //fiets toevoegen
    //zonder opmerking
    @Test public void testToevoegenFietsZonderOpmerking() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, null);

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets ophalen
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets.getRegistratienummer());

            //vergelijk opgehaalde fiets met toegevoegde fiets
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets.getRegistratienummer());
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(fiets.getStandplaats());
            assertThat(ophaalFiets.getOpmerking()).isNull();

        } finally {
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    @Test
    public void testToevoegenFietsZonderStandplaats() {
        Fiets fiets = maakFiets(null, "Test fietsopmerking");

        assertThatThrownBy(() -> {
            fietsDAO.toevoegenFiets(fiets);
            //NullpointerException want null .toString() kan niet
        }).isInstanceOf(NullPointerException.class);
    }

    //geen fiets moet null weergeven
    @Test
    public void testToevoegenFietsNull() throws Exception {
        Integer fietsID = fietsDAO.toevoegenFiets(null);

        assertThat(fietsID).isNull();
    }


    @Test
    public void testWijzigenFiets() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets wijzigen
            fietsDAO.wijzigenToestandFiets(fiets.getRegistratienummer(), Status.uit_omloop, "fiets is onherstelbaar");

            //gewijzigde fiets ophalen
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets.getRegistratienummer());

            //vergelijk opgehaalde fiets met toegevoegde fiets
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets.getRegistratienummer());
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.uit_omloop);
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(fiets.getStandplaats());
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("fiets is onherstelbaar");

        } finally {
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

}