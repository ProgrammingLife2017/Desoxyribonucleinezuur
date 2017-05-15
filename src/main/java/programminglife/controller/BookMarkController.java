package programminglife.controller;

import java.io.File;

/**
 * Created by marti_000 on 15-5-2017.
 */
public class BookMarkController {
    private static String PATH = "/bookmarks";


    public BookMarkController() {

    }

    public static void loadBookmark(String graphName, String bookMarkName) {
        String localPath = PATH + "/" + graphName + "/" + bookMarkName;
        File file = new File(localPath);

    }
}
