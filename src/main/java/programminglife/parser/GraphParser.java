package programminglife.parser;

import com.diffplug.common.base.Errors;
import programminglife.model.*;
import programminglife.model.exception.UnknownTypeException;
import programminglife.utility.FileProgressCounter;

import java.io.*;
import java.util.Observable;

/**
 * Created by toinehartman on 15/05/2017.
 */
public class GraphParser extends Observable implements Runnable {

    private static final boolean PARSE_LINE_VERBOSE_DEFAULT = true;

    private GenomeGraph graph;
    private File graphFile;
    private String name;
    private boolean verbose;
    private FileProgressCounter progressCounter;
    private long startTime;
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
        this.graph = new GenomeGraph(name);
        this.progressCounter = new FileProgressCounter("Lines read");
        this.startTime = System.nanoTime();
        this.isCached = DataManager.hasCache(this.name);
        DataManager.initialize(this.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            System.out.printf("[%s] Parsing GenomeGraph on separate Thread%n", Thread.currentThread().getName());
            parse(this.verbose);
            DataManager.commit();

            int secondsElapsed = (int) ((System.nanoTime() - this.startTime) / 1000000000.d);
            System.out.printf("[%s] Parsing took %d seconds%n", Thread.currentThread().getName(), secondsElapsed);
            this.setChanged();
            this.notifyObservers(this.graph);

            this.graph.getGenomes().forEach(genome -> {
                System.out.printf("Genome '%s' has %d segments%n", genome.getName(), genome.getSize());
            });
            System.out.printf("Average # of nodes in genome: %.1f\n", this.graph.getGenomes().stream().mapToInt(g -> g.getSize()).average().getAsDouble());
        } catch (Exception e) {
            DataManager.rollback();
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
            System.out.printf(
                    "[%s] Parsing file with name %s with path %s%n", Thread.currentThread().getName(),
                    this.name, this.graphFile.getAbsolutePath()
            );
        }

        System.out.printf("[%s] Calculating number of lines in file... ", Thread.currentThread().getName());
        int lineCount = countLines(this.graphFile.getPath());
        System.out.printf("done (%d lines)%n", lineCount);
        this.progressCounter.setTotalLineCount(lineCount);

        try (BufferedReader reader = new BufferedReader(new FileReader(this.graphFile))) {
            reader.lines().forEach(Errors.rethrow().wrap(line -> {
                char type = line.charAt(0);

                this.progressCounter.count();

                switch (type) {
                    case 'S':
                        if (!this.isCached) {
                            this.parseSegment(line);
                        }
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
                    DataManager.close();
                    System.out.printf("[%s] Stopping this thread gracefully...%n", Thread.currentThread().getName());
                }
            }));
        } catch (Errors.WrappedAsRuntimeException e) {
            if (e.getCause() instanceof UnknownTypeException) {
                throw (UnknownTypeException) e.getCause();
            } else {
                throw e;
            }
        }

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
     * Parse a {@link String} representing a {@link Segment}.
     * @param propertyString the {@link String} from a GFA file.
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

        Segment segment = new Segment(segmentID, sequence);
        if (!this.getGraph().contains(segmentID)) {
            this.getGraph().addNode(segment);
        }

        for (String genomeName : genomeNames) {
            if (this.getGraph().containsGenome(genomeName)) {
                this.getGraph().getGenome(genomeName).addSegment(segment);
            } else {
                try {
                    int genomeID = Integer.parseInt(genomeName);
                    String name = this.getGraph().getGenomeOrder().get(genomeID);
                    this.getGraph().getGenome(name).addSegment(segment);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    throw new UnknownTypeException(String.format("Genome '%s' does not exist in this graph", genomeName));
                }
            }
        }
    }

    /**
     * Parse a {@link String} representing a Link.
     * @param propertyString the {@link String} from a GFA file.
     */
    synchronized void parseLink(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("L")); // properties[0] is 'L'
        int sourceId = Integer.parseInt(properties[1]);
        // properties[2] is unused
        int destinationId = Integer.parseInt(properties[3]);
        // properties[4] and further are unused

        Node sourceNode = new Segment(sourceId);
        Node destinationNode = new Segment(destinationId);
        if (!this.getGraph().contains(sourceId)) {
            this.getGraph().addNode(sourceNode);
        }

        if (!this.getGraph().contains(destinationId)) {
            this.getGraph().addNode(destinationNode);
        }

        this.getGraph().addEdge(sourceNode, destinationNode);
    }

    /**
     * Parse a {@link String} representing a header.
     * @param propertyString the {@link String} from a GFA file
     */
    private void parseHeader(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("H"));
        if (properties[1].startsWith("ORI:Z:")) {
            String names[] = properties[1].split(";");
            names[0] = names[0].substring(6);
            for (String name : names) {
                this.getGraph().addGenome(new Genome(name));
            }
        } else if (properties[1].startsWith("VN:Z:")) {
            // Version, ignored
            System.out.printf("[%s] Version: %s\n", Thread.currentThread().getName(), properties[1].substring(5));
        } else {
            System.out.printf("[%s] Unrecognized header: %s\n", Thread.currentThread().getName(), properties[1]);
        }
    }

    public GenomeGraph getGraph() {
        return graph;
    }

    public FileProgressCounter getProgressCounter() {
        return progressCounter;
    }
}
