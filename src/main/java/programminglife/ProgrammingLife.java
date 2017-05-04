package programminglife;

import programminglife.model.Graph;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by marti_000 on 25-4-2017.
 */

public final class ProgrammingLife {

    private static final String DATA_FOLDER = "data/";
    private static final String TEST_DATA = DATA_FOLDER + "test/test.gfa";
    private static final String TB_DATA = DATA_FOLDER + "real/TB10.gfa";
    private static final String HUMAN_DATA = DATA_FOLDER + "real/chr19.hg38.w115.gfa";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("m:ss.SSS", Locale.getDefault());

    private ProgrammingLife() {
    }

    public static void main(String[] args) {
        Graph g;
        String graphFile = TEST_DATA;
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (args.length > 0) {
            graphFile = args[0];
        }

        try {
            long startTime = System.nanoTime();
            g = Graph.parse(graphFile);
            System.out.println(String.format("Parsing took %s", DATE_FORMAT.format((System.nanoTime() - startTime) / 1000000)));
        } catch (FileNotFoundException e) {
            System.err.println(String.format("File not found (%s)", graphFile));
        }
    }
}
