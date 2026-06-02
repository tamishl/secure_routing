import core.algorithms.Analyzer;
import java.util.ArrayList;
import java.util.List;


public class Main{
    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer();
        List<String> files = new ArrayList<>();

        // For report
//        files.add("1s3n1d-cf.csv");
//        files.add("1s4n1d-cf-2.csv");


        // Graphs with known result
        files.add("1s3n1d-cf.csv");
        files.add("1s3n1d-full.csv");
        files.add("1s4n1d-cf-1.csv");
        files.add("1s4n1d-cf-2.csv");
        files.add("1s4n1d-f17.csv");
        files.add("1s4n1d-f6.csv");
        files.add("1s4n1d-f19.csv");

        // Graphs without (fully) known result: randomness in cost and/or flow
        files.add("1s2n1d-c.csv");
        files.add("1s3n1d-edges.csv");
        files.add("1s8n1d-edges.csv");

        for(String graph: files) {
            System.out.println("FILE = " + graph);
            analyzer.analyze(graph, 10);
            System.out.println("\n");
        }
    }
}