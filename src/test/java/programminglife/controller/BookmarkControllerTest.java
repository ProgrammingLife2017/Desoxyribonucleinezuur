package programminglife.controller;

import org.junit.Before;
import org.junit.Test;
import programminglife.model.Bookmark;

import static org.junit.Assert.assertEquals;

/**
 * Created by marti_000 on 16-5-2017.
 */
public class BookmarkControllerTest {
    private Bookmark bookmark;

    @Before
    public void setup() {
        bookmark = new Bookmark( 2, 6,"testbm", "testing");
    }

    @Test
    public void readTest() {
        Bookmark newBookmark = BookmarkController.loadBookmark("test", "testbm");
        assertEquals(newBookmark, bookmark);
    }
}
