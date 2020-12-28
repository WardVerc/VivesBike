package be.vives.ti.service;

import be.vives.ti.dao.LidDAO;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Bevat alle functionaliteit van een lid met de nodige checks.
 * Toevoegen/wijzigen/verwijderen lid.
 */
public class LidService {

    private LidDAO lidDAO;
    private RitService ritService;

    public LidService(LidDAO lidDAO, RitService ritService) {
        this.lidDAO = lidDAO;
        this.ritService = ritService;
    }

    /**
     * Voegt een lid toe.
     * @param l lid dat zordt toegevoegd
     * @return Rijksregisternummer (String) van het toegevoegde lid
     * @throws ApplicationException Wordt gegooid wanneer geen lid
     *                              werd opgegeven, wanneer niet alle velden (correct) ingevuld zijn, of
     *                              wanneer een lid al bestaat.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public String toevoegenLid(Lid l) throws ApplicationException, DBException {
        //parameter moet ingevuld zijn
        if (l == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_NULL.getMessage());
        }

        //alle gegevens moeten ingevuld zijn
        checkAlleVeldenIngevuld(l);

        if (zoekLid(l.getRijksregisternummer()) != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_AL.getMessage());
        }

        //lid toevoegen
        return lidDAO.toevoegenLid(l);

    }

    /**
     * Wijzigt de velden naam, voornaam, emailadres, opmerking van het lid
     * @param teWijzigenLid te wijzigen lid
     * @throws ApplicationException Wordt gegooid wanneer er geen
     *                              lid werd opgegeven, niet alle velden van het lid (correct) zijn
     *                              ingevuld, wanneer het lid al uitgeschreven is, wanneer er al een lid
     *                              bestaat met dit rijksregisternummer, of wanneer het lid niet bestaat.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenLid(Lid teWijzigenLid) throws ApplicationException, DBException {
        //parameter moet ingevuld zijn
        if (teWijzigenLid == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_NULL.getMessage());
        }

        //alle gegevens moeten ingevuld zijn
        checkAlleVeldenIngevuld(teWijzigenLid);

        //check of lid in db zit
        Lid lid = zoekLid(teWijzigenLid.getRijksregisternummer());
        if (lid == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }

        //check dat lid niet uitgeschreven is
        if (lid.getEinde_lidmaatschap() != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_UITGESCHREVEN.getMessage());
        }

        //lid wijzigen
        lidDAO.wijzigenLid(teWijzigenLid);

    }

    /**
     * Wijzigt de startdatum van een lid. De startdatum kan niet jonger zijn dan de eerste rit van het lid.
     * @param rr rijksregisternummer van het lid
     * @param startDatum startdatum naar waar het gewijzigd moet worden
     * @throws ApplicationException wordt gegooid wanneer geen rijksregisternummer of startdatum werd opgegeven.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigStartDatumVanLid(String rr, LocalDate startDatum) throws ApplicationException, DBException {
        //parameters moeten ingevuld zijn
        if (rr == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_RR_LEEG.getMessage());
        }

        if (startDatum == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_STARTDATUM_LEEG.getMessage());
        }

        //check of lid in db zit
        Lid lid = zoekLid(rr);
        if (lid == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }

        //check dat lid niet uitgeschreven is
        if (lid.getEinde_lidmaatschap() != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_UITGESCHREVEN.getMessage());
        }

        //check dat de te wijzigen startdatum niet jonger is dan de eerste rit van het lid
        Integer eersteRitID = ritService.zoekEersteRitVanLid(rr);
        Rit eersteRit = ritService.zoekRit(eersteRitID);
        //omzetten datatypes + datums vergelijken
        if (startDatum.isAfter(LocalDate.parse(eersteRit.getStarttijd().toString()))) {
            throw new ApplicationException(ApplicationExceptionType.LID_STARTDATUM_TE_RECENT.getMessage());
        }

        //lid wijzigen
        lidDAO.wijzigenLid(zoekLid(rr));

    }

    /**
     * Schrijft het lid met meegegeven rijksregisternummer uit (wordt niet verwijderd).
     * @param rr rijksregisternummer
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public void uitschrijvenLid(String rr) throws ApplicationException, DBException {
        //check of lid in db zit
        Lid lid = zoekLid(rr);
        if (lid == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }

        //check dat lid niet al uitgeschreven is
        if (lid.getEinde_lidmaatschap() != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_UITGESCHREVEN.getMessage());
        }

        //check dat lid niet nog actieve ritten heeft
        if (ritService.zoekActieveRittenVanLid(rr) != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_HEEFT_ACTIEVE_RITTEN.getMessage());
        }

        //rijksregisternr - string omzetten in Rijksregisternummer-object
        Rijksregisternummer rijks = new Rijksregisternummer(rr);
        lidDAO.uitschrijvenLid(rijks);
    }

    /**
     * Zoekt een lid adhv het rijksregisternummer. Indien geen lid werd gevonden,
     * wordt null teruggegeven.
     * @param rijksregisternummer rijksregisternummer van het lid (kan null zijn)
     * @return lid dat gezocht wordt, null indien geen lid werd gevonden
     * @throws ApplicationException wordt opgegooid indien geen rijksregisternummer werd opgegeven
     * @throws DBException duidt op een verkeerde installatie van de be.vives.DAO of een fout in de query
     */
    public Lid zoekLid(String rijksregisternummer) throws ApplicationException, DBException {
        //parameter moet ingevuld zijn
        if (rijksregisternummer == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_ID.getMessage());
        }

        //rijksregisternr - string omzetten in Rijksregisternummer-object
        Rijksregisternummer rijks = new Rijksregisternummer(rijksregisternummer);
        return lidDAO.zoekLid(rijks);
    }

    /**
     * Zoekt alle leden en geeft ze terug in een lijst, gesorteerd op naam, voornaam.
     * @return lijst van leden
     * @throws DBException Exception die duidt op een verkeerde installatie van de
     * be.vives.DAO of een fout in de query
     * @throws ApplicationException
     */
    public List<Lid> zoekAlleLeden() throws ApplicationException, DBException {
        return lidDAO.zoekAlleLeden();
    }


    /**
     * Controleert of alle velden ingevuld zijn (id niet)
     * <p>
     * Gooit een be.vives.exception bij: - naam niet ingevuld - voornaam niet ingevuld -
     * adres niet ingevuld - postcode niet ingevuld - gemeente niet ingevuld -
     * status niet ingevuld of status = UITGESCHREVEN
     */
    private void checkAlleVeldenIngevuld(Lid l) throws ApplicationException {

        if (StringUtils.isBlank(l.getNaam())) {
            throw new ApplicationException(ApplicationExceptionType.LID_NAAM_LEEG.getMessage());
        }
        if (StringUtils.isBlank(l.getVoornaam())) {
            throw new ApplicationException(ApplicationExceptionType.LID_VOORNAAM_LEEG.getMessage());
        }
        if (StringUtils.isBlank(l.getEmailadres())) {
            throw new ApplicationException(ApplicationExceptionType.LID_EMAIL_LEEG.getMessage());
        }
        if (StringUtils.isBlank(l.getRijksregisternummer())) {
            throw new ApplicationException(ApplicationExceptionType.LID_RR_LEEG.getMessage());
        }

    }
}