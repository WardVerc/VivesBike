package be.vives.ti.ui;

import be.vives.ti.dao.FietsDAO;
import be.vives.ti.dao.LidDAO;
import be.vives.ti.dao.RitDAO;
import be.vives.ti.databag.Lid;
import be.vives.ti.service.FietsService;
import be.vives.ti.service.LidService;
import be.vives.ti.service.RitService;
import be.vives.ti.ui.controller.LedenBeheerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VIVESbike extends Application {

    private final Stage stage = new Stage();
    private LidService lidService;
    private LidDAO lidDAO;
    private RitService ritService;
    private RitDAO ritDAO;
    private FietsService fietsService;
    private FietsDAO fietsDAO;

    private LidService createLidService() {
        if (lidService == null) {
            this.lidService = new LidService(createLidDAO());
        }
        return lidService;
    }

    private LidDAO createLidDAO() {
        if (lidDAO == null) {
            this.lidDAO = new LidDAO();
        }
        return lidDAO;
    }

    private RitService createRitService() {
        if (ritService == null) {
            this.ritService = new RitService(createRitDAO());
        }
        return ritService;
    }

    private RitDAO createRitDAO() {
        if (ritDAO == null) {
            this.ritDAO = new RitDAO();
        }
        return ritDAO;
    }

    private FietsService createFietsService() {
        if (fietsService == null) {
            this.fietsService = new FietsService(createFietsDAO());
        }
        return fietsService;
    }

    private FietsDAO createFietsDAO() {
        if (fietsDAO == null) {
            this.fietsDAO = new FietsDAO();
        }
        return fietsDAO;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        laadLedenbeheer(null);
        stage.show();
    }


    public void laadLedenbeheer(Lid lid) {
        try {
            String fxmlFile = "/fxml/LedenBeheer.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // controller instellen
            LedenBeheerController controller = new LedenBeheerController(createRitService(), createLidService(), createFietsService());
            loader.setController(controller);

            Parent root = loader.load();
            controller.setParent(this);
            controller.setData(lid);

            Scene scene = new Scene(root);
            stage.setTitle("Leden beheren");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("SYSTEEMFOUT bij laden ledenbeheer: " + e.getMessage());
        }
    }

    public Stage getPrimaryStage() {
        return stage;
    }
}
