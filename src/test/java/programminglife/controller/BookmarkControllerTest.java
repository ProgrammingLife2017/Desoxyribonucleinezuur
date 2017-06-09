package programminglife.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.utility.InitFXThread;
import programminglife.model.Bookmark;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the bookmark controller.
 */
public class BookmarkControllerTest {
    private Bookmark bookmark;
    private String testPath;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setup() throws Exception {
        bookmark = new Bookmark("test", "dummypath", 2, 6,"testbm", "testing");
        testPath = new File(BookmarkControllerTest.class.getResource("/bookmarkTest.xml").toURI()).getAbsolutePath();
    }

    @After
    public void cleanup() {
        BookmarkController.deleteBookmark(testPath, "deleteTestGraph", "deleteTest");
        BookmarkController.deleteBookmark(testPath, "writeTestGraph", "writeTest");
        BookmarkController.deleteBookmark(testPath, "writeTestGraph", "writeTeest");
        BookmarkController.deleteBookmark(testPath, "test", "testbm2");
    }

    @Test
    public void readTest() {
        List<Bookmark> newBookmark = BookmarkController.loadAllGraphBookmarks(testPath,"test");
        assertEquals(newBookmark.get(0), bookmark);
    }

    @Test
    public void readAllGraphTest() {
        BookmarkController.storeBookmark(testPath, bookmark);
        List<Bookmark> actual = BookmarkController.loadAllGraphBookmarks(testPath, "test");
        List<Bookmark> expected = new ArrayList<>();
        expected.add(bookmark);
        assertEquals(expected, actual);
    }

    @Test
    public void writeTest() {
        BookmarkController.storeBookmark(testPath, new Bookmark("dummypath", "writeTestGraph",2, 4, "writeTest", "writing"));
        BookmarkController.storeBookmark(testPath, new Bookmark("dummypath", "writeTestGraph", 2, 4, "writeTeest", "writing"));
        assertNotNull(BookmarkController.loadAllGraphBookmarks(testPath, "writeTestGraph"));
    }

    @Test
    public void deleteTest() {
        BookmarkController.storeBookmark(testPath, new Bookmark("dummypath", "deleteTestGraph", 3, 5, "deleteTest", "deleting"));
        BookmarkController.deleteBookmark(testPath, "deleteTestGraph", "deleteTest");
        assertEquals(new ArrayList<>(), BookmarkController.loadAllGraphBookmarks(testPath, "deleteTestGraph"));
    }
}
