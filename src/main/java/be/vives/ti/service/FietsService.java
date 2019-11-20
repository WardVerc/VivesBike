package be.vives.ti.service;

import be.vives.ti.databag.Fiets;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.util.List;

public class FietsService {


    public Integer toevoegenFiets(Fiets fiets) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenStatusNaarHerstel(int regnr, String opmerking) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenStatusNaarUitOmloop(int regnr, String opmerking) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();

    }

    public void wijzigenStatusNaarActief(int regnr, String opmerking) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();

    }

    public Fiets zoekFiets(Integer registratienummer) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public List<Fiets> zoekAlleBeschikbareFietsen() throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

}
