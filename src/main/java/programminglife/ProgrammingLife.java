package programminglife;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

/**
 * Created by marti_000 on 25-4-2017.
 */

public final class ProgrammingLife extends Application {

    private static Stage primaryStage;
    private static VBox vbox;

    private static final String DATA_FOLDER = "data/";
    private static final String TEST_DATA = DATA_FOLDER + "test/test.gfa";
    private static final String TB_DATA = DATA_FOLDER + "real/TB10.gfa";
    private static final String HUMAN_DATA = DATA_FOLDER + "real/chr19.hg38.w115.gfa";

    /**
     * Main method for the application.
     * @param args argument
     */
    public static void main(String[] args) {
//        Graph g;
//        String graphFile = TEST_DATA;
//
//        if (args.length > 0) {
//            graphFile = args[0];
//        }
//
//        try {
//            g = Graph.parse(graphFile, true);
//        } catch (FileNotFoundException e) {
//            System.err.println(String.format("File not found (%s)", graphFile));
//        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Programming Life");
        primaryStage.minWidthProperty().set(1000);
        primaryStage.minHeightProperty().set(900);

        vbox = FXMLLoader.load(getClass().getResource("/Basic_Gui.fxml"));
        primaryStage.setScene(new Scene(vbox, 1000, 900));
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        Button close = new Button("Close Application");
        close.setOnAction(event -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        Alert closeConfirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit?"
        );
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
                ButtonType.OK
        );
        exitButton.setText("Exit");
        closeConfirmation.setHeaderText("Confirm Exit");
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(primaryStage);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        }
    };

    /**
     * Returns the VBox if called upon.
     * @return VBox
     */
    public static VBox getVBox() {
        return vbox;
    }

    /**
     * Returns the Stage if called upon.
     * @return stage
     */
    public static Stage getStage() {
        return primaryStage;
    }
}
