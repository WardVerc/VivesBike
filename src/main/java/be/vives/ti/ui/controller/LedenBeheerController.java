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

import java.time.LocalDate;
import java.util.ArrayList;

public class LedenBeheerController {

    private VIVESbike parent;
    private LidService lidService;
    private RitService ritService;

    //lid dat geselecteerd is/moet zijn.
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
    @FXML
    private TextField tfVoornaam;
    @FXML
    private TextField tfNaam;
    @FXML
    private TextField tfRijksregisternummer;
    @FXML
    private TextField tfEmail;
    @FXML
    private DatePicker dpStartdatum;
    @FXML
    private CheckBox cbUitgeschreven;
    @FXML
    private TextArea taOpmerking;

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

    //deze methode zou moeten aangeroepen worden als er een lid uit de tabel geselecteerd wordt
    @FXML
    private void selecteerLid(ActionEvent event) throws DBException, ApplicationException {
        resetErrorMessage();
        //geselecteerd lid ophalen en tonen
        Lid l = tvLeden.getSelectionModel().getSelectedItem();
        System.out.println(l);
        if (l != null) {
            geselecteerdLid = l;
        } else {
            geselecteerdLid = null;
        }
        selecteer(geselecteerdLid);
    }

    /**
     * Toon gegevens van het geselecteerde lid in de velden.
     * @param l het geselecteerde lid.
     */
    private void selecteer(Lid l) throws DBException, ApplicationException {
        if (l != null) {
            //gegevens van het geselecteerde lid ophalen en tonen
            tfRijksregisternummer.setText(l.getRijksregisternummer());
            tfVoornaam.setText(l.getVoornaam());
            tfNaam.setText(l.getNaam());
            tfEmail.setText(l.getEmailadres());
            dpStartdatum.setValue(l.getStart_lidmaatschap());
            taOpmerking.setText(l.getOpmerking());

            if (l.getEinde_lidmaatschap() != null) {
                cbUitgeschreven.setSelected(true);
            } else {
                cbUitgeschreven.setSelected(false);
            }
        } else {
            //geen lid = gegevens zijn leeg
            tfRijksregisternummer.setText("");
            tfVoornaam.setText("");
            tfNaam.setText("");
            tfEmail.setText("");
            dpStartdatum.setValue(LocalDate.of(1,1,1));
            taOpmerking.setText("");
            cbUitgeschreven.setSelected(true);
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
