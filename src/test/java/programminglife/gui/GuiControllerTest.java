package programminglife.gui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import programminglife.ProgrammingLife;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Yannick on 12/05/2017.
 */
public class GuiControllerTest extends FxRobot {
    public static Stage primaryStage;
    private ProgrammingLife pl;
    private final String f = new File(getClass().getResource("/test.gfa").getPath()).getAbsolutePath();

    @BeforeClass
    public static void setUpClass() {
        try {
            primaryStage = FxToolkit.registerPrimaryStage();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        try {
            this.pl = (ProgrammingLife) FxToolkit.setupApplication(ProgrammingLife.class);
            WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, primaryStage.showingProperty());
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws TimeoutException, RuntimeException {
        Platform.exit();
    }

    @Test
    public void clickOnTest() {
        clickOn("#menuFile");
        clickOn("#btnOpen");
        sleep(1, TimeUnit.SECONDS);
        for (int i = 0; i < f.length(); i++) {
            KeyCode kc = KeyCode.getKeyCode(f.toUpperCase().charAt(i) + "");
            if (":".equals(f.charAt(i) + "")) {
                kc = KeyCode.SEMICOLON;
                press(KeyCode.SHIFT).type(KeyCode.SEMICOLON);
                release(KeyCode.SHIFT);
            }
            if (".".equals(f.charAt(i) + "")) {
                kc = KeyCode.PERIOD;
            }
            if ("-".equals(f.charAt(i) + "")) {
                kc = KeyCode.MINUS;
            }
            if ("_".equals(f.charAt(i) + "")) {
                kc = KeyCode.UNDERSCORE;
            }
            if (kc == null) {
                kc = KeyCode.BACK_SLASH;
            }
            if (kc != KeyCode.SEMICOLON) {
                type(kc);
            }
        }
        sleep(1, TimeUnit.SECONDS);
        type(KeyCode.ENTER);
        sleep(10, TimeUnit.SECONDS);
        clickOn("#txtMaxDrawDepth").type(KeyCode.DIGIT9);
        sleep(1, TimeUnit.SECONDS);
        clickOn("#txtCenterNode").type(KeyCode.DIGIT1);
        sleep(1, TimeUnit.SECONDS);
        clickOn("#btnDraw");
        sleep(1, TimeUnit.SECONDS);
        clickOn("#btnZoomIn");
        sleep(1, TimeUnit.SECONDS);
        clickOn("#btnZoomOut");
        sleep(1, TimeUnit.SECONDS);
        clickOn("#btnZoomReset");
        sleep(1, TimeUnit.SECONDS);
        clickOn("#btnDrawRandom");
        sleep(1, TimeUnit.SECONDS);
        clickOn("#menuFile");
        sleep(1, TimeUnit.SECONDS);
        clickOn("#btnQuit");
        sleep(1, TimeUnit.SECONDS);
        type(KeyCode.ENTER);
        sleep(1, TimeUnit.SECONDS);
    }
}