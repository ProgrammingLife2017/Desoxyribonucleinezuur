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
        this.nodes.add(null);
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
        destinationNode.addChild(sourceNode);
    }

    public static Graph parse(String file) throws FileNotFoundException {
        Scanner lineScanner = new Scanner(new File(file));
        Graph g = new Graph(null);

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

            System.out.print(String.format("Token %s read (line %d)... ", token, numLinesRead));

            switch (token) {
                case "S":
                    // Parse segment
                    System.out.println("parsing segment");
                    Node parsedNode = Node.parseSegment(tokenScanner);
                    Node existingNode = g.getNode(parsedNode.getId());
                    if (existingNode == null)
                        g.addNode(parsedNode);
                    else
                        existingNode.setSequence(parsedNode.getSequence());

                    numNodesParsed++;
                    break;
                case "L":
                    // Parse link
                    System.out.println("parsing link");
                    g.parseLink(tokenScanner);
                    numLinksParsed++;
                    break;
                default:
                    System.out.println("strange token on line!");
                    miscParsed++;
            }
            tokenScanner.close();
        }

        lineScanner.close();

        System.out.println(String.format("%d lines read from file %s", numLinesRead, file));
        System.out.println(String.format("%d segments parsed", numNodesParsed));
        System.out.println(String.format("%d links parsed", numLinksParsed));
        System.out.println(String.format("%d miscellaneous parsed", miscParsed));

        return g;
    }
}
