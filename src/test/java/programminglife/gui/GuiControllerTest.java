package programminglife.gui;

import javafx.geometry.VerticalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import programminglife.ProgrammingLife;
import programminglife.parser.Cache;
import programminglife.utility.Console;

import java.io.File;
import java.io.PrintStream;
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
    private static final String TEST_DB = "test-gui.gfa.db";
    private static final String TEST_File = "/test-gui.gfa";
    private static final String TEST_DB2 = "test-gui.db";

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
        Cache.removeDB(TEST_DB);
        Cache.removeDB(TEST_File);
        FxToolkit.cleanupApplication(this.pl);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Console.setOut(new PrintStream(System.out));
        Cache.removeDB(TEST_DB);
        Cache.removeDB(TEST_File);
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
        verifyThat("#txtMaxDrawDepth", isDisabled());
        verifyThat("#txtCenterNode", isDisabled());
        verifyThat("#btnDraw", isDisabled());
        verifyThat("#btnZoomReset", isDisabled());
        verifyThat("#btnDrawRandom", isDisabled());
        verifyThat("#btnBookmark", isDisabled());

        openAndParseGFA(testFileName);
        sleep(1000);

        verifyThat("#txtMaxDrawDepth", isEnabled());
        verifyThat("#txtCenterNode", isEnabled());
        verifyThat("#btnDraw", isEnabled());
        verifyThat("#btnZoomReset", isEnabled());
        verifyThat("#btnDrawRandom", isEnabled());
        verifyThat("#btnBookmark", isEnabled());

        verifyThat("#txtMaxDrawDepth", hasText("10"));
        clickOn("#txtMaxDrawDepth").type(KeyCode.BACK_SPACE);
        clickOn("#txtMaxDrawDepth").write("4");
        verifyThat("#txtMaxDrawDepth", hasText("4"));
        verifyThat("#txtCenterNode", hasText("1"));
        clickOn("#txtCenterNode").type(KeyCode.BACK_SPACE);
        clickOn("#txtCenterNode").write("2");
        verifyThat("#txtCenterNode", hasText("2"));

        press(KeyCode.CONTROL).type(KeyCode.M).release(KeyCode.CONTROL);

        clickOn("#btnDraw");
        clickOn(350, 200);
        press(KeyCode.SHIFT).clickOn(350, 200).release(KeyCode.SHIFT);
        press(MouseButton.PRIMARY).moveBy(20, 20).release(MouseButton.PRIMARY);
        scroll(5, VerticalDirection.UP).scroll(5, VerticalDirection.DOWN);
        clickOn("#btnClipboard");
        clickOn("#btnZoomReset");
        clickOn("#btnDrawRandom");

        sleep(500);
        openWindows();
        highlight();
        bookmark();
    }

    private void highlight() {
        clickOn("#searchTab");
        clickOn("#txtGenome").write("TKK_REF.fasta");
        clickOn("#btnHighlight");
    }

    private void bookmark() {
        clickOn("#btnBookmark");
        verifyThat("#bookmarkCreator", isVisible());
        clickOn("#txtBookmarkName").write("bookmarkTest");
        clickOn("#btnOk");

        clickOn("#btnBookmark");
        clickOn("#btnOk");
        clickOn("Close");
        clickOn("#txtBookmarkName").write("bookmarkTest");
        clickOn("#btnOk");
        clickOn("Close");
        clickOn("#btnCancel");

        press(KeyCode.CONTROL).type(KeyCode.B).release(KeyCode.CONTROL);
        verifyThat("#bookmarkLoader", isVisible());
        sleep(500);

        doubleClickOn("bookmarkTest");
        sleep(2000);
        press(KeyCode.CONTROL).type(KeyCode.B).release(KeyCode.CONTROL);
        clickOn("bookmarkTest").clickOn("#btnDeleteBookmark");
        type(KeyCode.ENTER);

    }

    private void openWindows() {
        press(KeyCode.CONTROL).type(KeyCode.G).release(KeyCode.CONTROL);
        sleep(500);
        press(KeyCode.CONTROL).type(KeyCode.G).release(KeyCode.CONTROL);
        clickOn("#menuToggle").clickOn("#btnConsole");
        clickOn("#menuToggle").clickOn("#btnMiniMap");

        press(KeyCode.CONTROL).type(KeyCode.H).release(KeyCode.CONTROL);
        sleep(500);
        type(KeyCode.ENTER);
        sleep(500);

        clickOn("#menuHelp");
        clickOn("#btnAbout");
        sleep(500);
        type(KeyCode.ENTER);

        press(KeyCode.CONTROL).type(KeyCode.I).release(KeyCode.CONTROL);
        sleep(500);

        type(KeyCode.ENTER);
        sleep(500);

        clickOn("#menuHelp");
        clickOn("#btnInstructions");
        sleep(500);
        type(KeyCode.ENTER);
        sleep(500);
    }


    private void openAndParseGFA(String fileName) {
        press(KeyCode.CONTROL).type(KeyCode.O).release(KeyCode.CONTROL);
        sleep(1, TimeUnit.SECONDS);

        type(KeyCode.K, 5);
        type(KeyCode.ENTER).type(KeyCode.ENTER);

        sleep(500);
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
            KeyCode kc = KeyCode.getKeyCode(s.toUpperCase().charAt(i) + "");
            switch (s.charAt(i)) {
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
