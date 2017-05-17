package programminglife.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

/**
 * Created by toinehartman on 17/05/2017.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();

    private DB db;

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        System.out.println("Creating MapDB...");
        this.db = DBMaker.heapDB().make();
        System.out.println("Heap DB created!");
    }

    public DB getDb() {
        return db;
    }

    public static HTreeMap<Integer, Segment> createSegmentStorage(String name) {
        DB db = DataManager.getInstance().getDb();
        if (db.exists(name)) {
            System.out.printf("Storage with name '%s' exists\n", name);
            ((HTreeMap) db.get(name)).clear();
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
