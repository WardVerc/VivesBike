package be.vives.ti.dao;

import be.vives.ti.databag.Rit;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.util.List;

public class RitDAO {

    public Integer toevoegenRit(Rit rit) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void afsluitenRit(Rit rit) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Rit zoekRit(int ritID) throws DBException, ApplicationException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public int zoekEersteRitVanLid(String rr) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }


    public List<Rit> zoekActieveRittenVanLid(String rr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException("Not implemented yet!");

    }

    public List<Rit> zoekActieveRittenVanFiets(int regnr) throws DBException, ApplicationException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }


}
