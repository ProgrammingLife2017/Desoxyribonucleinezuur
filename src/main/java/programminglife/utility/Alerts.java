package programminglife.utility;

import com.google.common.base.Charsets;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;

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
     * @return Alert error.
     */
    @NotNull
    public static Alert error(String message) {
        return new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
    }

    /**
     * Alert method used if the alert is a warning.
     * @param message String containing the message to be given to the user.
     * @return Alert warning.
     */
    @NotNull
    public static Alert warning(String message) {
        return new Alert(Alert.AlertType.WARNING, message, ButtonType.CLOSE);
    }

    /**
     * Alert method used if the user wants to quit.
     */
    public static void quitAlert() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm Exit");
        a.setHeaderText("Do you really want to exit?");
        Optional<ButtonType> result = a.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                Platform.exit();
                System.exit(0);
            }
            if (result.get() == ButtonType.CANCEL) {
                a.close();
            }
        }
    }

    /**
     * Alert method used to show the infomation from the group.
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
            Alerts.error("Can't open the about file").show();
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
            Alerts.error("Can't open the instructions file").show();
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
        alert.show();
    }
}
