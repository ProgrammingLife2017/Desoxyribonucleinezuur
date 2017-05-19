package programminglife.model;

import java.util.Set;

public interface Graph {
    String getID();

    void addNode(Node node);

    void addNode(Node node, Set<Node> children, Set<Node> parents);

    void replaceNode(Node node);

    void replaceNode(Node node, Set<Node> children, Set<Node> parents);

    int size();

    Set<Segment> getChildren(Node node);

    Set<Segment> getChildren(int nodeID);

    Set<Segment> getParents(Node node);

    Set<Segment> getParents(int nodeID);

    Set<Genome> getGenomes(Node node);

    boolean contains(Node node);

    boolean contains(int nodeID);

    void addEdge(Node source, Node destination);
}
