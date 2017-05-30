package programminglife.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.InitFXThread;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class GenomeTest {
    private Genome longGenome;
    private Genome emptyGenome;
    private GenomeGraph graph;
    private List<Segment> segments;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        graph = new GenomeGraph("test graph");
        segments = Arrays.asList(
                new Segment(graph, 1, "CAT"),
                new Segment(graph, 2, "TAG"),
                new Segment(graph, 3, "GG"),
                new Segment(graph, 4, "TATA"));
        longGenome = new Genome("test genome", segments);
        graph.addGenome(longGenome);

        emptyGenome = new Genome("empty test genome");
    }

    @After
    public void tearDown() throws Exception {
        graph.removeCache();
        graph.removeCache();
    }

    @Test
    public void genomeTest() throws Exception {
        assertEquals(12, longGenome.length());
        assertEquals("test genome", longGenome.getName());

        assertEquals(0, emptyGenome.length());
        assertEquals("empty test genome", emptyGenome.getName());
    }

    @Test(expected = NoSuchElementException.class)
    public void segmentOutOfBoundsTest() throws Exception {
        // As this coordinate does not exist, an Exception should be thrown
        longGenome.getSegment(13).getSequence();
    }

    @Test
    public void segmentsWithinBoundsTest() {
        assertEquals(1, longGenome.getStart().getIdentifier());
        assertEquals(4, longGenome.getEnd().getIdentifier());

        assertEquals("CAT", longGenome.getSegment(1).getSequence());
        assertEquals("CAT", longGenome.getSegment(3).getSequence());
        assertEquals("TAG", longGenome.getSegment(4).getSequence());
        assertEquals("TAG", longGenome.getSegment(6).getSequence());
        assertEquals("GG", longGenome.getSegment(7).getSequence());
        assertEquals("GG", longGenome.getSegment(8).getSequence());
        assertEquals("TATA", longGenome.getSegment(9).getSequence());
        assertEquals("TATA", longGenome.getSegment(12).getSequence());
    }
}
