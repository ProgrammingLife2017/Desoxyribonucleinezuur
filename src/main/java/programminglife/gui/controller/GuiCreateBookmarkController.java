package programminglife.gui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import programminglife.controller.BookmarkController;

/**
 * Class for the GuiCreateBookmarkController. This class handles the FXML file that comes with it.
 */
public class GuiCreateBookmarkController {

    private String graphName;

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
            BookmarkController.storeBookmark(graphName, txtBookmarkName.getText(), txtDescription.getText(),
                    Integer.parseInt(txtId.getText()), Integer.parseInt(txtRadius.getText()));
            Stage s = (Stage) btnOk.getScene().getWindow();
            s.close();
        });
        btnCancel.setOnAction(event -> {
            Stage s = (Stage) btnCancel.getScene().getWindow();
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

    public void setGraph(String graphName) {
        this.graphName = graphName;
    }
}
