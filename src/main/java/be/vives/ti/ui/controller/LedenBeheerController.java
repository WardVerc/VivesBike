package be.vives.ti.ui.controller;

import be.vives.ti.databag.Lid;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.LidService;
import be.vives.ti.service.RitService;
import be.vives.ti.ui.VIVESbike;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;

import java.util.ArrayList;

public class LedenBeheerController {

    private VIVESbike parent;
    private LidService lidService;
    private RitService ritService;

    //Lid dat geselecteerd is/moet zijn.
    private Lid geselecteerdLid;

    public LedenBeheerController(RitService ritService, LidService lidService) {
        this.ritService = ritService;
        this.lidService = lidService;
    }

    @FXML
    private TableView<Lid> tvLeden;
    @FXML
    private TableColumn tcVoornaam;
    @FXML
    private TableColumn tcNaam;
    @FXML
    private TableColumn tcRijksreg;
    @FXML
    private Label laErrorLeden;

    public void initialize() {
        //kolommen van de tabel initialiseren (koppelen met velden uit bag)
        tcVoornaam.setCellValueFactory(
                new PropertyValueFactory<>("voornaam")
        );

        tcNaam.setCellValueFactory(
                new PropertyValueFactory<>("naam")
        );

        tcRijksreg.setCellValueFactory(
                new PropertyValueFactory<>("rijksregisternummer")
        );

    }

    public void setData(Lid lid) {
        if ( lid != null) {
            geselecteerdLid = lid;
        } else {
            geselecteerdLid = null;
        }

        //tabel vullen met alle leden
        initialiseerTabel();
    }

    /**
     * De foutboodschap op het scherm verwijderen
     */
    private void resetErrorMessage() {
        laErrorLeden.setText("");
    }

    /**
     * Tabel opvullen met alle leden.
     */
    private void initialiseerTabel() {
        resetErrorMessage();
        try {
            //alle leden ophalen
            ArrayList<Lid> ledenlijst = lidService.zoekAlleLeden();

            ObservableList<Lid> leden = FXCollections.observableArrayList(ledenlijst);
            tvLeden.setItems(leden);
        } catch (DBException | ApplicationException ae) {
            laErrorLeden.setText("Onherstelbare fout: " + ae.getMessage());
        }
    }

    public void wijzigenLid() {
    }

    public void wijzigenStartdatum() {
    }

    public void uitschrijvenLid() {
    }

    public void annuleren() {
    }

    public void opslaan() {
    }

    /**
     * Referentie naar VIVESBike instellen.
     * @param p referentie naar runnable class die alle oproepen naar alle schermen bestuurt.
     */
    public void setParent(VIVESbike p) {
        this.parent = p;
    }
}
