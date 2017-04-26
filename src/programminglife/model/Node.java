package programminglife.model;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Node {
    private int id;
    private String sequence;

    private Set<Node> parents;
    private Set<Node> children;

    public Node(int id) {
        this(id, new String(), new HashSet<>(), new HashSet<>());
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

    public static Node parseSegment(Scanner sc) {
        int id = Integer.parseInt(sc.next());
        String segment = sc.next();
        sc.next();
        String origin = sc.next();
        String offset = sc.next();
        String readCount = sc.next();

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
}
