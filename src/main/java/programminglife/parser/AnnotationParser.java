package programminglife.parser;

import programminglife.model.Annotation;
import programminglife.model.exception.UnknownTypeException;

import java.io.BufferedReader;
import java.io.File;
import java.util.EnumSet;

/**
 * A Parser for annotations.
 */
public class AnnotationParser implements Runnable {
    private File file;

    public AnnotationParser(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        parseFile();
    }

    private void parseFile() {
        // TODO: implement

        // keep a set of annotations (important: make sure previous values are not replaced!)
        // parse all lines with parseLine()
        // merge annotations with the same id.
        throw new Error("Not yet implemented");
    }

    /**
     * Parse a String as an Annotation
     * @param line The String to parse
     * @return The parsed Annotation. If this line is empty or a comment (starts with #), return null.
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
                    "The annotation does not contain the required 9 columns. Actua number of columns: %d",
                    columns.length
            ));
        }



        // Notes:
        //  - ignore empty lines and lines starting with # (comments)
        //  - use decode to decode values
        throw new Error("Not yet implemented");
    }

    /**
     * Decode an encoded String (i.e. unescape % signs according to the gff spec)
     * @param encoded The encoded String
     * @param encoding The encoding that is decoded (this should be the encoding used to encode the string).
     * @return The decoded String.
     */
    private String decode(String encoded, Encoding encoding) {
        String result = encoded;

        if (encoding.contains(EncodingSubtype.WHITESPACE)) {
            result  .replaceAll("%09", "\t")
                    .replaceAll("%0A", "\n")
                    .replaceAll("%0D", "\r");
        }

        if (encoding.contains(EncodingSubtype.KEY_VALUE)) {
            result  .replaceAll("%3B", ";")
                    .replaceAll("%3D", "=")
                    .replaceAll("%26", "&")
                    .replaceAll("%2C", ",");
        }

        if (encoding.contains(EncodingSubtype.SPACE)) {
            result.replaceAll("%20", " ");
        }

        if (encoding.contains(EncodingSubtype.PERCENT)) {
            result.replaceAll("%25", "%");
        }

        return result;
    }

    private enum Encoding {
        NONE(EnumSet.noneOf(EncodingSubtype.class)),
        BASIC(EnumSet.of(EncodingSubtype.WHITESPACE, EncodingSubtype.PERCENT)),
        EXTENDED(EnumSet.of(
                EncodingSubtype.WHITESPACE,
                EncodingSubtype.PERCENT,
                EncodingSubtype.SPACE,
                EncodingSubtype.KEY_VALUE
        ));

        private final EnumSet<EncodingSubtype> encodingSubtypes;

        private Encoding(EnumSet<EncodingSubtype> encodingSubTypes) {
            this.encodingSubtypes = encodingSubTypes;
        }

        private boolean contains(EncodingSubtype subtype) {
            return encodingSubtypes.contains(subtype);
        }
    }

    private enum EncodingSubtype {
        PERCENT,
        SPACE,
        WHITESPACE,
        KEY_VALUE
    }
}
