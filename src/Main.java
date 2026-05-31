import core.Network;
import core.algorithms.*;
import core.models.FlowCost;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;

import java.util.Random;

public class Main{
    public static void main(String[] args){

//        RandomNumberGenerator rng = new RandomNumberGenerator(new Random(13));
//        NetworkReader ng = new NetworkReader.Builder(rng).build();
//        Network network = new Network();
//
//        network.addNodeIfAbsent("S1",0.5);
//        network.addNodeIfAbsent("A", 0.5);
//        network.addNodeIfAbsent("B" ,0.5);
//        network.addNodeIfAbsent("C", 0.5);
//        network.addNodeIfAbsent("D", 0.5);
//
//        network.insertEdge("S1", "A", 100, 5.0);
//        network.insertEdge("S1", "B", 50, 5.0);
//        network.insertEdge("B", "A", 80, -6.0);
//        network.insertEdge("A", "D", 70, 3.0);
//        network.insertEdge("B", "D", 60, 2.0);

//        System.out.println(network.minCostPath("S1", "D"));

        PathFinder pathFinder = new PathFinder(new BellmanFord(), new Dijkstra(), new BFS());
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader defaultNetworkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        NetworkReader customNetworkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).maxCost(50).costDecimals(2).build();
        Network network = defaultNetworkReader.getNetwork("1s4n1d-cf.csv");
        System.out.println(flowAlgorithms.maxFlow(network, "S", "D"));
        FlowCost result = flowAlgorithms.minCostFlow(network, "S", "D");
        System.out.println(result.flow);
        System.out.println(result.cost);



    }
}