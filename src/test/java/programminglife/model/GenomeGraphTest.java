package programminglife.model;

import org.junit.*;

import java.io.File;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by toinehartman on 03/05/2017.
 */
public class GenomeGraphTest {
    private static final String TEST_DB = "test.db";

    GenomeGraph graph;
    Segment node;
    String link;

    private static String TEST_PATH, TEST_FAULTY_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DataManager.initialize(TEST_DB);

        TEST_PATH = new File(GenomeGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
        TEST_FAULTY_PATH = new File(
                GenomeGraphTest.class.getClass().getResource("/test-faulty.gfa").toURI()
        ).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        graph = new GenomeGraph("test graph");
        node = new Segment(3, "ATCG", graph);

        graph.addNode(node);
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
    public void addNodeTest() throws Exception {
        Segment secondNode = new Segment(8, graph);
        graph.addNode(secondNode);

        assertEquals(2, graph.size());
        assertTrue(graph.contains(3));
        assertTrue(graph.contains(8));
    }

    @Test
    public void getNodeTest2() {
        assertTrue(graph.contains(3));
    }

    @Test(expected = NullPointerException.class)
    public void getNodeTest1() {
        graph.getChildren(121);
    }

    @Test
    public void sizeTest() {
        assertEquals(1,graph.size());
        graph.addNode(new Segment(2,"AAAAT", graph));
        assertEquals(2,graph.size());
    }

    @Test
    public void containsTest() {
        Node node2 = new Segment( 2, "ATTCTT", graph);
        graph.addNode(node2);
        assertTrue(graph.contains(node2));
        Node node3 = new Segment(37,"AAAAAAAA", graph);
        assertFalse(graph.contains(node3));
    }
}
