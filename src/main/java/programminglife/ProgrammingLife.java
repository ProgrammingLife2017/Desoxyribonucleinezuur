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
import programminglife.gui.controller.GuiController;
import programminglife.parser.Cache;
import programminglife.utility.Alerts;
import programminglife.utility.Console;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Main class for starting the application.
 */

public final class ProgrammingLife extends Application {

    private static Stage primaryStage;
    private static VBox vbox;

    /**
     * Main method for the application.
     * @param args argument
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Basic_Gui.fxml"));

        vbox = loader.load();
        primaryStage = stage;
        primaryStage.setTitle("Programming Life");
        primaryStage.setScene(new Scene(vbox, 0.8 * screenWidth, 0.8 * screenHeight));
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        Button close = new Button("Close Application");
        close.setOnAction(event -> primaryStage.fireEvent(
                new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST))
        );
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setMaximized(true);

        GuiController ctrl = loader.getController();
        try {
            arguments(ctrl);
        } catch (IOException e) {
            Alerts.warning("An error occurred opening the specified file!");
        }
    }

    /**
     * Process command line arguments.
     * @param guiCtrl the {@link GuiController}, needed for opening files
     * @throws IOException if a specified file cannot be opened
     */
    private void arguments(GuiController guiCtrl) throws IOException {
        Parameters params = this.getParameters();
        if (params.getNamed().containsKey("file")) {
            String fileName = params.getNamed().get("file");
            File file = new File(fileName);
            if (params.getUnnamed().contains("--clean")) {
                boolean removed = Cache.removeDB(file.getName());
                Console.println("[%s] Removed: %b", Thread.currentThread().getName(), removed);
            }
            guiCtrl.openFile(file);
        }
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
        closeResponse.ifPresent(buttonType -> {
            if (!ButtonType.OK.equals(buttonType)) {
                event.consume();
            } else {
                Platform.exit();
                System.exit(0);
            }
        });
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
