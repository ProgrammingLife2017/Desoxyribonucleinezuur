package programminglife.model;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import programminglife.model.exception.NodeExistsException;
import programminglife.parser.Cache;

import java.io.IOException;
import java.util.*;

/**
 * The class that handles the genome graph.
 */
public class GenomeGraph implements Graph {
    private String id;
    private Cache cache;

    // TODO cache genomes
    private Map<String, Genome> genomes;

    /**
     * Create a genomeGraph with id.
     * @param id id of the graph
     */
    public GenomeGraph(String id) {
        this.id = id;
        this.genomes = new HashMap<>();
        this.cache = new Cache(id);
    }

    /**
     * Get the {@link GenomeGraph} ID.
     * @return the ID
     */
    public String getID() {
        return id;
    }

    @Override
    public void addNode(Node node) {
        this.addNode(node, new int[0], new int[0]);
    }

    @Override
    public void addNode(Node node, int[] children, int[] parents) {
        if (this.contains(node)) {
            throw new NodeExistsException(String.format("%s already exists in graph %s",
                    node.toString(), this.getID()));
        }

        this.replaceNode(node, children, parents);
    }

    @Override
    public void replaceNode(Node node) {
        this.replaceNode(node, new int[0], new int[0]);
    }

    @Override
    public void replaceNode(Node node, int[] children, int[] parents) {
        this.cache.getChildrenAdjacencyMap().put(node.getIdentifier(), children);
        this.cache.getParentsAdjacencyMap().put(node.getIdentifier(), parents);
    }

    /**
     * Get the number of nodes in the {@link GenomeGraph}.
     * @return the number of nodes
     */
    public int size() {
        assert (this.cache.getChildrenAdjacencyMap().size() == this.cache.getParentsAdjacencyMap().size());
        return this.cache.getChildrenAdjacencyMap().size();
    }

    @Override
    public int[] getChildIDs(Node node) {
        return this.getChildIDs(node.getIdentifier());
    }

    @Override
    public int[] getChildIDs(int nodeID) {
        return this.cache.getChildrenAdjacencyMap().get(nodeID);
    }

    @Override
    public int[] getParentIDs(Node node) {
        return this.getParentIDs(node.getIdentifier());
    }

    @Override
    public Link getLink(Node parent, Node child) {
        return new Link(parent, child, getGenomes(parent, child));
    }

    @Override
    public Collection<Node> getParents(Node node) {
        int[] parents = this.getParentIDs(node.getIdentifier());
        Collection<Node> parentNodes = new LinkedHashSet<>();
        for (int parent : parents) {
            parentNodes.add(new Segment(this, parent));
        }
        return parentNodes;
    }

    @Override
    public Collection<Node> getChildren(Node node) {
        int[] children = this.getChildIDs(node.getIdentifier());
        Collection<Node> childNodes = new LinkedHashSet<>();
        for (int aChildren : children) {
            childNodes.add(new Segment(this, aChildren));
        }
        return childNodes;
    }

    /**
     * Getter for the ID's of the parents of a node.
     * @param nodeID node for which the parents are to be found.
     * @return int[] list of the parents.
     */
    public int[] getParentIDs(int nodeID) {
        return this.cache.getParentsAdjacencyMap().get(nodeID);
    }

    @Override
    public int[] getGenomes(Node node) {
        throw new NotImplementedException("GenomeGraph#getGenomes(Node) is not yet implemented");
    }

    /**
     * Get for the genomes of a parent and child.
     * @param parent Node of the parent.
     * @param child Node of the child.
     * @return int[] list the genomes.
     */
    public int[] getGenomes(Node parent, Node child) {
        return null;
    }

    @Override
    public boolean contains(Node node) {
        return this.contains(node.getIdentifier());
    }

    @Override
    public boolean contains(int nodeID) {
        return this.cache.getChildrenAdjacencyMap().containsKey(nodeID);
    }

    @Override
    public void addEdge(Node source, Node destination) {
        this.addChild(source, destination);
        this.addParent(destination, source);
    }

    /**
     * Add a child to a node.
     * @param node Node to which the child will be added.
     * @param child Node of the child to be added.
     */
    private void addChild(Node node, Node child) {
        if (this.cache.getCurrentParentID() == -1) {
            this.cache.setCurrentParentID(node.getIdentifier());
        }

        if (node.getIdentifier() == this.cache.getCurrentParentID()) {
            // if same parent as previous link || if first link of graph,
            // just add the child
            this.cache.getCurrentParentChildren().add(child.getIdentifier());
        } else {
            // write previous list to cache
            int[] oldChildren = this.getChildIDs(this.cache.getCurrentParentID());
            int[] allChildren = this.append(oldChildren, this.cache.getCurrentParentChildren());
            this.cache.getChildrenAdjacencyMap().put(this.cache.getCurrentParentID(), allChildren);

            // reset node id
            this.cache.setCurrentParentID(node.getIdentifier());
            // reset children list
            this.cache.setCurrentParentChildren(new LinkedList<>());
            this.cache.getCurrentParentChildren().add(child.getIdentifier());
        }
    }

    /**
     * Add a parent to a node.
     * @param node Node to which the parent will be added.
     * @param parent Node of the parent to be added.
     */
    private void addParent(Node node, Node parent) {
        int[] oldParents = this.getParentIDs(node.getIdentifier());
        //TODO find a way to do this more efficiently
        int[] newParents = Arrays.copyOf(oldParents, oldParents.length + 1);
        newParents[newParents.length - 1] = parent.getIdentifier();
        this.cache.getParentsAdjacencyMap().put(node.getIdentifier(), newParents);
    }

    /**
     * Add a {@link Genome} to this {@link GenomeGraph}.
     * @param genome the {@link Genome} to add
     */
    public void addGenome(Genome genome) {
        this.cache.addGenomeName(genome.getName());
        this.genomes.put(genome.getName(), genome);
    }

    /**
     * Get the name of a {@link Genome}.
     * @param id the id of the {@link Genome} as it occurs in the GFA header
     * @return its name as described in the GFA header
     */
    public String getGenomeName(int id) {
        return this.cache.getGenomeName(id);
    }

    /**
     * Get the {@link Genome} by name.
     * @param name the name as in the GFA header
     * @return the {@link Genome} with this name
     */
    public Genome getGenome(String name) {
        Genome res = this.genomes.get(name);
        if (res != null) {
            return res;
        } else {
            throw new NoSuchElementException(
                    String.format("The Graph %s does not contain a genome with name %s",
                            this.getID(), name));
        }
    }

    /**
     * Get the {@link Genome}s of this {@link GenomeGraph}.
     * @return a {@link Collection} of {@link Genome}s
     */
    public Collection<Genome> getGenomes() {
        return this.genomes.values();
    }

    /**
     * Check if this {@link GenomeGraph} contains a {@link Genome}.
     * @param genomeName the name to look up
     * @return if this {@link Genome} is in there
     */
    public boolean containsGenome(String genomeName) {
        return this.genomes.containsKey(genomeName);
    }

    /**
     * Set the sequence for a {@link Segment}.
     * @param nodeID the ID of the {@link Segment}
     * @param sequence the sequence {@link String}
     */
    public void setSequence(int nodeID, String sequence) {
        this.cache.setSequence(nodeID, sequence);
    }

    /**
     * Get the sequence of a {@link Segment}.
     * @param nodeID the ID of the {@link Segment}
     * @return the sequence {@link String}
     */
    @NotNull
    public String getSequence(int nodeID) {
        return this.cache.getSequence(nodeID);
    }

    /**
     * Get the sequence length of a {@link Segment}.
     * @param nodeID the ID of the {@link Segment}
     * @return the sequence length {@link String}
     */
    public int getSequenceLength(int nodeID) {
        return this.cache.getSequenceLength(nodeID);
    }

    /**
     * Get the number of lines in the GFA file.
     * @return # of lines
     */
    public int getNumberOfLines() {
        return this.cache.getNumberOfLines();
    }

    /**
     * Set the number of lines in the GFA file.
     * @param numberOfLines # of lines
     */
    public void setNumberOfLines(int numberOfLines) {
        this.cache.setNumberOfLines(numberOfLines);
    }

    /**
     * Roll back the latest changes to the cache.
     * @throws IOException when something strange happens during deletion
     */
    public void rollback() throws IOException {
        this.cache.rollback();
    }

    /**
     * Commit all changes to the cache.
     */
    public void commit() {
        this.cache.commit();
    }

    /**
     * Remove the cache file for this {@link GenomeGraph}.
     * @throws IOException if something strange happens
     */
    public void removeCache() throws IOException {
        this.cache.removeDB();
    }

    /**
     * Closes the cache of the {@link GenomeGraph}.
     * @throws IOException when strange things happen
     */
    public void close() throws IOException {
        if (this.cache != null) {
            this.cache.close();
            this.cache = null;
        }
    }

    /**
     * Append an {@link List<Integer>} to a int[].
     * @param oldArray the int[] to go first
     * @param newList the {@link List<Integer>} to be appended
     * @return a int[] consisting of all elements
     */
    private int[] append(int[] oldArray, List<Integer> newList) {
        int[] newArray;
        if (oldArray == null) {
            newArray = oldArray;
        } else {
            newArray = ArrayUtils.addAll(oldArray, newList.stream().mapToInt(i -> i).toArray());
        }

        return newArray;
    }

    /**
     * Cache the group of edges from the last parent.
     *
     * Necessary because these are skipped during parsing.
     */
    public void cacheLastEdges() {
        int[] oldChildren = this.getChildIDs(this.cache.getCurrentParentID());
        int[] allChildren = this.append(oldChildren, this.cache.getCurrentParentChildren());
        this.cache.getChildrenAdjacencyMap().put(this.cache.getCurrentParentID(), allChildren);
    }
}
