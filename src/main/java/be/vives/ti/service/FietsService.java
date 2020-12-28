package be.vives.ti.service;

import be.vives.ti.dao.FietsDAO;
import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;

import java.util.List;

public class FietsService {

    private FietsDAO fietsDAO;
    private RitService ritService;

    public FietsService(FietsDAO fietsDAO, RitService ritService) {
        this.fietsDAO = fietsDAO;
        this.ritService = ritService;
    }


    /**
     * Voegt een fiets toe adhv een standplaats
     * @param fiets
     * @return
     * @throws ApplicationException wordt gegooid wanneer geen fiets werd gegeven of
     * de fiets bevat al een registratienummer.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public Integer toevoegenFiets(Fiets fiets) throws ApplicationException, DBException {
        //check dat parameter is ingevuld
        if (fiets == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }

        //check dat het registratienummer van de fiets nog niet bestaat
        if (fiets.getRegistratienummer() != null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_ID_WORDT_GEGENEREERD.getMessage());
        }

        //toevoegen fiets
        return fietsDAO.toevoegenFiets(fiets);
    }


    /**
     * Wijzigt de status van de fiets naar Herstel.
     * @param regnr registratienummer van de fiets
     * @param opmerking opmerking kan gegeven worden
     * @throws ApplicationException wordt gegooid indien het registratienummer niet gevonden werd
     * of de fiets heeft status HERSTEL of UIT OMLOOP.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenStatusNaarHerstel(int regnr, String opmerking) throws ApplicationException, DBException {
        //check dat fiets bestaat
        Fiets fiets = zoekFiets(regnr);
        if (fiets == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }

        //check dat fiets status ACTIEF heeft
        if (fiets.getStatus() != Status.actief) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NIET_CORRECTE_STATUS.getMessage());
        }

        //fietsstatus wijzigen naar HERSTEL
        fietsDAO.wijzigenToestandFiets(regnr, Status.herstel, opmerking);

    }

    /**
     * Wijzigt de status van de fiets naar UIT OMLOOP.
     * @param regnr registratienummer van de fiets
     * @param opmerking opmerking kan gegeven worden
     * @throws ApplicationException wordt gegooid indien het registratienummer niet gevonden werd
     * of de fiets heeft status UIT OMLOOP al.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenStatusNaarUitOmloop(int regnr, String opmerking) throws ApplicationException, DBException {
        //check dat fiets bestaat
        Fiets fiets = zoekFiets(regnr);
        if (fiets == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }

        //check dat fiets niet status UIT OMLOOP heeft
        if (fiets.getStatus() == Status.uit_omloop) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NIET_CORRECTE_STATUS.getMessage());
        }

        //fietsstatus wijzigen naar UIT OMLOOP
        fietsDAO.wijzigenToestandFiets(regnr, Status.uit_omloop, opmerking);

    }

    /**
     * Wijzigt de status van de fiets naar ACTIEF.
     * @param regnr registratienummer van de fiets
     * @param opmerking opmerking kan gegeven worden
     * @throws ApplicationException wordt gegooid indien het registratienummer niet gevonden werd
     * of de fiets heeft status ACTIEF of UIT OMLOOP.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenStatusNaarActief(int regnr, String opmerking) throws ApplicationException, DBException {
        //check dat fiets bestaat
        Fiets fiets = zoekFiets(regnr);
        if (fiets == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }

        //check dat fiets status HERSTEL heeft
        if (fiets.getStatus() != Status.herstel) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NIET_CORRECTE_STATUS.getMessage());
        }

        //fietsstatus wijzigen naar ACTIEF
        fietsDAO.wijzigenToestandFiets(regnr, Status.actief, opmerking);
    }

    /**
     * Wijzigt de opmerking van de fiets.
     * @param regnr registratienummer van de fiets
     * @param opmerking opmerking kan gegeven worden
     * @throws ApplicationException wordt gegooid indien het registratienummer niet gevonden werd.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws ApplicationException, DBException {
        //check dat fiets bestaat
        Fiets fiets = zoekFiets(regnr);
        if (fiets == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }

        //opmerking fiets wijzigen
        fietsDAO.wijzigenOpmerkingFiets(regnr, opmerking);

    }

    /**
     * Zoekt een fiets in de db adhv het registratienummer van de fiets.
     * @param registratienummer
     * @return de gezochte fiets
     * @throws ApplicationException wordt gegooid indien het registratienummer niet is ingevuld
     * of de fiets werd niet gevonden
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public Fiets zoekFiets(Integer registratienummer) throws ApplicationException, DBException {
        //check dat parameter is ingevuld
        if (registratienummer == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }
        return fietsDAO.zoekFiets(registratienummer);
    }

    /**
     * Zoekt alle beschikbare fietsen (met status ACTIEF + heeft geen actieve rit).
     * @return lijst met beschikbare fietsen
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de be.vives.DAO of een fout in de query.
     */
    public List<Fiets> zoekAlleBeschikbareFietsen() throws DBException, ApplicationException {
        return fietsDAO.zoekAlleBeschikbareFietsen();
    }

}
