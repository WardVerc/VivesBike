package be.vives.ti.dao;

import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.VerwijderTestData;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

public class RitDAOTest {

    private RitDAO ritDAO = new RitDAO();

    /**
     * Maakt een nieuw test rit aan adhv een lid en een fiets.
     * De rit heeft een starttijd van het moment dat het aangemaakt wordt.
     * De rit heeft geen eindtijd en dus geen prijs.
     * Het id wordt gegenereerd tijdens het toevoegen aan de db.
     *
     * @param rijksLid rijksregisternummer van het lid
     * @param regnrFiets registratienummer van de fiets
     * @return gemaakte test rit
     */
    private Rit maakRit(Rijksregisternummer rijksLid, Integer regnrFiets) {
        Rit rit = new Rit();

        //withNano(0) zorgt dat er geen cijfers na de komma staan
        //dit is nodig want de database slaat deze tijd op zonder cijfers na de komma
        //bij het verglijken in de test komen deze waarden dus niet overeen
        //en faalt de test
        rit.setStarttijd(LocalDateTime.now().withNano(0));
        rit.setLidRijksregisternummer(rijksLid);
        rit.setFietsRegistratienummer(regnrFiets);

        return rit;
    }

    private Lid maakLid(String voornaam, String naam, String email, Rijksregisternummer rijks, String opmerking) {
        Lid ward = new Lid();
        ward.setVoornaam(voornaam);
        ward.setNaam(naam);
        ward.setEmailadres(email);
        ward.setStart_lidmaatschap(LocalDate.now());
        ward.setRijksregisternummer(rijks);
        ward.setOpmerking(opmerking);

        return ward;
    }


    private Fiets maakFiets(Standplaats standplaats, String opmerking) {
        Fiets fiets = new Fiets();
        fiets.setStatus(Status.actief);
        fiets.setStandplaats(standplaats);
        fiets.setOpmerking(opmerking);

        return fiets;
    }

    //checkt of de data overeenkomt van de opgehaalde rit en de toegevoegde rit
    @Test
    public void testToevoegenRit() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        try {
            //toevoegen rit
            rit.setId(ritDAO.toevoegenRit(rit));

            //ophalen rit
            Rit ophaalRit = ritDAO.zoekRit(rit.getId());

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit.getId()).isEqualTo(rit.getId());
            assertThat(ophaalRit.getStarttijd()).isEqualTo(rit.getStarttijd());
            assertThat(ophaalRit.getEindtijd()).isEqualTo(rit.getEindtijd());
            assertThat(ophaalRit.getPrijs()).isEqualTo(rit.getPrijs());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(ward.getRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(fiets.getRegistratienummer());

        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRit(rit.getId());
            VerwijderTestData.removeTestLid(rijks);
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //checkt dat het niet mogelijk is om een rit toe te voegen
    //zonder rijksregisternummer
    @Test
    public void testToevoegenRitZonderRijksnr() throws Exception {

        //maak testdata aan
        //geen lid, want is null
        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(null, fiets.getRegistratienummer());

        assertThatThrownBy(() -> {
            ritDAO.toevoegenRit(rit);
        }).isInstanceOf(DBException.class);

        VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());

    }

    //checkt dat het niet mogelijk is om een rit toe te voegen
    //zonder registratienummer (fiets)
    @Test
    public void testMaakRitZonderFietsnr() throws Exception {

        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //geen fiets want is null

        assertThatThrownBy(() -> {
            Rit rit = maakRit(rijks, null);
        }).isInstanceOf(NullPointerException.class);

        VerwijderTestData.removeTestLid(rijks);
    }

    //toevoegen rit null mag niet mogelijk zijn
    @Test
    public void testToevoegenRitNull() throws Exception {
        Integer ritID = ritDAO.toevoegenRit(null);

        assertThat(ritID).isNull();
    }

    //checkt of de data van een opgehaald rit gelijk is aan de data van het toegevoegde rit
    //na het afsluiten van de rit
    @Test
    public void testAfsluitenRit() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //toevoegen rit
        rit.setId(ritDAO.toevoegenRit(rit));

        //eindtijd en prijs toevoegen
        //withNano(0) zorgt dat er geen cijfers na de komma staan
        //dit is nodig want de database slaat deze tijd op zonder cijfers na de komma
        //bij het verglijken in de test komen deze waarden dus niet overeen
        //en faalt de test
        rit.setEindtijd(LocalDateTime.now().withNano(0));
        rit.setPrijs(BigDecimal.valueOf(5));

        try {
            //afsluiten rit
            ritDAO.afsluitenRit(rit);

            //ophalen rit
            Rit ophaalRit = ritDAO.zoekRit(rit.getId());

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit.getId()).isEqualTo(rit.getId());
            assertThat(ophaalRit.getStarttijd()).isEqualTo(rit.getStarttijd());
            assertThat(ophaalRit.getEindtijd()).isEqualTo(rit.getEindtijd());
            assertThat(ophaalRit.getPrijs()).isEqualTo(rit.getPrijs());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(ward.getRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(fiets.getRegistratienummer());

        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRit(rit.getId());
            VerwijderTestData.removeTestLid(rijks);
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //Checkt dat afsluiten van een rit zonder eindtijd niet mogelijk is
    //er moet altijd een eindtijd zijn als de rit afgesloten wordt
    @Test
    public void testAfsluitenRitZonderEindtijd() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //toevoegen rit
        rit.setId(ritDAO.toevoegenRit(rit));

        //eindtijd is null
        rit.setEindtijd(null);

        assertThatThrownBy(() -> {
            ritDAO.afsluitenRit(rit);

        }).isInstanceOf(NullPointerException.class);

        //verwijder testdata
        VerwijderTestData.removeTestRit(rit.getId());
        VerwijderTestData.removeTestLid(rijks);
        VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());

    }

    //Checkt dat afsluiten van een rit zonder prijs niet mogelijk is
    //er moet altijd een prijs zijn als de rit afgesloten wordt
    @Test
    public void testAfsluitenRitZonderPrijs() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //toevoegen rit
        rit.setId(ritDAO.toevoegenRit(rit));

        //eindtijd en prijs toevoegen
        //withNano(0) zorgt dat er geen cijfers na de komma staan
        //dit is nodig want de database slaat deze tijd op zonder cijfers na de komma
        //bij het verglijken in de test komen deze waarden dus niet overeen
        //en faalt de test
        rit.setEindtijd(LocalDateTime.now().withNano(0));
        rit.setPrijs(null);

        assertThatThrownBy(() -> {
            ritDAO.afsluitenRit(rit);

        }).isInstanceOf(NullPointerException.class);

        //verwijder testdata
        VerwijderTestData.removeTestRit(rit.getId());
        VerwijderTestData.removeTestLid(rijks);
        VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
    }

    //zoek rit adhv rit ID
    @Test
    public void testZoekRit() throws Exception {
        //extra ritten toevoegen had ik eerst apart gezet extraRittenToevoegen()
        //maar toen kon ik de testdata leden en fietsen niet meer verwijderen, ritten wel
        //om dit op te lossen staat heel die methode om extra data te maken hier

        //extra leden
        LidDAO lidDAO = new LidDAO();

        Rijksregisternummer rijks1 = new Rijksregisternummer("94031820982");
        Lid lid1 = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks1, "Test opmerking");
        Rijksregisternummer rijks2 = new Rijksregisternummer("94090200136");
        Lid lid2 = maakLid("Michiel", "Demoor", "michiel@hotmail.be", rijks2, "Test opmerking");
        Rijksregisternummer rijks3 = new Rijksregisternummer("96030800249");
        Lid lid3 = maakLid("Kyra", "Matton", "kyra@hotmail.be", rijks3, "Test opmerking");

        lidDAO.toevoegenLid(lid1);
        lidDAO.toevoegenLid(lid2);
        lidDAO.toevoegenLid(lid3);

        //extra fietsen
        FietsDAO fietsDAO = new FietsDAO();

        Fiets fiets1 = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        Fiets fiets2 = maakFiets(Standplaats.Oostende, "Geen");
        Fiets fiets3 = maakFiets(Standplaats.Brugge, "Goeie fiets hoor");

        fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
        fiets2.setRegistratienummer(fietsDAO.toevoegenFiets(fiets2));
        fiets3.setRegistratienummer(fietsDAO.toevoegenFiets(fiets3));

        //extra ritten
        Rit rit1 = maakRit(rijks1, fiets1.getRegistratienummer());
        Rit rit2 = maakRit(rijks2, fiets2.getRegistratienummer());
        Rit rit3 = maakRit(rijks3, fiets3.getRegistratienummer());

        rit1.setId(ritDAO.toevoegenRit(rit1));
        rit2.setId(ritDAO.toevoegenRit(rit2));
        rit3.setId(ritDAO.toevoegenRit(rit3));

        ArrayList<Rit> ritten = new ArrayList<>();
        ritten.add(rit1);
        ritten.add(rit2);
        ritten.add(rit3);

        try {
            Rit ophaalRit = ritDAO.zoekRit(ritten.get(0).getId());

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit.getId()).isEqualTo(ritten.get(0).getId());
            assertThat(ophaalRit.getStarttijd()).isEqualTo(ritten.get(0).getStarttijd());
            assertThat(ophaalRit.getEindtijd()).isEqualTo(ritten.get(0).getEindtijd());
            assertThat(ophaalRit.getPrijs()).isEqualTo(ritten.get(0).getPrijs());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(ritten.get(0).getLidRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(ritten.get(0).getFietsRegistratienummer());
        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRitten(ritten);

            //arraylist maakt het mogelijk deze testdata terug te verwijderen
            ArrayList<Lid> leden = new ArrayList<>();
            leden.add(lid1);
            leden.add(lid2);
            leden.add(lid3);
            VerwijderTestData.removeTestLeden(leden);

            //arraylist maakt het mogelijk deze testdata terug te verwijderen
            ArrayList<Fiets> fietsen = new ArrayList<>();
            fietsen.add(fiets1);
            fietsen.add(fiets2);
            fietsen.add(fiets3);
            VerwijderTestData.removeTestFietsen(fietsen);
        }
    }

    //zoek rit adhv rit ID
    @Test
    public void testZoekRitIDNull() throws Exception {
        //extra ritten toevoegen had ik eerst apart gezet extraRittenToevoegen()
        //maar toen kon ik de testdata leden en fietsen niet meer verwijderen, ritten wel
        //om dit op te lossen staat heel die methode om extra data te maken hier

        //extra leden
        LidDAO lidDAO = new LidDAO();

        Rijksregisternummer rijks1 = new Rijksregisternummer("94031820982");
        Lid lid1 = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks1, "Test opmerking");
        Rijksregisternummer rijks2 = new Rijksregisternummer("94090200136");
        Lid lid2 = maakLid("Michiel", "Demoor", "michiel@hotmail.be", rijks2, "Test opmerking");
        Rijksregisternummer rijks3 = new Rijksregisternummer("96030800249");
        Lid lid3 = maakLid("Kyra", "Matton", "kyra@hotmail.be", rijks3, "Test opmerking");

        lidDAO.toevoegenLid(lid1);
        lidDAO.toevoegenLid(lid2);
        lidDAO.toevoegenLid(lid3);

        //extra fietsen
        FietsDAO fietsDAO = new FietsDAO();

        Fiets fiets1 = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        Fiets fiets2 = maakFiets(Standplaats.Oostende, "Geen");
        Fiets fiets3 = maakFiets(Standplaats.Brugge, "Goeie fiets hoor");

        fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
        fiets2.setRegistratienummer(fietsDAO.toevoegenFiets(fiets2));
        fiets3.setRegistratienummer(fietsDAO.toevoegenFiets(fiets3));

        //extra ritten
        Rit rit1 = maakRit(rijks1, fiets1.getRegistratienummer());
        Rit rit2 = maakRit(rijks2, fiets2.getRegistratienummer());
        Rit rit3 = maakRit(rijks3, fiets3.getRegistratienummer());

        rit1.setId(ritDAO.toevoegenRit(rit1));
        rit2.setId(ritDAO.toevoegenRit(rit2));
        rit3.setId(ritDAO.toevoegenRit(rit3));

        ArrayList<Rit> ritten = new ArrayList<>();
        ritten.add(rit1);
        ritten.add(rit2);
        ritten.add(rit3);

        try {
            //rit zoeken met id null
            Rit ophaalRit = ritDAO.zoekRit(null);

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit).isNull();
        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRitten(ritten);

            //arraylist maakt het mogelijk deze testdata terug te verwijderen
            ArrayList<Lid> leden = new ArrayList<>();
            leden.add(lid1);
            leden.add(lid2);
            leden.add(lid3);
            VerwijderTestData.removeTestLeden(leden);

            //arraylist maakt het mogelijk deze testdata terug te verwijderen
            ArrayList<Fiets> fietsen = new ArrayList<>();
            fietsen.add(fiets1);
            fietsen.add(fiets2);
            fietsen.add(fiets3);
            VerwijderTestData.removeTestFietsen(fietsen);
        }
    }

    //checkt of de eerste (starttijd is de oudste) rit van een lid opgehaald wordt
    @Test
    public void testZoekEersteRitVanLid() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        LocalDateTime oudsteDate = LocalDateTime.of(2019, Month.JANUARY, 1, 8, 00, 00);
        rit.setStarttijd(oudsteDate);

        Rit rit2 = maakRit(rijks, fiets.getRegistratienummer());
        LocalDateTime middenDate = LocalDateTime.of(2020, Month.JANUARY, 1, 8, 00, 00);
        rit2.setStarttijd(middenDate);

        Rit rit3 = maakRit(rijks, fiets.getRegistratienummer());
        LocalDateTime jongsteDate = LocalDateTime.of(2020, Month.FEBRUARY, 1, 8, 00, 00);
        rit3.setStarttijd(jongsteDate);

        //toevoegen ritten
        rit.setId(ritDAO.toevoegenRit(rit));
        rit2.setId(ritDAO.toevoegenRit(rit2));
        rit3.setId(ritDAO.toevoegenRit(rit3));

        ArrayList<Rit> ritten = new ArrayList<>();
        ritten.add(rit);
        ritten.add(rit2);
        ritten.add(rit3);

        try {

            //ophalen eerste rit
            Rit ophaalRit = ritDAO.zoekRit(ritDAO.zoekEersteRitVanLid(rijks.getRijksregisternummer()));

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit.getStarttijd()).isEqualTo(oudsteDate);

        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRitten(ritten);
            VerwijderTestData.removeTestLid(rijks);
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //checkt of de actieve rit van een lid opgehaald wordt
    @Test
    public void testZoekActieveRitVanLid() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Rit rit2 = maakRit(rijks, fiets.getRegistratienummer());
        Rit rit3 = maakRit(rijks, fiets.getRegistratienummer());

        //start- en eindtijden en prijzen toevoegen behave rit 3 (= actieve rit)
        LocalDateTime oudsteDate = LocalDateTime.of(2019, Month.JANUARY, 1, 8, 00, 00).withNano(0);
        rit.setStarttijd(oudsteDate);
        LocalDateTime eindDate = LocalDateTime.of(2019, Month.JANUARY, 1, 10, 00, 00).withNano(0);
        rit.setEindtijd(eindDate);
        rit.setPrijs(BigDecimal.valueOf(1));

        LocalDateTime middenDate = LocalDateTime.of(2020, Month.JANUARY, 5, 8, 00, 00).withNano(0);
        rit2.setStarttijd(middenDate);
        LocalDateTime eindDate2 = LocalDateTime.of(2020, Month.JANUARY, 6, 10, 00, 00).withNano(0);
        rit2.setEindtijd(eindDate2);
        rit2.setPrijs(BigDecimal.valueOf(2));

        LocalDateTime jongsteDate = LocalDateTime.of(2020, Month.FEBRUARY, 1, 8, 00, 00);
        rit3.setStarttijd(jongsteDate);

        //toevoegen ritten
        rit.setId(ritDAO.toevoegenRit(rit));
        rit2.setId(ritDAO.toevoegenRit(rit2));
        rit3.setId(ritDAO.toevoegenRit(rit3));

        //afsluiten ritten
        ritDAO.afsluitenRit(rit);
        ritDAO.afsluitenRit(rit2);

        ArrayList<Rit> ritten = new ArrayList<>();
        ritten.add(rit);
        ritten.add(rit2);
        ritten.add(rit3);

        try {

            //ophalen actieve rit
            Rit ophaalRit = ritDAO.zoekRit(ritDAO.zoekActieveRitVanLid(rijks.getRijksregisternummer()));

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit.getId()).isEqualTo(rit3.getId());
            assertThat(ophaalRit.getStarttijd()).isEqualTo(rit3.getStarttijd());
            assertThat(ophaalRit.getEindtijd()).isEqualTo(rit3.getEindtijd());
            assertThat(ophaalRit.getPrijs()).isEqualTo(rit3.getPrijs());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(rit3.getLidRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(rit3.getFietsRegistratienummer());

        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRitten(ritten);
            VerwijderTestData.removeTestLid(rijks);
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

    //checkt of de actieve rit van een fiets opgehaald wordt
    @Test
    public void testZoekActieveRitVanFiets() throws Exception {
        //maak testdata aan
        //lid
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        //fiets
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //rit
        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Rit rit2 = maakRit(rijks, fiets.getRegistratienummer());
        Rit rit3 = maakRit(rijks, fiets.getRegistratienummer());

        //start- en eindtijden en prijzen toevoegen behave rit 3 (= actieve rit)
        LocalDateTime oudsteDate = LocalDateTime.of(2019, Month.JANUARY, 1, 8, 00, 00).withNano(0);
        rit.setStarttijd(oudsteDate);
        LocalDateTime eindDate = LocalDateTime.of(2019, Month.JANUARY, 1, 10, 00, 00).withNano(0);
        rit.setEindtijd(eindDate);
        rit.setPrijs(BigDecimal.valueOf(1));

        LocalDateTime middenDate = LocalDateTime.of(2020, Month.JANUARY, 5, 8, 00, 00).withNano(0);
        rit2.setStarttijd(middenDate);
        LocalDateTime eindDate2 = LocalDateTime.of(2020, Month.JANUARY, 6, 10, 00, 00).withNano(0);
        rit2.setEindtijd(eindDate2);
        rit2.setPrijs(BigDecimal.valueOf(2));

        LocalDateTime jongsteDate = LocalDateTime.of(2020, Month.FEBRUARY, 1, 8, 00, 00);
        rit3.setStarttijd(jongsteDate);

        //toevoegen ritten
        rit.setId(ritDAO.toevoegenRit(rit));
        rit2.setId(ritDAO.toevoegenRit(rit2));
        rit3.setId(ritDAO.toevoegenRit(rit3));

        //afsluiten ritten
        ritDAO.afsluitenRit(rit);
        ritDAO.afsluitenRit(rit2);

        ArrayList<Rit> ritten = new ArrayList<>();
        ritten.add(rit);
        ritten.add(rit2);
        ritten.add(rit3);

        try {

            //ophalen actieve rit
            Rit ophaalRit = ritDAO.zoekRit(ritDAO.zoekActieveRitVanFiets(fiets.getRegistratienummer()));

            //vergelijk opgehaalde rit met toegevoegde rit
            assertThat(ophaalRit.getId()).isEqualTo(rit3.getId());
            assertThat(ophaalRit.getStarttijd()).isEqualTo(rit3.getStarttijd());
            assertThat(ophaalRit.getEindtijd()).isEqualTo(rit3.getEindtijd());
            assertThat(ophaalRit.getPrijs()).isEqualTo(rit3.getPrijs());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(rit3.getLidRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(rit3.getFietsRegistratienummer());

        } finally {
            //eerst moet je de rit verwijderen aangezien deze foreign keys (fiets, lid) bevat!
            //dan pas kan je fiets en lid verwijderen
            VerwijderTestData.removeTestRitten(ritten);
            VerwijderTestData.removeTestLid(rijks);
            VerwijderTestData.removeTestFiets(fiets.getRegistratienummer());
        }
    }

}