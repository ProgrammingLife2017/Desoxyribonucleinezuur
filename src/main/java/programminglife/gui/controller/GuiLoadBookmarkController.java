package programminglife.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import programminglife.ProgrammingLife;
import programminglife.controller.BookmarkController;
import programminglife.model.Bookmark;
import programminglife.model.Graph;
import programminglife.parser.GraphParser;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

/**
 * Created by Martijn van Meerten.
 * Controller for loading bookmarks.
 */
public class GuiLoadBookmarkController implements Observer {
    private String graphName;
    private GraphController graphController;

    @FXML private TableColumn<Bookmark, String> clmnName;
    @FXML private TableColumn<Bookmark, String> clmnDescription;
    @FXML private Button btnOpenBookmark;
    @FXML private Button btnCancelBookmark;
    @FXML private Button btnDeleteBookmark;
    @FXML private Button btnCreateBookmark;
    @FXML private TableView<Bookmark> tblBookmark;

    /**
     * Initialize method for BookmarkController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        initButtons();
    }

    /**
     * Checks whether the user has selected a bookmark.
     * @return True if selected, false otherwise.
     */
    private boolean checkBookmarkSelection() {
        if (tblBookmark.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No bookmark selected");
            alert.setContentText("Please select a bookmark to open");
            alert.setHeaderText(null);
            alert.show();
            return false;
        }
        return true;
    }

    /**
     * Initializes the buttons in the window.
     */
    private void initButtons() {
        btnOpenBookmark.setOnAction(event -> {
            if (checkBookmarkSelection()) {
                Bookmark bookmark = tblBookmark.getSelectionModel().getSelectedItem();
                graphController.clear();
                graphController.draw(bookmark.getNodeID(), bookmark.getRadius());
                System.out.println("Loaded bookmark " + bookmark.getBookmarkName()
                        + " Center Node: " + bookmark.getNodeID() + " Radius: " + bookmark.getRadius());
                Stage s = (Stage) btnOpenBookmark.getScene().getWindow();
                s.close();
            }
        });
        btnDeleteBookmark.setOnAction(event -> {
            if (checkBookmarkSelection()) {
                Bookmark bookmark = tblBookmark.getSelectionModel().getSelectedItem();
                Alert alert =  new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Do you really want to delete bookmark: \"" + bookmark.getBookmarkName() + "\"?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        BookmarkController.deleteBookmark(graphName, bookmark.getBookmarkName());
                        System.out.println("Deleted bookmark " + bookmark.getBookmarkName()
                                + " Center Node: " + bookmark.getNodeID() + " Radius: " + bookmark.getRadius());
                        this.initColumns();

                    } else {
                        alert.close();
                    }
                }
                Stage s = (Stage) btnDeleteBookmark.getScene().getWindow();
            }
        });

        btnCancelBookmark.setOnAction(event -> {
            Stage s = (Stage) btnCancelBookmark.getScene().getWindow();
            s.close();
        });

        btnCreateBookmark.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(ProgrammingLife.class.getResource("/CreateBookmarkWindow.fxml"));
                AnchorPane page = loader.load();
                GuiCreateBookmarkController gc = loader.getController();
                gc.setGraphController(graphController);
                Scene scene = new Scene(page);
                Stage bookmarkDialogStage = new Stage();
                bookmarkDialogStage.setScene(scene);
                bookmarkDialogStage.setTitle("Create Bookmark");
                bookmarkDialogStage.initOwner(ProgrammingLife.getStage());
                bookmarkDialogStage.showAndWait();
                this.initColumns();
            } catch (IOException e) {
                (new Alert(Alert.AlertType.ERROR, "This bookmark cannot be created.", ButtonType.CLOSE)).show();
            }

        });
    }

    /**
     * Fills the columns with the names and descriptions of the bookmarks.
     */
    public void initColumns() {
        ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();
        for (Bookmark bm : BookmarkController.loadAllGraphBookmarks(graphName)) {
            bookmarks.add(bm);
        }
        clmnName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        clmnDescription.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        tblBookmark.setItems(bookmarks);

    }

    /**
     * Sets the graphController for drawing the bookmarks.
     * @param graphController The graphcontroller for drawing
     */
    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
        this.graphName = graphController.getGraph().getID();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GraphParser) {
            if (arg instanceof Graph) {
                graphName = ((Graph) arg).getID();
            }
        }
    }
}
