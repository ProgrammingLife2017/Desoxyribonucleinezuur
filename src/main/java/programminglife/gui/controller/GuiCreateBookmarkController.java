package programminglife.gui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import programminglife.controller.BookmarkController;
import programminglife.utility.Alerts;

/**
 * Class for the GuiCreateBookmarkController. This class handles the FXML file that comes with it.
 */
public class GuiCreateBookmarkController {

    private GuiController guiController;

    @FXML private Button btnOk;
    @FXML private Button btnCancel;
    @FXML private TextField txtBookmarkName;
    @FXML private TextField txtId;
    @FXML private TextField txtRadius;
    @FXML private TextArea txtDescription;

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
            int size = guiController.getGraphController().getGraph().size();
            String name = guiController.getGraphController().getGraph().getID();
            int inputRadius = 0;
            int inputCenter = 0;
            try {
                inputRadius = Integer.parseInt(txtId.getText());
                inputCenter = Integer.parseInt(txtRadius.getText());
            } catch (NumberFormatException e) {
                Alerts.warning("Input is bigger than the maximum int");
            }
            if (inputCenter <= 0) {
                Alerts.warning("Center node can only contain positive integers").show();
            } else if (inputCenter > size) {
                Alerts.warning("Center node is larger than graph size: " + size).show();
            } else if (inputRadius < 0) {
                Alerts.warning("Radius can only contain positive integers").show();
            } else if (!BookmarkController.storeBookmark(name, guiController.getFile().getAbsolutePath(),
                    txtBookmarkName.getText(), txtDescription.getText(),
                    inputCenter, inputRadius)) {
                Alerts.warning("Bookmarks must have unique names in files").show();
            } else {
                Stage s = (Stage) btnOk.getScene().getWindow();
                s.close();
            }
        });
        btnCancel.setOnAction(event -> {
            Stage s = (Stage) btnCancel.getScene().getWindow();
            s.close();
        });
    }

    void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Set the text in the center node and radius areas.
     * @param center The text to fill the center area with
     * @param radius The text to fill the radius area with
     */
    void setText(String center, String radius) {
        txtId.setText(center);
        txtRadius.setText(radius);
    }
}
