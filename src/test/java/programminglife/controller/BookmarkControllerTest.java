package programminglife.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.utility.InitFXThread;
import programminglife.model.Bookmark;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by marti_000 on 16-5-2017.
 */
public class BookmarkControllerTest {
    private Bookmark bookmark;
    private String testPath;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setup() {
        bookmark = new Bookmark("test", "dummypath", 2, 6,"testbm", "testing");
        testPath = BookmarkControllerTest.class.getResource("/bookmarkTest.xml").getPath();
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
        BookmarkController.storeBookmark(testPath, "dummypath", "test", "testbm2", "testing", 2, 4);
        List<Bookmark> actual = BookmarkController.loadAllGraphBookmarks(testPath, "test");
        List<Bookmark> expected = new ArrayList<>();
        expected.add(bookmark);
        expected.add(new Bookmark("test", "dummypath", 2, 4, "testbm2", "testing"));
        assertEquals(expected, actual);
    }

    @Test
    public void writeTest() {
        BookmarkController.storeBookmark(testPath, "dummypath", "writeTestGraph", "writeTest", "writing", 2, 4);
        BookmarkController.storeBookmark(testPath, "dummypath", "writeTestGraph", "writeTeest", "writing", 2, 4);
        assertNotNull(BookmarkController.loadAllGraphBookmarks(testPath, "writeTestGraph"));
    }

    @Test
    public void deleteTest() {
        BookmarkController.storeBookmark(testPath, "dummypath", "deleteTestGraph", "deleteTest", "deleting", 3, 5);
        BookmarkController.deleteBookmark(testPath, "deleteTestGraph", "deleteTest");
        assertEquals(new ArrayList<>(), BookmarkController.loadAllGraphBookmarks(testPath, "deleteTestGraph"));
    }
}
