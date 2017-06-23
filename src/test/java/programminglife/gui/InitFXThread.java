package programminglife.gui;

import javafx.embed.swing.JFXPanel;
import programminglife.utility.Console;

import javax.swing.*;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This class is needed to setup the JavaFX environment that the other classes make use of.
 * It sets the console to be printed in a correct manner since our own console isn't booted up.
 */
public final class InitFXThread {

    /**
     * Constructor.
     */
    private InitFXThread() {

    }

    /**
     * Sets up the output and JFXPanel.
     *
     * @throws Exception when creating the panel.
     */
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
}
