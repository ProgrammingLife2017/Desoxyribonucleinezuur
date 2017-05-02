package programminglife.gui;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

/**
 * Created by Yannick on 02/05/2017.
 */
public class GuiController {

    @FXML
    private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;

    private void initialize() {
        initApp();
    }

    private void initApp() {
        btnOpen.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter extFilterGFA = new FileChooser.ExtensionFilter("JPG files (*.gfa)", "*.GFA");
            fileChooser.getExtensionFilters().addAll(extFilterGFA);

            /*try {
                File file = fileChooser.
                if (file != null) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        });
    }
}
