package programminglife.gui.controller;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.model.Graph;
import programminglife.model.Node;
import programminglife.model.SubGraph;
import programminglife.parser.GraphParser;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Ivo on 2017-05-14.
 */
public class SubGraphTest {
    private static String TEST_PATH;

    @BeforeClass
    public static void setUpClass() throws Exception {
        TEST_PATH = new File(SubGraphTest.class.getResource("/test.gfa").toURI()).getAbsolutePath();
    }

    @Test
    public void addAllEmpty() throws Exception {
        // TODO: Add all on an empty subgraph
    }

    @Test
    public void addAllNew() throws Exception {
        // TODO: Add all on a subgraph that already contains nodes
    }

    @Test
    public void addAllDuplicates() throws Exception {
        // TODO: Add all with duplicates (should throw exception)
    }

    @Test
    public void replaceAllEmpty() throws Exception {
        // TODO: Replace all in empty graph
        // check:
        //   all nodes are added to graph
    }

    @Test
    public void replaceAllPartial() throws Exception {
        // TODO: Replace all on a graph where some nodes will be replaced
        // checks:
        //   new nodes are added to graph
        //   all duplicates are replaced (!sg.contains(oldNode))
        //   previous nodes are still there.
    }

    @Test
    public void containsPositive() throws Exception {
        // TODO check contains on graph that actually contains it
    }

    @Test
    public void containsNegative() throws Exception {
        // TODO check contains on graph that does not contain it
    }

    @Test
    public void addNodeNew() throws Exception {
        // TODO: add a new node
        // Checks:
        //   new node is inserted
        //   old Nodes are still there
    }

    @Test
    public void addNodeDuplicate() throws Exception {
        // TODO: add a new node that already exists (should throw exception)
        // Checks:
        //   throws Exception
        //   new Node is NOT inserted
        //   old nodes are still there.
    }

    @Test
    public void replaceNodeEmpty() throws Exception {
        // TODO: replace in empty graph
        // Checks:
        //   new node is inserted
        //   returns null;
    }

    @Test
    public void replaceNodeNew() throws Exception {
        // TODO: replace in graph that already contains nodes, but do not replace something that exists
        // Checks:
        //   new node is inserted
        //   old nodes are still there.
        //   returns null;
    }

    @Test
    public void replaceNodeDuplicate() throws Exception {
        // TODO: replace in graph that already contains nodes, replace something that exists
        // Checks:
        //   new node is inserted
        //   old node is deleted.
        //   returns old Node.
    }

    @Test
    public void removeNodeExists() throws Exception {
        // TODO: check that removing a Node from the graph removes it (!sg.contains(node))
    }

    @Test
    public void removeNodeNotExists() throws Exception {
        // TODO: check that removing a Node that does not exist from the graph does not change it.
    }

    @Test
    public void containsAnyEmpty() throws Exception {
        // TODO: check that it returns false when the collection is empty.
    }

    @Test
    public void containsAnyNone() throws Exception {
        // TODO: check that it returns false when none of the Nodes match.
    }

    @Test
    public void containsAnyOne() throws Exception {
        // TODO: check that it returns true if one node is in the graph.
    }

    @Test
    public void containsAnySome() throws Exception {
        // TODO: check that it returns true if some nodes are in the graph.
    }

    @Test
    public void containsAnyAll() throws Exception {
        // TODO: check that it returns true if all nodes are in the graph.
    }

    @Test
    public void recalculateRootsAndEnds() throws Exception {
        // TODO: think of something that is not already tested in the individual method tests (change both at once?)
    }

    @Test
    public void recalculateRootsNothing() throws Exception {
        // TODO: changing nothing should not change the roots
    }

    @Test
    public void recalculateRootsAdd() throws Exception {
        // TODO: check when adding a root
    }

    @Test
    public void recalculateRootsRemove() throws Exception {
        // TODO: check when removing a root
    }

    @Test
    public void recalculateRootsBoth() throws Exception {
        // TODO: check when removing and adding a root at the same time.
    }

    @Test
    public void recalculateEndsNothing() throws Exception {
        // TODO: changing nothing should not change the ends
    }

    @Test
    public void recalculateEndsAdd() throws Exception {
        // TODO: check when adding an end
    }

    @Test
    public void recalculateEndsRemove() throws Exception {
        // TODO: check when removing an end
    }

    @Test
    public void recalculateEndsBoth() throws Exception {
        // TODO: check when removing and adding an end at the same time.
    }

    @Test
    public void iterator() throws Exception {
        // TODO: check that iterator returns all Nodes.
    }

    @Test
    public void topoSortNormal() throws Exception {
        GraphParser gp = new GraphParser(new File(TEST_PATH));
        gp.parse();

        SubGraph sg = new SubGraph(gp.getGraph());

        List<Node> sorted = sg.topoSort();
        Set<Node> found = new HashSet<>();

        for (Node n : sorted) {
            // n should not yet have been in the list.
            assertFalse(found.contains(n));

            for (Node p : n.getParents()) {
                assertTrue(found.contains(p));
            }

            for (Node c : n.getChildren()) {
                assertFalse(found.contains(c));
            }

            found.add(n);
        }
    }

    @Test
    public void topoSortEmpty() throws Exception {
        assertEquals(new ArrayList<>(new SubGraph().topoSort()), new ArrayList<Node>());
    }

    @Test
    public void topoSortCircular() throws Exception {
        // TODO: check that it throws an exception when the graph contains a cycle
        // Note: set a time check (can be done with JUnit)
        // so that it doesn't run forever when implemented improperly.
    }

    @Test
    public void calculateNodeLocations() throws Exception {
        // TODO: think of a way to test this.
    }

}