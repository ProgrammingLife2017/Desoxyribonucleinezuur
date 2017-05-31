package programminglife.model;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SegmentTest {
    private static final String TEST_DB = "test.db";

    Segment node;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DataManager.initialize(TEST_DB);
    }

    @Before
    public void setUp() throws Exception {
        node = new Segment(null, 1, "ATCG");
    }

    @After
    public void tearDown() throws Exception {
        DataManager.clearDB(TEST_DB);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DataManager.removeDB(TEST_DB);
    }



    @Test
    public void NodeId() {
        node = new Segment(null, 8);

        assertEquals(8, node.getIdentifier());
        assertEquals(null, node.getSequence());
    }

    @Test
    public void NodeIdSequence() {
        node = new Segment(null, 8, "ATCG");

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
