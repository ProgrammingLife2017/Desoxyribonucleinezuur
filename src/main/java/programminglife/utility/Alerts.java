package programminglife.utility;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * A class which contains all the possible alerts we can give to the user.
 */
public final class Alerts {
    private static final double ABOUT_MIN_WIDTH = 500;
    private static final double INSTRUCTIONS_MIN_WIDTH = 800;

    private static Alerts alerts;

    /**
     * Constructor for the Alert.
     */
    private Alerts() { }

    /**
     * Alert method used if the alert is an error.
     * @param message String containing the message to be given to the user.
     * @return Alert error.
     */
    public static Alert error(String message) {
        return new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
    }

    /**
     * Alert method used if the alert is a warning.
     * @param message String containing the message to be given to the user.
     * @return Alert warning.
     */
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
        alert.setContentText("This application is made by Contextproject group Desoxyribonucleïnezuur:\n\n"
                + "Ivo Wilms \n" + "Iwan Hoogenboom \n" + "Martijn van Meerten \n" + "Toine Hartman\n"
                + "Yannick Haveman");
        alert.show();
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
        alert.setContentText("Open a gfa file, wait for it to be parsed.\n"
                + "Give the start node and the amount of layers (depth) to be drawn on the left.\n\n"
                + "Zoom using the zoom buttons or alt + scrollwheel.\n"
                + "Move the graph by pressing alt + dragging a node or edge.\n"
                + "Reset the zoom with reset zoom and jump back to the beginning"
                + " of the drawn graph with the Reset X/Y button.\n"
                + "The suprise me! button chooses a random start node and draws with the depth you gave.");
        alert.show();
    }
}
