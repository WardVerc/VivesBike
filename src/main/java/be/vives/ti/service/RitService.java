package be.vives.ti.service;

import be.vives.ti.dao.LidDAO;
import be.vives.ti.dao.RitDAO;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class RitService {

    private RitDAO ritDAO;
    private LidService lidService;
    private FietsService fietsService;

    public RitService(RitDAO ritDAO, LidService lidService, FietsService fietsService) {
        this.ritDAO = ritDAO;
        this.lidService = lidService;
        this.fietsService = fietsService;
    }

    /**
     * Voegt een rit toe.
     * @param rit rit dat toegevoegd moet worden
     * @return id van de rit dat werd toegevoegd
     * @throws ApplicationException wordt gegooid wanneer geen rit werd opgegeven,
     * rit al bestaat, lid niet bestaat, lid niet ingeschreven is,
     * lid al bezig is met een rit, fiets niet bestaat, fiets niet beschikbaar is,
     * of als de rit al afgesloten is.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public Integer toevoegenRit(Rit rit) throws ApplicationException, DBException {
        //check dat parameter is ingevuld
        if (rit == null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_NULL.getMessage());
        }

        //check dat het id van de rit nog niet bestaat
        if (rit.getId() != null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_ID_WORDT_GEGENEREERD.getMessage());
        }

        //check dat lid bestaat
        if (lidService.zoekLid(rit.getLidRijksregisternummer()) == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }

        //check dat lid niet uitgeschreven is
        if (lidService.zoekLid(rit.getLidRijksregisternummer()).getEinde_lidmaatschap() != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_UITGESCHREVEN.getMessage());
        }

        //check dat lid geen actieve ritten heeft
        if (zoekActieveRitVanLid(rit.getLidRijksregisternummer()) != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_HEEFT_ACTIEVE_RITTEN.getMessage());
        }

        //check dat fiets bestaat
        if (fietsService.zoekFiets(rit.getFietsRegistratienummer()) == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }

        //check dat fiets actief is
        if (fietsService.zoekFiets(rit.getFietsRegistratienummer()).getStatus() != Status.actief) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NIET_CORRECTE_STATUS.getMessage());
        }

        //check dat fiets beschikbaar is
        if (zoekActieveRitVanFiets(rit.getFietsRegistratienummer()) != null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_IN_GEBRUIK.getMessage());
        }

        //check dat rit nog niet afgesloten is
        if (rit.getEindtijd() != null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_AL_AFGESLOTEN.getMessage());
        }

        //zet de starttijd van de rit op nu
        rit.setStarttijd(LocalDateTime.now());

        //voeg de rit toe
        return ritDAO.toevoegenRit(rit);
    }

    /**
     * Sluit een rit af en de prijs van de rit wordt bepaald.
     * @param ritId id van de rit die wordt afgesloten
     * @throws ApplicationException wordt gegooid indien geen ritID opgegeven wordt,
     * de ritId niet gevonden wordt, rit nog niet gestart was, rit al beeindigd was,
     * of prijs al ingevuld was.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public void afsluitenRit(Integer ritId) throws ApplicationException, DBException {
        //check dat parameter is ingevuld
        if (ritId == null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_NULL.getMessage());
        }

        //check dat rit bestaat
        if (zoekRit(ritId) == null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_BESTAAT_NIET.getMessage());
        }

        //check dat rit gestart is
        if (zoekRit(ritId).getStarttijd() == null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_NIET_GESTART.getMessage());
        }

        //check dat rit nog niet geeindigd is
        if (zoekRit(ritId).getEindtijd() != null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_AL_AFGESLOTEN.getMessage());
        }

        //check dat prijs nog niet is ingevuld
        if (zoekRit(ritId).getPrijs() != null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_PRIJS_AL_BEPAALD.getMessage());
        }

        //eindtijd invullen
        zoekRit(ritId).setEindtijd(LocalDateTime.now());

        //prijs bepalen
        //trek start- en eind datum van elkaar, bepaal het aantal uur
        LocalDateTime startTijd = zoekRit(ritId).getStarttijd();
        LocalDateTime eindTijd = zoekRit(ritId).getEindtijd();

        Duration duration = Duration.between(startTijd, eindTijd);
        double uren = (double)duration.toHours();

        //resterende aantal seconden
        double seconden = (double)duration.getSeconds() - ((double)duration.toMinutes()*60);

        //resterende seconden omzetten naar uren en bij het aantal uur optellen
        uren = uren + (seconden/3600);

        //bij elke veelvoud van 24u moet er 1 euro betaald worden
        // (rond altijd af naar boven naar het volgende natuurlijk getal)
        //-> Math.ceil()
        //dus prijs is minimum 1€
        //dus 0-24u = 1€, 24-48u = 2€
        //indien de prijs zou veranderen, vermenigvuldig dit met de nieuwe prijs
        int prijsAfgerond = (int)Math.ceil(uren/24);

        BigDecimal prijs = BigDecimal.valueOf(prijsAfgerond);

        //prijs invullen
        zoekRit(ritId).setPrijs(prijs);

        //rit afsluiten
        ritDAO.afsluitenRit(zoekRit(ritId));
    }

    public Rit zoekRit(Integer ritID) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public Integer zoekEersteRitVanLid(String rr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public Integer zoekActieveRitVanLid(String rr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException();
    }

    public Integer zoekActieveRitVanFiets(Integer regnr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException();
    }
}
