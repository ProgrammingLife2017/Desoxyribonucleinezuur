package programminglife.gui;

import com.google.common.base.Charsets;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * A class which contains all the possible alerts we can give to the user.
 */
public final class Alerts {
    private static final double ABOUT_MIN_WIDTH = 500;
    private static final double INSTRUCTIONS_MIN_WIDTH = 800;

    /**
     * Constructor for the Alert.
     */
    private Alerts() { }

    /**
     * Alert method used if the alert is an error.
     * @param message String containing the message to be given to the user.
     */
    public static void error(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE).show());
    }

    /**
     * Alert method used if the alert is a warning.
     * @param message String containing the message to be given to the user.
     */
    public static void warning(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, message, ButtonType.CLOSE).show());
    }


    /**
     * Alert method used if the alert is informative.
     * @param message String containing the message to be given to the user.
     */
    public static void info(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE).show());
    }

    /**
     * Alert method used if the user wants to quit.
     */
    public static void quitAlert() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm Exit");
        a.setHeaderText("Do you really want to exit?");
        Optional<ButtonType> result = a.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Platform.exit();
                System.exit(0);
            }
            if (buttonType == ButtonType.CANCEL) {
                a.close();
            }
        });
    }

    /**
     * Alert method used to show the information from the group.
     */
    public static void infoAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(ABOUT_MIN_WIDTH);
        URL url = Alerts.class.getResource("/texts/About.txt");
        try {
            alert.setContentText(com.google.common.io.Resources.toString(url, Charsets.UTF_8));
            alert.show();
        } catch (IOException e) {
            Alerts.error("Can't open the about file");
        }
    }

    /**
     * Alert method used to show the information from the instructions.
     */
    public static void infoInstructionAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instructions");
        alert.setHeaderText(null);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(INSTRUCTIONS_MIN_WIDTH);
        URL url = Alerts.class.getResource("/texts/Instructions.txt");
        try {
            alert.setContentText(com.google.common.io.Resources.toString(url, Charsets.UTF_8));
            alert.show();
        } catch (IOException e) {
            Alerts.error("Can't open the instructions file");
        }
    }

    /**
     * Alert to show the information of a bookmark.
     * @param s String given in the alert.
     */
    public static void infoBookmarkAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getButtonTypes().add(ButtonType.CLOSE);
        alert.setTitle("Bookmark information");
        alert.setResizable(false);
        alert.setContentText(s);
        Platform.runLater(alert::show);
    }
}
