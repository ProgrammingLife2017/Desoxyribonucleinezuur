package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.DataManager;
import programminglife.model.GenomeGraph;
import programminglife.model.GenomeGraphTest;
import programminglife.model.Segment;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubGraphTest {
    private static final String TEST_DB = "test.db";

    GenomeGraph graph;
    DrawableNode centerNode;

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
        centerNode = new DrawableNode(new Segment(4));
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
    public void topoSort() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 5);
        List<DrawableNode> actual = sg.topoSort();

        assertEquals(8, actual.size());

        Set<DrawableNode> found = new HashSet<>();
        for (DrawableNode n : actual) {
            // assert that all parents were already found.
            assertTrue(found.containsAll(n.getParents()));
            assertTrue(Collections.disjoint(found, n.getChildren()));
            found.add(n);
        }
    }
}
