package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.GenomeGraph;
import programminglife.model.Segment;
import programminglife.model.XYCoordinate;
import programminglife.parser.Cache;
import programminglife.utility.InitFXThread;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the DrawableNode.
 */
public class DrawableNodeTest {
    private static final String GRAPH_NAME = "testGraph";

    DrawableNode node;
    GenomeGraph g;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        Cache.removeDB(GRAPH_NAME);
        g = new GenomeGraph(GRAPH_NAME);
        Segment segment = new Segment(g, 1, "ATCG");
        g.addNode(segment.getIdentifier());
        node = new DrawableNode(segment);
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Cache.removeDB(GRAPH_NAME);
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
        node.setLocation(new XYCoordinate(2, 2));
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(6, node.getRightBorderCenter().getX(), 0.0);
        assertEquals(3, node.getRightBorderCenter().getY(), 0.0);
    }

    @Test
    public void leftBorderCenterTest() {
        node.setLocation(new XYCoordinate(2, 2));
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(2, node.getLeftBorderCenter().getX(), 0.0);
        assertEquals(3, node.getLeftBorderCenter().getY(), 0.0);
    }

}
