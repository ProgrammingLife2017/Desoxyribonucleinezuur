package programminglife;

import programminglife.model.Graph;
import javafx.scene.layout.VBox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import programminglife.model.exception.UnknownTypeException;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by marti_000 on 25-4-2017.
 */

public final class ProgrammingLife extends Application {

    private static Stage primaryStage;
    private static VBox vbox;

    private static final String DATA_FOLDER = "data/";
    private static final String TB_DATA = DATA_FOLDER + "real/TB10.gfa";
    private static final String HUMAN_DATA = DATA_FOLDER + "real/chr19.hg38.w115.gfa";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("m:ss.SSS", Locale.getDefault());

    public static void main(String[] args) {
        Graph g;
        String graphFile;
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (args.length > 0) {
            graphFile = args[0];
        } else {
            System.err.println("No GFA file specified!");
            System.exit(1);
            return; // This is to make sure the Java compiler does not warn about uninitialized 'graphFile'
        }

        try {
            long startTime = System.nanoTime();
            g = Graph.parse(graphFile);
            long elapsedTimeMs = (System.nanoTime() - startTime) / 1000000;
            System.out.printf("Parsing took %s\n", DATE_FORMAT.format(elapsedTimeMs));
        } catch (FileNotFoundException e) {
            System.err.printf("File not found (%s)\n", graphFile);
        } catch (UnknownTypeException e) {
            System.err.printf("Parse error: %s\n", e.getMessage());
        }

//        launch(args);

        Platform.exit();
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
     * Quit the application.
     */
    public void close() {
        Platform.exit();
        System.exit(0);
    }

    public static VBox getVBox() {
        return vbox;
    }

    public static Stage getStage() {
        return primaryStage;
    }
}
