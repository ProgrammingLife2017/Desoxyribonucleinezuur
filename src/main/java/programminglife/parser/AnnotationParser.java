package programminglife.parser;

import com.diffplug.common.base.Errors;
import org.jetbrains.annotations.NotNull;
import programminglife.model.Annotation;
import programminglife.model.Feature;
import programminglife.model.exception.UnknownTypeException;

import java.io.*;
import java.util.*;

/**
 * A Parser for {@link Annotation Annotations}.
 */
public class AnnotationParser extends Observable implements Runnable {
    private File file;
    private Map<String, Feature> features;
    private ProgressCounter progressCounter;

    /**
     * Constructor for an AnnotationParser.
     * @param file The {@link File} to parse.
     */
    public AnnotationParser(File file) {
        this.file = file;
        this.features = null;
        progressCounter = new ProgressCounter("Lines read");
    }

    @Override
    public void run() {
        try {
            parseFile(file);
            this.setChanged();
            this.notifyObservers(features);
        } catch (UnknownTypeException | IOException e) {
            this.setChanged();
            this.notifyObservers(e);
        }
    }

    /**
     * Parse the file. This changes this AnnotationParser,
     * but it is the responsibility of the caller to notify the observers.
     * @param file The file to parse.
     * @throws IOException If there is an issue opening / closing the file.
     * @throws UnknownTypeException If the file is malformed.
     */
    private void parseFile(File file) throws IOException, UnknownTypeException {
        int numberOfLines = GraphParser.countLines(file);
        progressCounter.setTotal(numberOfLines);

        this.features = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines().forEach(Errors.rethrow().wrap(line -> {
                Annotation annotation = parseLine(line);

                features.computeIfAbsent(annotation.getId(), name -> new Feature(annotation)).add(annotation);

                progressCounter.count();
            }));
        } catch (Errors.WrappedAsRuntimeException e) {
            this.features = null;
            if (e.getCause() instanceof UnknownTypeException) {
                throw new UnknownTypeException(
                        String.format("An error occurred while parsing line %d", progressCounter.getProgress()),
                        e.getCause()
                );
            } else {
                throw e;
            }
        } catch (FileNotFoundException e) {
            features = null;
            throw e;
        } catch (OutOfMemoryError e) {
            System.out.println("line: " + progressCounter.getProgress());
            throw e;
        } finally {
            progressCounter.finished();
        }
    }

    /**
     * Parse a String as an Annotation.
     * @param line The String to parse
     * @return The parsed Annotation. If this line is empty or a comment (starts with #), this returns null.
     * @throws UnknownTypeException If the line is malformed.
     */
    private Annotation parseLine(String line) throws UnknownTypeException {
        // check that line is not a comment or is empty.
        if (line.length() == 0 || line.charAt(0) == '#') {
            return null;
        }

        String[] columns = line.split("\t");

        if (columns.length != 9) {
            throw new UnknownTypeException(String.format(
                    "The annotation does not contain the required 9 columns. Actual number of columns: %d",
                    columns.length
            ));
        }

        String genomeID = decode(columns[0], Encoding.BASIC);

        int start;
        int end;
        String id;

        try {
            start = Integer.parseInt(columns[3]);
            end   = Integer.parseInt(columns[4]);

            String afterID = columns[8].substring(columns[8].indexOf("ID=") + 3);
            int index = afterID.indexOf(';');
            if (index == -1) {
                index = afterID.length();
            }
            id = decode(afterID.substring(0, index), Encoding.EXTENDED);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new UnknownTypeException(
                    "One of the columns of this Annotation could not be parsed.",
                    e
            );
        }

        Annotation annotation = new Annotation(id, file.getAbsolutePath(), genomeID, start, end);

        annotation.addAttribute("source", decode(columns[1], Encoding.BASIC));
        annotation.addAttribute("type", decode(columns[2], Encoding.BASIC));
        annotation.addAttribute("score", decode(columns[5], Encoding.BASIC));
        annotation.addAttribute("strand", decode(columns[6], Encoding.BASIC));
        annotation.addAttribute("phase", decode(columns[7], Encoding.BASIC));

        parseAttributes(annotation, columns[8]);

        return annotation;
    }

    /**
     * Parse a String containing attributes for an {@link Annotation}.
     * @param annotation The annotation to add the attributes to.
     * @param attributes The String containing the attributes. This String should be column 9 in the gff spec.
     * @throws UnknownTypeException If the attributes are malformed.
     */
    private void parseAttributes(Annotation annotation, String attributes) throws UnknownTypeException {
        String[] attributesArray = attributes.split(";");
        for (String attribute : attributesArray) {
            String[] values = attribute.split(",");
            String name;
            try {
                name = values[0].substring(0, values[0].indexOf('='));
                values[0] = values[0].substring(values[0].indexOf('=') + 1);
            } catch (IndexOutOfBoundsException e) {
                throw new UnknownTypeException(
                        String.format("Malformed key-value pair in attributes: \"%s\".", attribute),
                        e
                );
            }
            for (int i = 0; i < values.length; i++) {
                values[i] = decode(values[i], Encoding.EXTENDED);
            }
            annotation.addMultiAttribute(name, values);
        }
    }

    /**
     * Decode an encoded String (i.e. unescape % signs according to the gff spec)
     * @param encoded The encoded String
     * @param encoding The encoding that is decoded (this should be the encoding used to encode the string).
     * @return The decoded String.
     */
    @NotNull
    private String decode(String encoded, Encoding encoding) {
        String result = encoded;

        if (encoding.contains(EncodingSubtype.WHITESPACE)) {
            result = result.replaceAll("%09", "\t")
                    .replaceAll("%0A", "\n")
                    .replaceAll("%0D", "\r");
        }

        if (encoding.contains(EncodingSubtype.KEY_VALUE)) {
            result = result.replaceAll("%3B", ";")
                    .replaceAll("%3D", "=")
                    .replaceAll("%26", "&")
                    .replaceAll("%2C", ",");
        }

        if (encoding.contains(EncodingSubtype.SPACE)) {
            result = result.replaceAll("%20", " ");
        }

        if (encoding.contains(EncodingSubtype.PERCENT)) {
            result = result.replaceAll("%25", "%");
        }

        return result;
    }

    /**
     * An Encoding, which is defined by a set of {@link EncodingSubtype EncodingSubtypes}.
     */
    private enum Encoding {
        NONE(EnumSet.noneOf(EncodingSubtype.class)),
        BASIC(EnumSet.of(EncodingSubtype.WHITESPACE, EncodingSubtype.PERCENT)),
        EXTENDED(EnumSet.of(
                EncodingSubtype.WHITESPACE,
                EncodingSubtype.PERCENT,
                EncodingSubtype.KEY_VALUE
        ));

        private final EnumSet<EncodingSubtype> encodingSubtypes;

        /**
         * Constructor for an Encoding.
         * @param encodingSubTypes An {@link EnumSet} of {@link EncodingSubtype EncodingSubtypes}
         *                         that this Encoding uses.
         */
        Encoding(EnumSet<EncodingSubtype> encodingSubTypes) {
            this.encodingSubtypes = encodingSubTypes;
        }

        /**
         * Whether this Encoding contains a certain {@link EncodingSubtype}.
         * @param subtype The Subtype to check for.
         * @return Whether this Encoding contains the subtype.
         */
        private boolean contains(EncodingSubtype subtype) {
            return encodingSubtypes.contains(subtype);
        }
    }

    /**
     * A subtype of an {@link Encoding}. This is a name for a set of encodings for some characters.
     */
    private enum EncodingSubtype {
        PERCENT,
        SPACE,
        WHITESPACE,
        KEY_VALUE
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public ProgressCounter getProgressCounter() {
        return progressCounter;
    }

    /**
     * Main method for parsing the annotations.
     * @param ignored String to be received.
     * @throws IOException If file is not found
     * @throws UnknownTypeException If file is not of the correct type.
     */
    public static void main(String... ignored) throws IOException, UnknownTypeException {
//        File file = new File("C:\\Users\\Ivo\\Google Drive local\\university\\Context project" +
//                "\\project\\ProgrammingLife\\data\\annotations\\intervalAnnotation.txt");
        File file = new File("C:\\Users\\Ivo\\Google Drive local\\university\\Context project"
                + "\\project\\ProgrammingLife\\data\\annotations\\GRCh38.chr19.gff");
        new AnnotationParser(file).parseFile(file);
    }
}
