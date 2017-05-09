package programminglife.gui.controller;

import javafx.scene.canvas.GraphicsContext;
import programminglife.model.Graph;
import programminglife.model.Node;

import java.util.Set;

/**
 * Created by Martijn van Meerten on 8-5-2017.
 * Controller for the graph.
 */
public class GraphController {
    private Graph graph;

    /**
     * Initialize controller object.
     * @param graph - The genome graph to control
     */
    public GraphController(Graph graph) {
        this.graph = graph;
    }

    /**
     * Getter for the graph.
     * @return - The graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Draws a subgraph on the canvas based on center node and radius.
     * @param graphicsContext - The graphicsContext used by the canvas
     * @param n - The node to draw
     * @return - A set of all neighbouring nodes
     */
    public Set<Node> drawSubGraph(GraphicsContext graphicsContext, Node n) {
        graphicsContext.rect(n.getSequence().length(), 20, 20, 20);
        Set<Node> nodes = n.getChildren();
        nodes.addAll(n.getParents());
        return nodes;
    }
}
