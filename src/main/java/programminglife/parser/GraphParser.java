package programminglife.parser;

import com.diffplug.common.base.Errors;
import programminglife.model.DataManager;
import programminglife.model.GenomeGraph;
import programminglife.model.Node;
import programminglife.model.Segment;
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
            System.out.printf("[%s] Parsing GenomeGraph on separate Thread\n", Thread.currentThread().getName());
            parse(this.verbose);
            DataManager.commit();

            int secondsElapsed = (int) ((System.nanoTime() - this.startTime) / 1000000000.d);
            System.out.printf("[%s] Parsing took %d seconds\n", Thread.currentThread().getName(), secondsElapsed);
            this.setChanged();
            this.notifyObservers(this.graph);
        } catch (Exception e) {
            DataManager.rollback();
            this.setChanged();
            this.notifyObservers(e);
        }
    }

    /**
     * Parse a GFA file as a {@link GenomeGraph}.
     * @throws FileNotFoundException when no file is found at the given path.
     * @throws UnknownTypeException when an unknown identifier (H/S/L) is read from the file.
     */
    public synchronized void parse() throws FileNotFoundException, UnknownTypeException {
        parse(PARSE_LINE_VERBOSE_DEFAULT);
    }

    /**
     * Parse a GFA file as a {@link GenomeGraph}.
     * @param verbose if log messages should be printed.
     * @throws FileNotFoundException when no file is found at the given path.
     * @throws UnknownTypeException when an unknown identifier (H/S/L) is read from the file.
     */
    protected synchronized void parse(boolean verbose) throws FileNotFoundException, UnknownTypeException {
        if (verbose) {
            System.out.printf(
                    "[%s] Parsing file with name %s with path %s\n", Thread.currentThread().getName(),
                    this.name, this.graphFile.getAbsolutePath()
            );
        }

        System.out.printf("[%s] Calculating number of lines in file\n", Thread.currentThread().getName());
        int lineCount = (int) (new BufferedReader(new FileReader(this.graphFile))).lines().count();
        System.out.printf("[%s] Done! %d lines.\n", Thread.currentThread().getName(), lineCount);
        this.progressCounter.setTotalLineCount(lineCount);

        BufferedReader reader = new BufferedReader(new FileReader(this.graphFile));

        try {
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
                        System.out.println(line);
                        break;
                    default:
                        throw new UnknownTypeException(String.format("Unknown symbol '%c'", type));
                }
                int lineSkipFactor;
                if (isCached) {
                    lineSkipFactor = 100;
                } else {
                    lineSkipFactor = 1;
                }
                if (progressCounter.getLineCount() % (1000 * lineSkipFactor) == 0) {
                    System.out.println(progressCounter);
                }

                if (Thread.currentThread().isInterrupted()) {
                    DataManager.close();
                    System.out.printf("[%s] Stopping this thread gracefully...\n", Thread.currentThread().getName());
                }
            }));
        } catch (Errors.WrappedAsRuntimeException e) {
            if (e.getCause() instanceof UnknownTypeException) {
                throw (UnknownTypeException) e.getCause();
            } else {
                throw e;
            }
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Unexpected (non-fatal) failure closing the GFA file.");
            }
        }
    }

    /**
     * Parse a {@link String} representing a {@link Segment}.
     * @param propertyString the {@link String} from a GFA file.
     */
    synchronized void parseSegment(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("S")); // properties[0] is 'S'
        int segmentID = Integer.parseInt(properties[1]);
        String sequence = properties[2];
        // properties[3] is +/-
        // rest of properties is unused

        Node segment = new Segment(segmentID, sequence);
        if (!this.getGraph().contains(segmentID)) {
            this.getGraph().addNode(segment);
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

    public GenomeGraph getGraph() {
        return graph;
    }

    public FileProgressCounter getProgressCounter() {
        return progressCounter;
    }
}
