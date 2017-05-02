package programminglife;

import programminglife.model.Graph;

import java.io.FileNotFoundException;

/**
 * Created by marti_000 on 25-4-2017.
 */

public class ProgrammingLife {

    private static final String DATA_FOLDER = "data/";
    private static final String TEST_DATA = DATA_FOLDER + "test/test.gfa";
    private static final String TB_DATA = DATA_FOLDER + "real/TB10.gfa";
    private static final String HUMAN_DATA = DATA_FOLDER + "real/chr19.hg38.w115.gfa";

    public static void main(String[] args) {
        Graph g;
        String graphFile = TEST_DATA;

        if (args.length > 0)
            graphFile = args[0];

        try {
            g = Graph.parse(graphFile, true);
        } catch (FileNotFoundException e) {
            throw new Error(String.format("File not found (%s)", graphFile));
        }
    }
}
