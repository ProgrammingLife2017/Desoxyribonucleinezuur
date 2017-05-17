package programminglife.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import programminglife.model.Bookmark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by marti_000 on 16-5-2017.
 */
public class BookmarkControllerTest {
    private Bookmark bookmark;
    private String testPath;

    @Before
    public void setup() {
        bookmark = new Bookmark( 2, 6,"testbm", "testing");
        testPath = BookmarkControllerTest.class.getResource("/bookmarkTest.xml").getPath();
    }

    @After
    public void cleanup() {
        BookmarkController.deleteBookmark(testPath, "deleteTestGraph", "deleteTest");
        BookmarkController.deleteBookmark(testPath, "writeTestGraph", "writeTest");
        BookmarkController.deleteBookmark(testPath, "writeTestGraph", "writeTeest");
    }

    @Test
    public void readTest() {
        Bookmark newBookmark = BookmarkController.loadBookmark(testPath,"test", "testbm");
        assertEquals(newBookmark, bookmark);

    }

    @Test
    public void writeTest() {
        BookmarkController.storeBookmark(testPath, "writeTestGraph", "writeTest", "writing", 2, 4);
        BookmarkController.storeBookmark(testPath, "writeTestGraph", "writeTeest", "writing", 2, 4);
        assertNotNull(BookmarkController.loadBookmark(testPath, "writeTestGraph", "writeTest"));
    }

    @Test
    public void deleteTest() {
        BookmarkController.storeBookmark(testPath, "deleteTestGraph", "deleteTest", "deleting", 3, 5);
        BookmarkController.deleteBookmark(testPath, "deleteTestGraph", "deleteTest");
        assertNull(BookmarkController.loadBookmark(testPath, "deleteTestGraph", "deleteTest"));
    }
}
