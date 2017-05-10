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

    private XYCoordinate size;
    private XYCoordinate location;

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
    }

    /**
     * Getter for the sequence.
     * @return the sequence of base pairs
     */
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Parse a {@link Node} from a {@link String}.
     * @param propertyString the {@link String} from a GFA file.
     * @return the parsed {@link Node}.
     */
    public static Node parseSegment(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("S")); // properties[0] is 'S'
        int id = Integer.parseInt(properties[1]);
        String segment = properties[2];
        // properties[3] is +/-
        // rest of properties is unused

        return new Node(id, segment);
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
        return this.getCenter().add(this.size.getX() >> 1, 0);
    }

    public XYCoordinate getLeftBorderCenter() {
        return this.getCenter().add(-(this.size.getX() >> 1), 0);
    }

    public XYCoordinate getCenter() {
        return location.add(new XYCoordinate(size.getX() >> 1, size.getY() >> 1));
    }

    /**
     * Getter for the id.
     * @return int.
     */
    public int getId() {
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
        return String.format("Node<%d>(c:%s, p:%s, s:%s)",
                this.getId(),
                this.getChildren().stream().map(c -> c.getId()).collect(Collectors.toList()),
                this.getParents().stream().map(p -> p.getId()).collect(Collectors.toList()),
                StringUtils.abbreviate(this.getSequence(), 11));
    }

    public XYCoordinate getSize() {
        return size;
    }

    public void setSize(XYCoordinate size) {
        this.size = size;
    }

    public XYCoordinate getLocation() {
        return location;
    }

    public void setLocation(XYCoordinate location) {
        this.location = location;
    }
}
