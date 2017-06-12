package programminglife.model;

/**
 * The class that handles the links used for the nodes.
 */
public class Link implements Edge {
    private int[] genomes;
    private int startID;
    private int endID;

    /**
     * Constructor for Link.
     * @param startID the startID (parent) of this Link
     * @param endID the endID (child) of this Link
     * @param genomes The genomes that flow through this Link.
     */
    public Link(int startID, int endID, int[] genomes) {
        this.startID = startID;
        this.endID = endID;
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
    public int getStartID() {
        return startID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEndID() {
        return endID;
    }

    @Override
    public String toString() {
        return String.format("Link from [node: %s] to [node: %s]",
                this.startID,
                this.endID);
    }
}
