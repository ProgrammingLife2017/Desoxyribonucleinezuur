package programminglife.gui;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import programminglife.ProgrammingLife;
import programminglife.model.DataManager;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * This test class is there to interactively test the GUI. It is capable of clicking on certain buttons and items
 * that are present within the GUI. It is important however not to move the mouse during this process!!!
 * This is only usable if you have a USA QWERTY layout on your keyboard!!!
 */
public class GuiControllerTest extends FxRobot {
    private static final String TEST_DB = "test.db";
    private static final String TEST_File = "/test.gfa";

    private static Stage primaryStage;
    private static String operatingSystem;
    private static String testFileName;

    private ProgrammingLife pl;

    public GuiControllerTest() {
        try {
            testFileName = new File(getClass().getResource(TEST_File).toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        operatingSystem = System.getProperty("os.name").toLowerCase();
        primaryStage = FxToolkit.registerPrimaryStage();
    }

    @Before
    public void setUp() throws TimeoutException {
        this.pl = (ProgrammingLife) FxToolkit.setupApplication(ProgrammingLife.class);
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, primaryStage.showingProperty());
    }

    @After
    public void tearDown() throws Exception {
        DataManager.removeDB(TEST_DB);
        DataManager.removeDB(TEST_File);
        FxToolkit.cleanupApplication(this.pl);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Platform.exit();
        DataManager.removeDB(TEST_DB);
        DataManager.removeDB(TEST_File);
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
        openAndParseFile(testFileName);

        clickOn("#txtMaxDrawDepth").type(KeyCode.BACK_SPACE);
        clickOn("#txtMaxDrawDepth").type(KeyCode.DIGIT9);
        clickOn("#txtCenterNode").type(KeyCode.BACK_SPACE);
        clickOn("#txtCenterNode").type(KeyCode.DIGIT2);
        clickOn("#btnDraw");
        assertEquals("2", ((TextField) lookup("#txtCenterNode").query()).getCharacters().toString());
        assertEquals("9", ((TextField) lookup("#txtMaxDrawDepth").query()).getCharacters().toString());
        clickOn("#btnZoomIn");
        clickOn("#btnZoomOut");
        clickOn("#btnZoomReset");
        clickOn("#btnDrawRandom");
        sleep(500);
        clickOn("#menuHelp");
        clickOn("#btnAbout");
        sleep(500);
        type(KeyCode.ENTER);
        clickOn("#menuHelp");
        clickOn("#btnInstructions");
        sleep(500);
        type(KeyCode.ENTER);
    }

    private void openAndParseFile(String fileName) {
        clickOn("#menuFile");
        clickOn("#btnOpen");
        sleep(1, TimeUnit.SECONDS);

        typeString(fileName);

        type(KeyCode.ENTER);
        if(operatingSystem.contains("mac")) {
            sleep(500, TimeUnit.MILLISECONDS);
            type(KeyCode.ENTER);
        }

        sleep(5, TimeUnit.SECONDS);
    }

    private void typeString(String s) {
        for (int i = 0; i < s.length(); i++) {
            KeyCode kc = KeyCode.getKeyCode(testFileName.toUpperCase().charAt(i) + "");
            switch (testFileName.charAt(i)) {
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
                case '^':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT6);
                    release(KeyCode.SHIFT);
                    continue;
                case '%':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT5);
                    release(KeyCode.SHIFT);
                    continue;
                case '$':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT4);
                    release(KeyCode.SHIFT);
                    continue;
                case '#':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT3);
                    release(KeyCode.SHIFT);
                    continue;
                case '@':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT2);
                    release(KeyCode.SHIFT);
                    continue;
                case '!':
                    press(KeyCode.SHIFT).type(KeyCode.DIGIT1);
                    release(KeyCode.SHIFT);
                    continue;
                case '_':
                    press(KeyCode.SHIFT).type(KeyCode.MINUS);
                    release(KeyCode.SHIFT);
                    continue;
                case '+':
                    press(KeyCode.SHIFT).type(KeyCode.EQUALS);
                    release(KeyCode.SHIFT);
                    continue;
                case '"':
                    press(KeyCode.SHIFT).type(KeyCode.QUOTE);
                    release(KeyCode.SHIFT);
                    continue;
                case '?':
                    press(KeyCode.SHIFT).type(KeyCode.SLASH);
                    release(KeyCode.SHIFT);
                    continue;
                case '>':
                    press(KeyCode.SHIFT).type(KeyCode.PERIOD);
                    release(KeyCode.SHIFT);
                    continue;
                case '<':
                    press(KeyCode.SHIFT).type(KeyCode.COMMA);
                    release(KeyCode.SHIFT);
                    continue;
                case '{':
                    press(KeyCode.SHIFT).type(KeyCode.OPEN_BRACKET);
                    release(KeyCode.SHIFT);
                    continue;
                case '}':
                    press(KeyCode.SHIFT).type(KeyCode.CLOSE_BRACKET);
                    release(KeyCode.SHIFT);
                    continue;
                case '~':
                    press(KeyCode.SHIFT).type(KeyCode.BACK_QUOTE);
                    release(KeyCode.SHIFT);
                    continue;
                case '`':
                    kc = KeyCode.BACK_QUOTE;
                    break;
                case '[':
                    kc = KeyCode.OPEN_BRACKET;
                    break;
                case ']':
                    kc = KeyCode.CLOSE_BRACKET;
                    break;
                case '\'':
                    kc = KeyCode.QUOTE;
                    break;
                case ',':
                    kc = KeyCode.COMMA;
                    break;
                case '.':
                    kc = KeyCode.PERIOD;
                    break;
                case '-':
                    kc = KeyCode.MINUS;
                    break;
                case '=':
                    kc = KeyCode.EQUALS;
                    break;
                case ' ':
                    kc = KeyCode.SPACE;
                    break;
                case ';':
                    kc = KeyCode.SEMICOLON;
                    break;
                case '/':
                    kc = KeyCode.SLASH;
                    break;
                case '\\':
                    kc = KeyCode.BACK_SLASH;
                    break;
            }
            type(kc);
        }
    }
}
