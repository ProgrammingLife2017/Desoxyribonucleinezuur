package programminglife.model;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Node {
    private int id;
    private String sequence;

    private Set<Node> parents;
    private Set<Node> children;

    public Node(int id) {
        this(id, "", new HashSet<>(), new HashSet<>());
    }

    public Node(int id, String sequence) {
        this(id, sequence, new HashSet<>(), new HashSet<>());
    }

    public Node(int id, String sequence, Set<Node> parents, Set<Node> children) {
        this.id = id;
        this.sequence = sequence;
        this.parents = parents;
        this.children = children;
    }

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

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void addParent(Node parent) {
        this.parents.add(parent);
    }

    public int getId() {
        return this.id;
    }

    public Set<Node> getParents() {
        return parents;
    }

    public Set<Node> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return String.format("Node<%d>(c:%s, p:%s, s:%s)",
                this.getId(),
                this.getChildren().stream().map(c -> c.getId()).collect(Collectors.toList()),
                this.getParents().stream().map(p -> p.getId()).collect(Collectors.toList()),
                StringUtils.abbreviate(this.getSequence(), 11));
    }
}
