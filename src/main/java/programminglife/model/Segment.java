package programminglife.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Segment implements Node {
    private int id;
    private GenomeGraph graph;
    private int sequenceLength = -1;

    /**
     * Constructor for a node with an id.
     * @param graph the {@link GenomeGraph} around this {@link Segment}
     * @param id int.
     */
    public Segment(GenomeGraph graph, int id) {
        this(graph, id, null);
    }

    /**
     * Constructor for a node with and id and sequence.
     * @param graph the {@link GenomeGraph} around this {@link Segment}
     * @param id int.
     * @param sequence String.
     */
    public Segment(GenomeGraph graph, int id, String sequence) {
        this.graph = graph;
        this.id = id;
        if (sequence != null) {
            this.graph.setSequence(id, sequence);
            sequenceLength = sequence.length();
        }
    }

    /**
     * Getter for the sequence.
     * @return the sequence of base pairs
     */
    public String getSequence() {
        return graph.getSequence(this.id);
    }

    /**
     * Set the sequence of base pairs of the {@link Segment}.
     * @param sequence A {@link String} representing the base pairs
     */
    public void setSequence(String sequence) {
        graph.setSequence(this.id, sequence);
        sequenceLength = sequence.length();
    }

    @Override
    public int getSequenceLength() {
        if (sequenceLength == -1) {
            this.sequenceLength = this.graph.getSequenceLength(this.id);
        }
        return sequenceLength;
    }

    /**
     * Getter for the id.
     * @return int.
     */
    public int getIdentifier() {
        return this.id;
    }

    @Override
    public Collection<? extends Edge> getChildEdges() {
        Collection<Link> result = new HashSet<>();
        for (Node node : graph.getChildren(this.id)) {
            result.add(new Link(this, node, graph.getGenomes(node)));
        }
        return result;
    }

    @Override
    public Collection<? extends Edge> getParentEdges() {
        Collection<Link> result = new HashSet<>();
        for (Node node : graph.getParents(this.id)) {
            result.add(new Link(node, this, graph.getGenomes(this)));
        }
        return result;
    }

    @Override
    public Collection<? extends Node> getChildren() {
        return new HashSet<>(graph.getChildren(this.id));
    }

    @Override
    public Collection<? extends Node> getParents() {
        return new HashSet<>(graph.getParents(this.id));
    }

    @Override
    public Collection<Genome> getGenomes() {
        return this.graph.getGenomes(this);
    }

    /**
     * toString method for the node.
     * @return the {@link String} representation of a {@link Segment}
     */
    @Override
    public String toString() {
        String sequence = this.getSequence();
        return String.format("Segment<%d>[s(%d):%s]",
                this.getIdentifier(),
                sequence.length(),
                StringUtils.abbreviate(sequence, 11));
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof Segment) {
            Segment other = (Segment) that;
            if (this.id == other.getIdentifier()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
