package programminglife.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

/**
 * Created by toinehartman on 17/05/2017.
 */
public final class DataManager {
    private static DataManager ourInstance = new DataManager();

    private DB db;

    public static DataManager getInstance() {
        return ourInstance;
    }

    /**
     * Create the DataManager and initialize the database.
     */
    private DataManager() {
        System.out.println("Creating MapDB...");
        this.db = DBMaker.heapDB().make();
        System.out.println("Heap DB created!");
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
            System.out.printf("Storage with name '%s' exists\n", name);
            return db.get(name);
        } else {
            return db
                    .hashMap(name)
                    .keySerializer(Serializer.INTEGER)
                    .valueSerializer(new Segment.SegmentSerializer())
                    .create();
        }
    }
}
