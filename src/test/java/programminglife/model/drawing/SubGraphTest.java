package programminglife.model.drawing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.gui.InitFXThread;
import programminglife.model.GenomeGraph;
import programminglife.parser.GraphParser;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubGraphTest {
    private GenomeGraph graph;
    private DrawableSegment centerNode;

    private static String TEST_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
        TEST_PATH = new File(SubGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        File testFile = new File(TEST_PATH);
        GraphParser graphParser = new GraphParser(testFile);
        graphParser.parse();
        graph = graphParser.getGraph();

        centerNode = new DrawableSegment(graph, 4, 1);
    }

    @After
    public void tearDown() throws Exception {
        graph.removeCache();
    }

    @Test
    public void testConstructor() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 0, false);
        Set<DrawableNode> nodes = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(8, nodes.size());
        assertTrue(nodes.contains(centerNode));
    }

    @Test
    public void topoSortTest() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 5, false);
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
