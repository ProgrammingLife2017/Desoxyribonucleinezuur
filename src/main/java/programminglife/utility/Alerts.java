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
     * return an instance of the alert class.
     * @return Alerts.
     */
    public static synchronized Alerts getInstance() {
        if (alerts == null) {
            alerts = new Alerts();
        }
        return alerts;
    }

    /**
     * Alert method used if a file can't be opened.
     */
    public void cantOpenAlert() {
        (new Alert(Alert.AlertType.ERROR, "This file can't be opened!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if a file can't be saved.
     */
    public void cantSaveAlert() {
        (new Alert(Alert.AlertType.ERROR, "This file can't be saved!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if a file can't be found.
     */
    public void cantFindAlert() {
        (new Alert(Alert.AlertType.ERROR, "This file can't be found!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if a file can't be created.
     */
    public void cantCreateAlert() {
        (new Alert(Alert.AlertType.ERROR, "This file can't be created!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if a file can't be created.
     */
    public void cantUpdateRecentAlert() {
        (new Alert(Alert.AlertType.ERROR, "The file containing the "
                + "recent documents can't be updated!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if a file is malformed.
     */
    public void malformedAlert() {
        (new Alert(Alert.AlertType.ERROR, "This file is malformed!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if a file can't be created.
     */
    public void unexpectedErrorAlert() {
        (new Alert(Alert.AlertType.ERROR, "An unexpected filesystem error occurred!", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if the bookmarks can't be opened.
     */
    public void loadBookmarkAlert() {
        (new Alert(Alert.AlertType.ERROR, "Bookmarks cannot be loaded.", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if the user didn't enter a number.
     */
    public void notANumberAlert() {
        (new Alert(Alert.AlertType.WARNING, "Make sure you have entered a number as input.", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if the user entered a number that isn't a node.
     */
    public void notANodeAlert() {
        (new Alert(Alert.AlertType.WARNING, "There isn't a node with this ID, "
                + "choose another.", ButtonType.CLOSE)).show();
    }

    /**
     * Alert method used if the user wants to quit.
     */
    public void quitAlert() {
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
    public void infoAboutAlert() {
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
    public void infoInstructionAlert() {
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
