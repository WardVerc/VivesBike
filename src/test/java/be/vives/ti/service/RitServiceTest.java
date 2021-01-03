package be.vives.ti.service;

import be.vives.ti.dao.RitDAO;
import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RitServiceTest {

    private RitService ritService;
    private RitDAO ritDAO;
    private LidService lidService;
    private FietsService fietsService;

    public RitServiceTest() {
        //simulatieobject maken voor RitDAO, LidService en FietsService.
        this.ritDAO = mock(RitDAO.class);
        this.lidService = mock(LidService.class);
        this.fietsService = mock(FietsService.class);

        this.ritService = new RitService(ritDAO);
    }

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
        Lid lid = new Lid();
        lid.setVoornaam(voornaam);
        lid.setNaam(naam);
        lid.setEmailadres(email);
        lid.setStart_lidmaatschap(LocalDate.now());
        lid.setRijksregisternummer(rijks);
        lid.setOpmerking(opmerking);

        return lid;
    }

    /**
     * Maakt een fiets aan adhv een standplaats.
     * @param standplaats standplaats van de fiets
     * @param opmerking eventuele opmerking over de fiets
     * @return de aangemaakte fiets.
     */
    private Fiets maakFiets(Standplaats standplaats, String opmerking) {
        Fiets fiets = new Fiets();
        fiets.setStatus(Status.actief);
        fiets.setStandplaats(standplaats);
        fiets.setOpmerking(opmerking);

        return fiets;
    }

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
    private Rit maakRit(Rijksregisternummer rijksLid, int regnrFiets) throws ApplicationException {
        Rit rit = new Rit();

        //withNano(0) zorgt dat er geen cijfers na de komma staan
        //dit is nodig want de database slaat deze tijd op zonder cijfers na de komma
        //bij het verglijken in de test komen deze waarden niet overeen
        //en faalt de test
        rit.setStarttijd(LocalDateTime.now().withNano(0));
        rit.setLidRijksregisternummer(rijksLid);
        rit.setFietsRegistratienummer(regnrFiets);

        return rit;
    }

    @Test
    public void toevoegenRitNull() throws Exception {

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_NULL.getMessage());

        verify(ritDAO,never()).toevoegenRit(null);
    }

    @Test
    public void toevoegenRitMetId() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat rit toegevoegd is
        rit.setId(123);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_ID_WORDT_GEGENEREERD.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitLidBestaatNiet() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid niet gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(null);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitLidIsUitgeschreven() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        //simuleer dat het lid uitgeschreven is
        LocalDate date = LocalDate.of(2020,03,18);
        lid.setEinde_lidmaatschap(date);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_UITGESCHREVEN.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitHeeftActieveRitten() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        //mocken dat lid actieve ritten heeft
        when(ritService.zoekActieveRitVanLid(rit.getLidRijksregisternummer())).thenReturn(123);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_HEEFT_ACTIEVE_RITTEN.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitFietsBestaatNiet() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        //mocken lid geen actieve rit heeft
        when(ritService.zoekActieveRitVanLid(rit.getLidRijksregisternummer())).thenReturn(null);

        //mocken dat fiets niet gevonden wordt
        when(fietsService.zoekFiets(rit.getFietsRegistratienummer())).thenReturn(null);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitFietsVerkeerdeStatus() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        //mocken lid geen actieve rit heeft
        when(ritService.zoekActieveRitVanLid(rit.getLidRijksregisternummer())).thenReturn(null);

        //mocken dat fiets gevonden
        when(fietsService.zoekFiets(rit.getFietsRegistratienummer())).thenReturn(fiets);

        //mocken dat fiets verkeerde status heeft
        fiets.setStatus(Status.herstel);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_NIET_CORRECTE_STATUS.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitFietsHeeftActieveRit() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        //mocken lid geen actieve rit heeft
        when(ritService.zoekActieveRitVanLid(rit.getLidRijksregisternummer())).thenReturn(null);

        //mocken dat fiets gevonden
        when(fietsService.zoekFiets(rit.getFietsRegistratienummer())).thenReturn(fiets);

        //mocken dat fiets actieve rit heeft
        when(ritService.zoekActieveRitVanFiets(rit.getFietsRegistratienummer())).thenReturn(123);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_IN_GEBRUIK.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitIsAlAfgesloten() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        //mocken lid geen actieve rit heeft
        when(ritService.zoekActieveRitVanLid(rit.getLidRijksregisternummer())).thenReturn(null);

        //mocken dat fiets gevonden
        when(fietsService.zoekFiets(rit.getFietsRegistratienummer())).thenReturn(fiets);

        //mocken dat fiets geen actieve rit heeft
        when(ritService.zoekActieveRitVanFiets(rit.getFietsRegistratienummer())).thenReturn(null);

        //mocken dat rit al is afgesloten
        LocalDateTime date = LocalDateTime.of(2020, 12, 29, 18, 0);
        rit.setEindtijd(date);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_AL_AFGESLOTEN.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRitSuccesvol() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        //mocken lid geen actieve rit heeft
        when(ritService.zoekActieveRitVanLid(rit.getLidRijksregisternummer())).thenReturn(null);

        //mocken dat fiets gevonden
        when(fietsService.zoekFiets(rit.getFietsRegistratienummer())).thenReturn(fiets);

        //mocken dat fiets geen actieve rit heeft
        when(ritService.zoekActieveRitVanFiets(rit.getFietsRegistratienummer())).thenReturn(null);

        assertThatCode(() -> {
            ritService.toevoegenRit(rit);
        }).doesNotThrowAnyException();
    }

    @Test
    public void afsluitenRitNull() throws Exception {
        assertThatThrownBy(() -> {
            ritService.afsluitenRit(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_NULL.getMessage());

        verify(ritDAO,never()).afsluitenRit(null);
    }

    @Test
    public void afsluitenRitBestaatNiet() throws Exception {
        assertThatThrownBy(() -> {
            ritService.afsluitenRit(123);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_BESTAAT_NIET.getMessage());

        verify(ritDAO,never()).afsluitenRit(null);
    }

    @Test
    public void afsluitenRitNietGestart() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);

        //mocken dat rit gevonden wordt
        when(ritService.zoekRit(ritId)).thenReturn(rit);

        //mocken dat rit geen starttijd heeft
        rit.setStarttijd(null);

        assertThatThrownBy(() -> {
            ritService.afsluitenRit(rit.getId());
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_NIET_GESTART.getMessage());

        verify(ritDAO,never()).afsluitenRit(rit);
    }

    @Test
    public void afsluitenRitAlAfgesloten() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);

        //mocken dat rit gevonden wordt
        when(ritService.zoekRit(ritId)).thenReturn(rit);

        //mocken dat rit al afgesloten is
        LocalDateTime date = LocalDateTime.of(2020, 12, 29, 18, 0);
        rit.setEindtijd(date);

        assertThatThrownBy(() -> {
            ritService.afsluitenRit(rit.getId());
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_AL_AFGESLOTEN.getMessage());

        verify(ritDAO,never()).afsluitenRit(rit);
    }

    @Test
    public void afsluitenRitMetPrijs() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);

        //mocken dat rit een prijs heeft
        rit.setPrijs(BigDecimal.ONE);

        //mocken dat rit gevonden wordt
        when(ritService.zoekRit(ritId)).thenReturn(rit);

        assertThatThrownBy(() -> {
            ritService.afsluitenRit(rit.getId());
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_PRIJS_AL_BEPAALD.getMessage());

        verify(ritDAO,never()).afsluitenRit(rit);
    }

    @Test
    public void afsluitenRitSuccesvol() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);

        //mocken dat rit gevonden wordt
        when(ritService.zoekRit(ritId)).thenReturn(rit);

        assertThatCode(() -> {
            ritService.afsluitenRit(rit.getId());
        }).doesNotThrowAnyException();
    }

    @Test
    public void afsluitenRitCorrectePrijsbepaling() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);

        //zorg ervoor dat de fiets 25uur gehuurd werd en dus de prijs 2 euro is
        LocalDateTime date = LocalDateTime.now().minusHours(25);
        rit.setStarttijd(date);

        //mocken dat rit gevonden wordt
        when(ritService.zoekRit(ritId)).thenReturn(rit);

        assertThatCode(() -> {
            ritService.afsluitenRit(rit.getId());
        }).doesNotThrowAnyException();

        assertThat(rit.getPrijs()).isEqualTo(BigDecimal.valueOf(2));
    }

    @Test
    public void zoekRitNull() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekRit(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_NULL.getMessage());

        verify(ritDAO,never()).zoekRit(null);
    }

    @Test
    public void zoekRitSuccesvol() throws Exception {
        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);

        when(ritDAO.zoekRit(ritId)).thenReturn(rit);

        assertThat(ritService.zoekRit(ritId)).isEqualTo(rit);
    }

    @Test
    public void zoekEersteRitVanLidNull() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekEersteRitVanLid(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RR_LEEG.getMessage());

        verify(ritDAO,never()).zoekEersteRitVanLid(null);
    }

    @Test
    public void zoekEersteRitVanLidLidBestaatNiet() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekEersteRitVanLid("123");
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());

    }

    @Test
    public void zoekEersteRitVanLidSuccesvol() throws Exception {
        //zie ook test van RitDAO: testZoekEersteRitvanLid()
        //daar wordt getest dat de rit inderdaad de oudste/eerste rit is van het lid

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);
        String rr = lid.getRijksregisternummer();

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        when(ritDAO.zoekEersteRitVanLid(rr)).thenReturn(ritId);

        assertThat(ritService.zoekEersteRitVanLid(rr)).isEqualTo(ritId);
    }

    @Test
    public void zoekActieveRitVanLidNull() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekActieveRitVanLid(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RR_LEEG.getMessage());

        verify(ritDAO,never()).zoekActieveRitVanLid(null);
    }

    @Test
    public void zoekActieveRitVanLidLidBestaatNiet() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekActieveRitVanLid("123");
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
    }

    @Test
    public void zoekActieveRitVanLidSuccesvol() throws Exception {
        //zie ook test van RitDAO: testZoekActieveRitvanLid()
        //daar wordt getest dat de rit inderdaad enkel die zonder eindtijd (met starttijd)
        // is van het lid

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);
        String rr = lid.getRijksregisternummer();

        //mocken dat lid gevonden wordt
        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        when(ritDAO.zoekActieveRitVanLid(rr)).thenReturn(ritId);

        assertThat(ritService.zoekActieveRitVanLid(rr)).isEqualTo(ritId);
    }

    @Test
    public void zoekActieveRitVanFietsNull() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekActieveRitVanFiets(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_NULL.getMessage());
    }

    @Test
    public void zoekActieveRitVanFietsGeenFiets() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekActieveRitVanFiets(123);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
    }

    @Test
    public void zoekActieveRitVanFietsSuccesvol() throws Exception {
        //zie ook test van RitDAO: testZoekActieveRitvanFiets()
        //daar wordt getest dat de rit inderdaad enkel die zonder eindtijd (met starttijd)
        // is van de fiets

        //testdata aanmaken
        Rijksregisternummer rijks = new Rijksregisternummer("94031820982");
        Lid lid = maakLid("Ward", "Vercruyssen", "ward@hotmail.be", rijks, "Test opmerking");
        Fiets fiets = maakFiets(Standplaats.Kortrijk, "Test fietsopmerking");

        //simuleer dat fiets is toegevoegd aan db en dus registratienummer heeft
        fiets.setRegistratienummer(123);

        Rit rit = maakRit(rijks, fiets.getRegistratienummer());
        Integer ritId = 123;
        rit.setId(ritId);
        Integer rr = fiets.getRegistratienummer();

        //mocken dat lid gevonden wordt
        when(fietsService.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);

        when(ritDAO.zoekActieveRitVanFiets(rr)).thenReturn(ritId);

        assertThat(ritService.zoekActieveRitVanFiets(rr)).isEqualTo(ritId);
    }
}