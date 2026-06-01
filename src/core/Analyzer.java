package core;

import core.algorithms.*;
import core.models.FlowCost;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;

import java.util.List;
import java.util.Random;

public class Analyzer {
    public void analyze(String file, int seed){
        PathFinder pathFinder = new PathFinder(new BellmanFord(), new Dijkstra(), new BFS());
        Random random = new Random(seed);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork(file);
        int flow;
        FlowCost result;

        System.out.println(" ----- MAXIMUM FLOW: maxFlow() ----- ");
        System.out.println("Paths:");
        flow = flowAlgorithms.maxFlow(network, "S", "T");
        System.out.println("Flow: " + flow);

        System.out.println(" ----- MAXIMUM FLOW AND COST: costFlow() ----- ");
        System.out.println("Paths:");
        result = flowAlgorithms.costFlow(network, "S", "T");
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);


        System.out.println(" ----- MINIMUM COST: Dijkstra.compute() ----- ");
        System.out.println("Path:");
        List<String> path = pathFinder.minCostPath(network, "S","T", Algorithm.DIJKSTRA);
        System.out.println(path);

        double cost = 0;
        String to = path.getFirst();
        String from;
        // Minimum path cost
        for (int i = 1; i < path.size()-1; i++) {
            from = path.get(i);
            cost += network.outEdges.get(from).get(to).cost;
            to = from;
        }

        System.out.println("Using MaxFlow = " + flow);
        System.out.println("Cost: " + cost * flow);


        System.out.println(" ----- MINUMUM COST MAXIMUM FLOW: minCostFlow() ----- ");
        System.out.println("Paths:");
        result = flowAlgorithms.minCostFlow(network, "S", "T");
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);

    }
}
