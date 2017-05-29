package programminglife.utility;

import javafx.application.Platform;
import jp.uphy.javafx.console.ConsoleView;

import java.io.PrintStream;

/**
 * Created by Yannick on 29/05/2017.
 */
public class Console {

    private static PrintStream out = null;

    /**
     * Prints the arguments given in the console
     * @param f String to be printed.
     * @param args Given with the string.
     */
    public static void println(String f, Object... args) {
        print(f + "%n", args);
    }

    /**
     * Prints the arguments given in the console
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
