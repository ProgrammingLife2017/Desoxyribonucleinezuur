package programminglife.model;


import org.jetbrains.annotations.NotNull;
import programminglife.model.exception.NodeExistsException;
import programminglife.parser.Cache;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class GenomeGraph implements Graph {
    private String id;
    private Cache cache;

    // TODO cache graph structure
    private Map<Integer, Set<Integer>> children;
    private Map<Integer, Set<Integer>> parents;

    // TODO cache genomes
    private Map<String, Genome> genomes;

    /**
     * Create a genomeGraph with id.
     * @param id id of the graph
     */
    public GenomeGraph(String id) {
        this(id, new HashMap<>(), new HashMap<>());
    }

    /**
     * The constructor for a GenomeGraph.
     * @param id String.úú
     * @param children {@link Set} which contains the children.
     * @param parents {@link Set} which contains the parents.
     */
    public GenomeGraph(String id, Map<Integer, Set<Integer>> children, Map<Integer, Set<Integer>> parents) {
        this.id = id;
        this.children = children;
        this.parents = parents;
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
        this.addNode(node, new HashSet<>(), new HashSet<>());
    }

    @Override
    public void addNode(Node node, Set<Node> children, Set<Node> parents) {
        if (this.contains(node)) {
            throw new NodeExistsException(String.format("%s already exists in graph %s",
                    node.toString(), this.getID()));
        }

        this.replaceNode(node, children, parents);
    }

    @Override
    public void replaceNode(Node node) {
        this.replaceNode(node, new HashSet<>(), new HashSet<>());
    }

    @Override
    public void replaceNode(Node node, Set<Node> children, Set<Node> parents) {
        this.children.put(node.getIdentifier(), children.stream().map(Node::getIdentifier).collect(Collectors.toSet()));
        this.parents.put(node.getIdentifier(), parents.stream().map(Node::getIdentifier).collect(Collectors.toSet()));
    }

    /**
     * Get the number of nodes in the {@link GenomeGraph}.
     * @return the number of nodes
     */
    public int size() {
        assert (children.size() == parents.size());
        return this.children.size();
    }

    @Override
    public Set<Segment> getChildren(Node node) {
        return this.getChildren(node.getIdentifier());
    }

    @Override
    public Set<Segment> getChildren(int nodeID) {
        return this.children.get(nodeID).stream().
                map(id -> new Segment(this, id, cache.getSequence(id))).collect(Collectors.toSet());
    }

    @Override
    public Set<Segment> getParents(Node node) {
        return this.getParents(node.getIdentifier());
    }

    @Override
    public Set<Segment> getParents(int nodeID) {
        return this.parents.get(nodeID).stream().
                map(id -> new Segment(this, id, cache.getSequence(id))).collect(Collectors.toSet());
    }

    @Override
    public Collection<Genome> getGenomes(Node node) {
        return null;
        //TODO
    }

    @Override
    public Collection<Genome> getGenomes(Node parent, Node child) {
        return null;
        //TODO
    }

    @Override
    public Link getLink(Node parent, Node child) {
        return new Link(parent, child, getGenomes(parent, child));
    }

    @Override
    public boolean contains(Node node) {
        return this.contains(node.getIdentifier());
    }

    @Override
    public boolean contains(int nodeID) {
        return this.children.containsKey(nodeID);
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
        this.children.get(node.getIdentifier()).add(child.getIdentifier());
    }

    /**
     * Add a parent to a node.
     * @param node Node to which the parent will be added.
     * @param parent Node of the parent to be added.
     */
    private void addParent(Node node, Node parent) {
        this.parents.get(node.getIdentifier()).add(parent.getIdentifier());
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
    @NotNull
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
    @NotNull
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
     * @throws IOException when strange things happen
     */
    public void close() throws IOException {
        if (this.cache != null) {
            this.cache.close();
            this.cache = null;
        }
    }
}
