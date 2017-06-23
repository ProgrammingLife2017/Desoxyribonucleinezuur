package programminglife.model;

import javafx.beans.property.SimpleStringProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the Bookmark.
 */
public class BookmarkTest {
    private Bookmark b1;
    private Bookmark b2;

    @Before
    public void setUp() {
        b1 = new Bookmark("graph", "path", 1, 3, "TestName", "testDescription");
        b2 = new Bookmark("graph", "path", 1, 3, "TestName", "testDescription");

    }

    @Test
    public void equalsTest() {
        assertTrue(b1.equals(b2));
    }

    @Test
    public void notEqualsGraphNameTest() {
        b2.setGraphName("OtherName");
        assertFalse(b1.equals(b2));
    }

    @Test
    public void notEqualsRadiusTest() {
        b2.setRadius(37);
        assertFalse(b1.equals(b2));
    }

    @Test
    public void notEqualsNodeIDTest() {
        b2.setNodeID(999);
        assertFalse(b1.equals(b2));
    }

    @Test
    public void notEqualsBookmarkName() {
        b2.setBookmarkName("A different Name");
        assertFalse(b1.equals(b2));
    }

    @Test
    public void notEqualsDescription() {
        b2.setDescription("New description");
        assertFalse(b1.equals(b2));
    }

    @Test
    public void notBookmarkEquals() {
        assertFalse(b1.equals("Fiets"));
    }

    @Test
    public void toStringTest() {
        assertEquals("{file: graph, name: TestName, description: testDescription, ID: 1, radius: 3}", b1.toString());
    }

    @Test
    public void getNamePropertyTest() {
        SimpleStringProperty expected = new SimpleStringProperty("TestName");
        assertEquals(expected.toString(), b1.getNameProperty().toString());
    }

    @Test
    public void getDescriptionPropertyTest() {
        SimpleStringProperty expected = new SimpleStringProperty("testDescription");
        assertEquals(expected.toString(), b1.getDescriptionProperty().toString());
    }
}
