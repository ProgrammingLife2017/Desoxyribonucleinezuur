package programminglife;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import programminglife.model.Graph;
import javafx.scene.layout.VBox;
import programminglife.gui.GuiController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
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
     * Main method for the application
     * @param args
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
        primaryStage.setOnCloseRequest(e -> close());
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * Closes the application in a neat manner
     */
    public void close() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm Exit");
        a.setHeaderText("Do you really want to exit?");
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK){
            Platform.exit();
            System.exit(0);
        }

        if(result.get()== ButtonType.CANCEL){
            a.close();
        }
    }

    /**
     * Returns the VBox if called upon
     * @return VBox
     */
    public static VBox getVBox() {
        return vbox;
    }

    /**
     * Returns the Stage if called upon
     * @return stage
     */
    public static Stage getStage() {
        return primaryStage;
    }
}
