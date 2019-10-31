package be.vives.ti.service;

import be.vives.ti.databag.Rit;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.util.ArrayList;

public class RitService {


    public Integer toevoegenRit(Rit rit) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public void afsluitenRit(Integer id) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public Rit zoekRit(Integer ritID) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public Integer zoekEersteRitVanLid(String rr) throws ApplicationException, DBException {
        throw new UnsupportedOperationException();
    }

    public ArrayList zoekActieveRittenVanLid(String rr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException();
    }

    public ArrayList zoekActieveRittenVanFiets(Integer regnr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException();
    }
}
