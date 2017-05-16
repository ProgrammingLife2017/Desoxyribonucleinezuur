package programminglife.model;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

/**
 * Created by toinehartman on 03/05/2017.
 */
public class GraphTest {
    Graph graph;
    Node node;

    @Before
    public void setUp() throws Exception {
        graph = new Graph("test graph");
        node = new Node(3, "ATCG");

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
    public void getNodeTest2() {
        assertEquals(node, graph.getNode(3));
    }

    @Test(expected = NoSuchElementException.class)
    public void getNodeTest1() {
        graph.getNode(121);
    }
}
