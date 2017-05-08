package programminglife.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import programminglife.ProgrammingLife;
import programminglife.model.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
     * Initializes the open button so that the user can decide which file to open
     */
    private void initApp() {
        btnOpen.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
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
    }


}
