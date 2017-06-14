package programminglife.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.model.GenomeGraph;
import programminglife.model.exception.UnknownTypeException;
import programminglife.utility.InitFXThread;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.*;

/**
 * The class that handles the tests for the parser.
 */
public class GraphParserTest implements Observer {
    private static String TEST_PATH, TEST_FAULTY_PATH;

    private String linkLine, nodeLine;
    private GraphParser graphParser, faultyGraphParser;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
        TEST_PATH = new File(GraphParserTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
        TEST_FAULTY_PATH = new File(
                GraphParserTest.class.getClass().getResource("/test-faulty.gfa").toURI()
        ).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        File testFile = new File(TEST_PATH);
        graphParser = new GraphParser(testFile);
        graphParser.getGraph().addGenome("TKK_04_0031.fasta");

        File faultyTestFile = new File(TEST_FAULTY_PATH);
        faultyGraphParser = new GraphParser(faultyTestFile);

        linkLine = "L\t34\t+\t35\t+\t0M";
        nodeLine = "S\t6\tC\t*\tORI:Z:TKK_04_0031.fasta\tCRD:Z:TKK_04_0031.fasta\tCRDCTG:Z:7000000219691771\tCTG:Z:7000000219691771\tSTART:Z:3039";
    }

    @After
    public void tearDown() throws Exception {
        graphParser.getGraph().removeCache();
        faultyGraphParser.getGraph().removeCache();
    }

    @Test(expected = UnknownTypeException.class)
    public void faultyParseTest() throws Exception {
        faultyGraphParser.parse();
    }

    @Test
    public void parseTest() throws Exception {
        GenomeGraph g = graphParser.getGraph();

        graphParser.parse();
        for (int i = 1; i < 9; i++)
            assertTrue(g.contains(i));
        assertEquals(8, g.size());
    }

    @Test
    public void parseFromCache() throws Exception {
        GenomeGraph g1 = graphParser.getGraph();

        graphParser.parse();
        for (int i = 1; i < 9; i++)
            assertTrue(g1.contains(i));
        assertEquals(8, g1.size());

        g1.close();

        GraphParser p = new GraphParser(new File(TEST_PATH));
        GenomeGraph g2 = p.getGraph();
        for (int i = 1; i < 9; i++)
            assertTrue(g2.contains(i));
        assertEquals(8, g2.size());

        g2.close();

        // To ensure no problems occur on #tearDown()
        graphParser = new GraphParser(new File(TEST_PATH));
    }

    @Test
    public void parseLinkTest() throws UnknownTypeException {
         graphParser.parseLink(linkLine);
    }

    @Test
    public void parseSegmentTest() throws UnknownTypeException {
        graphParser.parseSegment(nodeLine);
        GenomeGraph g = graphParser.getGraph();

        assertTrue(g.contains(6));
        assertEquals("C", g.getSequence(6));
        assertEquals(0, g.getParentIDs(6).length);
        assertEquals(0, g.getChildIDs(6).length);
        assertEquals(1, g.getGenomes(6).length);
        assertEquals(0, g.getGenomes(6)[0]);
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

                assertEquals(new File(TEST_PATH).getName(), graph.getID());
                assertEquals("GTC", graph.getSequence(8));
            } else if (arg instanceof Exception) {
                throw new RuntimeException((Exception) arg);
            }
        }
    }

    @Test
    public void parseSegmentNotStartingS() {
        try {
            graphParser.parseSegment("Fiets");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("Line (Fiets) is not a segment", e.getMessage());
        }
    }

    @Test
    public void parseLinkNotStartingL() throws UnknownTypeException {
        try {
            graphParser.parseLink("Fiets");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("Line (Fiets) is not a link", e.getMessage());
        }
    }

    @Test
    public void parseHeaderNotStartingH() throws UnknownTypeException {
        try {
            graphParser.parseHeader("Fiets");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("Line (Fiets) is not a header", e.getMessage());
        }
    }

    @Test
    public void parseMultipleHeaders() throws Exception {
        String[] lines = new String[] {"H\tVN:Z:1.0\n", "H\tBUILD:Z:VCF2GRAPH\n", "H\tORI:Z:SZAXPI008746-45"};
        for (String line : lines) {
            graphParser.parseHeader(line);
        }
        assertTrue(graphParser.getGraph().getGenomeNames().contains("SZAXPI008746-45"));
    }

    @Test
    public void parseSegmentIDNotNumber() throws UnknownTypeException {
        try {
            graphParser.parseSegment("S\tID\tT\t*\tORI:Z:TKK-01-0066.fasta;TKK_REF.fasta");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("The segment ID (ID) should be a number", e.getMessage());
        }
    }

    @Test
    public void parseSegmentNotEnoughProperties() throws UnknownTypeException {
        try {
            graphParser.parseSegment("S\t8\tT\t*");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("Segment has less than 5 properties (4)", e.getMessage());
        }
    }

    @Test
    public void parseSegmentNoGenomes() throws UnknownTypeException {
        try {
            graphParser.parseSegment("S\t8\tT\t*\tGenomes:A;B;C");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("Segment has no genomes", e.getMessage());
        }
    }

    @Test
    public void parseSegmentNonexistingGenome() throws Exception {
        try {
            graphParser.parseSegment("S\t8\tT\t*\tORI:Z:TKK_NOPE");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("Unknown genome name in segment", e.getMessage());
        }
    }

    @Test
    public void parseLinkFirstIDNotNumber() throws UnknownTypeException {
        try {
            graphParser.parseLink("L\tX\t+\t2\t+\t0M");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("The source ID (X) should be a number", e.getMessage());
        }
    }

    @Test
    public void parseLinkSecondIDNotNumber() throws UnknownTypeException {
        try {
            graphParser.parseLink("L\t1\t+\tY\t+\t0M");
            fail("This should not be parsed");
        } catch (Exception e) {
            assertEquals(UnknownTypeException.class, e.getClass());
            assertEquals("The destination ID (Y) should be a number", e.getMessage());
        }
    }
}
