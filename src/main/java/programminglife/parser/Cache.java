package programminglife.parser;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import programminglife.utility.Console;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A class for managing persistent data. It can open one cache, which contains the information for one gfa file.
 */
public final class Cache {
    private static final String SEQUENCE_MAP_NAME = "sequenceMap";
    private static final String SEQUENCE_LENGTH_MAP_NAME = "sequenceLengthMap";
    private static final String GENOME_NAMES_MAP_NAME = "genomeNamesMap";

    private String dbFileName;
    private DB db;
    private Map<Integer, String> sequenceMap;
    private Map<Integer, Integer> sequenceLengthMap;
    private Map<Integer, String> genomeNamesMap;

    /**
     * Create the Cache and initialize the database.
     * @param name The name of the {@link Cache}.
     *                 Note: this method will append .db if it doesn't already end with that.
     */
    public Cache(String name) {
        this.dbFileName = toDBFile(name);
        Console.println("[%s] Setting up cache (%s)...", Thread.currentThread().getName(), this.dbFileName);
        this.db = DBMaker.fileDB(new File(this.dbFileName))
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()
                .cleanerHackEnable()
                .closeOnJvmShutdown()
                .checksumHeaderBypass()
                .make();
        this.initialize();
    }

    /**
     * Initialize all collections in the cache.
     */
    private void initialize() {
        this.sequenceMap = getMap(db, SEQUENCE_MAP_NAME, Serializer.INTEGER, Serializer.STRING_ASCII);
        this.sequenceLengthMap = getMap(db, SEQUENCE_LENGTH_MAP_NAME, Serializer.INTEGER, Serializer.INTEGER);
        this.genomeNamesMap = getMap(db, GENOME_NAMES_MAP_NAME, Serializer.INTEGER, Serializer.STRING_ASCII);
    }

    /**
     * converts a name to the name that would be used for the cache.
     * @param name The name to be converted
     * @return The converted name.
     */
    @NotNull
    public static String toDBFile(String name) {
        if (name.toLowerCase().endsWith(".gfa")) {
            name = name.substring(0, name.length() - 4);
        }
        if (!name.toLowerCase().endsWith(".db")) {
            name += ".db";
        }
        return name;
    }

    /**
     * Check whether a cache exists for file named name.
     * @param name collection to check for
     * @return true iff a cache exists for the file, false iff otherwise.
     */
    @NotNull
    public static boolean hasCache(String name) {
        return Files.exists(Paths.get(toDBFile(name)));
    }

    /**
     * Get the HTreeMap cache for the cached sequence lengths.
     * @return the HTreeMap cache for the sequence lengths.
     */
    private Map<Integer, Integer> getSequenceLengthMap() {
        return this.sequenceLengthMap;
    }

    /**
     * Get the HTreeMap cache for the cached sequences.
     * @return the HTreeMap cache for the sequences.
     */
    private Map<Integer, String> getSequenceMap() {
        return this.sequenceMap;
    }

    /**
     * Get the HTreeMap cache for the cached genomes.
     * @return the HTreeMap cache for the sequences.
     */
    private Map<Integer, String> getGenomeNamesMap() {
        return this.genomeNamesMap;
    }

    /**
     * Get a disk-backed hashmap named name. If it doesn't exist, it is created using the provided serializers.
     * @param db the db to get the map from.
     * @param name The name of the hashmap
     * @param keySerializer The serializer for the keys
     * @param valueSerializer The serializer for th values
     * @param <K> The type of the keys
     * @param <V> The type of the values.
     * @return a disk-backed hashmap named name.
     */
    @NotNull
    private static <K, V> Map<K, V> getMap(DB db, String name,
                                           Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        if (db.exists(name)) {
            return db.get(name);
        } else {
            return db
                    .hashMap(name)
                    .keySerializer(keySerializer)
                    .valueSerializer(valueSerializer)
                    .create();
        }
    }

    /**
     * Close the database.
     * @throws IOException when unexpected things happen while closing
     */
    public void close() throws IOException {
        if (!this.db.isClosed()) {
            Console.println("[%s] Closing MapDB...", Thread.currentThread().getName());
            this.db.close();
        }
    }

    /**
     * Get the sequence for the node with NodeId.
     * @param nodeID ID of the node to get the sequence for.
     * @return the sequence.
     */
    @NotNull
    public String getSequence(int nodeID) {
        if (getSequenceMap().containsKey(nodeID)) {
            return getSequenceMap().get(nodeID);
        } else {
            throw new NoSuchElementException(String.format("No sequence is cached for node %d", nodeID));
        }
    }

    /**
     * Set the sequence for the node with NodeId.
     * @param nodeID ID of the node to set the sequence for.
     * @param sequence new sequence.
     */
    @NotNull
    public void setSequence(int nodeID, String sequence) {
        getSequenceMap().put(nodeID, sequence);
        getSequenceLengthMap().put(nodeID, sequence.length());
    }

    /**
     * Get the sequence length for the node with NodeId.
     * @param nodeID ID of the node to get the sequence length for.
     * @return the length of the sequence.
     */
    @NotNull
    public int getSequenceLength(int nodeID) {
        if (getSequenceLengthMap().containsKey(nodeID)) {
            return getSequenceLengthMap().get(nodeID);
        } else {
            throw new NoSuchElementException(String.format("No sequence length is cached for node %d", nodeID));
        }
    }

    /**
     * Get the name of a {@link programminglife.model.Genome} based on its index.
     * @param genomeID the index (0-based) of the {@link programminglife.model.Genome} in the GFA header
     * @return the name of the {@link programminglife.model.Genome}
     */
    @NotNull
    public String getGenomeName(int genomeID) {
        if (getGenomeNamesMap().containsKey(genomeID)) {
            return getGenomeNamesMap().get(genomeID);
        } else {
            throw new NoSuchElementException(String.format("No name is cached for genome %d", genomeID));
        }
    }

    /**
     * Add the name of a {@link programminglife.model.Genome}, index is previous one + 1.
     * @param genomeName the name of the {@link programminglife.model.Genome} to add
     */
    @NotNull
    public void addGenomeName(String genomeName) {
        int index = getGenomeNamesMap().size();
        getGenomeNamesMap().put(index, genomeName);
    }

    /**
     * Completely remove a database. This cannot be undone.
     * @return true if the file was removed, false if it did not exist
     * @throws IOException when something strange happenes during deletion
     */
    public boolean removeDB() throws IOException {
        Console.println("[%s] Removing database %s", Thread.currentThread().getName(), this.dbFileName);
        close();
        return Files.deleteIfExists(Paths.get(this.dbFileName));
    }

    /**
     * Remove a cache file.
     * @param name the name of the file to remove
     * @return true if the file was deleted
     * @throws IOException when strange things happen
     */
    public static boolean removeDB(String name) throws IOException {
        return Files.deleteIfExists(Paths.get(toDBFile(name)));
    }

    /**
     * Persist cache to disk.
     */
    public void commit() {
        this.db.commit();
    }

    /**
     * Rolls back non-persistent changes in database.
     * @throws IOException when something strange happens during deletion
     */
    public void rollback() throws IOException {
        // TODO find a way to handle a partially complete cache
        // Just removing the cache is the best solution for now
        this.removeDB();
    }
}
