package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.DataManager;
import programminglife.model.Segment;
import programminglife.model.XYCoordinate;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ivo on 2017-05-30.
 */
public class DrawableNodeTest {
    private static final String TEST_DB = "test.db";

    DrawableNode node;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DataManager.initialize(TEST_DB);
    }

    @Before
    public void setUp() throws Exception {
        node = new DrawableNode(new Segment(null, 1, "ATCG"));
    }

    @After
    public void tearDown() throws Exception {
        DataManager.clearDB(TEST_DB);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DataManager.removeDB(TEST_DB);
    }


    // TODO: implement other tests

    @Test
    public void locationTest() {
        node.setLocation(new XYCoordinate(1, 2));

        assertEquals(1, node.getLocation().getX());
        assertEquals(2, node.getLocation().getY());
    }

    @Test
    public void sizeTest() {
        node.setSize(new XYCoordinate(3, 4));

        assertEquals(3, node.getSize().getX());
        assertEquals(4, node.getSize().getY());
    }

    @Test
    public void centerTest() {
        node.setLocation(new XYCoordinate(2, 2));
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(4, node.getCenter().getX());
        assertEquals(3, node.getCenter().getY());
    }

    @Test
    public void rightBorderCenterTest() {
        node.setLocation(new XYCoordinate(2, 2));
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(6, node.getRightBorderCenter().getX());
        assertEquals(3, node.getRightBorderCenter().getY());
    }

    @Test
    public void leftBorderCenterTest() {
        node.setLocation(new XYCoordinate(2, 2));
        node.setSize(new XYCoordinate(4, 2));

        assertEquals(2, node.getLeftBorderCenter().getX());
        assertEquals(3, node.getLeftBorderCenter().getY());
    }

}
