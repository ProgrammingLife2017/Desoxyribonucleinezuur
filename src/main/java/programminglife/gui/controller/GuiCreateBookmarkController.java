package programminglife.gui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import programminglife.controller.BookmarkController;
import programminglife.model.Bookmark;
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
    private int inputRadius;
    private int inputCenter;
    private String bookmarkName;
    private String bookmarkDescription;

    /**
     * Initialize method for BookmarkController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        txtDescription.setWrapText(true);
        initButtons();
    }

    /**
     * Initializes the buttons in the window.
     */
    private void initButtons() {
        btnOk.setOnAction(event -> {
            String name = guiController.getGraphController().getGraph().getID();
            try {
                inputCenter = Integer.parseInt(txtId.getText());
                inputRadius = Integer.parseInt(txtRadius.getText());
                bookmarkName = txtBookmarkName.getText();
                System.out.println(bookmarkName);
                bookmarkDescription = txtDescription.getText();
                Bookmark bookmark = new Bookmark(name, guiController.getFile().getAbsolutePath(),
                        inputCenter, inputRadius, bookmarkName, bookmarkDescription);
                if (!guiController.getGraphController().getGraph().contains(inputCenter)) {
                    Alerts.warning("Center node is not present in graph");
                } else if (inputRadius < 0) {
                    Alerts.warning("Radius can only contain positive integers");
                } else if (bookmarkName.length() == 0) {
                    Alerts.warning("Bookmark must contain a name");
                } else if (!bookmarkName.matches("\\S|\\S.*\\S")) {
                    Alerts.warning("Bookmark name must begin and end with a non whitespace character");
                } else if (!BookmarkController.storeBookmark(bookmark)) {
                    Alerts.warning("Bookmarks must have unique names in files");
                } else {
                    ((Stage) btnOk.getScene().getWindow()).close();
                }
            } catch (NumberFormatException e) {
                Alerts.warning("Center node and radius input should be numeric values below 2 billion");
            }

        });
        btnCancel.setOnAction(event -> ((Stage) btnCancel.getScene().getWindow()).close());
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
