package programminglife.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Graph {
    private static final boolean PARSE_LINE_VERBOSE_DEFAULT = false;
    private static final int PARSE_LINE_LOG_INTERVAL_DEFAULT = 1000;

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
        try {
            return this.nodes.get(id);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private void parseSegment(Scanner sc) {
        Node parsedNode = Node.parseSegment(sc);
        Node existingNode = this.getNode(parsedNode.getId());
        if (existingNode == null) {
            this.addNode(parsedNode);
        } else {
            existingNode.setSequence(parsedNode.getSequence());
        }
    }

    private void parseLink(Scanner sc) {
        int sourceId = Integer.parseInt(sc.next());
        sc.next();
        int destinationId = Integer.parseInt(sc.next());
        sc.next();
        String overlap = sc.next();

        Node sourceNode = this.getNode(sourceId);
        Node destinationNode = this.getNode(destinationId);

        if (null == sourceNode) {
            throw new Error("Source node does not (yet) exist!");
        }

        if (null == destinationNode) {
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
        return parse(file, verbose, PARSE_LINE_LOG_INTERVAL_DEFAULT);
    }

    public static Graph parse(String file, boolean verbose, int lineInterval) throws FileNotFoundException {
        if (verbose) {
            System.out.println(String.format("Parsing file %s", file));
        }

        Scanner lineScanner = new Scanner(new File(file));
        Graph graph = new Graph(null);
        boolean logCurrentLine = false;

        int numLinesRead = 0;
        int numNodesParsed = 0;
        int numLinksParsed = 0;
        int miscParsed = 0;

        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            Scanner tokenScanner = new Scanner(line);
            tokenScanner.useDelimiter("\t");
            String token = tokenScanner.next();
            numLinesRead++;

            if (verbose && numLinesRead % lineInterval == 0) {
                logCurrentLine = true;
            } else {
                logCurrentLine = false;
            }

            if (logCurrentLine) {
                System.out.println(String.format("Token %s read (line %d)...", token, numLinesRead));
            }

            switch (token) {
                case "S":
                    // Parse segment
                    graph.parseSegment(tokenScanner);
                    numNodesParsed++;
                    break;
                case "L":
                    // Parse link
                    graph.parseLink(tokenScanner);
                    numLinksParsed++;
                    break;
                default:
                    // Otherwise
                    miscParsed++;
            }
            tokenScanner.close();
        }

        lineScanner.close();

        if (verbose) {
            System.out.println();
            System.out.println(String.format("%d lines read from file %s", numLinesRead, file));
            System.out.println(String.format("%d segments parsed", numNodesParsed));
            System.out.println(String.format("%d links parsed", numLinksParsed));
            System.out.println(String.format("%d miscellaneous parsed", miscParsed));
            System.out.println();
        }

        for (Node n : graph.nodes.values()) {
            if (n != null && n.getParents().isEmpty()) {
                graph.rootNodes.add(n);
                if (verbose) {
                    System.out.println(String.format("Node %d is a root node", n.getId()));
                }
            }
        }

        return graph;
    }
}
