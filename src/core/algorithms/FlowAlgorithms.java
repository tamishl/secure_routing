package core.algorithms;

import core.EdgeWeights;
import core.Network;
import core.models.FlowCost;
import core.models.FlowCostProb;

import java.util.List;
import java.util.Map;

public class FlowAlgorithms {
    private final PathFinder pathFinder;

    public FlowAlgorithms(PathFinder pathFinder){
        this.pathFinder = pathFinder;
    }


    public FlowCostProb minCostMaxFlow(Network network, String source, String sink) {
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        double totalCost = 0.0;
        double totalProbability = 0.0;
        double estimateOfReceival = 0;

        FlowCostProb updates;

        Map<String, Double> potentials = network.generatePotentials();

        List<String> path = pathFinder.minCostFlowPath(network, source, sink, residual, potentials);

        while (!path.isEmpty()) {
            updates = pathValues(network, residual, path);

            totalFlow += updates.flow;
            totalCost += updates.cost;
            totalProbability += updates.probability;
            path = pathFinder.minCostFlowPath(network, source, sink, residual, potentials);
        }

        return new FlowCostProb(totalFlow, totalCost, totalProbability);
    }

    public FlowCostProb maxProbMaxFlow(Network network, String source, String sink) {
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        double totalCost = 0.0;
        double totalProbability = 0.0;
        double estimateOfReceival = 0;

        FlowCostProb updates;

        List<String> path = pathFinder.minRiskFlowPath(network, source, sink, residual);

        while (!path.isEmpty()) {
            updates = pathValues(network, residual, path);

            totalFlow += updates.flow;
            totalCost += updates.cost;
            totalProbability += updates.probability;

            path = pathFinder.minRiskFlowPath(network, source, sink, residual);
        }

        return new FlowCostProb(totalFlow, totalCost, totalProbability);
    }

    // Flow, cost and probability of current path (cost & probability updated to flow)
    private FlowCostProb pathValues(Network network, Map<String, Map<String, EdgeWeights>> residual, List<String> path){
        // Doesn't handle probability correctly yet for reversed edges I think
        // Print path (for analysis)
        System.out.println(path.reversed());

        String parent;
        String child;
        EdgeWeights ew;

        int currentFlow = pathBottleneck(path, residual);
        double currentCost = 0.0;
        double currentProbability = 0.0;

        child = path.getFirst();

        // Update residual graph and track cost
        for (int i = 1; i < path.size(); i++) {
            parent = path.get(i);
            currentProbability += Math.log(1.0 - network.getNode(child).risk);

            // If reversed edge: flow in opposite direction than path
            if (residual.get(child).get(parent).flow != 0) {
                ew = residual.get(child).get(parent);
                currentCost -= ew.cost * currentFlow;
                ew.flow -= currentFlow;

                currentProbability -= Math.log(1 - network.getNode(child).risk) + Math.log(1 - network.getNode(parent).risk);
            }

            // If original edge
            else {
                ew = residual.get(parent).get(child);
                ew.flow += currentFlow;
                currentCost += ew.cost * currentFlow;
            }

            child = parent;
        }

        currentProbability += Math.log(1 - network.getNode(child).risk);
        currentProbability *= currentFlow;



        return new FlowCostProb(currentFlow, currentCost, currentProbability);
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public FlowCost maxFlow(Network network, String source, String sink){
        Map<String, Map<String, Integer>> residual = network.generateResidualFlowMap();
        List<String> path = pathFinder.flowPath(network, source, sink, residual);

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

            path = pathFinder.flowPath(network, source, sink, residual);
        }

        double cost = costOfFlow(network, residual);
        return new FlowCost(maxFlow, cost);
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




    // Adapted functions for analysis
    public double costOfFlow(Network network, Map<String, Map<String, Integer>> residual){
        double cost = 0.0;
        String from;
        String to;
        EdgeWeights ew;

        for (Map.Entry<String, Map<String, EdgeWeights>> outer : network.outEdges.entrySet()){
            from = outer.getKey();

            for (Map.Entry<String, EdgeWeights> inner: outer.getValue().entrySet()){
                to = inner.getKey();
                ew = inner.getValue();
                cost += (ew.capacity - residual.get(from).get(to)) * ew.cost;
            }
        }

        return cost;
    }


    public FlowCost minCostFlow(Network network, String source, String sink){
        Map<String, Map<String, EdgeWeights>> residual = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeWeights ew;

        List<String> path = pathFinder.minCostPath(network, source, sink, residual);

        while(!path.isEmpty()) {
            // Print path (for analysis)
            System.out.println(path.reversed());

            // Get bottleneck
            currentFlow = pathBottleneck(path, residual);

            totalFlow += currentFlow;
            to = path.getFirst();

            // Update residual graph and track probability
            for (int i = 1; i < path.size(); i++){
                from = path.get(i);
                ew = residual.get(from).get(to);
                totalCost += ew.cost * currentFlow;
                ew.flow += currentFlow;
                to = from;
            }

            path = pathFinder.minCostPath(network, source, sink, residual);
        }

        return new FlowCost(totalFlow, totalCost);
    }


    // To be removed, but part of hand-in
    // Incorrect: can not handle reversed edges properly
    public FlowCost maxFlowCost(Network network, String source, String sink){
        Map<String, Map<String, Integer>> residual = network.generateResidualFlowMap();
        Map<String, Map<String, EdgeWeights>> original = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String parent;
        String child;
        double cost;

        List<String> path = pathFinder.flowPath(network, source, sink, residual);

        while(!path.isEmpty()) {
            // Print path (for analysis)
            System.out.println(path.reversed());

            // Get bottleneck
            currentFlow = pathBottleneckInt(path, residual);

            totalFlow += currentFlow;
            child = path.getFirst();

            // Update residual graph and track probability
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

            path = pathFinder.flowPath(network, source, sink, residual);
        }
        return new FlowCost(totalFlow, totalCost);
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
