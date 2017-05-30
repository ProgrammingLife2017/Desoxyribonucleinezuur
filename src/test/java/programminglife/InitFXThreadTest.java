package programminglife;

import javafx.embed.swing.JFXPanel;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.utility.Console;

import javax.swing.*;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This class is needed to setup the JavaFX environment that the other classes make use of.
 * It sets the console to be printed in a correct manner since our own console isn't booted up.
 */
public class InitFXThreadTest {

    @BeforeClass
    public static void setupClass() throws Exception {
        Console.setOut(new PrintStream(System.out));
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            Console.setOut(new PrintStream(System.out));
            latch.countDown();
        });

        if (!latch.await(5L, TimeUnit.SECONDS)) {
            throw new ExceptionInInitializerError();
        }
    }

    @Test
    public void nothingTest() {
        //Can be left empty since it ony needed to be seen as an actual test class.
    }
}
