package programminglife.model;

import org.junit.*;
import programminglife.model.exception.NodeExistsException;
import programminglife.parser.Cache;
import programminglife.utility.InitFXThread;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * The class that handles the tests for the genome graph.
 */
public class GenomeGraphTest {


    private static final String TEST_DB = "test.db";
    GenomeGraph graph;
    int nodeID;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp() throws Exception {
        graph = new GenomeGraph("test");
        nodeID = 3;
        graph.setSequence(nodeID, "ATCG");

        graph.replaceNode(nodeID);
    }

    @After
    public void tearDown() throws Exception {
        graph.removeCache();
    }

    @Test
    public void getNodeTest() {
        assertTrue(graph.contains(3));
    }

    @Test
    public void getNodeNullTest() {
        assertNull(graph.getChildIDs(121));
    }

    @Test(expected = NoSuchElementException.class)
    public void getNonExistingGenomeTest() {
        graph.getNodeIDs(graph.getGenomeID("nonexistent"));
    }

    @Test
    public void sizeTest() {
        assertEquals(1,graph.size());
        graph.replaceNode(2);
        graph.setSequence(2, "A");
        assertEquals(2,graph.size());
    }

    @Test
    public void containsTest() {
        int node2ID = 2;
        graph.setSequence(2, "ATTCTT");
        graph.replaceNode(node2ID);
        assertTrue(graph.contains(node2ID));

        int node3ID = 37;
        graph.setSequence(37, "AAAAAAAA");
        assertFalse(graph.contains(node3ID));
    }

    @Test
    public void replaceExistingNodeTest() {
        assertEquals("ATCG", graph.getSequence(3));
        graph.setSequence(nodeID, "AAAA");
        graph.replaceNode(nodeID);
        assertEquals("AAAA", graph.getSequence(3));
    }

    @Test
    public void addEdgeTest(){
        graph.replaceNode(42);
        graph.addEdge(3,42);
        //cacheLastEdges has to be called because else the child relations are not good
        graph.cacheLastEdges();
        assertArrayEquals(new int[]{42}, graph.getChildIDs(3));

    }

//    /**
//     * This test should not pass for now untill a fix from Toine his branch is merged.
//     */
//    @Test
//    public void addEdgeNoSource() {
//        graph.addEdge(17, 18);
//        graph.cacheLastEdges();
//        //both nodes should exist.
//        assertTrue(graph.contains(17));
//        assertTrue(graph.contains(18));
//    }

    @Test
    public void addMultipleEdgesSameSource() {
        graph.replaceNode(4);
        graph.replaceNode(5);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        assertTrue(graph.contains(4));
        assertTrue(graph.contains(5));

    }

    @Test
    public void addMultipleEdgesDifferentSources() {
        graph.replaceNode(4);
        graph.replaceNode(5);
        graph.addEdge(3,4);
        graph.addEdge(4,5);
        assertArrayEquals(new int[]{4},graph.getChildIDs(3));
        //This should return an empty array because cacheLastEdges is not called yet.
        assertArrayEquals(new int[]{}, graph.getChildIDs(4));
        graph.cacheLastEdges();
        //Now it should return 5.
        assertArrayEquals(new int[]{5}, graph.getChildIDs(4));

    }

    @Test
    public void addAndGetGenomeTest() {
        int[] genomeID = {37};
        graph.setGenomes(3, genomeID);
        assertArrayEquals(genomeID, graph.getGenomes(3));

    }

    @Test
    public void addAndGetMultipleGenomeTest() {
        int[] genomeIDs = {37,38,39};
        graph.setGenomes(4, genomeIDs);
        assertArrayEquals(genomeIDs, graph.getGenomes(4));
        assertEquals(3, graph.getGenomes(4).length);
    }

////    TODO: WIP
//    @Test
//    public void getGenomesOnEdgeTest() {
//        int[] genomeIDsA = {37,55,89};
//        int[] genomeIDsB = {37,55,103};
//        graph.setGenomes(3, genomeIDsA);
//        graph.setGenomes(4, genomeIDsB);
//        graph.replaceNode(4);
//        assertArrayEquals(new int[]{}, graph.getGenomes(3, 4));
//        graph.replaceNode(4);
//        graph.addEdge(3,4);
//        graph.cacheLastEdges();
//        assertArrayEquals(new int[]{37, 55}, graph.getGenomes(3, 4));
//    }




}
