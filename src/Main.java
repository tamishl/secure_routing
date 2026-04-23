import core.Network;
import core.TrustSystem;

import java.util.HashMap;
import java.util.Map;

public class Main{
    public static void main(String[] args){
            Network network = new Network(new TrustSystem(100.00));
            network.createHost("A1");
            network.createHost("A2");
            network.createHost("A3");
            network.createHost("A4");
            network.createHost("A5");
            network.createHost("A6");

            network.insertEdge("A1", "A5", 2);
            network.insertEdge("A1", "A4", 8);
            network.insertEdge("A1", "A2", 1);
            network.insertEdge("A2", "A5", 6);
            network.insertEdge("A6", "A5", 3);
            network.insertEdge("A3", "A4", 10);
            network.insertEdge("A4", "A3", 3);
            network.insertEdge("A6", "A2", 7);

            network.printGraph();
    }
}