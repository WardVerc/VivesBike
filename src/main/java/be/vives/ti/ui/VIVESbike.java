package be.vives.ti.ui;

import be.vives.ti.ui.controller.LedenBeheerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VIVESbike extends Application {

    private final Stage stage = new Stage();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        laadLedenbeheer();
        stage.show();
    }


    public void laadLedenbeheer() {
        try {
            String fxmlFile = "/fxml/LedenBeheer.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // controller instellen
            LedenBeheerController controller = new LedenBeheerController();
            loader.setController(controller);

            Parent root = loader.load();
            controller.setParent(this);
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
