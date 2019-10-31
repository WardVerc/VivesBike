package be.vives.ti.service;

import be.vives.ti.databag.Fiets;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

public class FietsService {


    public Integer toevoegenFiets(Fiets fiets) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenActiefNaarHerstel(int regnr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenActiefNaarUitOmloop(int regnr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();

    }

    public void wijzigenHerstelNaarActief(int regnr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenHerstelNaarUitOmloop(int regnr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();

    }

    public Fiets zoekFiets(Integer registratienummer) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

}
