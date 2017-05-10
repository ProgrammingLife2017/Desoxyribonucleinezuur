package programminglife.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import programminglife.ProgrammingLife;
import programminglife.model.Graph;
import programminglife.model.XYCoordinate;
import programminglife.model.exception.UnknownTypeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * The controller for the GUI that is used in the application.
 * The @FXML tag is needed in initialize so that javaFX knows what to do.
 */
public class GuiController {

    @FXML private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;
    @FXML private Canvas graphCanvas;

    private GraphController graphController;

    @FXML @SuppressWarnings("Unused")
    /**
     * The initialize will call the other methods that are run in the GUI
     */
    private void initialize() {
        this.graphController = new GraphController(new Graph(""), this.graphCanvas);
        initApp();

        File tempFile = new File("data/real/TB10.gfa");
//        File tempFile = new File("data/real/chr19.hg38.w115.gfa");
        try {
            Graph graph = Graph.parse(tempFile, true);
            this.graphController.setGraph(graph);
            this.graphController.drawDFS(this.graphController.getGraph().getNode(1), new XYCoordinate(10, 10));

        } catch (UnknownTypeException | FileNotFoundException e) {
            throw new RuntimeException("This should absolutely not have happened", e);
        }
    }

    /**
     * Initializes the open button so that the user can decide which file to open.
     * Sets the action for the open MenuItem.
     * Sets the event for the quit MenuItem.
     */
    private void initApp() {
        btnOpen.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            final ExtensionFilter extFilterGFA = new ExtensionFilter("GFA files (*.gfa)", "*.GFA");
            fileChooser.getExtensionFilters().addAll(extFilterGFA);
            try {
                File file = fileChooser.showOpenDialog(ProgrammingLife.getStage());
                if (file != null) {
                    Graph graph = Graph.parse(file, true);
                    this.graphController.setGraph(graph);
                }
            } catch (FileNotFoundException | UnknownTypeException e) {
                //Should not happen, because it gets handled by FileChooser and ExtensionFilter
                throw new RuntimeException("This should absolutely not have happened", e);
            }
        });
        btnQuit.setOnAction(event -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Confirm Exit");
            a.setHeaderText("Do you really want to exit?");
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                Platform.exit();
                System.exit(0);
            }

            if (result.get() == ButtonType.CANCEL) {
                a.close();
            }
        });
    }
}
