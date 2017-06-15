package programminglife.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An Annotation for a genome.
 */
public class Annotation {
    private final String id;
    private final String file;
    private int genomeIndex;
    private final String originalGenomeID;

    private final int startCoordinate;
    private final int endCoordinate;

    private final Map<String, Set<String>> textFields;

    /**
     * A constructor for an Annotation.
     * @param id The ID of this Annotation.
     * @param file The file for this Annotation.
     * @param originalGenomeID The original String for the genome. These should be mapped to the
     *                         genome indices in the {@link GenomeGraph}.
     * @param startCoordinate The starting coordinate within the genome.
     * @param endCoordinate The end coordinate within the genome.
     */
    public Annotation(String id, String file, String originalGenomeID,
                      int startCoordinate, int endCoordinate) {
        this.id = id;
        this.file = file;
        this.genomeIndex = -1;
        this.originalGenomeID = originalGenomeID;
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.textFields = new LinkedHashMap<>();
    }

    /**
     * Add a text attribute.
     * If the attribute already exists, this value is added to it.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    public void addAttribute(String name, String value) {
        getOrCreateAttribute(name).add(value);
    }

    /**
     * Add multiple values to a single attribute.
     * If the attribute did not exist yet, it is created.
     * @param name The name of the attribute.
     * @param values The values to add.
     */
    public void addMultiAttribute(String name, Set<String> values) {
        getOrCreateAttribute(name).addAll(values);
    }

    /**
     * Add multiple values to a single attribute.
     * If the attribute did not exist yet, it is created.
     * @param name The name of the attribute.
     * @param values The values to add.
     */
    public void addMultiAttribute(String name, String[] values) {
        Collections.addAll(getOrCreateAttribute(name), values);
    }

    /**
     * Get or create an attribute. This returns the value set for an attribute.
     * If the attribute didn't exist yet, it is created.
     * @param name The name of the attribute.
     * @return The set of values for this attribute.
     */
    @NotNull
    private Set<String> getOrCreateAttribute(String name) {
        return textFields.computeIfAbsent(name, k -> new HashSet<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Annotation that = (Annotation) o;

        return id.equals(that.id)
                && file.equals(that.file)
                && startCoordinate == that.startCoordinate
                && endCoordinate == that.endCoordinate
                && (
                        genomeIndex == that.genomeIndex && genomeIndex != -1 // they have the same genomeIndex
                        || originalGenomeID.equals(that.originalGenomeID) // or the original names were equal
                )
                && textFields.equals(that.textFields);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 31 * file.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Annotations {ID: %s; file: %s; genomeIndex: %d; "
                        + "originalGenomeID: %s; start: %d; end: %d; textFields: %s}",
                id,
                file,
                genomeIndex,
                originalGenomeID,
                startCoordinate,
                endCoordinate,
                textFields.entrySet().stream().map(entry ->
                    String.format("{%s: {%s}}", entry.getKey(),
                            entry.getValue().stream().collect(Collectors.joining(", ")))
                ).collect(Collectors.joining(", "))
        );
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return file;
    }

    public int getGenomeIndex() {
        return genomeIndex;
    }

    public void setGenomeIndex(int genomeIndex) {
        this.genomeIndex = genomeIndex;
    }

    public String getOriginalGenomeID() {
        return originalGenomeID;
    }

    public int getStartCoordinate() {
        return startCoordinate;
    }

    public int getEndCoordinate() {
        return endCoordinate;
    }

    public Map<String, Set<String>> getTextFields() {
        return textFields;
    }
}
