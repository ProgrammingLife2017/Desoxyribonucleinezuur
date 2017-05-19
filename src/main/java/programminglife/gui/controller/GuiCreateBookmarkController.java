package programminglife.gui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private GraphController graphController;

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
            if (txtBookmarkName.getText().contains(" ")) {
                nameSpaceAlert();
            } else if (txtBookmarkName.getText().matches(".*\\d+.*")) {
                nameIntAlert();
            } else if (!txtId.getText().matches("^[1-9]\\d*$")) {
                intAlert("Center Node");
            } else if (Integer.parseInt(txtId.getText()) > graphController.getGraph().size()) {
                sizeAlert(graphController.getGraph().size());
            } else if (!txtRadius.getText().matches("^[1-9]\\d*$")) {
               intAlert("radius");
            } else {
                BookmarkController.storeBookmark(graphName, txtBookmarkName.getText(), txtDescription.getText(),
                        Integer.parseInt(txtId.getText()), Integer.parseInt(txtRadius.getText()));
                Stage s = (Stage) btnOk.getScene().getWindow();
                s.close();
            }
        });
        btnCancel.setOnAction(event -> {
            Stage s = (Stage) btnCancel.getScene().getWindow();
            s.close();
        });
    }

    /**
     * Alert for title if numbers are in name.
     */
    private void nameIntAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Name error");
        alert.setHeaderText(null);
        alert.setContentText("Title can not contains numbers");
        alert.show();
    }

    /**
     * Alert for when node id is too large for graph size.
     * @param size The size of the graph
     */
    private void sizeAlert(int size) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Center Node error");
        alert.setHeaderText(null);
        alert.setContentText("Center Node is larger than graph size: " + size);
        alert.show();
    }

    /**
     * Check bookmark for positive integers.
     * @param field The field that is not an integer
     */
    private void intAlert(String field) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(field + " error");
        alert.setContentText("Bookmark " + field + " can only contains positive integers");
        alert.setHeaderText(null);
        alert.show();
    }

    /**
     * Alerts the user if a space is present in the bookmark name.
     */
    private void nameSpaceAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Name error");
        alert.setContentText("Bookmark name can not contain spaces");
        alert.setHeaderText(null);
        alert.show();
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

    /**
     * Set the graphcontroller.
     * @param graphController The graphcontroller.
     */
    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
        this.graphName = graphController.getGraph().getID();
    }
}
