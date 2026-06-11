package core.algorithms;

import core.Network;
import core.models.FlowCost;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;
import java.util.Random;

public class Analyzer {
    public void analyze(String file, int seed){
        PathFinder pathFinder = new PathFinder(new BellmanFord(), new Dijkstra(), new BFS());
        Random random = new Random(seed);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork(file);

        FlowCost result;

        int flow;
        System.out.println("MAXIMUM FLOW: maxFlow()");
        System.out.println("Paths:");
        flow = flowAlgorithms.maxFlow(network, "S", "T");
        System.out.println("Flow: " + flow);

        System.out.println();
        System.out.println("MAXIMUM FLOW AND COST: maxFlowCost()");
        System.out.println("Paths:");
        result = flowAlgorithms.maxFlowCost(network, "S", "T");
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);

        System.out.println();
        System.out.println("MINIMUM COST AND FLOW: minCostFlow()");
        System.out.println("Paths:");
        result = flowAlgorithms.minCostFlow(network, "S", "T");
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);

        System.out.println();
        System.out.println("MINIMUM COST AND MAXIMUM FLOW: minCostMaxFlow()");
        System.out.println("Paths:");
        result = flowAlgorithms.minCostMaxFlow(network, "S", "T");
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);
    }
}
