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
    Segment node;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        graph = new GenomeGraph("test graph");
        node = new Segment(graph, 3, "ATCG");

        graph.addNode(node.getIdentifier());
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
        Segment secondNode = new Segment(graph, 8);
        graph.addNode(secondNode.getIdentifier());

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
        graph.getGenome("nonexistent!");
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
        Node node2 = new Segment(graph, 2, "ATTCTT");
        graph.addNode(node2.getIdentifier());
        assertTrue(graph.contains(node2));
        Node node3 = new Segment(graph, 37,"AAAAAAAA");
        assertFalse(graph.contains(node3));
    }

    @Test(expected = NodeExistsException.class)
    public void addExistingNodeTest() {
        graph.addNode(node.getIdentifier());
    }

    @Test
    public void replaceExistingNodeTest() {
        assertEquals("ATCG", graph.getSequence(3));
        node.setSequence("AAAA");
        graph.replaceNode(node.getIdentifier());
        assertEquals("AAAA", graph.getSequence(3));
    }
}
