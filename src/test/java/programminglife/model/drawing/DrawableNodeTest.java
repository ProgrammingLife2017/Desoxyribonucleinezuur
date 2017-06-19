package programminglife.model.drawing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
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
        g.setGenomes(1, new int[0]);
        node = new DrawableSegment(g, 1);
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }


    // TODO: implement other tests

    @Test
    public void sizeTest() {
        node.setSize(3, 4);

        assertEquals(3, node.getWidth(), 0.0);
        assertEquals(4, node.getHeight(), 0.0);
    }

    @Test
    public void rightBorderCenterTest() {
        node.setLocation(2, 2);
        node.setSize(4, 2);

        assertEquals(6, node.getRightBorderCenter().getX(), 0.0);
        assertEquals(3, node.getRightBorderCenter().getY(), 0.0);
    }

    @Test
    public void leftBorderCenterTest() {
        node.setLocation(2, 2);
        node.setSize(4, 2);

        assertEquals(2, node.getLeftBorderCenter().getX(), 0.0);
        assertEquals(3, node.getLeftBorderCenter().getY(), 0.0);
    }

}
