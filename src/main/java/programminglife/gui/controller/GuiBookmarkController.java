package programminglife.gui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Class for the GuiBookmarkController. This class handles the FXML file that comes with it.
 */
public class GuiBookmarkController {

    @FXML private Button btnOk;
    @FXML private Button btnCancel;

    /**
     * Initialize method for BookmarkController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        initButtons();
    }

    /**
     * Initializes the buttons in the window.
     */
    private void initButtons() {
        btnOk.setOnAction(event -> {
            Stage s = (Stage) btnOk.getScene().getWindow();
            s.close();
        });
        btnCancel.setOnAction(event -> {
            Stage s = (Stage) btnOk.getScene().getWindow();
            s.close();
        });
    }

    /**
     * Getter for the Ok Button.
     * @return Button.
     */
    public Button getBtnOk() {
        return btnOk;
    }

    /**
     * Getter for the Cancel Button.
     * @return Button.
     */
    public Button getBtnCancel() {
        return btnCancel;
    }
}
