package be.vives.ti.service;

import be.vives.ti.databag.Lid;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.time.LocalDate;
import java.util.ArrayList;


public class LidService {


    public void toevoegenLid(Lid l) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigenLid(Lid teWijzigenLid) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void wijzigStartDatumVanLid(String rr, LocalDate startDatum) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void uitschrijvenLid(String rr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public Lid zoekLid(String rijksregisternummer) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public ArrayList<Lid> zoekAlleLeden() throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }
}