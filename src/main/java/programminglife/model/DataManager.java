package programminglife.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class DataManager {
    private static final String DB_FILE_PATH = "dbFile.db";
    private static DataManager ourInstance = null;
    private static String currentCollectionName = null;

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
     * @param name collection to check for
     * @return true iff a cache exists for the file, false iff otherwise.
     */
    public static boolean hasCache(String name) {
        return DataManager.getInstance().getDb().exists(name);
    }

    /**
     * Create a clean (empty) Segment storage.
     * WARNING: this operation overwrites the cache collection, if it exists.
     * @param collectionName name of collection
     * @return A clean (empty) HTreeMap
     */
    public static Map<Integer, String> getCleanCollection(String collectionName) {
        Map<Integer, String> res = getCollection(collectionName);
        res.clear();
        return res;
    }

    /**
     * Get the HTreeMap cache with this name.
     * @param collectionName name for the cache
     * @return The HTreeMap associated with collectionName.
     */
    public static Map<Integer, String> getCollection(String collectionName) {
        DB db = DataManager.getInstance().getDb();
        if (db.exists(collectionName)) {
            System.out.printf("%s Storage %s exists\n", Thread.currentThread(), collectionName);
            return db.get(collectionName);
        } else {
            System.out.printf("%s Storage %s does not exist.\n%s Creating storage %s...\n",
                    Thread.currentThread(), collectionName, Thread.currentThread(), collectionName);
            HTreeMap<Integer, String> res = db
                    .hashMap(collectionName)
                    .keySerializer(Serializer.INTEGER)
                    .valueSerializer(Serializer.STRING_ASCII)
                    .create();
            System.out.printf("%s Storage %s created\n", Thread.currentThread(), collectionName);

            currentCollectionName = collectionName;

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

    public static String getSequence(int nodeID) {
        return getCollection(currentCollectionName).get(nodeID);
    }
}
