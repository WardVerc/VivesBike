package be.vives.ti.dao;

//deze hieronder is nodig voor assertThat()
import static org.assertj.core.api.Assertions.*;

import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.VerwijderTestData;
import org.junit.Test;

import java.util.ArrayList;

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

    private ArrayList<Fiets> extraFietsenToevoegen() throws DBException {
        Fiets fiets1 = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        Fiets fiets2 = maakFiets(Standplaats.Oostende, "Geen");
        Fiets fiets3 = maakFiets(Standplaats.Brugge, "Goeie fiets hoor");
        Fiets fiets4 = maakFiets(Standplaats.Tielt, "Spatbord ontbreekt");
        Fiets fiets5 = maakFiets(Standplaats.Torhout, "Toffe bel");

        //fietsen toevoegen
        fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
        fiets2.setRegistratienummer(fietsDAO.toevoegenFiets(fiets2));
        fiets3.setRegistratienummer(fietsDAO.toevoegenFiets(fiets3));
        fiets4.setRegistratienummer(fietsDAO.toevoegenFiets(fiets4));
        fiets5.setRegistratienummer(fietsDAO.toevoegenFiets(fiets5));

        //wijzigen status fiets 3 en fiets 5
        fietsDAO.wijzigenToestandFiets(fiets3.getRegistratienummer(), Status.uit_omloop, "Onherstelbaar");
        fietsDAO.wijzigenToestandFiets(fiets5.getRegistratienummer(), Status.herstel, "Kapot");

        ArrayList<Fiets> fietsen = new ArrayList<>();
        fietsen.add(fiets1);
        fietsen.add(fiets2);
        fietsen.add(fiets3);
        fietsen.add(fiets4);
        fietsen.add(fiets5);

        return fietsen;
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

    //checkt dat de status van de fiets correct gewijzigd wordt
    //met wijzigenToestandFiets()
    @Test
    public void testWijzigenToestandFietsStatus() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets wijzigen
            fietsDAO.wijzigenToestandFiets(fiets.getRegistratienummer(), Status.uit_omloop, "Test fietsopmerking");

            //gewijzigde fiets ophalen
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets.getRegistratienummer());

            //vergelijk opgehaalde fiets met toegevoegde fiets
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets.getRegistratienummer());
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.uit_omloop);
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(fiets.getStandplaats());
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Test fietsopmerking");

        } finally {
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //checkt dat de opmerking van de fiets correct gewijzigd wordt
    //met wijzigenToestandFiets()
    @Test
    public void testWijzigenToestandFietsOpmerking() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets wijzigen
            fietsDAO.wijzigenToestandFiets(fiets.getRegistratienummer(), Status.actief, "fiets is actief");

            //gewijzigde fiets ophalen
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets.getRegistratienummer());

            //vergelijk opgehaalde fiets met toegevoegde fiets
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets.getRegistratienummer());
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(fiets.getStandplaats());
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("fiets is actief");

        } finally {
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //checkt dat de opmerking van de fiets correct gewijzigd wordt
    //met wijzigenOpmerkingFiets()
    @Test
    public void testWijzigenOpmerkingFiets() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets wijzigen
            fietsDAO.wijzigenOpmerkingFiets(fiets.getRegistratienummer(), "fiets is actief");

            //gewijzigde fiets ophalen
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets.getRegistratienummer());

            //vergelijk opgehaalde fiets met toegevoegde fiets
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets.getRegistratienummer());
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(fiets.getStandplaats());
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("fiets is actief");

        } finally {
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //negatieve test
    //wijzigenToestandFiets() waarbij status null is
    //mag niet werken
    @Test
    public void testWijzigenToestandFietsZonderStatus() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //fiets toevoegen
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        assertThatThrownBy(() -> {
            fietsDAO.wijzigenToestandFiets(fiets.getRegistratienummer(), null, "Test fietsopmerking");
            //NullpointerException want null .toString() kan niet
        }).isInstanceOf(NullPointerException.class);

        VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
    }

    //negatieve test
    //wijzigenToestandFiets() waarbij opmerking null is
    //moet werken
    @Test
    public void testWijzigenToestandFietsOpmerkingZonderOpmerking() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets wijzigen
            fietsDAO.wijzigenToestandFiets(fiets.getRegistratienummer(), Status.actief, null);

            //gewijzigde fiets ophalen
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


    //negatieve test
    //wijzigenOpmerkingFiets() waarbij opmerking null is
    //moet werken
    @Test
    public void testWijzigenOpmerkingFietsZonderOpmerking() throws Exception {
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        try {
            //fiets toevoegen
            fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

            //fiets wijzigen
            fietsDAO.wijzigenOpmerkingFiets(fiets.getRegistratienummer(), null);

            //gewijzigde fiets ophalen
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

    //Checkt of het aantal beschikbare fietsen correct wordt teruggegeven
    @Test
    public void testZoekAlleBeschikbareFietsen() throws Exception {
        //reeds bestaande fietsen tellen
        int aantalFietsen = fietsDAO.zoekAlleBeschikbareFietsen().size();

        //extra Fietsen toevoegen
        ArrayList<Fiets> fietsen = extraFietsenToevoegen();

        try {
            //alle beschikbare fietsen zoeken
            ArrayList<Fiets> gevondenFietsen = fietsDAO.zoekAlleBeschikbareFietsen();

            //+ 3 want twee van de 5 toegevoegde fietsen hebben een andere status dan actief gekregen
            assertThat(gevondenFietsen.size()).isEqualTo(aantalFietsen + 3);
        } finally {
            VerwijderTestData.removeTestFietsen(fietsen);
        }
    }

}