package programminglife.model;

import org.junit.*;
import programminglife.model.exception.NodeExistsException;
import programminglife.parser.Cache;
import programminglife.utility.InitFXThread;
import programminglife.utility.ProgressCounter;

import java.util.*;

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

////    TODO: WIP Cant get this test to work at the moment.

//    @Test
//    public void getGenomesOnEdgeTest() {
//        graph.replaceNode(4);
//
//        int[] genomeIDsA = {37,55,89};
//        int[] genomeIDsB = {37,55,103};
//
//        graph.setGenomes(3, genomeIDsA);
//        graph.setGenomes(4, genomeIDsB);
//
//        graph.addEdge(3,4);
//        graph.cacheLastEdges();
//        graph.loadGenomes(new ProgressCounter("testLoading"));
//        assertArrayEquals(new int[]{37,55}, graph.getGenomes(3, 4));
////        graph.replaceNode(4);
////        graph.addEdge(3,4);
////        graph.cacheLastEdges();
////        assertArrayEquals(new int[]{37, 55}, graph.getGenomes(3, 4));
//    }

    @Test (expected = NoSuchElementException.class)
    public void getSequenceNotExistingNode() {
        graph.getSequence(4);
    }

    @Test (expected = NoSuchElementException.class)
    public void getSequenceLengthNotExistingNode() {
        graph.getSequenceLength(4);
    }

    @Test
    public void getGenomeNames() {
        graph.addGenome("Genome1");
        graph.addGenome("fasta123");
        HashSet<String> genomes = new HashSet<>();
        genomes.add("Genome1");
        genomes.add("fasta123");
        assertEquals(genomes, graph.getGenomeNames());
        genomes.add("AnotherGenome");
        assertEquals(3,genomes.size());
        assertNotEquals(genomes, graph.getGenomeNames());
    }

    @Test
    public void getGenomeFractionNode(){
        graph.addGenome("Genome1");
        graph.addGenome("Genome2");
        graph.addGenome("Genome3");
        int[] genomeIDs = new int[]{graph.getGenomeID("Genome1"),
                graph.getGenomeID("Genome2")};

        graph.setGenomes(nodeID, genomeIDs);
        assertEquals(0.66666,graph.getGenomeFraction(nodeID), 0.001);
        graph.addGenome("Genome4");
        assertEquals(0.5, graph.getGenomeFraction(nodeID), 0.001);
    }

    @Test (expected = NoSuchElementException.class)
    public void getGenomeNameNoCache() {
        graph.getGenomeName(1231231);
    }

    @Test (expected = NoSuchElementException.class)
    public void getGenomeIDNonExisting() {
        graph.getGenomeID("ThisGenomeDoesNotExist");
    }

    @Test
    public void getGenomeNameCorrect () {
        graph.addGenome("TestGenome");
        graph.addGenome("OtherGenome");
        int testID = graph.getGenomeID("TestGenome");
        int otherID = graph.getGenomeID("OtherGenome");
        assertNotEquals(testID, otherID);

        assertEquals("TestGenome", graph.getGenomeName(testID));
        assertNotEquals("TestGenome", graph.getGenomeName(otherID));

    }

    @Test
    public void getMultipleGenomeNamesCorrect () {
        graph.addGenome("TestGenome");
        graph.addGenome("OtherGenome");
        int testID = graph.getGenomeID("TestGenome");
        int otherID = graph.getGenomeID("OtherGenome");

        HashSet<String> genomeNamesExpected = new HashSet<>();
        genomeNamesExpected.add("TestGenome");
        genomeNamesExpected.add("OtherGenome");
        HashSet<String> actual = new HashSet<>(graph.getGenomeNames(new int[]{testID, otherID}));
        assertEquals(genomeNamesExpected, actual);
    }

    @Test
    public void getGenomeNodeIDs() {
        graph.replaceNode(4);
        graph.replaceNode(5);

        int[] genomeIDsA = {37,55,89};
        int[] genomeIDsB = {37,55,103};
        int[] genomeIDsC = {37,66,89};

        graph.setGenomes(3, genomeIDsA);
        graph.setGenomes(4, genomeIDsB);
        graph.setGenomes(5, genomeIDsC);

        HashSet<Integer> expected = new HashSet<Integer>();
        expected.add(3);
        expected.add(4);

        HashSet<Integer> actual = new HashSet<>(graph.getNodeIDs(55));
        assertEquals(expected,actual);

        expected.add(5);
        actual = new HashSet<>(graph.getNodeIDs(37));
        assertEquals(expected, actual);
    }






}
