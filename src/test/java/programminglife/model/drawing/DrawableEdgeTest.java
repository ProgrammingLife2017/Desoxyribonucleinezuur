package programminglife.model.drawing;

import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.gui.InitFXThread;
import programminglife.model.GenomeGraph;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Created by iwanh on 23/06/2017.
 */
public class DrawableEdgeTest {

    DrawableSegment parent;
    DrawableSegment child;
    DrawableEdge edge;
    SubGraph subGraph;
    GenomeGraph graph;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void SetUp(){
        graph = new GenomeGraph("Fiets");
        graph.replaceNode(3);
        graph.replaceNode(4);
        graph.replaceNode(5);

        graph.setSequence(3, "AATC");
        graph.setSequence(4, "ATTC");
        graph.setSequence(5, "AGTC");


        int[] genomeIDsA = {0, 1, 3};
        int[] genomeIDsB = {0, 1, 4};
        int[] genomeIDsC = {0, 2, 3};
        
        graph.addGenome("genoom1");
        graph.addGenome("genoom2");
        graph.addGenome("genoom3");
        graph.addGenome("genoom4");
        graph.addGenome("genoom5");

        graph.setGenomes(3, genomeIDsA);
        graph.setGenomes(4, genomeIDsB);
        graph.setGenomes(5, genomeIDsC);
    }

    @Test
    public void colorizeTest(){
        graph.addEdge(3,4);
        graph.cacheLastEdges();

        subGraph = new SubGraph(new DrawableSegment(graph, 3, 1),10,false);
        DrawableNode seg1 = subGraph.getNodes().get(3);
        DrawableNode seg2 = subGraph.getNodes().get(4);

        DrawableEdge edge = new DrawableEdge(seg1, seg2);

        edge.colorize(subGraph);

        assertEquals(3.2, edge.getStrokeWidth(), 0.001);
        assertEquals(0.4600000, edge.getStrokeColor().getBrightness(), 0.001);
    }

}
