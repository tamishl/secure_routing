import core.EdgeWeights;
import core.Network;
import core.algorithms.*;
import core.models.FlowCost;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;

import java.util.Map;
import java.util.Random;

public class Main{
    public static void main(String[] args){
        PathFinder pathFinder = new PathFinder(new BellmanFord(), new Dijkstra(), new BFS());
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-cf.csv");
        Map<String, Map<String, EdgeWeights>> res = network.generateResidualMap();
        System.out.println(flowAlgorithms.maxFlow(network, "S", "D"));
//        FlowCost result = flowAlgorithms.minCostFlow(network, "S", "D");
//        System.out.println(result.flow);
//        System.out.println(result.cost);



    }
}