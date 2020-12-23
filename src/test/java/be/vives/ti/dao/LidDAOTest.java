package be.vives.ti.dao;

import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.VerwijderTestData;
import org.assertj.core.api.Assertions;
//deze hieronder is nodig voor assertThat()
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;


import java.time.LocalDate;

public class LidDAOTest {

    private LidDAO lidDAO = new LidDAO();

    /**
     * Maakt een nieuw test lid aan
     *
     * @param voornaam van het test lid
     * @param naam
     * @param email
     * @param rijks
     * @param opmerking
     * @return gemaakte test lid
     */
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

    //Checkt dat data van het aangemaakte lid klopt met data dat opgehaald wordt
    //bij eenzelfde rijksregisternummer
    @Test
    public void testToevoegenLid() throws Exception {

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");

        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");

        try {
            //de te testen methodeuitvoeren met de testdata
            lidDAO.toevoegenLid(ward);

            //haal het lid op met hetzelfde rijksregisternummer
            Lid ophaalLid = lidDAO.zoekLid(rijks);

            //check of de data tussen deze twee leden hetzelfde is
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo(ward.getRijksregisternummer());
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Ward");
            assertThat(ophaalLid.getNaam()).isEqualTo("Vercruyssen");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("ward@hotmail.be");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(ward.getStart_lidmaatschap());
            assertThat(ophaalLid.getEinde_lidmaatschap()).isEqualTo(ward.getEinde_lidmaatschap());
            assertThat(ophaalLid.getOpmerking()).isEqualTo("Test opmerking");

        } finally {
            //testdata verwijderen:
            VerwijderTestData.removeTestLid(rijks);
        }
    }

    //checkt of je ook leden kan toevoegen zonder opmerking
    @Test
    public void testToevoegenLidZonderOpmerking() throws Exception {

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");

        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks,null);

        try {
            //de te testen methodeuitvoeren met de testdata
            lidDAO.toevoegenLid(ward);

            //haal het lid op met hetzelfde rijksregisternummer
            Lid ophaalLid = lidDAO.zoekLid(rijks);

            //check of de data tussen deze twee leden hetzelfde is
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo(ward.getRijksregisternummer());
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Ward");
            assertThat(ophaalLid.getNaam()).isEqualTo("Vercruyssen");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("ward@hotmail.be");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(ward.getStart_lidmaatschap());
            assertThat(ophaalLid.getEinde_lidmaatschap()).isEqualTo(ward.getEinde_lidmaatschap());
            assertThat(ophaalLid.getOpmerking()).isEqualTo(null);

        } finally {
            //testdata verwijderen:
            VerwijderTestData.removeTestLid(rijks);
        }
    }

    //negatieve test: lid toevoegen zonder voornaam
    @Test
    public void testToevoegenLidZonderVoornaam() throws Exception {

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid(null, "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");

        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(ward);
        }).isInstanceOf(DBException.class);
    }

    //negatieve test: lid toevoegen zonder naam
    @Test
    public void testToevoegenLidZonderNaam() throws Exception {

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", null, "ward@hotmail.be", rijks, "Test opmerking");

        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(ward);
        }).isInstanceOf(DBException.class);
    }

    //negatieve test: lid toevoegen zonder email
    @Test
    public void testToevoegenLidZonderEmail() throws Exception {

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid ward = maakLid("Ward", "Vercruyssen", null, rijks, "Test opmerking");

        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(ward);
        }).isInstanceOf(DBException.class);
    }

    //negatieve test: lid toevoegen zonder rijksregisternummer
    @Test
    public void testToevoegenLidZonderRijksregisternummer() throws Exception {

        //testdata aanmaken
        Lid ward = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", null, "Test opmerking");

        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(ward);
        }).isInstanceOf(DBException.class);
    }

}