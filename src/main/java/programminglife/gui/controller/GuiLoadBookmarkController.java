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
import java.util.*;

/**
 * Created by Martijn van Meerten.
 * Controller for loading bookmarks.
 */
public class GuiLoadBookmarkController implements Observer {
    private String graphName;
    private GraphController graphController;
    private GuiController guiController;

    @FXML private Button btnOpenBookmark;
    @FXML private Button btnCancelBookmark;
    @FXML private Button btnDeleteBookmark;
    @FXML private Button btnCreateBookmark;
//    @FXML private TableView<Bookmark> tblBookmark;
    @FXML private Accordion accordionBookmark;
    private List<TableView<Bookmark>> tableViews;

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
    private Bookmark checkBookmarkSelection() {
        Bookmark bookmark = null;
        for (TableView<Bookmark> tableView : tableViews) {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                bookmark = tableView.getSelectionModel().getSelectedItem();
                return bookmark;
            }
        }
        for (TableView<Bookmark> tableView : tableViews) {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No bookmark selected");
                alert.setContentText("Please select a bookmark to open");
                alert.setHeaderText(null);
                alert.show();
                return null;
            }
        }
        return bookmark;
    }

    /**
     * Initializes the buttons in the window.
     */
    private void initButtons() {
        btnOpenBookmark.setOnAction(event -> {
            Bookmark bookmark = checkBookmarkSelection();
            if (bookmark != null) {
                guiController.setText(bookmark.getNodeID(), bookmark.getRadius());
                graphController.clear();
                graphController.draw(bookmark.getNodeID(), bookmark.getRadius());
                System.out.println("Loaded bookmark " + bookmark.getBookmarkName()
                        + " Center Node: " + bookmark.getNodeID() + " Radius: " + bookmark.getRadius());
                Stage s = (Stage) btnOpenBookmark.getScene().getWindow();
                s.close();
            }
        });
        btnDeleteBookmark.setOnAction(event -> {
            Bookmark bookmark = checkBookmarkSelection();
            if (bookmark != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Do you really want to delete bookmark: \"" + bookmark.getBookmarkName() + "\"?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        BookmarkController.deleteBookmark(graphName, bookmark.getBookmarkName());
                        System.out.println("Deleted bookmark " + bookmark.getBookmarkName()
                                + " Center Node: " + bookmark.getNodeID() + " Radius: " + bookmark.getRadius());
                        initBookmarks();
                    } else {
                        alert.close();
                    }
                }
            }
            Stage s = (Stage) btnDeleteBookmark.getScene().getWindow();
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
                bookmarkDialogStage.setResizable(false);
                bookmarkDialogStage.setScene(scene);
                bookmarkDialogStage.setTitle("Create Bookmark");
                bookmarkDialogStage.initOwner(ProgrammingLife.getStage());
                bookmarkDialogStage.showAndWait();
                this.initBookmarks();
            } catch (IOException e) {
                (new Alert(Alert.AlertType.ERROR, "This bookmark cannot be created.", ButtonType.CLOSE)).show();
            }

        });
    }

    private void createTableView(String graph, List<Bookmark> bookmarks) {
        TableColumn<Bookmark, String> tableColumn = new TableColumn<Bookmark, String>();
        tableColumn.setText("Name");
        tableColumn.setId("Name" + graph);
        tableColumn.setPrefWidth(120);

        TableColumn<Bookmark, String> tableColumn1 = new TableColumn<Bookmark, String>();
        tableColumn1.setText("Description");
        tableColumn1.setId("Description" + graph);
        tableColumn1.setPrefWidth(460);

        TableView<Bookmark> tableView = new TableView<>();
        tableView.getColumns().addAll(tableColumn, tableColumn1);
        tableViews.add(tableView);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(tableView);

        TitledPane titledPane = new TitledPane();
        titledPane.setText(graph);

        titledPane.setContent(anchorPane);


        ObservableList<Bookmark> bookmarksList = FXCollections.observableArrayList();
        for (Bookmark bm : bookmarks) {
            bookmarksList.add(bm);
        }
        tableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        tableColumn1.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        tableView.setItems(bookmarksList);
        accordionBookmark.getPanes().add(titledPane);
    }

    public void initBookmarks() {
        tableViews = new ArrayList<>();

        Map<String, List<Bookmark>> bookmarks = BookmarkController.loadAllBookmarks();
        for (Map.Entry<String, List<Bookmark>> graphBookmarks : bookmarks.entrySet()) {
            createTableView(graphBookmarks.getKey(), graphBookmarks.getValue());
        }
    }

//    /**
//     * Fills the columns with the names and descriptions of the bookmarks.
//     */
//    public void initColumns() {
//        ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();
//        for (Bookmark bm : BookmarkController.loadAllGraphBookmarks(graphName)) {
//            bookmarks.add(bm);
//        }
//        clmnFile.setCellValueFactory(cellData -> cellData.getValue().getFileProperty());
//        clmnName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
//        clmnDescription.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
//        tblBookmark.setItems(bookmarks);
//
//    }

    /**
     * Sets the graphController for drawing the bookmarks.
     * @param graphController The graphcontroller for drawing
     */
    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
        this.graphName = graphController.getGraph().getID();
    }

    /**
     * Sets the guicontroller for controlling the menu.
     * Is used for setting center node and radius text fields.
     * @param guiController The gui controller
     */
    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
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
