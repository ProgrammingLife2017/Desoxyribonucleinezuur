package programminglife.gui.controller;

import javafx.application.Platform;
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
import programminglife.model.exception.UnknownTypeException;
import programminglife.utility.Alerts;
import programminglife.utility.Console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for loading bookmarks.
 */
public class GuiLoadBookmarkController {
    private GuiController guiController;

    @FXML private Button btnOpenBookmark;
    @FXML private Button btnDeleteBookmark;
    @FXML private Button btnCreateBookmark;
    @FXML private Button btnShowInfo;
    @FXML private Accordion accordionBookmark;
    private List<TableView<Bookmark>> tableViews;

    /**
     * Initialize method for BookmarkController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        btnCreateBookmark.setDisable(true);
        initButtons();
    }

    /**
     * Checks whether the user has selected a bookmark.
     * @return True if selected, false otherwise.
     */
    private Bookmark checkBookmarkSelection() {
        Bookmark bookmark;
        for (TableView<Bookmark> tableView : tableViews) {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                bookmark = tableView.getSelectionModel().getSelectedItem();
                return bookmark;
            }
        }
        Alerts.warning("No bookmark selected").show();
        return null;
    }

    /**
     * Initializes the buttons in the window.
     */
    private void initButtons() {
        btnOpenBookmark.setOnAction(event -> buttonOpen());
        btnDeleteBookmark.setOnAction(event -> buttonDelete());
        btnCreateBookmark.setOnAction(event -> createBookmark());
        btnShowInfo.setOnAction(event -> showInfo());
    }

    /**
     * Handles the actions of the open bookmark button.
     */
    private void buttonOpen() {
        Bookmark bookmark = checkBookmarkSelection();
        if (bookmark != null) {
            if (guiController.getFile() == null
                    || !bookmark.getPath().equals(guiController.getFile().getAbsolutePath())) {
                File file = new File(bookmark.getPath());
                try {
                    guiController.setFile(file);
                    guiController.openFile(file);
                } catch (IOException | UnknownTypeException e) {
                    Alerts.error("File location has changed");
                }
            }
            guiController.getGraphController().clear();
            guiController.setText(bookmark.getNodeID(), bookmark.getRadius());

            Console.println("Loaded bookmark " + bookmark.getBookmarkName()
                    + " Center Node: " + bookmark.getNodeID() + " Radius: " + bookmark.getRadius());
            ((Stage) btnOpenBookmark.getScene().getWindow()).close();
            Platform.runLater(() -> guiController.getBtnDraw().fire());
        }
    }

    /**
     * Handles the actions of the delete bookmark button.
     */
    private void buttonDelete() {
        Bookmark bookmark = checkBookmarkSelection();
        if (bookmark != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Do you really want to delete bookmark: \"" + bookmark.getBookmarkName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    BookmarkController.deleteBookmark(bookmark.getFile(), bookmark.getBookmarkName());
                    Console.println("Deleted bookmark " + bookmark.getBookmarkName()
                            + " Center Node: " + bookmark.getNodeID() + " Radius: " + bookmark.getRadius());
                    initBookmarks();
                } else {
                    alert.close();
                }
            }
        }
    }

    /**
     * Called when create bookmark button is triggered.
     * Creates a new bookmark and stores it.
     */
    private void createBookmark() {
        try {
            FXMLLoader loader = new FXMLLoader(ProgrammingLife.class.getResource("/CreateBookmarkWindow.fxml"));
            AnchorPane page = loader.load();
            GuiCreateBookmarkController gc = loader.getController();
            gc.setGuiController(guiController);
            Scene scene = new Scene(page);
            Stage bookmarkDialogStage = new Stage();
            bookmarkDialogStage.setResizable(false);
            bookmarkDialogStage.setScene(scene);
            bookmarkDialogStage.setTitle("Create Bookmark");
            bookmarkDialogStage.initOwner(ProgrammingLife.getStage());
            bookmarkDialogStage.showAndWait();
            initBookmarks();
        } catch (IOException e) {
            Alerts.error("This bookmark cannot be created.").show();
        }
    }

    /**
     * Shows the complete info of a bookmark.
     */
    private void showInfo() {
        Bookmark bookmark = checkBookmarkSelection();
        if (bookmark != null) {
            Alerts.infoBookmarkAlert(String.format("Name: %s"
                            + "%nNode ID: %d%nNode radius: %d%nDescription: %s", bookmark.getBookmarkName(),
                    bookmark.getNodeID(), bookmark.getRadius(), bookmark.getDescription()));
        }
    }

    /**
     * Creates the tableview with the menu's for the bookmarks.
     * @param graph String the graph for which we have bookmarks.
     * @param bookmarks List of bookmarks that are created for the graphs.
     */
    private void createTableView(String graph, List<Bookmark> bookmarks) {
        TableColumn<Bookmark, String> tableColumn = new TableColumn<>("Name");
        tableColumn.setId("Name" + graph);
        tableColumn.setPrefWidth(120);
        tableColumn.setResizable(false);

        TableColumn<Bookmark, String> tableColumn1 = new TableColumn<>("Description");
        tableColumn1.setId("Description" + graph);
        tableColumn1.setPrefWidth(455);
        tableColumn1.setResizable(false);

        TableView<Bookmark> tableView = new TableView<>();
        tableView.getColumns().add(0, tableColumn);
        tableView.getColumns().add(1, tableColumn1);
        tableViews.add(tableView);

        AnchorPane anchorPane = new AnchorPane(tableView);
        AnchorPane.setBottomAnchor(tableView, 0.d);
        AnchorPane.setTopAnchor(tableView, 0.d);
        AnchorPane.setLeftAnchor(tableView, 0.d);
        AnchorPane.setRightAnchor(tableView, 0.d);

        TitledPane titledPane = new TitledPane();
        titledPane.setText(graph);

        titledPane.setContent(anchorPane);
        accordionBookmark.getPanes().add(titledPane);

        ObservableList<Bookmark> bookmarksList = FXCollections.observableArrayList();
        for (Bookmark bm : bookmarks) {
            bookmarksList.addAll(bm);
        }
        tableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        tableColumn1.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        tableView.setItems(bookmarksList);
    }

    /**
     * Initializes the bookmarks from the different graphs.
     */
    void initBookmarks() {
        accordionBookmark.getPanes().clear();
        tableViews = new ArrayList<>();

        Map<String, List<Bookmark>> bookmarks = BookmarkController.loadAllBookmarks();
        for (Map.Entry<String, List<Bookmark>> graphBookmarks : bookmarks.entrySet()) {
            createTableView(graphBookmarks.getKey(), graphBookmarks.getValue());
        }
    }

    /**
     * Sets the guicontroller for controlling the menu.
     * Is used for setting center node and radius text fields.
     * @param guiController The gui controller
     */
    void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Sets the create bookmark button to active when a file is opened.
     * @param active true for active, false for inactive
     */
    public void setBtnCreateBookmarkActive(Boolean active) {
        btnCreateBookmark.setDisable(!active);
    }
}
