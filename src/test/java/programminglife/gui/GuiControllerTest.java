package programminglife.gui;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import programminglife.ProgrammingLife;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;

/**
 * This test class is there to interactively test the GUI. It is capable of clicking on certain buttons and items
 * that are present within the GUI. It is important however not to move the mouse during this process!!!
 * This is only usable if you have a USA QWERTY layout on your keyboard!!!
 */
public class GuiControllerTest extends FxRobot {
    private static Stage primaryStage;
    private ProgrammingLife pl;
    private String f;
    private static String OS = System.getProperty("os.name").toLowerCase();

    public GuiControllerTest() {
        try {
            f = new File(getClass().getResource("/test.gfa").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            f = null;
        }
    }

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

    @Test
    public void isActive() {
        assertTrue(primaryStage.isShowing());
    }

    /**
     * This test will open a file that has been added to the test resources. It uses the FXML that is also used
     * by the main class and relies heavily on that class.
     */
    @Test
    public void clickOnTest() {
        assertTrue(primaryStage.isShowing());
        clickOn("#menuFile");
        clickOn("#btnOpen");
        sleep(1, TimeUnit.SECONDS);
        for (int i = 0; i < f.length(); i++) {
            KeyCode kc = KeyCode.getKeyCode(f.toUpperCase().charAt(i) + "");
            switch (f.charAt(i)) {
                case ':':
                    press(KeyCode.SHIFT).type(KeyCode.SEMICOLON);
                    release(KeyCode.SHIFT);
                    continue;
                case ')':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT0);
                    release(KeyCode.SHIFT);
                    continue;
                case '(':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT9);
                    release(KeyCode.SHIFT);
                    continue;
                case '*':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT8);
                    release(KeyCode.SHIFT);
                    continue;
                case '&':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT7);
                    release(KeyCode.SHIFT);
                    continue;
                case '^' :
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT6);
                    release(KeyCode.SHIFT);
                    continue;
                case '%' :
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT5);
                    release(KeyCode.SHIFT);
                    continue;
                case '$' :
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT4);
                    release(KeyCode.SHIFT);
                    continue;
                case '#' :
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT3);
                    release(KeyCode.SHIFT);
                    continue;
                case '@' :
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT2);
                    release(KeyCode.SHIFT);
                    continue;
                case '!' :
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT1);
                    release(KeyCode.SHIFT);
                    continue;
                case '_' :
                    press(KeyCode.SHIFT).type(KeyCode.MINUS);
                    release(KeyCode.SHIFT);
                    continue;
                case '+' :
                    press(KeyCode.SHIFT).type(KeyCode.EQUALS);
                    release(KeyCode.SHIFT);
                    continue;
                case '"' :
                    press(KeyCode.SHIFT).type(KeyCode.QUOTE);
                    release(KeyCode.SHIFT);
                    continue;
                case '?' :
                    press(KeyCode.SHIFT).type(KeyCode.SLASH);
                    release(KeyCode.SHIFT);
                    continue;
                case '>' :
                    press(KeyCode.SHIFT).type(KeyCode.PERIOD);
                    release(KeyCode.SHIFT);
                    continue;
                case '<' :
                    press(KeyCode.SHIFT).type(KeyCode.COMMA);
                    release(KeyCode.SHIFT);
                    continue;
                case '{' :
                    press(KeyCode.SHIFT).type(KeyCode.OPEN_BRACKET);
                    release(KeyCode.SHIFT);
                    continue;
                case '}' :
                    press(KeyCode.SHIFT).type(KeyCode.CLOSE_BRACKET);
                    release(KeyCode.SHIFT);
                    continue;
                case '~' :
                    press(KeyCode.SHIFT).type(KeyCode.BACK_QUOTE);
                    release(KeyCode.SHIFT);
                    continue;
                case '`' :
                    kc = KeyCode.BACK_QUOTE;
                    break;
                case '[' :
                    kc = KeyCode.OPEN_BRACKET;
                    break;
                case ']' :
                    kc = KeyCode.CLOSE_BRACKET;
                    break;
                case '\'' :
                    kc = KeyCode.QUOTE;
                    break;
                case ',' :
                    kc = KeyCode.COMMA;
                    break;
                case '.' :
                    kc = KeyCode.PERIOD;
                    break;
                case '-' :
                    kc = KeyCode.MINUS;
                    break;
                case '=' :
                    kc = KeyCode.EQUALS;
                    break;
                case ' ' :
                    kc = KeyCode.SPACE;
                    break;
                case ';' :
                    kc = KeyCode.SEMICOLON;
                    break;
                case '/' :
                    kc = KeyCode.SLASH;
                    break;
                case '\\' :
                    kc = KeyCode.BACK_SLASH;
                    break;
            }
            type(kc);
        }

        type(KeyCode.ENTER);
        if(OS.contains("mac")) {
            sleep(500, TimeUnit.MILLISECONDS);
            type(KeyCode.ENTER);
        }

        sleep(5, TimeUnit.SECONDS);
        clickOn("#txtMaxDrawDepth").type(KeyCode.DIGIT9);
        clickOn("#txtCenterNode").type(KeyCode.DIGIT1);
        clickOn("#btnDraw");
        clickOn("#btnZoomIn");
        clickOn("#btnZoomOut");
        clickOn("#btnZoomReset");
        clickOn("#btnDrawRandom");
        sleep(1, TimeUnit.SECONDS);
    }
}
