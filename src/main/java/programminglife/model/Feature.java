package programminglife.model;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Feature within a Genome.
 */
public class Feature {
    private final String id;
    private final String fileName;

    private final Set<Annotation> annotations;

    /**
     * Create a Feature from an {@link Annotation}. The Annotation will be added to this feature.
     * @param annotation The Annotation to create this Feature from.
     */
    public Feature(Annotation annotation) {
        this(annotation.getId(), annotation.getFileName());
        add(annotation);
    }

    /**
     * Constructor for a Feature.
     * @param id The ID of this feature
     * @param file The file of this Feature.
     */
    public Feature(String id, File file) {
        this(id, file.getAbsolutePath());
    }

    /**
     * Create a Feature from an ID and a filename.
     * @param id The ID for this Feature.
     * @param fileName The fileName for this Feature.
     */
    private Feature(String id, String fileName) {
        this.id = id;
        this.fileName = fileName;
        this.annotations = new LinkedHashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Feature feature = (Feature) o;

        return id.equals(feature.id) && fileName.equals(feature.fileName);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 31 * fileName.hashCode();
    }

    /**
     * Add an annotation to this Feature. This Annotation should have the same ID as the Annotation.
     * @param annotation The annotation to add.
     */
    public void add(Annotation annotation) {
        assert (annotation.getId().equals(this.id));
        assert (annotation.getFileName().equals(this.fileName));

        this.annotations.add(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Feature {ID: %s; file %s; Annotations [%s]}", id, fileName,
                annotations.stream().map(Annotation::toString).collect(Collectors.joining(", ")));
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }
}
