package programminglife.parser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.model.GenomeGraph;
import programminglife.model.GenomeGraphTest;
import programminglife.model.Segment;
import programminglife.model.exception.UnknownTypeException;

import java.io.File;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by toinehartman on 16/05/2017.
 */
public class GraphParserTest implements Observer {
    private static String TEST_PATH, TEST_FAULTY_PATH;

    private String linkLine, nodeLine;
    private GraphParser graphParser, faultyGraphParser;

    @BeforeClass
    public static void setUpClass() throws Exception {
        TEST_PATH = new File(GenomeGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
        TEST_FAULTY_PATH = new File(
                GenomeGraphTest.class.getClass().getResource("/test-faulty.gfa").toURI()
        ).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        graphParser = new GraphParser(new File(TEST_PATH));
        faultyGraphParser = new GraphParser(new File(TEST_FAULTY_PATH));

        linkLine = "L\t34\t+\t35\t+\t0M";
        nodeLine = "S\t6\tC\t*\tORI:Z:TKK_04_0031.fasta\tCRD:Z:TKK_04_0031.fasta\tCRDCTG:Z:7000000219691771\tCTG:Z:7000000219691771\tSTART:Z:3039";
    }

    @Test(expected = UnknownTypeException.class)
    public void faultyParseTest() throws Exception {
        faultyGraphParser.parse();
    }

    @Test
    public void parseTest() throws Exception {
        graphParser.parse();
        GenomeGraph graph = graphParser.getGraph();
        Collection<Segment> nodes = graph.getNodes();

        assertEquals(8, nodes.size());
        assertEquals(9, nodes.stream()
                .mapToInt(node -> node.getChildren().size())
                .sum());
    }

    @Test
    public void parseLinkTest() {
         graphParser.parseLink(linkLine);
    }

    @Test
    public void parseSegmentTest() {
        graphParser.parseSegment(nodeLine);

        Segment node = graphParser.getGraph().getNode(6);

        assertEquals(6, node.getIdentifier());
        assertEquals("C", node.getSequence());
        assertEquals(0, node.getParents().size());
        assertEquals(0, node.getChildren().size());
    }

    @Test
    public void runTestSuccess() {
        graphParser.addObserver(this);
        graphParser.run();
    }

    @Test(expected = UnknownTypeException.class)
    public void runTestFailure() throws Throwable {
        try {
            faultyGraphParser.addObserver(this);
            faultyGraphParser.run();
            fail();
        } catch (RuntimeException re) {
            throw re.getCause();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GraphParser) {
            if (arg instanceof GenomeGraph) {
                GenomeGraph graph = (GenomeGraph) arg;
                Segment node = graph.getNode(8);

                assertEquals(new File(TEST_PATH).getName(), graph.getId());
                assertEquals("GTC", node.getSequence());
            } else if (arg instanceof Exception) {
                throw new RuntimeException((Exception) arg);
            }
        }
    }
}
