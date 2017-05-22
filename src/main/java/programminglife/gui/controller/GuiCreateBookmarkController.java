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
            if (!txtId.getText().matches("^[1-9]\\d*$")) {
                Alerts.warning("Center node can only contain positive integers").show();
            } else if (Integer.parseInt(txtId.getText()) > graphController.getGraph().size()) {
                Alerts.warning("Center node is larger than graph size: " + graphController.getGraph().size()).show();
            } else if (!txtRadius.getText().matches("^[1-9]\\d*$")) {
                Alerts.warning("Radius can only contain positive integers").show();
            } else if (!BookmarkController.storeBookmark(graphName, txtBookmarkName.getText(), txtDescription.getText(),
                    Integer.parseInt(txtId.getText()), Integer.parseInt(txtRadius.getText()))) {
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

    /**
     * Set the text in the center node and radius areas.
     * @param center The text to fill the center area with
     * @param radius The text to fill the radius area with
     */
    public void setText(String center, String radius) {
        txtId.setText(center);
        txtRadius.setText(radius);
    }
}
