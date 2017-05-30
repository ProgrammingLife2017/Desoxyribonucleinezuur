package programminglife.gui.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jp.uphy.javafx.console.ConsoleView;
import org.jetbrains.annotations.NotNull;
import programminglife.ProgrammingLife;
import programminglife.model.GenomeGraph;
import programminglife.model.exception.UnknownTypeException;
import programminglife.parser.GraphParser;
import programminglife.utility.Alerts;
import programminglife.utility.Console;
import programminglife.utility.FileProgressCounter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
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
    @FXML private Button btnZoomIn;
    @FXML private Button btnZoomOut;
    @FXML private Button btnZoomReset;
    @FXML private Button btnTranslate;
    @FXML private Button btnTranslateReset;
    @FXML private Button btnDraw;
    @FXML private Button btnDrawRandom;
    @FXML private Button btnBookmark;
    @FXML private ProgressBar progressBar;

    @FXML private TextField txtMaxDrawDepth;
    @FXML private TextField txtCenterNode;

    @FXML private Group grpDrawArea;
    @FXML private AnchorPane anchorLeftControlPanel;

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private int translateX;
    private int translateY;
    private GraphController graphController;
    private File file;
    private File recentFile = new File("Recent.txt");
    private String recentItems = "";
    private Thread parseThread;

    /**
     * The initialize will call the other methods that are run in the .
     */
    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        this.graphController = new GraphController(null, this.grpDrawArea);
        initRecent();
        initMenubar();
        initBookmarkMenu();
        initLeftControlpanelScreenModifiers();
        initLeftControlpanelDraw();
        initMouse();
        initConsole();
    }

    /**
     * Open and parse a file.
     * @param file The {@link File} to open.
     * @throws IOException if the {@link File} is not found.
     * @throws UnknownTypeException if the {@link File} is not compliant with the GFA standard.
     */
    public void openFile(File file) throws IOException, UnknownTypeException {
        if (file != null) {
            if (this.graphController != null && this.graphController.getGraph() != null) {
                this.graphController.getGraph().close();
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
        }
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
                Platform.runLater(() -> Alerts.error(e.getMessage()).show());
            }
        } else if (o instanceof FileProgressCounter) {
            progressBar.setVisible(true);
            FileProgressCounter progress = (FileProgressCounter) o;
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
    @NotNull
    public void setGraph(GenomeGraph graph) {
        this.graphController.setGraph(graph);
        disableGraphUIElements(graph == null);

        Console.println("[%s] Graph was set to %s.", Thread.currentThread().getName(), graph.getID());
        Console.println("[%s] The graph has %d nodes", Thread.currentThread().getName(), graph.size());
    }

    /**
     * Read out the file which contains all the recently opened files.
     */
    private void initRecent() {
        try {
            Files.createFile(new File("Recent.txt").toPath());
        } catch (FileAlreadyExistsException e) {
            //This will always happen if a user has used the program before.
            //Therefore it is unnecessary to handle further.
        } catch (IOException e) {
            Alerts.error("This file can't be opened").show();
            return;
        }
        if (recentFile != null) {
            try (Scanner sc = new Scanner(recentFile)) {
                while (sc.hasNextLine()) {
                    String next = sc.nextLine();
                    MenuItem mi = new MenuItem(next);
                    mi.setOnAction(event -> {
                        try {
                            file = new File(mi.getText());
                            openFile(file);
                        } catch (UnknownTypeException e) {
                            Alerts.error("This file is malformed and cannot be parsed").show();
                        } catch (IOException e) {
                            Alerts.error("This file can't be opened").show();
                        }
                    });
                    menuRecent.getItems().add(mi);
                    recentItems = recentItems.concat(next + System.getProperty("line.separator"));
                }
            } catch (FileNotFoundException e) {
                Alerts.error("This file can't be found").show();
            }
        }
    }

    /**
     * Initializes the open button so that the user can decide which file to open.
     * Sets the action for the open MenuItem.
     * Sets the event for the quit MenuItem.
     */
    private void initMenubar() {
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
                Alerts.error("This file can't be found").show();
            } catch (IOException e) {
                Alerts.error("This file can't be opened").show();
            } catch (UnknownTypeException e) {
                Alerts.error("This file is malformed and cannot be parsed").show();
            }
        });

        btnQuit.setOnAction(event -> Alerts.quitAlert());
        btnAbout.setOnAction(event -> Alerts.infoAboutAlert());
        btnInstructions.setOnAction(event -> Alerts.infoInstructionAlert());
    }

    /**
     * Updates the recent files file after opening a file.
     */
    private void updateRecent() {
        try (BufferedWriter recentsWriter = new BufferedWriter(new FileWriter(recentFile, true))) {
            if (!recentItems.contains(file.getAbsolutePath())) {
                recentsWriter.write(file.getAbsolutePath() + System.getProperty("line.separator"));
                recentsWriter.flush();
                recentsWriter.close();
                initRecent();
            }
        } catch (IOException e) {
            Alerts.error("This file can't be updated").show();
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
                Alerts.error("The bookmarks file can't be opened").show();
            }
        });
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

        btnTranslate.setOnAction(event -> {
            GridPane root = new GridPane();
            TextField f1 = new TextField();
            TextField f2 = new TextField();
            Button translate = new Button("Translate");
            root.add(new Label("X value"), 0, 0);
            root.add(f1, 1, 0);
            root.add(new Label("Y value"), 0, 1);
            root.add(f2, 1, 1);
            root.add(translate, 1, 2);
            Stage s = new Stage();
            s.setScene(new Scene(root, 300, 200));
            s.show();
            translate.setOnAction(event2 -> {
                this.translateX = Integer.valueOf(f1.getText());
                this.translateY = Integer.valueOf(f2.getText());
                grpDrawArea.setTranslateX(grpDrawArea.getTranslateX() + this.translateX);
                grpDrawArea.setTranslateY(grpDrawArea.getTranslateY() + this.translateY);
                s.close();
            });
        });

        btnTranslateReset.setOnAction(event -> {
            grpDrawArea.setTranslateX(0);
            grpDrawArea.setTranslateY(0);
        });

        btnZoomIn.setOnAction(event -> {
            grpDrawArea.setScaleX(grpDrawArea.getScaleX() + 0.05);
            grpDrawArea.setScaleY(grpDrawArea.getScaleY() + 0.05);
        });

        btnZoomOut.setOnAction(event -> {
            grpDrawArea.setScaleX(grpDrawArea.getScaleX() - 0.05);
            grpDrawArea.setScaleY(grpDrawArea.getScaleY() - 0.05);
        });

        btnZoomReset.setOnAction(event -> {
            grpDrawArea.setScaleX(1);
            grpDrawArea.setScaleY(1);
        });
    }

    /**
     * Initializes the button on the left side that are used to draw the graph.
     */
    private void initLeftControlpanelDraw() {
        disableGraphUIElements(true);

        btnDraw.setOnAction(event -> {
            Console.println("[%s] Drawing graph...", Thread.currentThread().getName());
            int centerNode = 0;
            int maxDepth = 0;
            try {
                centerNode = Integer.parseInt(txtCenterNode.getText());
                maxDepth = Integer.parseInt(txtMaxDrawDepth.getText());
            } catch (NumberFormatException e) {
                Alerts.warning("Input is not a number, try again with a number as input.").show();
            }

            if (graphController.getGraph().contains(centerNode)) {
                this.graphController.clear();
                this.graphController.draw(centerNode, maxDepth);
                Console.println("[%s] Graph drawn.", Thread.currentThread().getName());
            } else {
                Alerts.warning("The centernode is not a existing node, "
                        + "try again with a number that exists as a node.").show();
            }
        });

        btnDrawRandom.setOnAction(event -> {
            int randomNodeID = (int) Math.ceil(Math.random() * this.graphController.getGraph().size());
            txtCenterNode.setText(Integer.toString(randomNodeID));
            btnDraw.fire();
        });

        btnBookmark.setOnAction(event -> buttonBookmark());

        txtMaxDrawDepth.textProperty().addListener(new NumbersOnlyListener(txtMaxDrawDepth));
        txtMaxDrawDepth.setText(INITIAL_MAX_DRAW_DEPTH);

        txtCenterNode.textProperty().addListener(new NumbersOnlyListener(txtCenterNode));
        txtCenterNode.setText(INITIAL_CENTER_NODE);
    }

    /**
     * {@link ChangeListener} to make a {@link TextField} only accept numbers.
     */
    private class NumbersOnlyListener implements ChangeListener<String> {
        private final TextField tf;

        /**
         * Constructor for the Listener.
         * @param tf {@link TextField} is the text field on which the listener listens
         */
        NumbersOnlyListener(TextField tf) {
            this.tf = tf;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("\\d")) {
                tf.setText(newValue.replaceAll("[^\\d]", ""));
            }
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
            Alerts.error("This bookmark cannot be created.").show();
        }
    }

    /**
     * Initialises the mouse events.
     */
    private void initMouse() {
        grpDrawArea.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((Group) (event.getSource())).getTranslateX();
            orgTranslateY = ((Group) (event.getSource())).getTranslateY();
        });
        grpDrawArea.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.isAltDown()) {
                ((Group) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - orgSceneX);
                ((Group) (event.getSource())).setTranslateY(orgTranslateY + event.getSceneY() - orgSceneY);
            }
        });
        grpDrawArea.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isAltDown()) {
                grpDrawArea.setScaleX(grpDrawArea.getScaleX() + event.getDeltaY() / 250);
                grpDrawArea.setScaleY(grpDrawArea.getScaleY() + event.getDeltaY() / 250);
            }
        });
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
     * Sets the text field for drawing the graph.
     * @param center The center node
     * @param radius The radius of the subgraph
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

    public Button getBtnDraw() {
        return btnDraw;
    }

    GraphController getGraphController() {
        return this.graphController;
    }
}
