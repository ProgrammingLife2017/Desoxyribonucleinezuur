package programminglife.model;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Created by Ivo on 2017-05-17.
 */
public class Genome {
    private String name;

    /**
     * An ordered Map of coordinates and Segments. Their order is the order in which order they appear in this Genome.
     * The Integer represents the coordinate of the first base pair of the Segment it maps to.
     */
    private TreeMap<Integer, Segment> segments;
    private int size;

    /**
     * Constructor for {@link Genome}, no segments added.
     * @param name the name of the {@link Genome}
     */
    public Genome(String name) {
        this.name = name;
        this.size = 0;
        this.segments = new TreeMap<>();
    }

    /**
     * Constructor for a Genome. The coordinates are used as is, so modifying it will also modify this Genome!
     * This is done for the sake of performance.
     * @param name The name of this Genome.
     * @param segments A list of segments, sorted in the order in which they appear in this Genome.
     */
    public Genome(String name, List<Segment> segments) {
        this.name = name;
        this.size = 0;
        this.segments = new TreeMap<>();

        // TODO: verify (assert) that the order of segments is correct.

        int coordinate = 1; // coordinates are 1 based.
        for (Segment s : segments) {
            this.segments.put(coordinate, s);
            coordinate += s.getSequenceLength();
            this.size += 1;
        }
    }

    /**
     * Get the Segment with the base pair at coordinate.
     * @param coordinate The coordinate of the base pair
     * @return The Segment that contains the base pair.
     */
    public Segment getSegment(int coordinate) {
        return segments.floorEntry(coordinate).getValue();
    }

    /**
     * get the first Segment in this Genome.
     * @return the first Segment in this Genome.
     */
    public Segment getStart() {
        return this.segments.firstEntry().getValue();
    }

    /**
     * get the last Segment in this Genome.
     * @return the last Segment in this Genome.
     */
    public Segment getEnd() {
        return this.segments.lastEntry().getValue();
    }

    /**
     * Get the name of the {@link Genome}.
     * @return its name
     */
    public String getName() {
        return name;
    }

    /**
     * Add a {@link Segment} to this {@link Genome}.
     * @param segment the specific {@link Segment}
     */
    public void addSegment(Segment segment) {
        int coordinate;
        try {
            coordinate = this.segments.lastKey() + segment.getSequenceLength();
        } catch (NoSuchElementException e) {
            coordinate = 1;
        }

        this.segments.put(coordinate, segment);
        this.size += 1;
    }

    public int getSize() {
        return this.size;
    }
}
