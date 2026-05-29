package core.algorithms;

import core.EdgeWeights;
import core.Network;
import core.models.FlowCost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowAlgorithms {
    private final PathFinder pathFinder;
    private final BFS bfs;

    public FlowAlgorithms(PathFinder pathFinder, BFS bfs){
        this.pathFinder = pathFinder;
        this.bfs = bfs;
    }


    public FlowCost minCostFlow(Network network, String source, String sink){
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeWeights ew;

        Map<String, Double> potentials = new HashMap<>();

        for (String node: network.nodes.keySet()){
            potentials.put(node, 0.0);
        }

        List<String> path = pathFinder.minCostPath(network, source, sink, Algorithm.DIJKSTRA_JOHNSON, residual, potentials);

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

            path = pathFinder.minCostPath(network, source, sink, Algorithm.DIJKSTRA_JOHNSON, residual, potentials);
        }

        return new FlowCost(totalFlow, totalCost);
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public int maxFlow(Network network, String source, String sink){
        Map<String, Map<String, Integer>> rNetwork = network.generateResidualFlowMap();
        Map<String, String> parents = bfs.compute(network, source, sink, rNetwork);

        int maxFlow = 0;

        String child;
        String parent;
        int pathFlow;

        while (!parents.isEmpty()) {
            child = sink;
            pathFlow = Integer.MAX_VALUE;

            // Get maximum flow over given path / bottleneck
            while (!child.equals(source)) {
                parent = parents.get(child);
                pathFlow = Math.min(pathFlow, rNetwork.get(parent).get(child));
                child = parent;
            }

            // Add child total flow
            maxFlow += pathFlow;

            // Update residual network: subtract flow parent given direction, add in reversed direction
            child = sink;
            while (!child.equals(source)) {
                parent = parents.get(child);
                rNetwork.get(parent).put(child, rNetwork.get(parent).get(child) - pathFlow);
                rNetwork.get(child).put(parent, rNetwork.get(child).get(parent) + pathFlow);
                child = parent;
            }
            parents = bfs.compute(network, source, sink, rNetwork);
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


    // Maximum flow in the cheapest path
    public int maxFlowMinPath(Network network, String source, String sink, Algorithm algorithm){

        List<String> path = pathFinder.minCostPath(network, source, sink, algorithm);
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
}
