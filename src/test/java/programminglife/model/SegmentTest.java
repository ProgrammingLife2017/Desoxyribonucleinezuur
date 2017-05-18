package programminglife.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SegmentTest {
    Segment node;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DataManager.initialize("test");
    }

    @Before
    public void setUp() throws Exception {
        node = new Segment(1, "ATCG");
    }

    @Test
    public void NodeId() {
        node = new Segment(8);

        assertEquals(8, node.getIdentifier());
        assertEquals("", node.getSequence());
    }

    @Test
    public void NodeIdSequence() {
        node = new Segment(8, "ATCG");

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
