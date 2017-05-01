package programminglife.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Graph {
    private String id;
    private Set<Node> rootNodes;
    private List<Node> nodes;

    public Graph(String id) {
        this.nodes = new ArrayList<>();
        this.nodes.add(new Node(-1));
        this.id = id;
        this.rootNodes = new HashSet<>();
    }

    public boolean addNode(Node node) {
        return this.nodes.add(node);
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
        if (existingNode == null)
            this.addNode(parsedNode);
        else
            existingNode.setSequence(parsedNode.getSequence());
    }

    private void parseLink(Scanner sc) {
        int sourceId = Integer.parseInt(sc.next());
        sc.next();
        int destinationId = Integer.parseInt(sc.next());
        sc.next();
        String overlap = sc.next();

        Node sourceNode = this.getNode(sourceId);
        Node destinationNode = this.getNode(destinationId);

        if (null == sourceNode)
            throw new Error("Source node does not (yet) exist!");

        if (null == destinationNode) {
            destinationNode = new Node(destinationId);
            this.addNode(destinationNode);
        }

        sourceNode.addChild(destinationNode);
        destinationNode.addParent(sourceNode);
    }

    public static Graph parse(String file) throws FileNotFoundException {
        System.out.println(String.format("Parsing file %s", file));

        Scanner lineScanner = new Scanner(new File(file));
        Graph graph = new Graph(null);

        int numLinesRead = 0;
        int numNodesParsed = 0;
        int numLinksParsed = 0;
        int miscParsed = 0;

        boolean verbose = false;

        while (lineScanner.hasNextLine()) {
            String line = lineScanner.nextLine();
            Scanner tokenScanner = new Scanner(line);
            tokenScanner.useDelimiter("\t");
            String token = tokenScanner.next();
            numLinesRead++;

            if (numLinesRead % 2500 == 0)
                verbose = true;
            else
                verbose = false;

            if (verbose) System.out.println(String.format("Token %s read (line %d)... ", token, numLinesRead));

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

        System.out.println();
        System.out.println(String.format("%d lines read from file %s", numLinesRead, file));
        System.out.println(String.format("%d segments parsed", numNodesParsed));
        System.out.println(String.format("%d links parsed", numLinksParsed));
        System.out.println(String.format("%d miscellaneous parsed", miscParsed));
        System.out.println();

        for (Node n : graph.nodes) {
            if (n != null && n.getParents().isEmpty() && n.getId() > 0) {
                graph.rootNodes.add(n);
                System.out.println(String.format("Node %d is a root node", n.getId()));
            }
        }

        return graph;
    }
}
