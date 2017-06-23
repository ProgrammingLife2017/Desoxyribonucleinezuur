package programminglife.model;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import programminglife.parser.Cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class that handles the genome graph.
 */
public class GenomeGraph {
    private final String id;
    private Cache cache;

    /**
     * Create a genomeGraph with id.
     *
     * @param id id of the graph
     */
    public GenomeGraph(String id) {
        this.id = id;
        this.cache = new Cache(id);
    }

    /**
     * Get the {@link GenomeGraph} ID.
     *
     * @return the ID
     */
    public String getID() {
        return id;
    }


    /**
     * Replace a node in the graph.
     *
     * @param nodeID the ID of the node
     */
    public void replaceNode(int nodeID) {
        this.replaceNode(nodeID, new int[0], new int[0]);
    }

    /**
     * Replace a node in the graph.
     *
     * @param nodeID   the ID of the node
     * @param children the new children
     * @param parents  the new parents
     */
    private void replaceNode(int nodeID, int[] children, int[] parents) {
        this.cache.getChildrenAdjacencyMap().put(nodeID, children);
        this.cache.getParentsAdjacencyMap().put(nodeID, parents);
    }

    /**
     * Get the number of nodes in the {@link GenomeGraph}.
     * This is based on the amount of nodes with a sequence in the cache not the amount of nodes
     * in the GenomeGraph.
     *
     * @return the number of nodes
     */
    public int size() {
        return this.cache.getNumberOfNodes();
    }

    /**
     * Get IDs of children of a node.
     *
     * @param nodeID the ID of the node to look up
     * @return an int[] of IDs
     */
    public int[] getChildIDs(int nodeID) {
        return this.cache.getChildrenAdjacencyMap().get(nodeID);
    }

    /**
     * Getter for the ID's of the parents of a node.
     *
     * @param nodeID node for which the parents are to be found.
     * @return int[] list of the parents.
     */
    public int[] getParentIDs(int nodeID) {
        return this.cache.getParentsAdjacencyMap().get(nodeID);
    }

    /**
     * Get the Genomes through a specific Node.
     *
     * @param nodeID the Node to look up
     * @return a {@link Collection} of Genome IDs
     */
    public int[] getGenomes(int nodeID) {
        return this.cache.getGenomes(nodeID);
    }

    /**
     * Return a collection of names.
     *
     * @param genomes the IDs of the genomes to identify
     * @return their names
     */
    public Collection<String> getGenomeNames(int[] genomes) {
        return Arrays.stream(genomes)
                .boxed()
                .map(this::getGenomeName)
                .collect(Collectors.toList());
    }

    /**
     * Return a collection of names.
     *
     * @param genomes the IDs of the genomes to identify
     * @return their names
     */
    public Collection<String> getGenomeNames(Collection<Integer> genomes) {
        return genomes.stream()
                .map(this::getGenomeName)
                .collect(Collectors.toList());
    }

    /**
     * Get Nodes through a Genome.
     *
     * @param genomeID the Genome to look up
     * @return a {@link Collection} of Node IDs in the Genome
     */
    public Collection<Integer> getNodeIDs(int genomeID) {
        return this.cache.getGenomeNodeIDs(genomeID);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(int nodeID) {
        return this.cache.getChildrenAdjacencyMap().containsKey(nodeID);
    }

    /**
     * {@inheritDoc}
     */
    public void addEdge(int sourceID, int destinationID) {
        this.addChild(sourceID, destinationID);
        this.addParent(destinationID, sourceID);
    }

    /**
     * Add a child to a node.
     *
     * @param nodeID  Node ID to which the child will be added.
     * @param childID Node ID of the child to be added.
     */
    private void addChild(int nodeID, int childID) {
        if (this.cache.getCurrentParentID() == -1) {
            this.cache.setCurrentParentID(nodeID);
        }

        if (nodeID == this.cache.getCurrentParentID()) {
            // if same parent as previous link || if first link of graph,
            // just add the child
            this.cache.getCurrentParentChildren().add(childID);
        } else {
            // write previous list to cache
            int[] oldChildren = this.getChildIDs(this.cache.getCurrentParentID());
            int[] allChildren = this.append(oldChildren, this.cache.getCurrentParentChildren());
            this.cache.getChildrenAdjacencyMap().put(this.cache.getCurrentParentID(), allChildren);

            // reset node id
            this.cache.setCurrentParentID(nodeID);
            // reset children list
            this.cache.setCurrentParentChildren(new LinkedList<>());
            this.cache.getCurrentParentChildren().add(childID);
        }
    }

    /**
     * Add a parent to a node.
     *
     * @param nodeID   Node ID to which the parent will be added.
     * @param parentID Node ID of the parent to be added.
     */
    private void addParent(int nodeID, int parentID) {
        int[] oldParents = this.getParentIDs(nodeID);
        //TODO find a way to do this more efficiently
        int[] newParents = Arrays.copyOf(oldParents, oldParents.length + 1);
        newParents[newParents.length - 1] = parentID;
        this.cache.getParentsAdjacencyMap().put(nodeID, newParents);
    }

    /**
     * Set Genomes through a Node.
     *
     * @param nodeID    the Node to address
     * @param genomeIDs the Genomes through this Node
     */
    public void setGenomes(int nodeID, int[] genomeIDs) {
        this.cache.setGenomes(nodeID, genomeIDs);
    }

    /**
     * Get the name of a Genome.
     *
     * @param genomeID the ID of the Genome
     * @return its name
     */
    public String getGenomeName(int genomeID) {
        return this.cache.getGenomeName(genomeID);
    }

    /**
     * Get all names of genomes in the graph.
     *
     * @return a {@link Collection} of names
     */
    public Collection<String> getGenomeNames() {
        return this.cache.getGenomeNamesIdMap().keySet();
    }

    /**
     * Get the ID of a Genome.
     *
     * @param genomeName the name of the Genome
     * @return its ID
     */
    public int getGenomeID(String genomeName) {
        return this.cache.getGenomeID(genomeName);
    }

    /**
     * Add a Genome to the {@link GenomeGraph}.
     *
     * @param name its name
     */
    public void addGenome(String name) {
        this.cache.addGenomeName(name);
    }

    /**
     * Set the sequence for a Segment.
     *
     * @param nodeID   the ID of the Segment
     * @param sequence the sequence {@link String}
     */
    public void setSequence(int nodeID, String sequence) {
        this.cache.setSequence(nodeID, sequence);
    }

    /**
     * Get the sequence of a Segment.
     *
     * @param nodeID the ID of the Segment
     * @return the sequence {@link String}
     */
    @NotNull
    public String getSequence(int nodeID) {
        return this.cache.getSequence(nodeID);
    }

    /**
     * Get the sequence length of a Segment.
     *
     * @param nodeID the ID of the Segment
     * @return the sequence length {@link String}
     */
    public int getSequenceLength(int nodeID) {
        return this.cache.getSequenceLength(nodeID);
    }

    /**
     * Roll back the latest changes to the cache.
     *
     * @throws IOException when something strange happens during deletion
     */
    public void rollback() throws IOException {
        this.cache.rollback();
    }

    /**
     * Remove the cache file for this {@link GenomeGraph}.
     *
     * @throws IOException if something strange happens
     */
    public void removeCache() throws IOException {
        this.cache.removeDB();
    }

    /**
     * Closes the cache of the {@link GenomeGraph}.
     */
    public void close() {
        if (this.cache != null) {
            this.cache.close();
            this.cache = null;
        }
    }

    /**
     * Append an {@link List<Integer>} to a int[].
     *
     * @param oldArray the int[] to go first
     * @param newList  the {@link List<Integer>} to be appended
     * @return a int[] consisting of all elements
     */
    private int[] append(int[] oldArray, List<Integer> newList) {
        int[] newArray;
        if (oldArray == null) {
            newArray = null;
        } else {
            newArray = ArrayUtils.addAll(oldArray, newList.stream().mapToInt(i -> i).toArray());
        }

        return newArray;
    }

    /**
     * Cache the group of edges from the last parent.
     * <p>
     * Necessary because these are skipped during parsing.
     */
    public void cacheLastEdges() {
        int[] oldChildren = this.getChildIDs(this.cache.getCurrentParentID());
        int[] allChildren = this.append(oldChildren, this.cache.getCurrentParentChildren());
        this.cache.getChildrenAdjacencyMap().put(this.cache.getCurrentParentID(), allChildren);
    }

    /**
     * The fraction of genomes through a node.
     *
     * @param nodeID the ID of the nods
     * @return the fraction
     */
    public double getGenomeFraction(int nodeID) {
        return this.getGenomesNumber(nodeID) / (double) this.cache.getGenomeNamesIdMap().size();
    }

    /**
     * The number of genomes through a node.
     *
     * @param nodeID the ID of the node
     * @return the number
     */
    private int getGenomesNumber(int nodeID) {
        return this.cache.getNodeIdGenomesNumberMap().get(nodeID);
    }

    public int getTotalGenomeNumber() {
        return this.cache.getGenomeNamesIdMap().size();
    }
}
