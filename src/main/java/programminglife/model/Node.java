package programminglife.model;

import javafx.scene.shape.Rectangle;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Node extends Rectangle {
    private int id;
    private String sequence;

    private Set<Node> parents;
    private Set<Node> children;

    /**
     * Constructor for a node with an id.
     * @param id int.
     */
    public Node(int id) {
        this(id, "", new HashSet<>(), new HashSet<>());
    }

    /**
     * Constructor for a node with and id and sequence.
     * @param id int.
     * @param sequence String.
     */
    public Node(int id, String sequence) {
        this(id, sequence, new HashSet<>(), new HashSet<>());
    }

    /**
     * Constructor for a node with an id, sequence, parents and children.
     * @param id the id of the {@link Node}
     * @param sequence the sequence of base pairs
     * @param parents a {@link Set} of parents
     * @param children a {@link Set} of children
     */
    public Node(int id, String sequence, Set<Node> parents, Set<Node> children) {
        this.id = id;
        this.sequence = sequence;
        this.parents = parents;
        this.children = children;

        this.setDrawDimensions();
    }

    /**
     * Getter for the sequence.
     * @return the sequence of base pairs
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set the sequence of base pairs of the {@link Node}.
     * @param sequence A {@link String} representing the base pairs
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;

        this.setDrawDimensions();
    }

    /**
     * Method to add a child to a node.
     * @param child Node.
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Method to add a parent to a node.
     * @param parent Node.
     */
    public void addParent(Node parent) {
        this.parents.add(parent);
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
     * Get the {@link Set} of parents.
     * @return the {@link Set} of parents
     */
    public Set<Node> getParents() {
        return parents;
    }

    /**
     * Get the {@link Set} of children.
     * @return the {@link java.util.Set} of children
     */
    public Set<Node> getChildren() {
        return children;
    }

    /**
     * toString method for the node.
     * @return the {@link String} representation of a {@link Node}
     */
    @Override
    public String toString() {
        return String.format("Node<%d>(c(%d):%s, p(%d):%s, s(%d):%s)",
                this.getIdentifier(),
                this.getChildren().size(),
                this.getChildren().stream().map(c -> c.getIdentifier()).collect(Collectors.toList()),
                this.getParents().size(),
                this.getParents().stream().map(p -> p.getIdentifier()).collect(Collectors.toList()),
                this.getSequence().length(),
                StringUtils.abbreviate(this.getSequence(), 11));
    }

    /**
     * Get a {@link XYCoordinate} representing the size of the {@link Node}.
     * @return The size of the {@link Node}
     */
    public XYCoordinate getSize() {
        return new XYCoordinate((int) this.getWidth(), (int) this.getHeight());
    }

    /**
     * Set the size {@link XYCoordinate} of the {@link Node}.
     * @param size The {@link XYCoordinate} representing the size
     */
    void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
    }

    /**
     * Getter for top left corner of a {@link Node}.
     * @return {@link XYCoordinate} with the values of the top left corner.
     */
    public XYCoordinate getLocation() {
        return new XYCoordinate((int) this.getX(), (int) this.getY());
    }

    /**
     * Set an {@link XYCoordinate} representing the location of the {@link Node}.
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
}
