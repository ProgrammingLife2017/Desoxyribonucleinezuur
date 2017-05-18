package programminglife.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A class for managing persistent data. It can open one cache, which contains the information for one gfa file.
 */
public final class DataManager {
    private static final String SEQUENCE_MAP_SUFFIX = "_sequenceMap";
    private static final String SEQUENCE__LENGTH_MAP_SUFFIX = "_sequenceLengthMap";

    private static DataManager ourInstance = null;
    private static String currentFileName = null;

    private DB db;

    /**
     * Initialize this DataManager. Opens database and stuff.
     * @param fileName The name of the cache file.
     * @throws IOException When an IO Exception occurs while opening the database.
     */
    public static synchronized void initialize(String fileName) throws IOException {
        if (ourInstance != null) {
            DataManager.close();
        }

        ourInstance = new DataManager(fileName);
    }

    /**
     * get the instance of this singleton. This will throw a RunTimeException if initialize has not
     * been called successfully beforehand and it could not be initialized automatically.
     * @return The singleton DataManager instance.
     */
    public static DataManager getInstance() {
        if (ourInstance == null) {
            try {
                initialize(currentFileName);
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
     * @param fileName The name of the cache file.
     * @throws IOException when an IOException occurs while opening the database.
     */
    private DataManager(String fileName) throws IOException {
        this.currentFileName = fileName;
        System.out.printf("%s Setting up MapDB %s...\n", Thread.currentThread(), fileName);
        this.db = DBMaker.fileDB(new File(fileName + ".db")).closeOnJvmShutdown().make();
        System.out.printf("%s MapDB %s set up!\n", Thread.currentThread(), fileName);
    }

    private DB getDb() {
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
    private static Map<Integer, String> getCleanCollection(String collectionName) {
        Map<Integer, String> res = getSequenceMap();
        res.clear();
        return res;
    }

    /**
     * Get the HTreeMap cache for the sequence lengths of the current file.
     * @return the HTreeMap cache for the sequence lengths of the current file.
     */
    private static Map<Integer, Integer> getSequenceLengthMap() {
        return getMap(currentFileName + SEQUENCE__LENGTH_MAP_SUFFIX, Serializer.INTEGER, Serializer.INTEGER);
    }


    /**
     * Get the HTreeMap cache for the sequences of the current file.
     * @return the HTreeMap cache for the sequences of the current file.
     */
    private static Map<Integer, String> getSequenceMap() {
        return getMap(currentFileName + SEQUENCE_MAP_SUFFIX, Serializer.INTEGER, Serializer.STRING_ASCII);
    }

    /**
     * Get a disk-backed hashmap named name. If it doesn't exist, it is created using the provided serializers.
     * @param name The name of the hashmap
     * @param keySerializer The serializer for the keys
     * @param valueSerializer The serializer for th values
     * @param <K> The type of the keys
     * @param <V> The type of the values.
     * @return a disk-backed hashmap named name.
     */
    private static <K, V> Map<K, V> getMap(String name, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        DB db = DataManager.getInstance().getDb();
        if (db.exists(name)) {
//            System.out.printf("%s Storage %s exists\n", Thread.currentThread(), name);
            return db.get(name);
        } else {
            System.out.printf("%s Storage %s does not exist.\n%s Creating storage %s...\n",
                    Thread.currentThread(), name, Thread.currentThread(), name);
            HTreeMap<K, V> res = db
                    .hashMap(name)
                    .keySerializer(keySerializer)
                    .valueSerializer(valueSerializer)
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

    /**
     * Get the sequence for the node with NodeId.
     * @param nodeID ID of the node to get the sequence for.
     * @return the sequence.
     */
    public static String getSequence(int nodeID) {
        return getSequenceMap().get(nodeID);
    }

    /**
     * Set the sequence for the node with NodeId.
     * @param nodeID ID of the node to set the sequence for.
     * @param sequence new sequence.
     */
    public static void setSequence(int nodeID, String sequence) {
        getSequenceMap().put(nodeID, sequence);
        getSequenceLengthMap().put(nodeID, sequence.length());
    }

    /**
     * Get the sequence length for the node with NodeId.
     * @param nodeID ID of the node to get the sequence length for.
     * @return the length of the sequence.
     */
    public static int getSequenceLength(int nodeID) {
        return getSequenceLengthMap().get(nodeID);
    }
}
