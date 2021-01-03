package be.vives.ti.ui.controller;

import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.FietsService;
import be.vives.ti.service.LidService;
import be.vives.ti.service.RitService;
import be.vives.ti.ui.VIVESbike;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;

import java.time.LocalDate;
import java.util.ArrayList;

public class LedenBeheerController {

    private VIVESbike parent;
    private LidService lidService;
    private RitService ritService;
    private FietsService fietsService;

    //lid dat geselecteerd is/moet zijn.
    private Lid geselecteerdLid;

    public LedenBeheerController(RitService ritService, LidService lidService, FietsService fietsService) {
        this.ritService = ritService;
        this.lidService = lidService;
        this.fietsService = fietsService;
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
    @FXML
    private Button btnAnnuleren;
    @FXML
    private Button btnOpslaan;
    @FXML
    private Button btnToevoegenLid;
    @FXML
    private Button btnWijzigenLid;
    @FXML
    private Button btnStartdatum;
    @FXML
    private Button btnUitschrijvenLid;


    public void initialize() {
        //kolommen van de tabel initialiseren (koppelen met velden uit bag)
        tcVoornaam.setCellValueFactory(
                new PropertyValueFactory<>("voornaam"));

        tcNaam.setCellValueFactory(
                new PropertyValueFactory<>("naam"));

        tcRijksreg.setCellValueFactory(
                new PropertyValueFactory<>("rijksregisternummer"));

        //zorgt er voor dat selecteer() opgeroepen wordt elke keer een lid
        //geselecteerd wordt in de tabel
        tvLeden.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    selecteerLid();
                } catch (DBException e) {
                    e.printStackTrace();
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Zorgt ervoor dat het geselecteerde lid ingesteld wordt en de tabel gevuld wordt
     * met alle leden.
     * @param lid lid dat geselecteerd is, null indien geen lid geselecteerd uit de tabel.
     */
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

    /**
     * Deze methode wordt aangeroepen als er een lid uit de tabel geselecteerd wordt.
     * Ze zorgt ervoor dat het juiste lid gebruikt wordt om de velden later in te vullen.
     */

    @FXML
    private void selecteerLid() throws DBException, ApplicationException {
        resetErrorMessage();
        //geselecteerd lid ophalen en tonen
        Lid l = tvLeden.getSelectionModel().getSelectedItem();
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
        //zorg dat niks editable is

        tfRijksregisternummer.setEditable(false);
        tfVoornaam.setEditable(false);
        tfNaam.setEditable(false);
        tfEmail.setEditable(false);
        dpStartdatum.setEditable(false);
        taOpmerking.setEditable(false);

        //zorg dat alles disabled is (ziet er duidelijker uit dat de velden nog niet
        //editable zijn)
        tfRijksregisternummer.setDisable(true);
        tfVoornaam.setDisable(true);
        tfNaam.setDisable(true);
        tfEmail.setDisable(true);
        dpStartdatum.setDisable(true);
        taOpmerking.setDisable(true);
        cbUitgeschreven.setDisable(true);
        btnAnnuleren.setDisable(true);
        btnOpslaan.setDisable(true);

        //de knoppen toevoegen etc. moeten enabled zijn
        btnToevoegenLid.setDisable(false);
        btnWijzigenLid.setDisable(false);
        btnStartdatum.setDisable(false);
        btnUitschrijvenLid.setDisable(false);

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
            //geen lid geselecteerd => velden leegmaken
            tfRijksregisternummer.setText("");
            tfVoornaam.setText("");
            tfNaam.setText("");
            tfEmail.setText("");
            dpStartdatum.setValue(LocalDate.of(1,1,1));
            taOpmerking.setText("");
            cbUitgeschreven.setSelected(true);
        }
    }

    /**
     * Deze methode wordt aangeroepen indien de gebruiker op de knop Wijzigen drukt.
     * Hierdoor kunnen wijzigingen van het geselecteerde lid aangebracht worden
     * in de velden.
     */
    public void wijzigenLid() {
        //zorg dat de tabel gedisabled wordt
        tvLeden.setDisable(true);
        //vervolgens dat de velden editeerbaar zijn
        tfVoornaam.setEditable(true);
        tfNaam.setEditable(true);
        tfEmail.setEditable(true);
        taOpmerking.setEditable(true);

        tfVoornaam.setDisable(false);
        tfNaam.setDisable(false);
        tfEmail.setDisable(false);
        taOpmerking.setDisable(false);

        btnAnnuleren.setDisable(false);
        btnOpslaan.setDisable(false);

        //de knoppen toevoegen etc. moeten disabled zijn
        btnToevoegenLid.setDisable(true);
        btnWijzigenLid.setDisable(true);
        btnStartdatum.setDisable(true);
        btnUitschrijvenLid.setDisable(true);

        //startdatum en uitschrijven wordt via een andere methoden gedaan
        dpStartdatum.setEditable(false);
        dpStartdatum.setDisable(true);
        cbUitgeschreven.setDisable(true);
    }

    /**
     * Deze methode wordt aangeroepen indien de gebruiker op de knop Wijzigen Startdatum
     * drukt.
     * Hierdoor kunnen wijzigingen van de startdatum
     * van het geselecteerde lid aangebracht worden in het respectievelijke veld.
     */
    public void wijzigenStartdatum() {
        //zorg dat de tabel gedisabled wordt
        tvLeden.setDisable(true);
        //zorg dat enkel startdatum editeerbaar is
        dpStartdatum.setDisable(false);
        dpStartdatum.setEditable(true);

        btnAnnuleren.setDisable(false);
        btnOpslaan.setDisable(false);

        //de knoppen toevoegen etc. moeten disabled zijn
        btnToevoegenLid.setDisable(true);
        btnWijzigenLid.setDisable(true);
        btnStartdatum.setDisable(true);
        btnUitschrijvenLid.setDisable(true);
   }

    /**
     * Schrijft het lid uit indien het lid geen actieve ritten heeft.
     * De checkbox Uitsgeschreven wordt aangevinkt en wijzigingen aan dit lid kunnen niet worden
     * opgeslaan.
     */
    public void uitschrijvenLid() {
        try {
            //check dat lid niet nog actieve ritten heeft
            if (ritService.zoekActieveRitVanLid(geselecteerdLid.getRijksregisternummer()) != null) {
                throw new ApplicationException(ApplicationExceptionType.LID_HEEFT_ACTIEVE_RITTEN.getMessage());
            }

            cbUitgeschreven.setSelected(true);
            lidService.uitschrijvenLid(geselecteerdLid.getRijksregisternummer());
            //tabel updaten
            initialiseerTabel();

        } catch (ApplicationException | DBException e) {
            laErrorLeden.setText(e.getMessage());
        }

    }

    /**
     * Zorgt ervoor dat de wijzigingen niet worden opgeslaan en de gebruiker terug een lid
     * kan selecteren.
     */
    public void annuleren() {
        try {
            selecteer(geselecteerdLid);
            tvLeden.setDisable(false);
        } catch (ApplicationException | DBException e) {
            laErrorLeden.setText(e.getMessage());
        }

    }

    /**
     * Slaat de aangebrachte wijzigingen op van het geselecteerde lid.
     * De gebruiker kan hierna opnieuw een lid selecteren.
     */
    public void opslaan() {

        try {
            //wijzigingen uit de velden aanbrengen aan het lid
            geselecteerdLid.setVoornaam(tfVoornaam.getText());
            geselecteerdLid.setNaam(tfNaam.getText());
            geselecteerdLid.setEmailadres(tfEmail.getText());
            geselecteerdLid.setOpmerking(taOpmerking.getText());

            lidService.wijzigenLid(geselecteerdLid);

            //check dat de te wijzigen startdatum niet jonger is dan de eerste rit van het lid
            Integer eersteRitID = ritService.zoekEersteRitVanLid(geselecteerdLid.getRijksregisternummer());
            if (eersteRitID != null) {
                Rit eersteRit = ritService.zoekRit(eersteRitID);
                //omzetten datatypes + datums vergelijken
                if (dpStartdatum.getValue().isAfter(LocalDate.parse(eersteRit.getStarttijd().toString()))) {
                    throw new ApplicationException(ApplicationExceptionType.LID_STARTDATUM_TE_RECENT.getMessage());
                }
            }

            lidService.wijzigStartDatumVanLid(geselecteerdLid.getRijksregisternummer(), dpStartdatum.getValue());

            //alle velden terug disabled maken
            selecteer(geselecteerdLid);

            //tabel updaten en terug enabled maken
            initialiseerTabel();
            tvLeden.setDisable(false);
        } catch (ApplicationException | DBException e) {
            //toon aan gebruiker welke fout er is gebeurd
            laErrorLeden.setText(e.getMessage());
        }
    }

    /**
     * Referentie naar VIVESBike instellen.
     * @param p referentie naar runnable class die alle oproepen naar alle schermen bestuurt.
     */
    public void setParent(VIVESbike p) {
        this.parent = p;
    }
}
