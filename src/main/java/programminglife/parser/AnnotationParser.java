package programminglife.parser;

import org.jetbrains.annotations.NotNull;
import programminglife.model.Annotation;
import programminglife.model.exception.UnknownTypeException;

import java.io.File;
import java.util.EnumSet;

/**
 * A Parser for {@link Annotation Annotations}.
 */
public class AnnotationParser implements Runnable {
    private File file;

    /**
     * Constructor for an AnnotationParser.
     * @param file The {@link File} to parse.
     */
    public AnnotationParser(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        parseFile();
    }

    /**
     * Parse the file.
     */
    private void parseFile() {
        // TODO: implement

        // keep a set of annotations (important: make sure previous values are not replaced!)
        // parse all lines with parseLine()
        // merge annotations with the same id.
        throw new Error("Not yet implemented");
    }

    /**
     * Parse a String as an Annotation.
     * @param line The String to parse
     * @return The parsed Annotation. If this line is empty or a comment (starts with #), this returns null.
     * @throws UnknownTypeException If the line is malformed.
     */
    Annotation parseLine(String line) throws UnknownTypeException {
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

            String afterID = columns[8].substring(columns[8].indexOf("ID="));
            int index = afterID.indexOf(';');
            if (index == -1) {
                index = afterID.length();
            }
            id = afterID.substring(0, index);
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
}
