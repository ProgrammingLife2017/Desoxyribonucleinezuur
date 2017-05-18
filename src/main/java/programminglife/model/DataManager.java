package programminglife.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.io.IOException;

/**
 * Created by toinehartman on 17/05/2017.
 */
public final class DataManager {
    private static final String DB_FILE_PATH = "dbFile.db";
    private static DataManager ourInstance = null;

    private DB db;

    /**
     * Initialize this DataManager. Opens database and stuff.
     * @throws IOException When an IO Exception occurs while opening the database.
     */
    public static synchronized void initialize() throws IOException {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
    }

    /**
     * get the instance of this singleton. This will throw a RunTimeException if initialize has not
     * been called successfully beforehand and it could not be initialized automatically.
     * @return The singleton DataManager instance.
     */
    public static DataManager getInstance() {
        if (ourInstance == null) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException(
                        "DataManager had not been initialized and could not be initialized automatically",
                        e
                );
            }
        }
        return ourInstance;
    }

    /**
     * Create the DataManager and initialize the database.
     * @throws IOException when an IOException occurs while opening the database.
     */
    private DataManager() throws IOException {
        System.out.printf("%s Setting up MapDB %s...\n", Thread.currentThread(), DB_FILE_PATH);
        this.db = DBMaker.fileDB(new File(DB_FILE_PATH)).closeOnJvmShutdown().make();
        System.out.printf("%s MapDB %s set up!\n", Thread.currentThread(), DB_FILE_PATH);
    }

    public DB getDb() {
        return db;
    }

    /**
     * Check whether a cache exists for file named name.
     * @param name File to check for
     * @return true iff a cache exists for the file, false iff otherwise.
     */
    public static boolean hasCache(String name) {
        return DataManager.getInstance().getDb().exists(name);
    }

    /**
     * Create a clean (empty) Segment storage.
     * WARNING: this operation overwrites the cache name, if it exists.
     * @param name name of cache file
     * @return A clean (empty) HTreeMap
     */
    public static HTreeMap<Integer, Segment> createCleanSegmentStorage(String name) {
        HTreeMap<Integer, Segment> res = getSegmentStorage(name);
        res.clear();
        return res;
    }

    /**
     * Get the HTreeMap cache for name.
     * @param name name for the cache
     * @return The HTreeMap associated with name.
     */
    public static HTreeMap<Integer, Segment> getSegmentStorage(String name) {
        DB db = DataManager.getInstance().getDb();
        if (db.exists(name)) {
            System.out.printf("%s Storage %s exists\n", Thread.currentThread(), name);
            return db.get(name);
        } else {
            System.out.printf("%s Storage %s does not exist.\n%s Creating storage %s...\n",
                    Thread.currentThread(), name, Thread.currentThread(), name);
            HTreeMap<Integer, Segment> res = db
                    .hashMap(name)
                    .keySerializer(Serializer.INTEGER)
                    .valueSerializer(new Segment.SegmentSerializer())
                    .create();
            System.out.printf("%s Storage %s created\n", Thread.currentThread(), name);
            return res;
        }
    }

    /**
     * close the database.
     */
    public static void close() {
        System.out.printf("%s Closing MapDB...\n", Thread.currentThread());
        DataManager.getInstance().getDb().commit();
        DataManager.getInstance().getDb().close();
        System.out.printf("%s MapDB closed\n", Thread.currentThread());
    }
}
