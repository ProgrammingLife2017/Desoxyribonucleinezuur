package programminglife.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import programminglife.model.Graph;
import programminglife.model.exception.UnknownTypeException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Yannick on 12/05/2017.
 */
public class GuiControllerTest extends ApplicationTest {
    private Button btnZoomIn;
    private Button btnZoomOut;
    private Button btnZoomReset;
    private Button btnDrawRandom;
    private FXMLLoader loader = new FXMLLoader();

    @Override public void start(Stage stage) throws IOException, UnknownTypeException {
        Parent p = FXMLLoader.load(getClass().getResource("/Basic_Gui.fxml"));  
        stage.setScene(new Scene(p));
        stage.show();
        stage.toFront();
        File f = new File(getClass().getResource("/test.gfa").getPath());
        Graph g = Graph.parse(f);
    }

    private <T extends Node> T find(final String query) {
        return lookup(query).queryFirst();
    }

    @Before
    public void setUp() {
        btnZoomIn = find("#btnZoomIn");
        btnZoomOut = find("#btnZoomOut");
        btnZoomReset = find("#btnZoomReset");
        btnDrawRandom = find("#btnDrawRandom");
    }

    @After
    public void tearDown() throws TimeoutException, RuntimeException {
        Platform.exit();
    }

    @Test
    public void clickOnTest() {
        clickOn(btnDrawRandom);
        WaitForAsyncUtils.waitForFxEvents(1);
        clickOn(btnZoomIn);
        WaitForAsyncUtils.waitForFxEvents(1);
        clickOn(btnZoomOut);
        WaitForAsyncUtils.waitForFxEvents(1);
        clickOn(btnZoomReset);
        WaitForAsyncUtils.waitForFxEvents(1);
    }
}