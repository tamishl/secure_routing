package core.algorithms;

import core.EdgeWeights;
import core.Network;
import core.models.FlowCost;

import java.util.List;
import java.util.Map;

public class FlowAlgorithms {
    private final PathFinder pathFinder;

    public FlowAlgorithms(PathFinder pathFinder){
        this.pathFinder = pathFinder;
    }


    public FlowCost minCostMaxFlow(Network network, String source, String sink){
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeWeights ew;

        Map<String, Double> potentials = network.generatePotentials();

        List<String> path = pathFinder.minCostPath(network, source, sink, residual, potentials);

        while(!path.isEmpty()) {
            // Print path (for analysis)
            System.out.println(path.reversed());

            currentFlow = this.pathBottleneck(path, residual);
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

            path = pathFinder.minCostPath(network, source, sink, residual, potentials);
        }

        return new FlowCost(totalFlow, totalCost);
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public int maxFlow(Network network, String source, String sink){
        Map<String, Map<String, Integer>> residual = network.generateResidualFlowMap();
        List<String> path = pathFinder.path(network, source, sink, residual);

        int maxFlow = 0;

        String child;
        String parent;
        int pathFlow;

        while (!path.isEmpty()) {
            // Print path (for analysis)
            System.out.println(path.reversed());

            child = sink;
            pathFlow = Integer.MAX_VALUE;

            // Get maximum flow over given path / bottleneck
            for (int i = 1; i < path.size(); i++) {
                parent = path.get(i);
                pathFlow = Math.min(pathFlow, residual.get(parent).get(child));
                child = parent;
            }

            // Add child total flow
            maxFlow += pathFlow;

            // Update residual network: subtract flow parent given direction, add in reversed direction
            child = sink;

            for (int i = 1; i < path.size(); i++) {
                parent = path.get(i);
                residual.get(parent).put(child, residual.get(parent).get(child) - pathFlow);
                residual.get(child).put(parent, residual.get(child).get(parent) + pathFlow);
                child = parent;
            }

            path = pathFinder.path(network, source, sink, residual);
        }

        return maxFlow;
    }


    public FlowCost maxFlowCost(Network network, String source, String sink){
        Map<String, Map<String, Integer>> residual = network.generateResidualFlowMap();
        Map<String, Map<String, EdgeWeights>> original = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String parent;
        String child;
        double cost;

        List<String> path = pathFinder.path(network, source, sink, residual);

        while(!path.isEmpty()) {
            // Print path (for analysis)
            System.out.println(path.reversed());

            // Get bottleneck
            currentFlow = pathBottleneckInt(path, residual);

            totalFlow += currentFlow;
            child = path.getFirst();

            // Update residual graph and track cost
            for (int i = 1; i < path.size(); i++){
                parent = path.get(i);

                // If reversed edge: flow in opposite direction than path
                if (residual.get(child).get(parent) != 0){
                    cost = original.get(child).get(parent).cost;
                    totalCost -= cost * currentFlow;
                    residual.get(child).put(parent, residual.get(child).get(parent)- currentFlow);
                }

                // If original edge
                else {
                    cost = original.get(parent).get(child).cost;
                    totalCost += cost * currentFlow;
                    residual.get(parent).put(child, residual.get(parent).get(child)-currentFlow);
                }

                child = parent;
            }

            path = pathFinder.path(network, source, sink, residual);
        }

        return new FlowCost(totalFlow, totalCost);
    }


    public FlowCost minCostFlow(Network network, String source, String sink){
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeWeights ew;

        List<String> path = pathFinder.minCostPathFlow(network, source, sink, residual);

        while(!path.isEmpty()) {
            // Print path (for analysis)
            System.out.println(path.reversed());

            // Get bottleneck
            currentFlow = pathBottleneck(path, residual);

            totalFlow += currentFlow;
            to = path.getFirst();

            // Update residual graph and track cost
            for (int i = 1; i < path.size(); i++){
                from = path.get(i);
                ew = residual.get(from).get(to);
                totalCost += ew.cost * currentFlow;
                ew.flow += currentFlow;
                to = from;
            }

            path = pathFinder.minCostPathFlow(network, source, sink, residual);
        }

        return new FlowCost(totalFlow, totalCost);
    }

    // Maximum flow in the cheapest path
    public int pathBottleneck(List<String> path, Map<String, Map<String, EdgeWeights>> residual){
        String to = path.getFirst();
        String from;
        int flowRest;
        int maxFlow = Integer.MAX_VALUE;

        for (int i = 1; i < path.size(); i++){
            from = path.get(i);

            // If reversed edge: flow in opposite direction of path
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


    public int pathBottleneckInt(List<String> path, Map<String, Map<String, Integer>> residual){
        String to = path.getFirst();
        String from;
        int flowRest;
        int maxFlow = Integer.MAX_VALUE;

        for (int i = 1; i < path.size(); i++){
            from = path.get(i);

            // If reversed edge: flow in opposite direction of path
            if (residual.get(to).get(from) != 0){
                flowRest = residual.get(to).get(from);
            }
            // If original edge
            else {
                flowRest = residual.get(from).get(to);
            }
            maxFlow = Math.min(maxFlow, flowRest);
            to = from;
        }

        return maxFlow;
    }
}
