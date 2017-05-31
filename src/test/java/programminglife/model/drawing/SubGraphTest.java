package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.*;
import programminglife.parser.GraphParser;

import java.io.File;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

public class SubGraphTest {
    private static final String TEST_DB = "test.db";

    GenomeGraph graph;
    DrawableNode centerNode;

    private static String TEST_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DataManager.initialize(TEST_DB);

        TEST_PATH = new File(GenomeGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        File testFile = new File(TEST_PATH);
        GraphParser graphParser = new GraphParser(testFile);
        graphParser.parse();
        graph = graphParser.getGraph();

        centerNode = new DrawableNode(new Segment(graph, 4));
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
    public void testConstructorOnlyCenterNode() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 0);
        Set<DrawableNode> nodes = sg.getNodes();
        assertEquals(1, nodes.size());
        assertTrue(nodes.contains(centerNode));
    }

    @Test
    public void testConstructorRadius1() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 1);
        Set<DrawableNode> nodes = sg.getNodes();
        assertEquals(3, nodes.size());
        assertTrue(nodes.contains(centerNode));
        assertTrue(nodes.containsAll(centerNode.getChildren()));
        assertTrue(nodes.containsAll(centerNode.getParents()));
    }

    @Test
    public void testConstructorRadius4() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 4);

        Set<DrawableNode> expected = new HashSet<>();
        for (Integer id : new int[] {1, 2, 4, 5, 6, 7, 8}) {
            expected.add(new DrawableNode(new Segment(graph, id)));
        }

        Set<DrawableNode> actual = sg.getNodes();
        assertEquals(7, actual.size());
        assertEquals(expected, actual);
    }



    @Test
    public void topoSortTest() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 5);
        List<DrawableNode> actual = sg.topoSort();

        assertEquals(8, actual.size());

        Set<DrawableNode> found = new HashSet<>();
        for (DrawableNode n : actual) {
            // assert that all parents were already found.
            assertTrue(found.containsAll(n.getParents())); // All parents of this node were already found
            assertTrue(Collections.disjoint(found, n.getChildren())); // none of the children of this node were found
            found.add(n);
        }
    }

    @Test
    public void atLocationNodeTest() {
        SubGraph sg = new SubGraph(centerNode, 0); // only include centerNode
        sg.layout();
        double x = centerNode.getX();
        double y = centerNode.getY();
        double width = centerNode.getWidth();
        double height = centerNode.getHeight();
        // Note: use == to make sure it is the exact same object.
        // in order: assert that all corners, the center, and some other points inside the node can be found.
        assertTrue(centerNode == sg.atLocation(x, y));
        assertTrue(centerNode == sg.atLocation(x + width, y));
        assertTrue(centerNode == sg.atLocation(x, y + height));
        assertTrue(centerNode == sg.atLocation(x + width, y + height));
        assertTrue(centerNode == sg.atLocation(x + width / 2, y + height / 2));
        assertTrue(centerNode == sg.atLocation(x + width / 3, y + height / 6));
        assertTrue(centerNode == sg.atLocation(x + width / 4, y + height));

        // assert that places outside the node do not return the node (return null)
        assertNull(sg.atLocation(x - 1, y));
        assertNull(sg.atLocation(x, y - 1));
        assertNull(sg.atLocation(x - 1, y - 1));
        assertNull(sg.atLocation(x + width + 1, y));
        assertNull(sg.atLocation(x, y + height + 1));
        assertNull(sg.atLocation(x + width + 1, y + height + 1));
    }

    @Test
    public void atLocationEdgeTest() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 1); // only include node 2, 4 and 5
        sg.layout();
        Collection<DrawableEdge> parentsEdges = centerNode.getParentEdges();
        assumeThat(parentsEdges.size(), is(1));
        List<DrawableEdge> list = new ArrayList<>(parentsEdges);
        DrawableEdge parentEdge = list.get(0);
        DrawableNode parent = parentEdge.getStart();
        XYCoordinate parentPoint = parent.getLeftBorderCenter();
        XYCoordinate childPoint = centerNode.getRightBorderCenter();

        // check that some points on the line between childPoint and ParentPoint are on the edge
        assertTrue(parentEdge == sg.atLocation(childPoint));
        assertTrue(parentEdge == sg.atLocation(parentPoint));
        assertTrue(parentEdge == sg.atLocation(
                (parentPoint.getX() + childPoint.getX()) / 2,
                (parentPoint.getY() + childPoint.getY()) / 2
        ));
    }
}
