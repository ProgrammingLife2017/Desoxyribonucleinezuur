package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.*;
import programminglife.parser.Cache;
import programminglife.parser.GraphParser;
import programminglife.utility.InitFXThread;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubGraphTest {
    GenomeGraph graph;
    DrawableSegment centerNode;

    private static String TEST_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();

        TEST_PATH = GenomeGraphTest.class.getResource("/test.gfa").getPath();
    }

    @Before
    public void setUp() throws Exception {
        File testFile = new File(TEST_PATH);
        GraphParser graphParser = new GraphParser(testFile);
        graphParser.parse();
        graph = graphParser.getGraph();

        centerNode = new DrawableSegment(graph, 4);
    }

    @After
    public void tearDown() throws Exception {
        graph.removeCache();
    }

    @Test
    public void testConstructorOnlyCenterNode() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 0);
        Set<DrawableNode> nodes = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(1, nodes.size());
        assertTrue(nodes.contains(centerNode));
    }

    @Test
    public void testConstructorRadius1() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 1);
        Set<DrawableNode> nodes = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(4, nodes.size());
        assertTrue(nodes.contains(centerNode));
        assertTrue(nodes.containsAll(sg.getChildren(centerNode)));
        assertTrue(nodes.containsAll(sg.getParents(centerNode)));
    }

    @Test
    public void testConstructorRadius4() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 4);

        Set<DrawableNode> expected = new HashSet<>();
        for (Integer id : new int[] {1, 2, 3, 4, 5, 6, 7, 8}) {
            expected.add(new DrawableSegment(graph, id));
        }

        Set<DrawableNode> actual = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(8, actual.size());
        assertEquals(expected, actual);
    }



    @Test
    public void topoSortTest() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 5);
        List<DrawableNode> actual = sg.topoSort();

        Set<DrawableNode> graphNodes = new LinkedHashSet<>(sg.getNodes().values());

        assertEquals(graphNodes.size(), actual.size());

        Set<DrawableNode> found = new HashSet<>();
        for (DrawableNode n : actual) {
            Collection<DrawableNode> parents = sg.getParents(n);
            parents.retainAll(graphNodes);

            // assert that all parents that are also in the SubGraph were already found.
            assertTrue(found.containsAll(parents)); // All parents of this node were already found
            assertTrue(Collections.disjoint(found, sg.getChildren(n))); // none of the children of this node were found
            found.add(n);
        }
    }
}
