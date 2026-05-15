import core.EdgeWeights;
import core.Network;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main{
    public static void main(String[] args){

        RandomNumberGenerator rng = new RandomNumberGenerator(new Random(13));
        NetworkReader ng = new NetworkReader.Builder(rng).build();
        Network network = new Network();

        network.addNodeIfAbsent("S1",0.5);
        network.addNodeIfAbsent("A", 0.5);
        network.addNodeIfAbsent("B" ,0.5);
        network.addNodeIfAbsent("C", 0.5);
        network.addNodeIfAbsent("D", 0.5);

        network.insertEdge("S1", "A", 100, 5.0);
        network.insertEdge("S1", "B", 50, 5.0);
        network.insertEdge("B", "A", 80, -6.0);
        network.insertEdge("A", "D", 70, 3.0);
        network.insertEdge("B", "D", 60, 2.0);

        System.out.println(network.minCostPath("S1", "D"));


    }
}