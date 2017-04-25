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

    public boolean add(Node node) {
        return this.nodes.add(node);
    }

    public static void parse(String file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("data/chr19.hg38.w115.gfa"));
        while (scanner.hasNext()) {
            continue;
        }
    }
}
