package programminglife.model;

import org.apache.commons.lang3.NotImplementedException;
import programminglife.model.exception.NodeExistsException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class GenomeGraph implements Graph {
    private String id;

    private Map<Integer, Set<Integer>> children;
    private Map<Integer, Set<Integer>> parents;

    /**
     * Create a genomeGraph with name.
     * @param name name of the graph
     */
    public GenomeGraph(String name) {
        this(name, new HashMap<>(), new HashMap<>());
    }

    /**
     * The constructor for a GenomeGraph.
     * @param id String id.
     */
    public GenomeGraph(String id, Map<Integer, Set<Integer>> children, Map<Integer, Set<Integer>> parents) {
        this.children = children;
        this.parents = parents;
        this.id = id;
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
            throw new NodeExistsException(String.format("%s already exists in graph %s", node.toString(), this.getID()));
        }

        this.replaceNode(node, children, parents);
    }

    @Override
    public void replaceNode(Node node) {
        this.replaceNode(node, new HashSet<>(), new HashSet<>());
    }

    @Override
    public void replaceNode(Node node, Set<Node> children, Set<Node> parents) {
        this.children.put(node.getIdentifier(), children.stream().map(c ->
                c.getIdentifier()).collect(Collectors.toSet()));
        this.parents.put(node.getIdentifier(), parents.stream().map(p ->
                p.getIdentifier()).collect(Collectors.toSet()));
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
        return this.children.get(nodeID).stream().map(id -> new Segment(id)).collect(Collectors.toSet());
    }

    @Override
    public Set<Segment> getParents(Node node) {
        return this.getParents(node.getIdentifier());
    }

    @Override
    public Set<Segment> getParents(int nodeID) {
        return this.parents.get(nodeID).stream().map(id -> new Segment(id)).collect(Collectors.toSet());
    }

    @Override
    public Set<Genome> getGenomes(Node node) {
        throw new NotImplementedException("GenomeGraph#getGenomes(Node) is not yet implemented");
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

    private void addChild(Node node, Node child) {
        this.children.get(node.getIdentifier()).add(child.getIdentifier());
    }

    private void addParent(Node node, Node parent) {
        this.parents.get(node.getIdentifier()).add(parent.getIdentifier());
    }
}