package programminglife.model;

import org.junit.*;
import programminglife.utility.InitFXThread;
import programminglife.parser.Cache;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class SegmentTest {
    private static final String TEST_DB = "test.db";

    Segment node;
    GenomeGraph g;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        g = new GenomeGraph("segment test graph");
        node = new Segment(g, 1, "ATCG");
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Cache.removeDB(TEST_DB);
    }



    @Test(expected = NoSuchElementException.class)
    public void NodeId() {
        node = new Segment(g, 8);

        assertEquals(8, node.getIdentifier());
        node.getSequence();
    }

    @Test
    public void NodeIdSequence() {
        node = new Segment(g, 8, "ATCG");

        assertEquals(8, node.getIdentifier());
        assertEquals("ATCG", node.getSequence());
    }

    @Test
    public void getSequenceTest() {
        assertEquals("ATCG", node.getSequence());
    }

    @Test
    public void setSequenceTest() {
        node.setSequence("GCTA");

        assertEquals("GCTA", node.getSequence());
    }

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
