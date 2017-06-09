package programminglife.model;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import programminglife.model.exception.NodeExistsException;
import programminglife.parser.Cache;
import programminglife.utility.ProgressCounter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The class that handles the genome graph.
 */
public class GenomeGraph implements Graph {
    private String id;
    private Cache cache;
    private Map<Integer, Collection<Integer>> parentGenomesNodes;

    /**
     * Create a genomeGraph with id.
     * @param id id of the graph
     */
    public GenomeGraph(String id) {
        this.id = id;
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
    public void addNode(int nodeID) {
        this.addNode(nodeID, new int[0], new int[0]);
    }

    @Override
    public void addNode(int nodeID, int[] children, int[] parents) {
        if (this.contains(nodeID)) {
            throw new NodeExistsException(String.format("Node<%d> already exists in graph %s",
                    nodeID, this.getID()));
        }

        this.replaceNode(nodeID, children, parents);
    }

    @Override
    public void replaceNode(int nodeID) {
        this.replaceNode(nodeID, new int[0], new int[0]);
    }

    @Override
    public void replaceNode(int nodeID, int[] children, int[] parents) {
        this.cache.getChildrenAdjacencyMap().put(nodeID, children);
        this.cache.getParentsAdjacencyMap().put(nodeID, parents);
    }

    /**
     * Get the number of nodes in the {@link GenomeGraph}.
     * @return the number of nodes
     */
    public int size() {
        return this.cache.getNumberOfNodes();
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
        return new Link(parent, child, getGenomes(parent.getIdentifier(), child.getIdentifier()));
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

    /**
     * Get the Genomes through a specific Node.
     * @param nodeID the Node to look up
     * @return a {@link Collection} of Genome IDs
     */
    public int[] getGenomes(int nodeID) {
        return this.cache.getGenomes(nodeID);
    }

    /**
     * Get for the genomes of a parent and child.
     * @param parentID Node of the parent.
     * @param childID Node of the child.
     * @return int[] list the genomes.
     */
    public int[] getGenomes(int parentID, int childID) {
        Collection<Integer> mutualGenomes = new LinkedHashSet<>();
        int[] parentGenomes = getGenomes(parentID);

        // For every genome of the parent
        for (int parentGenome : parentGenomes) {
            Collection<Integer> genome = parentGenomesNodes.get(parentGenome);
            // set this flag to false
            boolean parentFound = false;

            // for every node of this genome
            for (int nodeID : genome) {
                if (nodeID == parentID) {
                    // if it is the parent, set the flag
                    parentFound = true;
                } else if (parentFound && nodeID == childID) {
                    // the child is a direct successor of the parent,
                    // so the genome does go through this edge
                    mutualGenomes.add(parentGenome);
                    break;
                } else if (parentFound) {
                    // the child is not a direct successor of the parent,
                    // so the genome does not go through this edge
                    break;
                }
            }
        }
        return mutualGenomes.stream().mapToInt(x -> x).toArray();
    }

    /**
     * Return a collection of names.
     * @param genomes the IDs of the genomes to identify
     * @return their names
     */
    public Collection<String> getGenomeNames(int[] genomes) {
        return Arrays.stream(genomes)
                .mapToObj(this::getGenomeName)
                .collect(Collectors.toList());
    }

    /**
     * Get Nodes through a Genome.
     * @param genomeID the Genome to look up
     * @return a {@link Collection} of Node IDs in the Genome
     */
    Collection<Integer> getNodeIDs(int genomeID) {
        return this.cache.getGenomeNodeIDs(genomeID);
    }

    /**
     * Get Nodes through several Genomes.
     * @param progressCounter ProgressCounter keep track of the progress.
     * @param genomeIDs the Genomes to look up
     * @return a {@link Map} mapping Genome names to {@link Collection}s of Node IDs
     */
    private Map<Integer, Collection<Integer>> getNodeIDs(ProgressCounter progressCounter, int... genomeIDs) {
        return this.cache.getGenomeNodeIDs(progressCounter, genomeIDs);
    }

    /**
     * Get nodes from a collection.
     * @param pc ProgressCounter keep track of the progress.
     * @param genomeIDs Collection of the genome id's.
     * @return the mapped genome id's.
     */
    private Map<Integer, Collection<Integer>> getNodeIDs(ProgressCounter pc, Collection<Integer> genomeIDs) {
        return this.getNodeIDs(pc, genomeIDs.stream().mapToInt(x -> x).toArray());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Node node) {
        return this.contains(node.getIdentifier());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(int nodeID) {
        return this.cache.getChildrenAdjacencyMap().containsKey(nodeID);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdge(int sourceID, int destinationID) {
        this.addChild(sourceID, destinationID);
        this.addParent(destinationID, sourceID);
    }

    /**
     * Add a child to a node.
     * @param nodeID Node ID to which the child will be added.
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
     * @param nodeID Node ID to which the parent will be added.
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
     * @param nodeID the Node to address
     * @param genomeIDs the Genomes through this Node
     */
    public void setGenomes(int nodeID, int[] genomeIDs) {
        this.cache.setGenomes(nodeID, genomeIDs);
    }

    /**
     * Get the name of a Genome.
     * @param genomeID the ID of the Genome
     * @return its name
     */
    public String getGenomeName(int genomeID) {
        return this.cache.getGenomeName(genomeID);
    }

    public Collection<String> getGenomeNames() {
        return this.cache.getGenomeNamesIdMap().keySet();
    }

    /**
     * Get the ID of a Genome.
     * @param genomeName the name of the Genome
     * @return its ID
     */
    public int getGenomeID(String genomeName) {
        return this.cache.getGenomeID(genomeName);
    }

    /**
     * Add a Genome to the {@link GenomeGraph}.
     * @param name its name
     */
    public void addGenome(String name) {
        this.cache.addGenomeName(name);
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
     */
    public void close() {
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

    /**
     * The fraction of genomes through a node.
     * @param nodeID the ID of the nods
     * @return the fraction
     */
    public double getGenomeFraction(int nodeID) {
        return this.getGenomesNumber(nodeID) / (double) this.cache.getGenomeNamesIdMap().size();
    }

    /**
     * The fraction of genomes through a link.
     * @param link the link
     * @return the fraction
     */
    public double getGenomeFraction(Link link) {
        return link.getGenomes().length / (double) this.cache.getGenomeNamesIdMap().size();
    }

    /**
     * The number of genomes through a node.
     * @param nodeID the ID of the node
     * @return the number
     */
    private int getGenomesNumber(int nodeID) {
        return this.cache.getNodeIdGenomesNumberMap().get(nodeID);
    }

    public int getTotalGenomeNumber() {
        return this.cache.getGenomeNamesIdMap().size();
    }

    /**
     * Load the genomes from a map.
     * @param progressCounter ProgressCounter keeps track of the progress.
     */
    public void loadGenomes(ProgressCounter progressCounter) {
        this.parentGenomesNodes = getNodeIDs(progressCounter, this.cache.getGenomeIdNamesMap().keySet());
    }
}
