package programminglife.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by iwanh on 30/05/2017.
 */
public class Dummy implements Node {
    private int id;
    private Link link;
    private Node parent;
    private Node child;

    /**
     * Create a dummy node.
     * @param parent {@link Node} is the parent node.
     * @param child {@link Node} is the child node.
     * @param link {@link Link} the link between two segments in which this dummy node gets drawn.
     */
    public Dummy(Node parent, Node child, Link link) {
        id = -1;
        this.parent = parent;
        this.child = child;
        this.link = link;
    }

    @Override
    public int getIdentifier() {
        return id;
    }

    @Override
    public Collection<? extends Edge> getChildEdges() {
        Collection result = new HashSet<Link>();
        result.add(new Link(this, child, child.getGenomes()));
        return result;
    }

    @Override
    public Collection<? extends Edge> getParentEdges() {
        Collection result = new HashSet<Link>();
        result.add(new Link(parent, this, this.getGenomes()));
        return result;
    }

    @Override
    public Collection<? extends Node> getChildren() {
        Collection result = new HashSet();
        result.add(child);
        return result;
    }

    @Override
    public Collection<? extends Node> getParents() {
        Collection result = new HashSet();
        result.add(child);
        return result;
    }

    @Override
    public Collection<Genome> getGenomes() {
        return this.link.getGenomes();
    }

    @Override
    public int getSequenceLength() {
        return this.getSequenceLength();
    }
}