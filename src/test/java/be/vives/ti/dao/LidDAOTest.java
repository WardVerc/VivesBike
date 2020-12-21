package be.vives.ti.dao;

import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import org.assertj.core.api.Assertions;
//deze hieronder is nodig voor assertThat()
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import java.time.LocalDate;

public class LidDAOTest {

    private LidDAO lidDAO = new LidDAO();

    private Lid maakLid(String voornaam, String naam, String email, LocalDate startdatum, LocalDate einddatum, Rijksregisternummer rijks, String opmerking) {
        Lid ward = new Lid();
        ward.setVoornaam(voornaam);
        ward.setNaam(naam);
        ward.setEmailadres(email);
        ward.setStart_lidmaatschap(startdatum);
        ward.setEinde_lidmaatschap(einddatum);
        ward.setRijksregisternummer(rijks);
        ward.setOpmerking(opmerking);

        return ward;
    }

    @Test
    public void testToevoegenLid() throws Exception {

        LocalDate local = LocalDate.of(2020, 12, 15);
        LocalDate local2 = LocalDate.of(2020, 12, 30);
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");

        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", local, local2, rijks, "Test opmerking");

        try {
            lidDAO.toevoegenLid(ward);

            Lid ophaalLid = lidDAO.zoekLid(rijks);

            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo(ward.getRijksregisternummer());
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Ward");
            assertThat(ophaalLid.getNaam()).isEqualTo("Vercruyssen");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("ward@hotmail.be");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(ward.getStart_lidmaatschap());
            assertThat(ophaalLid.getEinde_lidmaatschap()).isEqualTo(ward.getEinde_lidmaatschap());
            assertThat(ophaalLid.getOpmerking()).isEqualTo("Test opmerking");

        } finally {
            System.out.println("Nu is de test gelukt?");
            //test was OK nu nog de data verwijderen:

        }



    }
}