import core.Network;
import core.TrustSystem;

import java.util.HashSet;
import java.util.Set;

public class Main{
    public static void main(String[] args){
            Network network = new Network(new TrustSystem(100.00));
//            network.createHost("S1");
//            network.createHost("S2");
//            network.createHost("S3");
//            network.createHost("A");
//            network.createHost("B");
//            network.createHost("D");
//
//            // From Wei et al. with own given capacity
//            network.insertEdge("S1", "A", 100, 0.4);
//            network.insertEdge("S2", "A", 50, 0.4);
//            network.insertEdge("S2", "B", 50, 0.4);
//            network.insertEdge("S3", "A", 100, 0.6);
//            network.insertEdge("S3", "B", 100, 0.5);
//            network.insertEdge("A", "D", 100, 0.4);
//            network.insertEdge("B", "D", 150, 0.9);
//
//            network.printGraph();

            network.createNode("A");
            network.createNode("B");
            network.createNode("C");
            network.createNode("D");
            network.createNode("E");
            network.createNode("F");
            network.createNode("G");
            network.createNode("H");
            network.createNode("I");
            network.createNode("J");

            network.insertEdge("A", "D", 50, 0.5);
            network.insertEdge("A", "B", 40, 0.4);

            network.insertEdge("B", "D", 60, 0.6);
            network.insertEdge("B", "E", 70, 0.7);

            network.insertEdge("C", "E", 80, 0.8);
            network.insertEdge("C", "F", 30, 0.3);

            network.insertEdge("D", "H", 90, 1.0);

            network.insertEdge("E", "H", 40, 0.9);
            network.insertEdge("E", "F", 60, 0.6);

            network.insertEdge("F", "E", 20, 0.1);
            network.insertEdge("F", "H", 70, 1.2);
            network.insertEdge("F", "G", 50, 0.5);

            network.insertEdge("G", "I", 80, 0.8);

            network.insertEdge("H", "I", 60, 1.1);
            network.insertEdge("H", "J", 90, 1.3);

            network.insertEdge("I", "H", 40, 0.6);
            network.insertEdge("I", "J", 70, 0.7);

            network.printGraph();

    }
}