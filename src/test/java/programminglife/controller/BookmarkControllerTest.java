package programminglife.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.model.Bookmark;
import programminglife.utility.InitFXThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the bookmark controller.
 */
public class BookmarkControllerTest {
    private Bookmark bookmark;
    private String testPath;
    private File file = new File("src/test/resources/checkFile.xml");


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
        file.delete();
    }

    @Test
    public void readTest() {
        List<Bookmark> newBookmark = BookmarkController.loadAllBookmarks(testPath).get("test");
        assertEquals(newBookmark.get(0), bookmark);
    }

    @Test
    public void checkFileCreationTest(){
        assertFalse(file.exists());
        BookmarkController.storeBookmark("src/test/resources/checkFile.xml" , bookmark);
        assertTrue(file.exists());
    }

    @Test
    public void readAllGraphTest() {
        BookmarkController.storeBookmark(testPath, bookmark);
        List<Bookmark> actual = BookmarkController.loadAllBookmarks(testPath).get(bookmark.getGraphName());
        List<Bookmark> expected = new ArrayList<>();
        expected.add(bookmark);
        assertEquals(expected, actual);
    }

    @Test
    public void writeTest() {
        BookmarkController.storeBookmark(testPath, new Bookmark("writeTestGraph","dummypath",2, 4, "writeTest", "writing"));
        BookmarkController.storeBookmark(testPath, new Bookmark( "writeTestGraph", "dummypath",2, 4, "writeTeest", "writing"));
        assertNotNull(BookmarkController.loadAllBookmarks(testPath).get("writeTestGraph"));
    }

    @Test
    public void deleteTest() {
        BookmarkController.storeBookmark(testPath, new Bookmark("deleteTestGraph","dummypath", 3, 5, "deleteTest", "deleting"));
        assertNotEquals(new ArrayList<>(), BookmarkController.loadAllBookmarks(testPath).get("deleteTestGraph"));
        BookmarkController.deleteBookmark(testPath, "deleteTestGraph", "deleteTest");
        assertEquals(new ArrayList<>(), BookmarkController.loadAllBookmarks(testPath).get("deleteTestGraph"));
    }
}
