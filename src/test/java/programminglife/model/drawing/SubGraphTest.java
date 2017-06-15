package programminglife.model.drawing;

import org.junit.*;
import programminglife.model.*;
import programminglife.parser.Cache;
import programminglife.parser.GraphParser;
import programminglife.utility.InitFXThread;
import programminglife.utility.ProgressCounter;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubGraphTest {
    GenomeGraph graph;
    DrawableSegment centerNode;

    private static String TEST_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();

        TEST_PATH = new File(SubGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        File testFile = new File(TEST_PATH);
        GraphParser graphParser = new GraphParser(testFile);
        graphParser.parse();
        graph = graphParser.getGraph();

        centerNode = new DrawableSegment(graph, 4);
    }

    @After
    public void tearDown() throws Exception {
        graph.removeCache();
    }

    @Test
    public void testConstructorOnlyCenterNode() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 0);
        Set<DrawableNode> nodes = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(1, nodes.size());
        assertTrue(nodes.contains(centerNode));
    }

    @Test
    public void testConstructorRadius1() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 1);
        Set<DrawableNode> nodes = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(4, nodes.size());
        assertTrue(nodes.contains(centerNode));
        assertTrue(nodes.containsAll(sg.getChildren(centerNode)));
        assertTrue(nodes.containsAll(sg.getParents(centerNode)));
    }

    @Test
    public void testConstructorRadius4() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 4);

        Set<DrawableNode> expected = new HashSet<>();
        for (Integer id : new int[] {1, 2, 3, 4, 5, 6, 7, 8}) {
            expected.add(new DrawableSegment(graph, id));
        }

        Set<DrawableNode> actual = new LinkedHashSet<>(sg.getNodes().values());
        assertEquals(8, actual.size());
        assertEquals(expected, actual);
    }



    @Test
    public void topoSortTest() throws Exception {
        SubGraph sg = new SubGraph(centerNode, 5);
        List<DrawableNode> actual = sg.topoSort();

        Set<DrawableNode> graphNodes = new LinkedHashSet<>(sg.getNodes().values());

        assertEquals(graphNodes.size(), actual.size());

        Set<DrawableNode> found = new HashSet<>();
        for (DrawableNode n : actual) {
            Collection<DrawableNode> parents = sg.getParents(n);
            parents.retainAll(graphNodes);

            // assert that all parents that are also in the SubGraph were already found.
            assertTrue(found.containsAll(parents)); // All parents of this node were already found
            assertTrue(Collections.disjoint(found, sg.getChildren(n))); // none of the children of this node were found
            found.add(n);
        }
    }

    @Test
    public void genomesOnSimpleInsertion() {
        // [3]--- 0 2 3 ---[5]
        //   \            /
        //   1 4         /
        //     \        /
        //     [4]-- 1 4

        GenomeGraph g = new GenomeGraph("test-simple");

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

        SubGraph sg = new SubGraph(new DrawableSegment(g, 3), 5);
        Map<DrawableNode, Map<DrawableNode, Collection<Integer>>> genomes = sg.calculateGenomes();

        DrawableNode seg3 = sg.getNodes().get(3);
        DrawableNode seg4 = sg.getNodes().get(4);
        DrawableNode seg5 = sg.getNodes().get(5);

        assertEquals(new HashSet<>(Arrays.asList(1, 4)), new HashSet<>(genomes.get(seg3).get(seg4)));
        assertEquals(new HashSet<>(Arrays.asList(0, 2, 3)), new HashSet<>(genomes.get(seg3).get(seg5)));
        assertEquals(new HashSet<>(Arrays.asList(1, 4)), new HashSet<>(genomes.get(seg4).get(seg5)));
    }

    @Test
    public void genomesOnComplexInsertion() {
        GenomeGraph g = new GenomeGraph("test-complex");

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

        SubGraph sg = new SubGraph(new DrawableSegment(g, 3), 5);
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
    public void genomesOnStoppingGenome() {
        GenomeGraph g = new GenomeGraph("test-stopping");

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

        SubGraph sg = new SubGraph(new DrawableSegment(g, 3), 5);

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
