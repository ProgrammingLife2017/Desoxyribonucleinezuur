package programminglife.model;

import java.util.Set;

/**
 * An Annotation for a genome.
 */
public class Annotation {
    private final String id;
    private final String file;
    private int genomeIndex;

    private final int startCoordinate;
    private final int endCoordinate;

    private final String source;
    private final String type;
    private final String score;
    private final String strand;
    private final int phase; // phase can be 0, 1 or 2.

    private final String originalGenomeID;
    private final String name;
    private final Set<String> aliases;
    private final String displayName;

    private final Set<String> parents;
    private final Set<String> children;

    private final String TargetID;
    private final int TargetStart;
    private final int TargetEnd;
    private final String targetStrand;

    private final String gap;
    private final String DerivesFrom;

    private final Set<String> notes;
    private final Set<String> Dbxrefs;
    private final Set<String> OntologyTerms;

    private final boolean is_circular;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Annotation that = (Annotation) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
