package programminglife.utility;

import javafx.application.Platform;

import java.io.PrintStream;

/**
 * Separate class that handles the consoles of the application.
 */
public final class Console {

    /**
     * Constructor for the console.
     */
    private Console() {

    }

    private static PrintStream out = null;

    /**
     * Prints the arguments given in the console.
     * @param f String to be printed.
     * @param args Given with the string.
     */
    public static void println(String f, Object... args) {
        print(f + "%n", args);
    }

    /**
     * Prints the arguments given in the console.
     * @param f String to be printed.
     * @param args Given with the string.
     */
    public static void print(String f, Object... args) {
        Platform.runLater(() -> out.printf(f, args));
    }

    public static void setOut(PrintStream ps) {
        out = ps;
    }
}
