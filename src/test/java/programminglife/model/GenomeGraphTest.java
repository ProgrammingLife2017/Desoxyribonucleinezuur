package programminglife.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by toinehartman on 03/05/2017.
 */
public class GenomeGraphTest {
    GenomeGraph graph;
    Segment node;
    String link;

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
        node = new Segment(3, "ATCG");

        graph.addNode(node);
    }

    @Test
    public void addNodeTest() throws Exception {
        Segment secondNode = new Segment(8);
        graph.addNode(secondNode);

        assertEquals(2, graph.size());
        assertTrue(graph.contains(3));
        assertTrue(graph.contains(8));
    }

    @Test
    public void getNodeTest2() {
        assertTrue(graph.contains(3));
    }

    @Test(expected = NoSuchElementException.class)
    public void getNodeTest1() {
        graph.getChildren(121);
    }
}
