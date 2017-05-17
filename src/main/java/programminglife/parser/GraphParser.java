package programminglife.parser;

import com.diffplug.common.base.Errors;
import com.diffplug.common.base.Throwing;
import programminglife.model.GenomeGraph;
import programminglife.model.Segment;
import programminglife.model.exception.UnknownTypeException;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Observable;

/**
 * Created by toinehartman on 15/05/2017.
 */
public class GraphParser extends Observable implements Runnable {

    private static final boolean PARSE_LINE_VERBOSE_DEFAULT = true;

    private GenomeGraph graph;
    private File graphFile;
    private boolean verbose;

    /**
     * Initiates an empty graph and the {@link File} to parse.
     * @param graphFile the file to parse the {@link GenomeGraph} from
     */
    public GraphParser(File graphFile) {
        this.graphFile = graphFile;
        this.verbose = PARSE_LINE_VERBOSE_DEFAULT;
        this.graph = new GenomeGraph("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            System.out.printf("%s Parsing GenomeGraph on separate Thread", Thread.currentThread());
            parse(this.verbose);
            this.setChanged();
            this.notifyObservers(this.graph);
        } catch (Exception e) {
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
            System.out.printf("%s Parsing file %s\n", Thread.currentThread(), this.graphFile.getAbsolutePath());
        }

        BufferedReader reader = new BufferedReader(new FileReader(this.graphFile));
        this.graph = new GenomeGraph(this.graphFile.getName());

        try {
            reader.lines().forEach(Errors.rethrow().wrap((Throwing.Consumer<String>) line -> {
                char type = line.charAt(0);

                switch (type) {
                    case 'S':
                        this.parseSegment(line);
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

        this.findRootNodes();
    }

    /**
     * Parse a {@link String} representing a {@link Segment}.
     * @param propertyString the {@link String} from a GFA file.
     */
    synchronized void parseSegment(String propertyString) {
        String[] properties = propertyString.split("\\s");
        assert (properties[0].equals("S")); // properties[0] is 'S'
        int id = Integer.parseInt(properties[1]);
        String segment = properties[2];
        // properties[3] is +/-
        // rest of properties is unused

        Segment parsedNode = new Segment(id, segment);
        Segment existingNode;
        try {
            existingNode = this.getGraph().getNode(parsedNode.getIdentifier());
            existingNode.setSequence(parsedNode.getSequence());
        } catch (NoSuchElementException e) {
            this.getGraph().addNode(parsedNode);
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

        Segment sourceNode, destinationNode;

        try {
            sourceNode = this.getGraph().getNode(sourceId);
        } catch (NoSuchElementException e) {
            sourceNode = new Segment(sourceId);
            this.getGraph().addNode(sourceNode);
        }

        try {
            destinationNode = this.getGraph().getNode(destinationId);
        } catch (NoSuchElementException e) {
            destinationNode = new Segment(destinationId);
            this.getGraph().addNode(destinationNode);
        }

        sourceNode.addChild(destinationNode);
        destinationNode.addParent(sourceNode);
    }

    /**
     * Find all {@link Segment}s without parents and mark them as root nodes.
     */
    private synchronized void findRootNodes() {
        for (Segment n : this.getGraph().getNodes()) {
            if (n != null && n.getParents().isEmpty()) {
                this.getGraph().getRootNodes().add(n);
            }
        }
    }

    public GenomeGraph getGraph() {
        return graph;
    }
}
