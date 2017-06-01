package programminglife.model.drawing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import programminglife.model.GenomeGraph;
import programminglife.model.GenomeGraphTest;
import programminglife.model.Segment;

import java.io.File;

public class SubGraphTest {
    private static final String TEST_DB = "test.db";

    GenomeGraph graph;
    DrawableNode centerNode;

    private static String TEST_PATH, TEST_FAULTY_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        TEST_PATH = new File(GenomeGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
        TEST_FAULTY_PATH = new File(
                GenomeGraphTest.class.getClass().getResource("/test-faulty.gfa").toURI()
        ).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        graph = new GenomeGraph("test graph");
        centerNode = new DrawableNode(new Segment(graph, 4));
    }

    @After
    public void tearDown() throws Exception {
        graph.close();
        graph.removeCache();
    }

//    @Test
//    public void topoSort() throws Exception {
//        SubGraph sg = new SubGraph(centerNode, 5);
//        List<DrawableNode> actual = sg.topoSort();
//
//        assertEquals(8, actual.size());
//
//        Set<DrawableNode> found = new HashSet<>();
//        for (DrawableNode n : actual) {
//            // assert that all parents were already found.
//            assertTrue(found.containsAll(n.getParents()));
//            assertTrue(Collections.disjoint(found, n.getChildren()));
//            found.add(n);
//        }
//    }
}
