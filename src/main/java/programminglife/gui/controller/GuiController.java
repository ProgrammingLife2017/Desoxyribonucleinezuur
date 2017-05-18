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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jp.uphy.javafx.console.ConsoleView;
import programminglife.ProgrammingLife;
import programminglife.model.Graph;
import programminglife.model.exception.UnknownTypeException;
import programminglife.parser.GraphParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * The controller for the GUI that is used in the application.
 * The @FXML tag is needed in initialize so that javaFX knows what to do.
 */




public class GuiController implements Observer {
    //static finals
    private static final String INITIAL_CENTER_NODE = "1";
    private static final String INITIAL_MAX_DRAW_DEPTH = "10";
    private static final double INSTRUCTIONS_MIN_WIDTH = 800;
    private static final double ABOUT_MIN_WIDTH = 500;

    //FXML imports.
    @FXML private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;
    @FXML private MenuItem btnCreateBookmark;
    @FXML private MenuItem btnLoadBookmark;
    @FXML private MenuItem btnAbout;
    @FXML private MenuItem btnInstructions;
    @FXML private Button btnZoomIn;
    @FXML private Button btnZoomOut;
    @FXML private Button btnZoomReset;
    @FXML private Button btnTranslate;
    @FXML private Button btnTranslateReset;
    @FXML private Button btnDraw;
    @FXML private Button btnDrawRandom;

    @FXML private TextField txtMaxDrawDepth;
    @FXML private TextField txtCenterNode;

    @FXML private Group grpDrawArea;
    @FXML private AnchorPane anchorLeftControlPanel;
    @FXML private AnchorPane anchorConsolePanel;

    //Privates used by method.
    private ConsoleView consoleView;
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private int translateX;
    private int translateY;
    private GraphController graphController;
    private File file;

    /**
     * The initialize will call the other methods that are run in the .
     */
    @FXML
    @SuppressWarnings("Unused")
    private void initialize() {
        this.graphController = new GraphController(null, this.grpDrawArea);
        initMenubar();
        initBookmarkMenu();
        initLeftControlpanelScreenModifiers();
        initLeftControlpanelDraw();
        initMouse();
        consoleView = initConsole(anchorConsolePanel);
        this.graphController.setConsole(consoleView);
    }

    /**
     * Open and parse a file.
     * @param file The {@link File} to open.
     * @throws FileNotFoundException if the {@link File} is not found.
     * @throws UnknownTypeException if the {@link File} is not compliant with the GFA standard.
     */
    public void openFile(File file) throws FileNotFoundException, UnknownTypeException {
        if (file != null) {
            GraphParser graphParser = new GraphParser(file);
            graphParser.addObserver(this);
            (new Thread(graphParser)).start();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GraphParser) {
            if (arg instanceof Graph) {
                Graph graph = (Graph) arg;
                this.graphController.setGraph(graph);

                disableGraphUIElements(graph == null);

                System.out.printf("%s File Parsed.\n", Thread.currentThread());
                System.out.printf("%s The graph has %d nodes\n", Thread.currentThread(), graph.size());
            } else if (arg instanceof Exception) {
                Exception e = (Exception) arg;
                // TODO find out a smart way to catch Exceptions across threads
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
                this.openFile(file);
            } catch (FileNotFoundException | UnknownTypeException e) {
                // Should not happen, because it gets handled by FileChooser and ExtensionFilter
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

        btnAbout.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText(null);
            alert.setResizable(true);
            alert.getDialogPane().setMinWidth(ABOUT_MIN_WIDTH);
            alert.setContentText("This application is made by Contextproject group Desoxyribonucleïnezuur:\n\n"
                    + "Ivo Wilms \n" + "Iwan Hoogenboom \n" + "Martijn van Meerten \n" + "Toine Hartman\n"
                    + "Yannick Haveman");

            alert.show();
        });


        btnInstructions.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Instructions");
            alert.setHeaderText(null);
            alert.setResizable(true);
            alert.getDialogPane().setMinWidth(INSTRUCTIONS_MIN_WIDTH);
            alert.setContentText("Open a gfa file, wait for it to be parsed.\n"
                    + "Give the start node and the amount of layers (depth) to be drawn on the left.\n\n"
                    + "Zoom using the zoom buttons or alt + scrollwheel.\n"
                    + "Move the graph by pressing alt + dragging a node or edge.\n"
                    + "Reset the zoom with reset zoom and jump back to the beginning"
                    + " of the drawn graph with the Reset X/Y button.\n"
                    + "The suprise me! button chooses a random start node and draws with the depth you gave.");
            alert.show();
        });

    }

    /**
     * Initializes the bookmark buttons in the menu.
     */
    private void initBookmarkMenu() {

        btnCreateBookmark.setOnAction(event -> {
            try {
                System.out.println("came here");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ProgrammingLife.class.getResource("/CreateBookmarkWindow.fxml"));
                AnchorPane page = loader.load();
                Scene scene = new Scene(page);
                Stage bookmarkDialogStage = new Stage();
                bookmarkDialogStage.setScene(scene);
                bookmarkDialogStage.setTitle("Create Bookmark");
                bookmarkDialogStage.initOwner(ProgrammingLife.getStage());
                bookmarkDialogStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
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
            root.add(new Label("X value"), 0, 0);
            root.add(f1, 1, 0);
            TextField f2 = new TextField();
            root.add(new Label("Y value"), 0, 1);
            root.add(f2, 1, 1);
            Button ok = new Button("Translate");
            root.add(ok, 1, 2);
            Stage s = new Stage();
            s.setScene(new Scene(root, 300, 200));
            s.show();
            ok.setOnAction(event2 -> {
                this.translateX = Integer.valueOf(f1.getText());
                this.translateY = Integer.valueOf(f2.getText());
                grpDrawArea.setTranslateX(grpDrawArea.getTranslateX() + this.translateX);
                grpDrawArea.setTranslateY(grpDrawArea.getTranslateY() + this.translateY);
                s.close();
            });
        });
//
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
            System.out.printf("%s Drawing graph...\n", Thread.currentThread());

            int centerNode = 0;
            int maxDepth = 0;

            try {
                centerNode = Integer.parseInt(txtCenterNode.getText());
                maxDepth = Integer.parseInt(txtMaxDrawDepth.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Make sure you have entered a number as input.");
                alert.show();
            }

            try {
                this.graphController.getGraph().getNode(centerNode);
                this.graphController.clear();
                this.graphController.draw(centerNode, maxDepth);
                System.out.printf("%s Graph drawn.\n", Thread.currentThread());
            } catch (NoSuchElementException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "There is no node with this ID."
                        + " Choose another start Node.", ButtonType.OK);
                alert.show();
            }


        });

        btnDrawRandom.setOnAction(event -> {
            int randomNodeID = (int) Math.ceil(Math.random() * this.graphController.getGraph().size());
            txtCenterNode.setText(Integer.toString(randomNodeID));
            btnDraw.fire();
        });

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
     * @param parent is the {@link AnchorPane} in which the console is placed.
     * @return the ConsoleView to print to
     */
    private ConsoleView initConsole(AnchorPane parent) {
        final ConsoleView console = new ConsoleView(Charset.forName("UTF-8"));
        parent.getChildren().add(console);

        AnchorPane.setBottomAnchor(console, 0.d);
        AnchorPane.setTopAnchor(console, 0.d);
        AnchorPane.setRightAnchor(console, 0.d);
        AnchorPane.setLeftAnchor(console, 0.d);

        console.setMinHeight(50.d);
        console.prefHeight(50.d);
        console.maxHeight(50.d);

        System.setOut(console.getOut());

        return console;
    }
}
