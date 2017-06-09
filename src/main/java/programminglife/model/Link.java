package programminglife.model;

/**
 * The class that handles the links used for the nodes.
 */
public class Link implements Edge {
    private int[] genomes;
    private Node start;
    private Node end;

    /**
     * Constructor for Link.
     * @param start the start (parent) of this Link
     * @param end the end (child) of this Link
     * @param genomes Thegenomes that flow through this Link.
     */
    public Link(Node start, Node end, int[] genomes) {
        this.start = start;
        this.end = end;
        this.genomes = genomes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getGenomes() {
        return this.genomes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getStart() {
        return start;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("Link from [node: %s] to [node: %s]",
                this.start.toString(),
                this.end.toString());
    }
}
