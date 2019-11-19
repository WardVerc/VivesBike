package be.vives.ti.dao;

import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;

import java.util.List;

public class FietsDAO {

    public Integer toevoegenFiets(Fiets fiets) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void wijzigenToestandFiets(int regnr, Status status, String opmerking) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Fiets zoekFiets(int regnr) throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public List<Fiets> zoekAlleActieveFietsen() throws DBException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

}
