package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
import programminglife.parser.Cache;
import programminglife.utility.InitFXThread;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the DrawableNode.
 */
public class DrawableNodeTest {
    private static final String GRAPH_NAME = "testGraph";

    DrawableSegment node;
    GenomeGraph g;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        g = new GenomeGraph(GRAPH_NAME);
        g.setSequence(1, "ATCG");
        g.replaceNode(1);
        node = new DrawableSegment(g, 1);
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }


    // TODO: implement other tests

    @Test
    public void sizeTest() {
        node.setSize(new XYCoordinate(3, 4));

        assertEquals(3, node.getSize().getX(), 0.0);
        assertEquals(4, node.getSize().getY(), 0.0);
    }

    @Test
    public void rightBorderCenterTest() {
        node.setLocation(2, 2);
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(6, node.getRightBorderCenter().getX(), 0.0);
        assertEquals(3, node.getRightBorderCenter().getY(), 0.0);
    }

    @Test
    public void leftBorderCenterTest() {
        node.setLocation(2, 2);
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(2, node.getLeftBorderCenter().getX(), 0.0);
        assertEquals(3, node.getLeftBorderCenter().getY(), 0.0);
    }

}
