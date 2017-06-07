package programminglife.parser;

import com.diffplug.common.base.Errors;
import javafx.application.Platform;
import programminglife.model.GenomeGraph;
import programminglife.model.exception.UnknownTypeException;
import programminglife.utility.Alerts;
import programminglife.utility.Console;
import programminglife.utility.FileProgressCounter;

import java.io.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Observable;

/**
 * The class that handles the parsing of the graphs.
 */
public class GraphParser extends Observable implements Runnable {

    private static final boolean PARSE_LINE_VERBOSE_DEFAULT = true;

    private GenomeGraph graph;
    private File graphFile;
    private String name;
    private boolean verbose;
    private FileProgressCounter progressCounter;
    private boolean isCached;


    /**
     * Initiates an empty graph and the {@link File} to parse.
     * @param graphFile the file to parse the {@link GenomeGraph} from.
     * @throws IOException when the file can't be read.
     */
    public GraphParser(File graphFile) throws IOException {
        this.graphFile = graphFile;
        this.name = graphFile.getName();
        this.verbose = PARSE_LINE_VERBOSE_DEFAULT;
        this.progressCounter = new FileProgressCounter("Lines read");
        this.isCached = Cache.hasCache(this.name);
        this.graph = new GenomeGraph(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            long startTime = System.nanoTime();
            if (!this.isCached) {
                Console.println("[%s] Parsing %s on separate Thread", Thread.currentThread().getName(), this.name);
                parse(this.verbose);
            } else {
                Console.println("[%s] Loaded %s from cache", Thread.currentThread().getName(), this.name);
            }

            this.progressCounter.finished();
            int secondsElapsed = (int) ((System.nanoTime() - startTime) / 1000000000.d);
            Console.println("[%s] Parsing took %d seconds", Thread.currentThread().getName(), secondsElapsed);
            this.setChanged();
            this.notifyObservers(this.graph);
        } catch (Exception e) {
            try {
                this.getGraph().rollback();
            } catch (IOException eio) {
                Platform.runLater(() ->
                        Alerts.error(String.format("An error occured while removing the cache. "
                                + "Please remove %s manually.", Cache.toDBFile(this.getGraph().getID()))));
            }
            this.setChanged();
            this.notifyObservers(e);
        }
    }

    /**
     * Parse a GFA file as a {@link GenomeGraph}.
     * @throws IOException when no file is found at the given path.
     * @throws UnknownTypeException when an unknown identifier (H/S/L) is read from the file.
     */
    public synchronized void parse() throws IOException, UnknownTypeException {
        parse(PARSE_LINE_VERBOSE_DEFAULT);
    }

    /**
     * Parse a GFA file as a {@link GenomeGraph}.
     * @param verbose if log messages should be printed.
     * @throws IOException when no file is found at the given path.
     * @throws UnknownTypeException when an unknown identifier (H/S/L) is read from the file.
     */
    protected synchronized void parse(boolean verbose) throws IOException, UnknownTypeException {
        if (verbose) {
            Console.println(
                    "[%s] Parsing file with name %s with path %s", Thread.currentThread().getName(),
                    this.name, this.graphFile.getAbsolutePath()
            );
        }

        Console.print("[%s] Calculating number of lines in file... ", Thread.currentThread().getName());
        int lineCount = countLines(this.graphFile.getPath());
        Console.println("done (%d lines)", lineCount);
        this.progressCounter.setTotalLineCount(lineCount);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.graphFile)))) {
            reader.lines().forEach(Errors.rethrow().wrap(line -> {
                char type = line.charAt(0);

                this.progressCounter.count();

                switch (type) {
                    case 'S':
                        this.parseSegment(line);
                        break;
                    case 'L':
                        this.parseLink(line);
                        break;
                    case 'H':
                        this.parseHeader(line);
                        break;
                    default:
                        throw new UnknownTypeException(String.format("Unknown symbol '%c'", type));
                }

                if (Thread.currentThread().isInterrupted()) {
                    this.getGraph().rollback();
                    Console.println("[%s] Stopping this thread gracefully...", Thread.currentThread().getName());
                    this.progressCounter.finished();
                    return;
                }
            }));
        } catch (Errors.WrappedAsRuntimeException e) {
            if (e.getCause() instanceof UnknownTypeException) {
                throw (UnknownTypeException) e.getCause();
            } else {
                throw e;
            }
        }

        this.graph.cacheLastEdges();
    }

    /**
     * Count the number of newlines in a file.
     *
     * This method does not handle files with/without final newline differently,
     * but as it is just for optimisation purposes, this does not matter.
     * @param filename The file to count the number of lines of
     * @return The number of lines in the file
     * @throws IOException if the file cannot be found or another problem occurs opening the file.
     */
    private int countLines(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return count + 1;
        }
    }

    /**
     * Parse a {@link String} representing a Segment.
     * @param propertyString the {@link String} from a GFA file.
     * @throws UnknownTypeException when a Segment references a Genome that is not in the GFA header
     */
    synchronized void parseSegment(String propertyString) throws UnknownTypeException {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("S")); // properties[0] is 'S'
        int segmentID = Integer.parseInt(properties[1]);
        String sequence = properties[2];
        // properties[3] is +/-
        // rest of properties is unused
        assert (properties[4].startsWith("ORI:Z:"));
        String[] genomeNames = properties[4].split(";");
        genomeNames[0] = genomeNames[0].substring(6);
        int[] genomeIDs;
        try {
            genomeIDs = Arrays.stream(genomeNames).mapToInt(this.graph::getGenomeID).toArray();
        } catch (NoSuchElementException e) {
            try {
                genomeIDs = Arrays.stream(genomeNames).mapToInt(Integer::parseInt).toArray();
            } catch (NumberFormatException nfe) {
                throw new UnknownTypeException(nfe.getMessage());
            }
        }

        if (!this.graph.contains(segmentID)) {
            this.graph.replaceNode(segmentID);
        }
        this.graph.setSequence(segmentID, sequence);
        this.graph.setGenomes(segmentID, genomeIDs);
    }

    /**
     * Parse a {@link String} representing a Link.
     * @param propertyString the {@link String} from a GFA file.
     */
    synchronized void parseLink(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("L")); // properties[0] is 'L'
        int sourceID = Integer.parseInt(properties[1]);
        // properties[2] is unused
        int destinationID = Integer.parseInt(properties[3]);
        // properties[4] and further are unused
        if (!this.graph.contains(sourceID)) {
            this.graph.replaceNode(sourceID);
        }

        if (!this.graph.contains(destinationID)) {
            this.graph.replaceNode(destinationID);
        }

        this.graph.addEdge(sourceID, destinationID);
    }

    /**
     * Parse a {@link String} representing a header.
     * @param propertyString the {@link String} from a GFA file
     */
    private void parseHeader(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("H"));
        if (properties[1].startsWith("ORI:Z:")) {
            String[] names = properties[1].split(";");
            names[0] = names[0].substring(6);
            for (String name : names) {
                this.getGraph().addGenome(name);
            }
        } else if (properties[1].startsWith("VN:Z:")) {
            // Version, ignored
            Console.println("[%s] Version: %s", Thread.currentThread().getName(), properties[1].substring(5));
        } else {
            Console.println("[%s] Unrecognized header: %s", Thread.currentThread().getName(), properties[1]);
        }
    }

    public GenomeGraph getGraph() {
        return graph;
    }

    public FileProgressCounter getProgressCounter() {
        return progressCounter;
    }
}
