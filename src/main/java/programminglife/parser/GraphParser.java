package programminglife.parser;

import com.diffplug.common.base.Errors;
import javafx.application.Platform;
import programminglife.ProgrammingLife;
import programminglife.model.GenomeGraph;
import programminglife.model.exception.UnknownTypeException;
import programminglife.utility.Alerts;
import programminglife.utility.Console;
import programminglife.utility.ProgressCounter;

import java.io.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Observable;

/**
 * The class that handles the parsing of the graphs.
 */
public class GraphParser extends Observable implements Runnable {
    private GenomeGraph graph;
    private File graphFile;
    private String name;
    private ProgressCounter progressCounter;
    private boolean isCached;


    /**
     * Initiates an empty graph and the {@link File} to parse.
     * @param graphFile the file to parse the {@link GenomeGraph} from.
     */
    public GraphParser(File graphFile) {
        this.graphFile = graphFile;
        this.name = graphFile.getName();
        this.progressCounter = new ProgressCounter("Lines read");
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
                Platform.runLater(() -> ProgrammingLife.getStage().setTitle("Parsing " + this.graphFile.getPath()));
                parse();
            } else {
                Console.println("[%s] Loaded %s from cache", Thread.currentThread().getName(), this.name);
            }

            Platform.runLater(() -> ProgrammingLife.getStage().setTitle("Loading genomes..."));
            this.graph.loadGenomes(this.progressCounter);

            int secondsElapsed = (int) ((System.nanoTime() - startTime) / 1000000000.d);
            Console.println("[%s] Parsing took %d seconds", Thread.currentThread().getName(), secondsElapsed);
            this.progressCounter.finished();
            this.setChanged();
            this.notifyObservers(this.graph);
        } catch (Exception e) {
            try {
                this.getGraph().rollback();
            } catch (IOException eio) {
                Platform.runLater(() ->
                        Alerts.error(String.format("An error occurred while removing the cache. "
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
        Console.println("[%s] Parsing file with name %s with path %s", Thread.currentThread().getName(),
                                this.name, this.graphFile.getAbsolutePath());

        Console.print("[%s] Calculating number of lines in file... ", Thread.currentThread().getName());
        int lineCount = countLines(this.graphFile.getPath());
        Console.println("done (%d lines)", lineCount);
        this.progressCounter.setTotal(lineCount);

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
        this.progressCounter.finished();
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
     * @throws UnknownTypeException when a segment cannot be parsed
     */
    synchronized void parseSegment(String propertyString) throws UnknownTypeException {
        int segmentID;
        String sequence;
        int[] genomeIDs;
        String[] genomeNames;

        String[] properties = propertyString.split("\\s");
        if (!properties[0].equals("S")) {
            throw new UnknownTypeException(String.format("Line (%s) is not a segment", properties[0]));
        }

        if (properties.length < 5) {
            throw new UnknownTypeException(String.format("Segment has less than 5 properties (%d)", properties.length));
        }

        try {
            segmentID = Integer.parseInt(properties[1]);
        } catch (NumberFormatException nfe) {
            throw new UnknownTypeException(String.format("The segment ID (%s) should be a number", properties[1]), nfe);
        }

        if (!properties[4].startsWith("ORI:Z:")) {
            throw new UnknownTypeException("Segment has no genomes");
        }

        sequence = properties[2];
        genomeNames = properties[4].split(";");
        genomeNames[0] = genomeNames[0].substring(6);
        try {
            genomeIDs = Arrays.stream(genomeNames).mapToInt(this.graph::getGenomeID).toArray();
        } catch (NoSuchElementException e) {
            try {
                genomeIDs = Arrays.stream(genomeNames).mapToInt(Integer::parseInt).toArray();
            } catch (NumberFormatException nfe) {
                throw new UnknownTypeException("Unknown genome name in segment", nfe);
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
     * @throws UnknownTypeException when a link cannot be parsed
     */
    synchronized void parseLink(String propertyString) throws UnknownTypeException {
        int sourceID, destinationID;

        String[] properties = propertyString.split("\\s");

        if (!properties[0].equals("L")) {
            throw new UnknownTypeException(String.format("Line (%s) is not a link", properties[0]));
        }

        if (properties.length < 4) {
            throw new UnknownTypeException(String.format("Link has less than 4 properties (%d)", properties.length));
        }

        try {
            sourceID = Integer.parseInt(properties[1]);
        } catch (NumberFormatException nfe) {
            throw new UnknownTypeException(String.format("The source ID (%s) should be a number", properties[1]), nfe);
        }
        // properties[2] is unused
        try {
            destinationID = Integer.parseInt(properties[3]);
        } catch (NumberFormatException nfe) {
            throw new UnknownTypeException(String.format("The destination ID (%s) should be a number",
                    properties[3]), nfe);
        }
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
     * @throws UnknownTypeException when a header cannot be parsed
     */
    void parseHeader(String propertyString) throws UnknownTypeException {
        String[] properties = propertyString.split("\\s");

        if (!properties[0].equals("H")) {
            throw new UnknownTypeException(String.format("Line (%s) is not a header", properties[0]));
        }

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

    public ProgressCounter getProgressCounter() {
        return progressCounter;
    }
}
