package programminglife.model.drawing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.gui.InitFXThread;
import programminglife.model.GenomeGraph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by toinehartman on 19/06/2017.
 */
public class SubGraphGenomesOnEdgeTest {
    GenomeGraph g;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        this.g = new GenomeGraph("genomes on edge test");
    }

    @After
    public void tearDown() throws Exception {
        g.removeCache();
    }

    @Test
    public void genomesOnSimpleInsertion() throws Exception {
        // [3]--- 0 2 3 ---[5]
        //   \            /
        //   1 4         /
        //     \        /
        //     [4]-- 1 4

        for (int i = 0; i <= 4; i++) {
            g.addGenome("GENOME" + i);
        }

        for (int i = 3; i <= 5; i++) {
            g.replaceNode(i);
            g.setSequence(i, "A");
        }

        g.setGenomes(3, new int[] {0, 1, 2, 3, 4});
        g.setGenomes(4, new int[] {1, 4});
        g.setGenomes(5, new int[] {0, 1, 2, 3, 4});

        g.addEdge(3,4);
        g.addEdge(4,5);
        g.addEdge(3,5);

        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 3), 5, false);
        Map<DrawableNode, Map<DrawableNode, Collection<Integer>>> genomes = sg.calculateGenomes();

        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);
        DrawableNode seg5 = sg.getNodes().get(5);

        assertEquals(new HashSet<>(Arrays.asList(1, 4)), new HashSet<>(genomes.get(seg3).get(seg4)));
        assertEquals(new HashSet<>(Arrays.asList(0, 2, 3)), new HashSet<>(genomes.get(seg3).get(seg5)));
        assertEquals(new HashSet<>(Arrays.asList(1, 4)), new HashSet<>(genomes.get(seg4).get(seg5)));
    }

    @Test
    public void genomesOnComplexInsertion() throws Exception {
        for (int i = 0; i <= 5; i++) {
            g.addGenome("GENOME" + i);
        }

        for (int i = 3; i <= 6; i++) {
            g.replaceNode(i);
            g.setSequence(i, "A");
        }

        g.setGenomes(3, new int[] {0, 1, 2, 3, 4, 5});
        g.setGenomes(4, new int[] {2, 5});
        g.setGenomes(5, new int[] {0, 1, 5});
        g.setGenomes(6, new int[] {0, 1, 2, 3, 4, 5});

        for (int[] edge : new int[][] {{3, 4}, {3, 5}, {3, 6}, {4, 5}, {4, 6}, {5, 6}}) {
            g.addEdge(edge[0], edge[1]);
        }

        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 3), 5, false);
        Map<DrawableNode, Map<DrawableNode, Collection<Integer>>> genomes = sg.calculateGenomes();

        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);
        DrawableNode seg5 = sg.getNodes().get(5);
        DrawableNode seg6 = sg.getNodes().get(6);

        assertEquals(new HashSet<>(Arrays.asList(2, 5)), new HashSet<>(genomes.get(seg3).get(seg4)));
        assertEquals(new HashSet<>(Arrays.asList(0, 1)), new HashSet<>(genomes.get(seg3).get(seg5)));
        assertEquals(new HashSet<>(Arrays.asList(3, 4)), new HashSet<>(genomes.get(seg3).get(seg6)));
        assertEquals(new HashSet<>(Arrays.asList(2)), new HashSet<>(genomes.get(seg4).get(seg6)));
        assertEquals(new HashSet<>(Arrays.asList(5)), new HashSet<>(genomes.get(seg4).get(seg5)));
        assertEquals(new HashSet<>(Arrays.asList(0, 1, 5)), new HashSet<>(genomes.get(seg5).get(seg6)));
    }

    @Test
    public void genomesOnStoppingGenome() throws Exception {
        for (int i = 0; i <= 3; i++) {
            g.addGenome("GENOME" + i);
        }

        for (int i = 3; i <= 6; i++) {
            g.replaceNode(i);
            g.setSequence(i, "A");
        }

        g.setGenomes(3, new int[] {0, 1, 2, 3});
        g.setGenomes(4, new int[] {0, 1, 2});
        g.setGenomes(5, new int[] {0, 3});
        g.setGenomes(6, new int[] {0, 2, 3});

        for (int[] edge : new int[][] {{3, 4}, {3, 5}, {4, 5}, {4, 6}, {5, 6}}) {
            g.addEdge(edge[0], edge[1]);
        }

        g.cacheLastEdges();

        SubGraph sg = new SubGraph(new DrawableSegment(g, 3), 5, false);

        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);
        DrawableNode seg5 = sg.getNodes().get(5);
        DrawableNode seg6 = sg.getNodes().get(6);

        Map<DrawableNode, Map<DrawableNode, Collection<Integer>>> genomes = sg.calculateGenomes();

        assertEquals(new HashSet<>(Arrays.asList(0, 1, 2)), new HashSet<>(genomes.get(seg3).get(seg4)));
        assertEquals(new HashSet<>(Arrays.asList(3)), new HashSet<>(genomes.get(seg3).get(seg5)));
        assertEquals(new HashSet<>(Arrays.asList(2)), new HashSet<>(genomes.get(seg4).get(seg6)));
        assertEquals(new HashSet<>(Arrays.asList(0)), new HashSet<>(genomes.get(seg4).get(seg5)));
        assertEquals(new HashSet<>(Arrays.asList(0, 3)), new HashSet<>(genomes.get(seg5).get(seg6)));
    }
}
