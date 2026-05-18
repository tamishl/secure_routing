package core.algorithms;

import core.EdgeWeights;
import core.Network;
import core.utils.FlowCost;

import java.util.List;
import java.util.Map;

public class FlowAlgorithms {
    public final PathFinder pathFinder;

    public FlowAlgorithms(PathFinder pathFinder){
        this.pathFinder = pathFinder;
    }


    public FlowCost minCostFlow(Network network, String source, String destination){
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeWeights ew;

        List<String> path = pathFinder.minCostPath(network, source, destination, Algorithm.DIJKSTRA_JOHNSON, residual);

        while(!path.isEmpty()) {
            currentFlow = pathBottleneck(path, residual);
            totalFlow += currentFlow;
            to = path.getFirst();

            // Update residual graph and track cost
            for (int i = 1; i < path.size(); i++){
                from = path.get(i);

                // If reversed edge: flow in opposite direction than path
                if (residual.get(to).get(from).flow != 0){
                    ew = residual.get(to).get(from);
                    totalCost -= ew.cost * currentFlow;
                    ew.flow -= currentFlow;

                }

                // If original edge
                else {
                    ew = residual.get(from).get(to);
                    ew.flow += currentFlow;
                    totalCost += ew.cost * currentFlow;
                }

                to = from;
            }

            path = pathFinder.minCostPath(network, source, destination, Algorithm.DIJKSTRA_JOHNSON, residual);
        }

        return new FlowCost(totalFlow, totalCost);
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public int maxFlow(Network network, String source, String destination){
        Map<String, Map<String, Integer>> rNetwork = network.generateResidualFlowMap();
        Map<String, String> parent = pathFinder.getParents(network, source, destination, rNetwork);

        int maxFlow = 0;

        String to;
        String from;
        int pathFlow;

        while (!parent.isEmpty()) {
            to = destination;
            pathFlow = Integer.MAX_VALUE;

            // Get maximum flow over given path / bottleneck
            while (!to.equals(source)) {
                from = parent.get(to);
                pathFlow = Math.min(pathFlow, rNetwork.get(from).get(to));
                to = from;
            }

            // Add to total flow
            maxFlow += pathFlow;

            // Update residual network: subtract flow from given direction, add in reversed direction
            to = destination;
            while (!to.equals(source)) {
                from = parent.get(to);
                rNetwork.get(from).put(to, rNetwork.get(from).get(to) - pathFlow);
                rNetwork.get(to).put(from, rNetwork.get(to).get(from) + pathFlow);
                to = from;
            }
            parent = pathFinder.getParents(network, source, destination, rNetwork);
        }

        return maxFlow;
    }


    // Maximum flow in the cheapest path
    public int maxFlowMinPath(Network network, String source, String destination, Algorithm algorithm){

        List<String> path = pathFinder.minCostPath(network, source, destination, algorithm);
        String current = path.getFirst();
        String parent;
        int maxFlow = Integer.MAX_VALUE;

        for (int i = 1; i < path.size(); i++){
            parent = path.get(i);
            maxFlow = Math.min(maxFlow, network.outEdges.get(parent).get(current).capacity);
            current = parent;
        }

        return maxFlow;
    }

    // Maximum flow in the cheapest path
    public int pathBottleneck(List<String> path, Map<String, Map<String, EdgeWeights>> residual){
        String to = path.getFirst();
        String from;
        int flowRest;
        int maxFlow = Integer.MAX_VALUE;

        for (int i = 1; i < path.size(); i++){
            from = path.get(i);

            // If reversed edge: flow in opposite direction than path
            if (residual.get(to).get(from).flow != 0){
                flowRest = residual.get(to).get(from).flow;
            }

            // If original edge
            else {
                flowRest = residual.get(from).get(to).capacity - residual.get(from).get(to).flow;
            }

            maxFlow = Math.min(maxFlow, flowRest);
            to = from;
        }

        return maxFlow;
    }
}
