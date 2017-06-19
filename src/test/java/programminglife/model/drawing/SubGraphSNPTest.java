package programminglife.model.drawing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.model.GenomeGraph;
import programminglife.utility.InitFXThread;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by toinehartman on 19/06/2017.
 */
public class SubGraphSNPTest {
    GenomeGraph g;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        g = new GenomeGraph("snp test graph");
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }

    @Test
    public void simpleSNP() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);
        g.replaceNode(3);

        g.setSequence(0, "ATCG");
        g.setSequence(1, "A");
        g.setSequence(2, "C");
        g.setSequence(3, "GCTA");

        for (int i = 0; i <= 3; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);
        DrawableNode seg3 = sg.getNodes().get(3);

        assertNotNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));
        assertNull(seg3.hasSNPChildren(sg));

        g.removeCache();
    }

    @Test
    public void quadSNP() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);
        g.replaceNode(3);
        g.replaceNode(4);
        g.replaceNode(5);

        g.setSequence(0, "ATCG");
        g.setSequence(1, "A");
        g.setSequence(2, "C");
        g.setSequence(3, "G");
        g.setSequence(4, "T");
        g.setSequence(5, "GCTA");

        for (int i = 0; i <= 5; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(0, 4);
        g.addEdge(1, 5);
        g.addEdge(2, 5);
        g.addEdge(3, 5);
        g.addEdge(4, 5);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);
        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);
        DrawableNode seg5 = sg.getNodes().get(5);

        assertNotNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));
        assertNull(seg3.hasSNPChildren(sg));
        assertNull(seg4.hasSNPChildren(sg));
        assertNull(seg5.hasSNPChildren(sg));

        g.removeCache();
    }

    @Test
    public void fiveSNP() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);
        g.replaceNode(3);
        g.replaceNode(4);
        g.replaceNode(5);
        g.replaceNode(6);

        g.setSequence(0, "ATCG");
        g.setSequence(1, "A");
        g.setSequence(2, "C");
        g.setSequence(3, "G");
        g.setSequence(4, "T");
        g.setSequence(5, "T");
        g.setSequence(6, "GCTA");

        for (int i = 0; i <= 6; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(0, 4);
        g.addEdge(0, 5);
        g.addEdge(1, 6);
        g.addEdge(2, 6);
        g.addEdge(3, 6);
        g.addEdge(4, 6);
        g.addEdge(5, 6);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);
        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);
        DrawableNode seg5 = sg.getNodes().get(5);
        DrawableNode seg6 = sg.getNodes().get(6);

        assertNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));
        assertNull(seg3.hasSNPChildren(sg));
        assertNull(seg4.hasSNPChildren(sg));
        assertNull(seg5.hasSNPChildren(sg));
        assertNull(seg6.hasSNPChildren(sg));

        g.removeCache();
    }

    @Test
    public void singleSNP() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);

        g.setSequence(0, "ATCG");
        g.setSequence(1, "A");
        g.setSequence(2, "CTGA");

        for (int i = 0; i <= 2; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);

        assertNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));

        g.removeCache();
    }

    @Test
    public void multipleNucleotideSNP() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);
        g.replaceNode(3);

        g.setSequence(0, "ATCG");
        g.setSequence(1, "AT");
        g.setSequence(2, "C");
        g.setSequence(3, "GCTA");

        for (int i = 0; i <= 3; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);
        DrawableNode seg3 = sg.getNodes().get(3);

        assertNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));
        assertNull(seg3.hasSNPChildren(sg));

        g.removeCache();
    }

    @Test
    public void simpleSNPWithParent() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);
        g.replaceNode(3);
        g.replaceNode(4);

        g.setSequence(0, "G");
        g.setSequence(1, "ATCG");
        g.setSequence(2, "A");
        g.setSequence(3, "C");
        g.setSequence(4, "GCTA");

        for (int i = 0; i <= 4; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(3, 4);
        g.addEdge(2, 4);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);
        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);

        assertNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));
        assertNull(seg3.hasSNPChildren(sg));
        assertNull(seg4.hasSNPChildren(sg));

        g.removeCache();
    }

    @Test
    public void simpleSNPWithChild() throws Exception {
        g.addGenome("GENOME");

        g.replaceNode(0);
        g.replaceNode(1);
        g.replaceNode(2);
        g.replaceNode(3);
        g.replaceNode(4);

        g.setSequence(0, "ATCG");
        g.setSequence(1, "A");
        g.setSequence(2, "C");
        g.setSequence(3, "GCTA");
        g.setSequence(4, "G");

        for (int i = 0; i <= 4; i++)
            g.setGenomes(i, new int[] {0});

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.addEdge(2, 4);
        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 0), 5);
        DrawableNode seg0 = sg.getNodes().get(0);
        DrawableNode seg1 = sg.getNodes().get(1);
        DrawableNode seg2 = sg.getNodes().get(2);
        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);

        assertNull(seg0.hasSNPChildren(sg));
        assertNull(seg1.hasSNPChildren(sg));
        assertNull(seg2.hasSNPChildren(sg));
        assertNull(seg3.hasSNPChildren(sg));
        assertNull(seg4.hasSNPChildren(sg));

        g.removeCache();
    }
}
