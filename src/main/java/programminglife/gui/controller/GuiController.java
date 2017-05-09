package programminglife.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import programminglife.ProgrammingLife;

import java.io.File;

/**
 * Created by Yannick on 02/05/2017.
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
        btnOpen.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            final ExtensionFilter extFilterGFA = new FileChooser.ExtensionFilter("JPG files (*.gfa)", "*.GFA");
            fileChooser.getExtensionFilters().addAll(extFilterGFA);

            try {
                File file = fileChooser.showOpenDialog(ProgrammingLife.getStage());
                if (file != null) {

                    System.out.println("WERKT DIT?");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }
}
