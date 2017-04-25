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
        this.rootNodes = new TreeSet<>();
    }

    public boolean addNode(Node node) {
        return this.nodes.add(node);
    }

    public Node getNode(int id) {
        return this.nodes.get(id);
    }

    public static Graph parse(String file) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(file));
        sc.useDelimiter("\t");
        Graph g = new Graph(null);
        int countLines = 0;

        while (sc.hasNext() && countLines < 10) {
            String token = sc.next();
            switch (token) {
                case "S":
                    Node parsedNode = Node.parseSegment(sc);
                    Node existingNode = g.getNode(parsedNode.getId());
                    if (existingNode == null)
                        g.addNode(parsedNode);
                    else {
                        existingNode.setSequence(parsedNode.getSequence());
                    }
                    break;
                case "L":
                    // Parse link
                    break;
            }

            countLines++;
        }

        return g;
    }
}
