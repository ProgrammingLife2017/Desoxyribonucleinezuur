package programminglife.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import programminglife.ProgrammingLife;
import programminglife.model.Graph;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * The controller for the GUI that is used in the application
 */
public class GuiController {

    @FXML private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;

    @FXML @SuppressWarnings("Unused")
    private void initialize() {
        initApp();
    }

    /**
     * Initializes the open button so that the user can decide which file to open
     */
    private void initApp() {
        /**
         * Sets the action for the open MenuItem
         */
        btnOpen.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            final ExtensionFilter extFilterGFA = new ExtensionFilter("GFA files (*.gfa)", "*.GFA");
            fileChooser.getExtensionFilters().addAll(extFilterGFA);

            try {
                File file = fileChooser.showOpenDialog(ProgrammingLife.getStage());
                if (file != null) {
                    Graph.parse(file.toString(), true);
                    System.out.println("WERKT DIT?");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        /**
        * Sets the event for the quit MenuItem
        */
        btnQuit.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
    }


}
