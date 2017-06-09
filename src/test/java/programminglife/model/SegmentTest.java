package programminglife.model;

import org.junit.*;
import programminglife.utility.InitFXThread;
import programminglife.parser.Cache;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class SegmentTest {
    private static final String TEST_DB = "test.db";

    Segment node;
    GenomeGraph g;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        g = new GenomeGraph("segment test graph");
        node = new Segment(g, 1, "ATCG");
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Cache.removeDB(TEST_DB);
    }



    @Test(expected = NoSuchElementException.class)
    public void NodeId() {
        node = new Segment(g, 8);

        assertEquals(8, node.getIdentifier());
        node.getSequence();
    }

    @Test
    public void NodeIdSequence() {
        node = new Segment(g, 8, "ATCG");

        assertEquals(8, node.getIdentifier());
        assertEquals("ATCG", node.getSequence());
    }

    @Test
    public void getSequenceTest() {
        assertEquals("ATCG", node.getSequence());
    }

    @Test
    public void setSequenceTest() {
        node.setSequence("GCTA");

        assertEquals("GCTA", node.getSequence());
    }
}
