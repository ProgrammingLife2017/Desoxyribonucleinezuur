package programminglife;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import programminglife.gui.GuiController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class ProgrammingLife extends Application {

    private static Stage primaryStage;
    private static VBox vbox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Programming Life");
        primaryStage.minWidthProperty().set(1000);
        primaryStage.minHeightProperty().set(900);

        vbox = FXMLLoader.load(getClass().getResource("gui/Basic_Gui.fxml"));
        primaryStage.setScene(new Scene(vbox, 1000, 900));
        primaryStage.setOnCloseRequest(e -> close());
        primaryStage.sizeToScene();
        primaryStage.show();

    }

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
