package programminglife.model;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by toinehartman on 03/05/2017.
 */
public class GraphTest {
    Graph graph;
    Node node;
    String link;

    @Before
    public void setUp() throws Exception {
        graph = new Graph("test graph");
        node = new Node(3, "ATCG");
        link = "L\t34\t+\t35\t+\t0M";

        graph.addNode(node);
    }

    @Test
    public void addNodeTest() throws Exception {
        Node secondNode = new Node(8);
        graph.addNode(secondNode);

        assertEquals(2, graph.getNodes().size());
        assertEquals(node, graph.getNode(3));
        assertEquals(secondNode, graph.getNode(8));
    }

    @Test
    public void parseTest() throws FileNotFoundException {
        graph = Graph.parse("data/test/test.gfa");
        Collection<Node> nodes = graph.getNodes();

        assertEquals(8, nodes.size());
        assertEquals(9, nodes.stream()
                                        .mapToInt(node -> node.getChildren().size())
                                        .sum());
    }

    @Test(expected = NoSuchElementException.class)
    public void getNodeTest() {
        assertEquals(node, graph.getNode(3));
        graph.getNode(121);
    }

    @Test
    public void parseLinkTest() {
        graph.parseLink(link);
    }
}
