package programminglife.model;

import javafx.scene.shape.Rectangle;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Segment extends Rectangle implements Node<Segment> {
    private int id;
    private String sequence;

    /**
     * Constructor for a node with an id.
     * @param id int.
     */
    public Segment(int id) {
        this(id, "");
    }

    /**
     * Constructor for a node with and id and sequence.
     * @param id int.
     * @param sequence String.
     */
    public Segment(int id, String sequence) {
        this.id = id;
        DataManager.setSequence(id, sequence);
    }

    /**
     * Getter for the sequence.
     * @return the sequence of base pairs
     */
    public String getSequence() {
        return DataManager.getSequence(this.id);
    }

    /**
     * Set the sequence of base pairs of the {@link Segment}.
     * @param sequence A {@link String} representing the base pairs
     */
    public void setSequence(String sequence) {
        DataManager.setSequence(this.id, sequence);
        this.setDrawDimensions();
    }

    public XYCoordinate getRightBorderCenter() {
        return this.getCenter().add(this.getSize().getX() >> 1, 0);
    }

    public XYCoordinate getLeftBorderCenter() {
        return this.getCenter().add(-(this.getSize().getX() >> 1), 0);
    }

    public XYCoordinate getCenter() {
        return this.getLocation().add(this.getSize().multiply(0.5));
    }

    /**
     * Getter for the id.
     * @return int.
     */
    public int getIdentifier() {
        return this.id;
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

    /**
     * Get a {@link XYCoordinate} representing the size of the {@link Segment}.
     * @return The size of the {@link Segment}
     */
    public XYCoordinate getSize() {
        return new XYCoordinate((int) this.getWidth(), (int) this.getHeight());
    }

    /**
     * Set the size {@link XYCoordinate} of the {@link Segment}.
     * @param size The {@link XYCoordinate} representing the size
     */
    void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
    }

    /**
     * Getter for top left corner of a {@link Segment}.
     * @return {@link XYCoordinate} with the values of the top left corner.
     */
    public XYCoordinate getLocation() {
        return new XYCoordinate((int) this.getX(), (int) this.getY());
    }

    /**
     * Set an {@link XYCoordinate} representing the location of the {@link Segment}.
     * @param location The {@link XYCoordinate}
     */
    public void setLocation(XYCoordinate location) {
        this.setX(location.getX());
        this.setY(location.getY());
    }

    public XYCoordinate getWidthCoordinate() {
        return new XYCoordinate((int) this.getWidth(), 0);
    }

    public XYCoordinate getHeightCoordinate() {
        return new XYCoordinate(0, (int) this.getHeight());
    }

    /**
     * Setter for the dimension of the node.
     */
    private void setDrawDimensions() {
        int segmentLength = this.getSequence().length();
        int width, height;

        width = 10 + (int) Math.pow(segmentLength, 1.0 / 2);
        height = 10;

        this.setSize(new XYCoordinate(width, height));
    }

    /**
     * get the length of the sequence of this segment.
     * @return the length of the sequence of this segment
     */
    public int getSequenceLength() {
        return this.getSequence().length();
    }
}
