package programminglife.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import programminglife.ProgrammingLife;
import programminglife.model.Graph;
import programminglife.model.exception.UnknownTypeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * The controller for the GUI that is used in the application.
 */
public class GuiController {

    @FXML private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;

    @FXML @SuppressWarnings("Unused")
    private void initialize() {
        initApp();
    }

    /**
     * Initializes the open button so that the user can decide which file to open.
     */
    private void initApp() {
        /**
         * Sets the action for the open MenuItem.
         */
        btnOpen.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            final ExtensionFilter extFilterGFA = new ExtensionFilter("GFA files (*.gfa)", "*.GFA");
            fileChooser.getExtensionFilters().addAll(extFilterGFA);

            try {
                File file = fileChooser.showOpenDialog(ProgrammingLife.getStage());
                if (file != null) {
                    Graph.parse(file.toString(), true);
                    //Graph.parse(file.toString(), true);
                    System.out.println("WERKT DIT?");
                }
            } catch (FileNotFoundException | UnknownTypeException e) {
                e.printStackTrace();
            }
        });
        /**
        * Sets the event for the quit MenuItem.
        */
        btnQuit.setOnAction(event -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Confirm Exit");
            a.setHeaderText("Do you really want to exit?");
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                Platform.exit();
                System.exit(0);
            }

            if (result.get() == ButtonType.CANCEL) {
                a.close();
            }
        });
    }


}
