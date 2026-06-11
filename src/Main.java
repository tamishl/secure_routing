import core.algorithms.Analyzer;
import java.util.ArrayList;
import java.util.List;


public class Main{
    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer();
        List<String> files = new ArrayList<>();

        // For report
//        files.add("1s3n1t-cf.csv");
//        files.add("1s4n1t-cf-2.csv");
//        files.add("1s2n1t-cf.csv");
//        files.add("1s3n1t-cf-1.csv");



        // Graphs with known result
//        files.add("1s3n1t-cf.csv");
//        files.add("1s3n1t-full.csv");
//        files.add("1s4n1t-cf-1.csv");
//        files.add("1s4n1t-cf-2.csv");
//        files.add("1s4n1t-f17.csv");
//        files.add("1s4n1t-f6.csv");
//        files.add("1s4n1t-f19.csv");
        files.add("1s6n1t.csv");

//         Graphs without known result: randomness in cost and/or flow
//        files.add("1s2n1t-c.csv");
//        files.add("1s3n1t-edges.csv");

        for(String graph: files) {
            System.out.println("FILE = " + graph);
            analyzer.analyze(graph, 10);
            System.out.println("\n");
        }
    }
}