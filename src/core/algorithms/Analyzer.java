package core.algorithms;

import core.Network;
import core.NodeType;
import core.models.FlowCost;
import core.models.FlowCostProb;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;

import java.util.Random;

public class Analyzer {
    private final String S = NodeType.SOURCE.getPrefix();
    private final String T = NodeType.SINK.getPrefix();
    public void analyze(String file, int seed){
        PathFinder pathFinder = new PathFinder(new BellmanFord(), new Dijkstra(), new BFS());
        Random random = new Random(seed);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork(file);

        FlowCost result;


        System.out.println("MAXIMUM FLOW AND COST: maxFlow()");
        System.out.println("Paths:");
        result = flowAlgorithms.maxFlow(network, S, T);
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);

        System.out.println();
        System.out.println("MINIMUM COST AND FLOW: minCostFlow()");
        System.out.println("Paths:");
        result = flowAlgorithms.minCostFlow(network, S, T);
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);

        System.out.println();
        System.out.println("MINIMUM COST AND MAXIMUM FLOW: minCostMaxFlow()");
        System.out.println("Paths:");
        result = flowAlgorithms.minCostMaxFlow(network, S, T);
        System.out.println("Flow: " + result.flow);
        System.out.println("Cost: " + result.cost);

        FlowCostProb resultP;
        System.out.println();
        System.out.println("MAXIMUM PROBABILITY AND FLOW: maxProbMaxFlow()");
        System.out.println("Paths:");
        resultP = flowAlgorithms.maxProbMaxFlow(network, S, T);
        System.out.println("Flow: " + resultP.flow);
        System.out.println("Cost: " + resultP.cost);
        System.out.println("Probability: " + resultP.probability);
    }
}
