package programminglife.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jp.uphy.javafx.console.ConsoleView;
import programminglife.ProgrammingLife;
import programminglife.model.GenomeGraph;
import programminglife.parser.GraphParser;
import programminglife.utility.Alerts;
import programminglife.utility.Console;
import programminglife.utility.NumbersOnlyListener;
import programminglife.utility.ProgressCounter;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;

/**
 * The controller for the GUI that is used in the application.
 * The @FXML tag is needed in initialize so that javaFX knows what to do.
 */
public class GuiController implements Observer {
    //static finals
    private static final String INITIAL_CENTER_NODE = "1";
    private static final String INITIAL_MAX_DRAW_DEPTH = "10";

    //FXML imports.
    @FXML private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;
    @FXML private MenuItem btnBookmarks;
    @FXML private MenuItem btnAbout;
    @FXML private MenuItem btnInstructions;
    @FXML private Menu menuRecent;
    @FXML private RadioMenuItem btnToggle;
    @FXML private Button btnZoomReset;
    @FXML private Button btnTranslateReset;
    @FXML private Button btnDraw;
    @FXML private Button btnDrawRandom;
    @FXML private Button btnBookmark;
    @FXML private Button btnClipboard;
    @FXML private Button btnClipboard2;
    @FXML private Button btnHighlight;
    @FXML private ProgressBar progressBar;

    @FXML private TextField txtMaxDrawDepth;
    @FXML private TextField txtCenterNode;

    @FXML private Canvas canvas;
    @FXML private AnchorPane anchorLeftControlPanel;
    @FXML private AnchorPane anchorCanvasPanel;
    @FXML private AnchorPane anchorGraphInfo;

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private double scale;
    private GraphController graphController;
    private File file;
    private File recentFile = new File("Recent.txt");
    private String recentItems = "";
    private Thread parseThread;

    private static final double MAX_SCALE = 5.0d;
    private static final double MIN_SCALE = .02d;
    private static final double ZOOM_FACTOR = 1.05d;



    /**
     * The initialize will call the other methods that are run in the .
     */
    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        this.graphController = new GraphController(null, this.canvas, this.anchorGraphInfo, this.anchorCanvasPanel);
        initRecent();
        initMenuBar();
        initBookmarkMenu();
        initLeftControlpanelScreenModifiers();
        initLeftControlpanelDraw();
        initMouse();
        initShowInfoTab();
        initConsole();
        initLeftControlpanelHighlight();
    }

    /**
     * Open and parse a file.
     * @param file The {@link File} to open.
     * @throws IOException if the {@link File} is not found.
     * @return the parser to be notified when it is finished
     */
    public GraphParser openFile(File file) throws IOException {
        if (file != null) {
            if (this.graphController != null && this.graphController.getGraph() != null) {
                this.graphController.getGraph().close();
                this.canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }

            disableGraphUIElements(true);

            GraphParser graphParser = new GraphParser(file);
            graphParser.addObserver(this);
            graphParser.getProgressCounter().addObserver(this);

            if (this.parseThread != null) {
                this.parseThread.interrupt();
            }
            this.parseThread = new Thread(graphParser);
            this.parseThread.start();
            this.setFile(file);

            return graphParser;
        }

        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GraphParser) {
            if (arg instanceof GenomeGraph) {
                GenomeGraph graph = (GenomeGraph) arg;

                Console.println("[%s] File Parsed.", Thread.currentThread().getName());

                this.setGraph(graph);
            } else if (arg instanceof Exception) {
                Exception e = (Exception) arg;
                e.printStackTrace();
                Alerts.error(e.getMessage());
            }
        } else if (o instanceof ProgressCounter) {
            progressBar.setVisible(true);
            ProgressCounter progress = (ProgressCounter) o;
            this.getProgressBar().setProgress(progress.percentage());
            if (progressBar.getProgress() == 1.0d) {
                progressBar.setVisible(false);
            }
        }
    }

    /**
     * Set the graph for this GUIController.
     * @param graph Graph to use.
     */
    public void setGraph(GenomeGraph graph) {
        this.graphController.setGraph(graph);
        disableGraphUIElements(graph == null);
        Platform.runLater(() -> {
            assert graph != null;
            ProgrammingLife.getStage().setTitle(graph.getID());
        });

        if (graph != null) {
            Console.println("[%s] Graph was set to %s.", Thread.currentThread().getName(), graph.getID());
            Console.println("[%s] The graph has %d nodes", Thread.currentThread().getName(), graph.size());
        }
    }

    /**
     * Read out the file which contains all the recently opened files.
     */
    private void initRecent() {
        try {
            Files.createFile(recentFile.toPath());
        } catch (FileAlreadyExistsException e) {
            //This will always happen if a user has used the program before.
            //Therefore it is unnecessary to handle further.
        } catch (IOException e) {
            Alerts.error("This file can't be opened");
            return;
        }
        if (recentFile != null) {
            try (Scanner sc = new Scanner(recentFile)) {
                menuRecent.getItems().clear();
                while (sc.hasNextLine()) {
                    String next = sc.nextLine();
                    MenuItem mi = new MenuItem(next);
                    mi.setOnAction(event -> {
                        try {
                            file = new File(mi.getText());
                            openFile(file);
                        } catch (IOException e) {
                            Alerts.error("This file can't be opened");
                        }
                    });
                    menuRecent.getItems().add(mi);
                    recentItems = recentItems.concat(next + System.getProperty("line.separator"));
                }
            } catch (FileNotFoundException e) {
                Alerts.error("This file can't be found");
            }
        }
    }

    /**
     * Initializes the open button so that the user can decide which file to open.
     * Sets the action for the open MenuItem.
     * Sets the event for the quit MenuItem.
     */
    private void initMenuBar() {
        btnOpen.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            final ExtensionFilter extFilterGFA = new ExtensionFilter("GFA files (*.gfa)", "*.GFA");
            fileChooser.getExtensionFilters().add(extFilterGFA);
            if (file != null) {
                File existDirectory = file.getParentFile();
                fileChooser.setInitialDirectory(existDirectory);
            }
            try {
                file = fileChooser.showOpenDialog(ProgrammingLife.getStage());
                if (file != null) {
                    this.openFile(file);
                    updateRecent();
                }
            } catch (FileNotFoundException e) {
                Alerts.error("This file can't be found");
            } catch (IOException e) {
                Alerts.error("This file can't be opened");
            }
        });

        btnOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));
        btnQuit.setOnAction(event -> Alerts.quitAlert());
        btnQuit.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCodeCombination.CONTROL_DOWN));
        btnAbout.setOnAction(event -> Alerts.infoAboutAlert());
        btnAbout.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCodeCombination.CONTROL_DOWN));
        btnInstructions.setOnAction(event -> Alerts.infoInstructionAlert());
        btnInstructions.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCodeCombination.CONTROL_DOWN));
    }

    /**
     * Updates the recent files file after opening a file.
     */
    private void updateRecent() {
        try (BufferedWriter recentWriter = new BufferedWriter(new FileWriter(recentFile, true))) {
            if (!recentItems.contains(file.getAbsolutePath())) {
                recentWriter.write(file.getAbsolutePath() + System.getProperty("line.separator"));
                recentWriter.flush();
                recentWriter.close();
                initRecent();
            }
        } catch (IOException e) {
            Alerts.error("This file can't be updated");
        }
    }

    /**
     * Initializes the bookmark buttons in the menu.
     */
    private void initBookmarkMenu() {

        btnBookmarks.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(ProgrammingLife.class.getResource("/LoadBookmarkWindow.fxml"));
                AnchorPane page = loader.load();
                GuiLoadBookmarkController gc = loader.getController();
                gc.setGuiController(this);
                gc.initBookmarks();
                if (this.graphController.getGraph() != null) {
                    gc.setBtnCreateBookmarkActive(true);
                }
                Scene scene = new Scene(page);
                Stage bookmarkDialogStage = new Stage();
                bookmarkDialogStage.setResizable(false);
                bookmarkDialogStage.setScene(scene);
                bookmarkDialogStage.setTitle("Load Bookmark");
                bookmarkDialogStage.initOwner(ProgrammingLife.getStage());
                bookmarkDialogStage.showAndWait();
            } catch (IOException e) {
                Alerts.error("The bookmarks file can't be opened");
            }
        });
        btnBookmarks.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCodeCombination.CONTROL_DOWN));
    }

    /**
     * Method to disable the UI Elements on the left of the GUI.
     * @param isDisabled boolean, true disables the left anchor panel.
     */
    private void disableGraphUIElements(boolean isDisabled) {
        anchorLeftControlPanel.setDisable(isDisabled);
    }

    /**
     * Initializes the buttons on the panel on the left side which do translation/zoom.
     */
    private void initLeftControlpanelScreenModifiers() {
        disableGraphUIElements(true);

        btnTranslateReset.setOnAction(event -> {
            grpDrawArea.setTranslateX(graphController.getLocationCenterX());
            grpDrawArea.setTranslateY(graphController.getLocationCenterY());
        });

        btnZoomReset.setOnAction(event -> {
            scale = 1;
            grpDrawArea.setScaleX(1);
            grpDrawArea.setScaleY(1);
        });
    }

    /**
     * Initializes the button on the left side that are used to draw the graph.
     */
    private void initLeftControlpanelDraw() {
        disableGraphUIElements(true);

        btnDraw.setOnAction(e -> this.draw());

        btnDrawRandom.setOnAction(event -> {
            int randomNodeID = (new Random()).nextInt(this.graphController.getGraph().size() - 1) + 1;
            txtCenterNode.setText(Integer.toString(randomNodeID));
            this.draw();
        });

        btnBookmark.setOnAction(event -> buttonBookmark());

        txtMaxDrawDepth.textProperty().addListener(new NumbersOnlyListener(txtMaxDrawDepth));
        txtMaxDrawDepth.setText(INITIAL_MAX_DRAW_DEPTH);

        txtCenterNode.textProperty().addListener(new NumbersOnlyListener(txtCenterNode));
        txtCenterNode.setText(INITIAL_CENTER_NODE);
    }

    /**
     * Initializes the buttons and textFields that are used to highlight.
     */
    private void initLeftControlpanelHighlight() {

        btnHighlight.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(ProgrammingLife.class.getResource("/HighlightWindow.fxml"));
                AnchorPane page = loader.load();
                HighlightController gc = loader.getController();
                gc.setGraphController(this.getGraphController());
                gc.initMinMax();
                gc.initGenome();
                Scene scene = new Scene(page);
                Stage highlightDialogStage = new Stage();
                highlightDialogStage.setResizable(false);
                highlightDialogStage.setScene(scene);
                highlightDialogStage.setTitle("Highlights");
                highlightDialogStage.initOwner(ProgrammingLife.getStage());
                highlightDialogStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Draw the current graph with current center node and depth settings.
     */
    void draw() {
        Console.println("[%s] Drawing graph...", Thread.currentThread().getName());
        int centerNode = 0;
        int maxDepth = 0;
        try {
            centerNode = Integer.parseInt(txtCenterNode.getText());
            try {
                maxDepth = Integer.parseInt(txtMaxDrawDepth.getText());
            } catch (NumberFormatException e) {
                Alerts.warning("Radius is not a number, try again with a number as input.");
            }
        } catch (NumberFormatException e) {
            Alerts.warning("Center node ID is not a number, try again with a number as input.");
        }

        if (graphController.getGraph().contains(centerNode)) {
            this.graphController.clear();
            this.graphController.draw(centerNode, maxDepth);
            Console.println("[%s] Graph drawn.", Thread.currentThread().getName());
        } else {
            Alerts.warning("The centernode is not a existing node, "
                    + "try again with a number that exists as a node.");
        }
    }

    /**
     * Handles the events of the bookmark button.
     */
    private void buttonBookmark() {
        try {
            FXMLLoader loader = new FXMLLoader(ProgrammingLife.class.getResource("/CreateBookmarkWindow.fxml"));
            AnchorPane page = loader.load();
            GuiCreateBookmarkController gc = loader.getController();
            gc.setGuiController(this);
            gc.setText(txtCenterNode.getText(), txtMaxDrawDepth.getText());
            Scene scene = new Scene(page);
            Stage bookmarkDialogStage = new Stage();
            bookmarkDialogStage.setResizable(false);
            bookmarkDialogStage.setScene(scene);
            bookmarkDialogStage.setTitle("Create Bookmark");
            bookmarkDialogStage.initOwner(ProgrammingLife.getStage());
            bookmarkDialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.error("This bookmark cannot be created.");
        }
    }

    /**
     * Initialises the mouse events.
     */
    private void initMouse() {
        anchorCanvasPanel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = grpDrawArea.getTranslateX();
            orgTranslateY = grpDrawArea.getTranslateY();
        });
        anchorCanvasPanel.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            grpDrawArea.setTranslateX((orgTranslateX + event.getSceneX() - orgSceneX));
            grpDrawArea.setTranslateY((orgTranslateY + event.getSceneY() - orgSceneY));
            event.consume();
        });
        anchorCanvasPanel.addEventHandler(ScrollEvent.SCROLL, event ->
                zoom(event.getDeltaX(), event.getDeltaY(), event.getSceneX(), event.getSceneY(), ZOOM_FACTOR));
    }

    /**
     * Handles the zooming in and out of the group.
     * @param deltaX The scroll amount in the X direction. See {@link ScrollEvent#getDeltaX()}
     * @param deltaY The scroll amount in the Y direction. See {@link ScrollEvent#getDeltaY()}
     * @param sceneX double for the x location.
     * @param sceneY double for the y location.
     * @param delta double the factor by which is zoomed.
     */
    private void zoom(double deltaX, double deltaY, double sceneX, double sceneY, double delta) {
        double oldScale = grpDrawArea.getScaleX();
        scale = oldScale;

        if (deltaX < 0 || deltaY < 0) {
            scale /= delta;
        } else {
            scale *= delta;
        }

        scale = clamp(scale, MIN_SCALE, MAX_SCALE);
        grpDrawArea.setScaleX(scale);
        grpDrawArea.setScaleY(scale);
        //factor to determine the difference in the scales.
        double factor = (scale / oldScale) - 1;
        Bounds bounds = grpDrawArea.localToScene(grpDrawArea.getBoundsInLocal());
        double dx = (sceneX - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (sceneY - (bounds.getHeight() / 2 + bounds.getMinY()));

        grpDrawArea.setTranslateX(grpDrawArea.getTranslateX() - factor * dx);
        grpDrawArea.setTranslateY(grpDrawArea.getTranslateY() - factor * dy);
    }

    /**
     * Clamp function used for zooming in and out.
     * @param value double current scale.
     * @param min double min scale value.
     * @param max double max scale value.
     * @return double scale value.
     */
    private static double clamp(double value, double min, double max) {
        if (Double.compare(value, min) < 0) {
            return min;
        }
        if (Double.compare(value, max) > 0) {
            return max;
        }
        return value;
    }

    /**
     * Initialises the Console.
     */
    private void initConsole() {
        final ConsoleView console = new ConsoleView(Charset.forName("UTF-8"));
        AnchorPane root = new AnchorPane(console);
        Stage st = new Stage();
        st.setScene(new Scene(root, 500, 500, Color.GRAY));
        st.setMinWidth(500);
        st.setMinHeight(250);

        AnchorPane.setBottomAnchor(console, 0.d);
        AnchorPane.setTopAnchor(console, 0.d);
        AnchorPane.setRightAnchor(console, 0.d);
        AnchorPane.setLeftAnchor(console, 0.d);

        btnToggle.setOnAction(event -> {
            if (btnToggle.isSelected()) {
                st.show();
            } else {
                st.close();
            }
        });

        st.show();
        btnToggle.setSelected(true);
        root.visibleProperty().bind(btnToggle.selectedProperty());

        Console.setOut(console.getOut());
    }

    private ProgressBar getProgressBar() {
        return this.progressBar;
    }

    /**
     * Initializes the info tab.
     */
    private void initShowInfoTab() {
        btnClipboard.setOnAction(event -> copyToClipboard(10));
        btnClipboard2.setOnAction(event -> copyToClipboard(250));
    }

    /**
     * Copies information to the clipboard.
     * @param x int used in the ID, to know which sequence to get.
     */
    private void copyToClipboard(int x) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String toClipboard = "";
        for (Node node : anchorGraphInfo.getChildren()) {
            if (node instanceof TextArea && node.getId().equals(x + " Sequence: ")) {
                toClipboard = toClipboard.concat(((TextArea) node).getText()) + System.getProperty("line.separator");
                toClipboard = toClipboard.replaceAll("(.{100})", "$1" + System.getProperty("line.separator"));
            }
        }
        StringSelection selection = new StringSelection(toClipboard);
        clipboard.setContents(selection, selection);
    }

    /**
     * Sets the text field for drawing the graph.
     * @param center The center node
     * @param radius The radius of the subGraph
     */
    void setText(int center, int radius) {
        txtCenterNode.setText(String.valueOf(center));
        txtMaxDrawDepth.setText(String.valueOf(radius));
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    GraphController getGraphController() {
        return this.graphController;
    }
}
