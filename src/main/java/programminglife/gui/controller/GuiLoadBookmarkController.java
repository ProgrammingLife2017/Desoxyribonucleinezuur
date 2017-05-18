package programminglife.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import programminglife.controller.BookmarkController;
import programminglife.model.Bookmark;

/**
 * Created by Martijn van Meerten.
 * Controller for loading bookmarks.
 */
public class GuiLoadBookmarkController {
    private String graphName;

    @FXML private TableColumn<Bookmark, String> clmnName;
    @FXML private TableColumn<Bookmark, String> clmnDescription;
    @FXML private Button btnOpenBookmark;
    @FXML private Button btnCancelBookmark;
    @FXML private TableView<Bookmark> tblBookmark;

    /**
     * Initialize method for BookmarkController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        initButtons();
        initColumns();
    }

    /**
     * Initializes the buttons in the window.
     */
    private void initButtons() {
        btnOpenBookmark.setOnAction(event -> {
            Stage s = (Stage) btnOpenBookmark.getScene().getWindow();
            s.close();
        });
        btnCancelBookmark.setOnAction(event -> {
            Stage s = (Stage) btnCancelBookmark.getScene().getWindow();
            s.close();
        });
    }

    /**
     * Fills the columns with the names and descriptions of the bookmarks.
     */
    private void initColumns() {
        ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();
        for (Bookmark bm : BookmarkController.loadAllGraphBookmarks(graphName)) {
            bookmarks.add(bm);
        }
        clmnName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        clmnDescription.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        tblBookmark.setItems(bookmarks);

    }

    public void setGraph(String graphName) {
        this.graphName = graphName;
    }
}
