package programminglife;

import javafx.application.Application;
import javafx.application.Platform;
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

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by Martijn van Meerten on 25-4-2017.
 * Main class for starting the application.
 */

public final class ProgrammingLife extends Application {

    private static Stage primaryStage;
    private static VBox vbox;

    private static final String DATA_FOLDER = "data/";
    private static final String TB_DATA = DATA_FOLDER + "real/TB10.gfa";
    private static final String HUMAN_DATA = DATA_FOLDER + "real/chr19.hg38.w115.gfa";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("m:ss.SSS", Locale.getDefault());

    /**
     * Main method for the application.
     * @param args argument
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        primaryStage = stage;
        primaryStage.setTitle("Programming Life");
        vbox = FXMLLoader.load(getClass().getResource("/Basic_Gui.fxml"));
        primaryStage.setScene(new Scene(vbox, 0.8 * screenWidth, 0.8 * screenHeight));
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        Button close = new Button("Close Application");
        close.setOnAction(event -> primaryStage.fireEvent(
                new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST))
        );
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * The event handler for when the application is closed.
     * It will give show a confirmation box if the user wants to exit the application.
     */
    private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        Alert closeConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to exit?");
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.OK);
        exitButton.setText("Exit");
        closeConfirmation.setHeaderText("Confirm Exit");
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(primaryStage);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        } else {
            Platform.exit();
            System.exit(0);
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
