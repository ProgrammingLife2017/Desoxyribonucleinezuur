package programminglife.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link XYCoordinate} model.
 */
public class XYCoordinateTest {
    private XYCoordinate coord1;
    private XYCoordinate coord2;
    private static double DELTA = 0.000001;

    @Before
    public void setUp() {
        this.coord1 = new XYCoordinate(8, 54);
        this.coord2 = new XYCoordinate(1, -1);
    }

    @Test
    public void getterTest() {
        assertEquals(8, coord1.getX(), DELTA);
        assertEquals(54, coord1.getY(), DELTA);
    }

    @Test
    public void setterTest() {
        coord1.setX(2);
        coord1.setY(-4);

        assertEquals(2, coord1.getX(), DELTA);
        assertEquals(-4, coord1.getY(), DELTA);
    }

    @Test
    public void addCoordTest() {
        XYCoordinate sum = coord1.add(coord2);

        assertEquals(9, sum.getX(), DELTA);
        assertEquals(53, sum.getY(), DELTA);
    }

    @Test
    public void addIntsTest() {
        XYCoordinate sum = coord1.add(1, -1);

        assertEquals(9, sum.getX(), DELTA);
        assertEquals(53, sum.getY(), DELTA);
    }

    @Test
    public void multiplyTest() {
        XYCoordinate multiplied = coord1.multiply(0.5);

        assertEquals(4, multiplied.getX(), DELTA);
        assertEquals(27, multiplied.getY(), DELTA);
    }

    @Test
    public void multiplyRoundedTest() {
        XYCoordinate multipliedRounded = coord1.multiply(0.1);

        assertEquals(0, multipliedRounded.getX(), DELTA);
        assertEquals(5, multipliedRounded.getY(), DELTA);
    }

    @Test
    public void toStringTest() {
        assertEquals("(8, 54)", coord1.toString());
    }
}
