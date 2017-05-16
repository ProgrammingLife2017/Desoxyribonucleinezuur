package programminglife.gui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import programminglife.ProgrammingLife;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This test class is there to interactively test the GUI. It is capable of clicking on certain buttons and items
 * that are present within the GUI. It is important however not to move the mouse during this process!!!
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
    public void tearDown() {
    }

    @AfterClass
    public static void tearDownClass() {

    }

    /**
     * This test will open a file that has been added to the test resources. It uses the FXML that is also used
     * by the main class and relies heavily on that class.
     */
    @Test
    public void clickOnTest() {
        clickOn("#menuFile");
        clickOn("#btnOpen");
        sleep(1, TimeUnit.SECONDS);
        for (int i = 0; i < f.length(); i++) {
            char a = f.charAt(i);
            KeyCode kc = KeyCode.getKeyCode(f.toUpperCase().charAt(i) + "");
            if (':' == a) {
                press(KeyCode.SHIFT).type(KeyCode.SEMICOLON);
                release(KeyCode.SHIFT);
                continue;
            }
            else if ('%' == a) {
                press(KeyCode.SHIFT).type(KeyCode.DIGIT2);
                release(KeyCode.SHIFT);
                continue;
            }
            else if ('.' == a) {
                kc = KeyCode.PERIOD;
            }
            else if ('-' == a) {
                kc = KeyCode.MINUS;
            }
            else if ('_' == a) {
                press(KeyCode.SHIFT).type(KeyCode.MINUS);
                release(KeyCode.SHIFT);
                continue;
            }
            else if ('(' == a) {
                press(KeyCode.SHIFT).type(KeyCode.DIGIT9);
                release(KeyCode.SHIFT);
                continue;
            }
            else if (')' == a) {
                press(KeyCode.SHIFT).type(KeyCode.DIGIT0);
                release(KeyCode.SHIFT);
                continue;
            }
            else if (' ' == a) {
                kc = KeyCode.SPACE;
            }
            else if (kc == null) {
                kc = KeyCode.BACK_SLASH;
            }
            type(kc);
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