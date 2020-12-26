package be.vives.ti.dao;

import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.extra.VerwijderTestData;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testToevoegenRit() throws Exception {
        //maak testdata aan
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        LidDAO lidDAO = new LidDAO();
        lidDAO.toevoegenLid(ward);

        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");
        FietsDAO fietsDAO = new FietsDAO();
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        //maak rit aan
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

}