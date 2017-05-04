package programminglife.model;

import programminglife.model.exception.UnknownTypeException;

import java.io.*;
import java.util.*;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Graph {
    private static final boolean PARSE_LINE_VERBOSE_DEFAULT = false;

    private String id;
    private Set<Node> rootNodes;

    /**
     * A list of nodes ordered by ID. Assumption: Nodes appear in GFA file in sequential order.
     */
    private Map<Integer, Node> nodes;

    public Graph(String id) {
        this.nodes = new HashMap<>();
        this.id = id;
        this.rootNodes = new HashSet<>();
    }

    public Node addNode(Node node) {
        return this.nodes.put(node.getId(), node);
    }

    public Node getNode(int id) {
        if (this.nodes.containsKey(id)) {
            return this.nodes.get(id);
        } else {
            throw new NoSuchElementException("There is no node with ID " + id);
        }
    }

    public Collection<Node> getNodes() {
        return this.nodes.values();
    }

    private void parseSegment(String propertyString) {
        Node parsedNode = Node.parseSegment(propertyString);
        Node existingNode;
        try {
            existingNode = this.getNode(parsedNode.getId());
            existingNode.setSequence(parsedNode.getSequence());
        } catch (NoSuchElementException e) {
            this.addNode(parsedNode);
        }
    }

    void parseLink(String propertyString) {
        String[] properties = propertyString.split("\\s");
        // properties[0] is 'L'
        int sourceId = Integer.parseInt(properties[1]);
        // properties[2] is unused
        int destinationId = Integer.parseInt(properties[3]);
        // properties[4] and further are unused

        Node sourceNode, destinationNode;

        try {
            sourceNode = this.getNode(sourceId);
        } catch (NoSuchElementException e) {
            sourceNode = new Node(sourceId);
            this.addNode(sourceNode);
        }

        try {
            destinationNode = this.getNode(destinationId);
        } catch (NoSuchElementException e) {
            destinationNode = new Node(destinationId);
            this.addNode(destinationNode);
        }

        sourceNode.addChild(destinationNode);
        destinationNode.addParent(sourceNode);
    }

    public static Graph parse(String file) throws FileNotFoundException {
        return parse(file, PARSE_LINE_VERBOSE_DEFAULT);
    }

    public static Graph parse(String file, boolean verbose) throws FileNotFoundException {
        if (verbose) {
            System.out.println(String.format("Parsing file %s", file));
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Graph graph = new Graph(null);

        reader.lines().forEach(line -> {
            char type = line.charAt(0);

            switch (type) {
                case 'S':
                    // Parse segment
                    graph.parseSegment(line);
                    break;
                case 'L':
                    // Parse link
                    graph.parseLink(line);
                    break;
                case 'H':
                    System.out.println(line);
                    break;
                default:
                    // Otherwise
                    throw new UnknownTypeException(String.format("Unknown type '%c'", type));
            }
        });

        for (Node n : graph.nodes.values()) {
            if (n != null && n.getParents().isEmpty()) {
                graph.rootNodes.add(n);
                if (verbose) {
                    System.out.println(String.format("Root node: %s", n));
                }
            }
        }

        return graph;
    }
}
