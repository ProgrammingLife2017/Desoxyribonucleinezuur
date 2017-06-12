package programminglife.model;

import org.junit.*;
import programminglife.model.exception.NodeExistsException;
import programminglife.parser.Cache;
import programminglife.utility.InitFXThread;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * The class that handles the tests for the genome graph.
 */
public class GenomeGraphTest {


    private static final String TEST_DB = "test.db";
    GenomeGraph graph;
    int nodeID;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        graph = new GenomeGraph("test graph");
        nodeID = 3;
        graph.setSequence(nodeID, "ATCG");
        graph.addNode(nodeID);
    }

    @After
    public void tearDown() throws Exception {
        graph.removeCache();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Cache.removeDB(TEST_DB);
    }

    @Test
    public void addNodeTest() throws Exception {
        graph.setSequence(8, "A");
        graph.addNode(8);

        assertEquals(2, graph.size());
        assertTrue(graph.contains(3));
        assertTrue(graph.contains(8));
    }

    @Test
    public void getNodeTest2() {
        assertTrue(graph.contains(3));
    }

    @Test
    public void getNodeTest1() {
        assertNull(graph.getChildIDs(121));
    }

    @Test(expected = NoSuchElementException.class)
    public void getNonExistingGenomeTest() {
        graph.getNodeIDs(graph.getGenomeID("nonexistent"));
    }

    @Test
    public void sizeTest() {
        assertEquals(1,graph.size());
        graph.addNode(2);
        graph.setSequence(2, "A");
        assertEquals(2,graph.size());
    }

    @Test
    public void containsTest() {
        int node2ID = 2;
        graph.setSequence(2, "ATTCTT");
        graph.addNode(node2ID);
        assertTrue(graph.contains(node2ID));

        int node3ID = 37;
        graph.setSequence(37, "AAAAAAAA");
        assertFalse(graph.contains(node3ID));
    }

    @Test(expected = NodeExistsException.class)
    public void addExistingNodeTest() {
        graph.addNode(nodeID);
    }

    @Test
    public void replaceExistingNodeTest() {
        assertEquals("ATCG", graph.getSequence(3));
        graph.setSequence(nodeID, "AAAA");
        graph.replaceNode(nodeID);
        assertEquals("AAAA", graph.getSequence(3));
    }
}
