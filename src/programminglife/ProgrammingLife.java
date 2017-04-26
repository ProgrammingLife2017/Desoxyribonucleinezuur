package programminglife;

import programminglife.model.Graph;

import java.io.FileNotFoundException;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class ProgrammingLife {
    public static void main(String[] args) {
        try {
            Graph g = Graph.parse("data/real/TB10.gfa");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
