package programminglife.gui.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import programminglife.model.DataManager;
import programminglife.model.GenomeGraph;
import programminglife.model.exception.UnknownTypeException;
import programminglife.parser.GraphParser;
import programminglife.utility.FileProgressCounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

/**
 * The controller for the GUI that is used in the application.
 * The @FXML tag is needed in initialize so that javaFX knows what to do.
 */
public class GuiController implements Observer {

    //FXML imports.
    @FXML private MenuItem btnOpen;
    @FXML private MenuItem btnQuit;
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

    private Thread parseThread;

    /**
     * The initialize will call the other methods that are run in the .
     */
    @FXML
    @SuppressWarnings("Unused")
    private void initialize() {
        this.graphController = new GraphController(null, this.grpDrawArea);

        initMenubar();
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
    public void openFile(File file) throws IOException, UnknownTypeException {
        if (file != null) {
            DataManager.initialize(file.getName());

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

                System.out.printf("[%s] File Parsed.\n", Thread.currentThread().getName());

                this.setGraph(graph);
            } else if (arg instanceof Exception) {
                Exception e = (Exception) arg;
                // TODO find out a smart way to catch Exceptions across threads
                throw new RuntimeException(e);
            }
        } else if (o instanceof FileProgressCounter) {
            FileProgressCounter progress = (FileProgressCounter) o;
            if (progress.getLineCount() % 250 == 0) {
                System.out.println(progress);
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

        if (graph != null) {
            System.out.printf("[%s] Graph was set to %s.\n", Thread.currentThread().getName(), graph.getID());
            System.out.printf("[%s] The graph has %d nodes\n", Thread.currentThread().getName(), graph.size());
        } else {
            System.out.printf("[%s] graph was set to null.\n", Thread.currentThread().getName());
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

            try {
                File file = fileChooser.showOpenDialog(ProgrammingLife.getStage());
                this.openFile(file);
            } catch (FileNotFoundException e) {
                (new Alert(Alert.AlertType.ERROR,
                        "This file was not found!",
                        ButtonType.CLOSE)).show();
            } catch (UnknownTypeException e) {
                (new Alert(Alert.AlertType.ERROR,
                        "This file is malformed!",
                        ButtonType.CLOSE)).show();
            } catch (IOException e) {
                (new Alert(Alert.AlertType.ERROR,
                        "An unexpected filesystem error occurred!",
                        ButtonType.CLOSE)).show();
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
            System.out.printf("[%s] Drawing graph...\n", Thread.currentThread().getName());

            int maxDepth = Integer.MAX_VALUE;
            int centerNode = 0;

            try {
                maxDepth = Integer.parseInt(txtMaxDrawDepth.getText());
                centerNode = Integer.parseInt(txtCenterNode.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Input is not a number", ButtonType.OK);
                alert.show();
                txtMaxDrawDepth.clear();
            }

            this.graphController.clear();
            this.graphController.draw(centerNode, maxDepth);
            System.out.printf("[%s] GenomeGraph drawn.\n", Thread.currentThread().getName());
        });

        btnDrawRandom.setOnAction(event -> {
            int randomNodeID = (int) Math.ceil(Math.random() * this.graphController.getGraph().size());
            txtCenterNode.setText(Integer.toString(randomNodeID));
            btnDraw.fire();
        });

        txtMaxDrawDepth.textProperty().addListener(new NumbersOnlyListener(txtMaxDrawDepth));
        txtCenterNode.textProperty().addListener(new NumbersOnlyListener(txtCenterNode));
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
