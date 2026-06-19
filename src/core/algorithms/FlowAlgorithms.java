package core.algorithms;

import core.EdgeAttributes;
import core.Network;
import core.models.CostProb;
import core.models.FlowCost;
import core.models.FlowCostProb;
import core.models.FlowEstCostProb;

import java.util.List;
import java.util.Map;

public class FlowAlgorithms {
    private final PathFinder pathFinder;

    public FlowAlgorithms(PathFinder pathFinder){
        this.pathFinder = pathFinder;
    }


    public FlowEstCostProb minCostMaxFlow(Network network, String source, String sink) {
        Map<String, Map<String, EdgeAttributes>> residual = network.generateResidualMap();

        int totalFlow = 0;
        double totalCost = 0.0;
        double totalProbability = 0.0;
        double estimateOfReceival = 0;

        FlowCostProb updates;

        Map<String, Double> potentials = network.generatePotentials();

        List<String> path = pathFinder.minCostFlowPath(network, source, sink, residual, potentials);

        while (!path.isEmpty()) {
//            totalFlow += update(residual, path);
            updates = updateAndCount(network, residual, path);

            totalFlow += updates.flow;
            totalCost += updates.cost * updates.flow;
            totalProbability += Math.log(updates.probability) * updates.flow;

            // Doesn't handle it correctly for reversed edges; once it's exp it's not additive anymore.
            estimateOfReceival += updates.flow * updates.probability;
            path = pathFinder.minCostFlowPath(network, source, sink, residual, potentials);
        }

//        CostProb cp = costProbOfFlow(network, residual, source);

        return new FlowEstCostProb(totalFlow, estimateOfReceival, totalCost, totalProbability);
    }

    public FlowEstCostProb maxProbMaxFlow(Network network, String source, String sink) {
        Map<String, Map<String, EdgeAttributes>> residual = network.generateResidualMap();

        int totalFlow = 0;
        double totalCost = 0.0;
        double totalProbability = 0.0;
        double estimateOfReceival = 0.0;

        FlowCostProb updates;

        List<String> path = pathFinder.minRiskFlowPath(network, source, sink, residual);


        while (!path.isEmpty()) {
            updates = updateAndCount(network, residual, path);

            totalFlow += updates.flow;
            totalCost += updates.cost * updates.flow;
            totalProbability += Math.log(updates.probability) * updates.flow;

            // Doesn't handle it correctly for reversed edges; once it's exp it's not additive anymore.
            estimateOfReceival += updates.flow * updates.probability;

            path = pathFinder.minRiskFlowPath(network, source, sink, residual);
        }

        return new FlowEstCostProb(totalFlow, estimateOfReceival, totalCost, totalProbability);
    }

    // Update the residual graph based on given path and calculate cost and probability of path
    // Return max flow, cost and probability of path (cost and prob not updated to flow)
    private FlowCostProb updateAndCount(Network network, Map<String, Map<String, EdgeAttributes>> residual, List<String> path){
        // Print path (for analysis)
        System.out.println(path.reversed());

        String parent;
        String child;
        EdgeAttributes edgeAttrs;

        int currentFlow = pathBottleneck(path, residual);
        double currentCost = 0.0;
        double currentProbability = 1.0;

        child = path.getFirst();

        // Update residual graph and track cost
        for (int i = 1; i < path.size(); i++) {
            parent = path.get(i);
            currentProbability *= 1.0 - network.getNode(child).risk;

            // If reversed edge: flow in opposite direction than path
            if (residual.get(child).get(parent).flow != 0) {
                edgeAttrs = residual.get(child).get(parent);
                currentCost -= edgeAttrs.cost;
                edgeAttrs.flow -= currentFlow;

                // Can use multiplication and division as it's a single path
                currentProbability /= (1.0 - network.getNode(child).risk) * (1.0 - network.getNode(parent).risk);
            }

            // If original edge
            else {
                edgeAttrs = residual.get(parent).get(child);
                edgeAttrs.flow += currentFlow;
                currentCost += edgeAttrs.cost;
            }

            child = parent;
        }

        currentProbability *= 1 - network.getNode(child).risk;

        return new FlowCostProb(currentFlow, currentCost, currentProbability);
    }


    // Update the residual graph based on given path
    // Return max flow on path
    private int update(Map<String, Map<String, EdgeAttributes>> residual, List<String> path){
        // Print path (for analysis)
        System.out.println(path.reversed());

        String parent;
        String child;
        EdgeAttributes edgeAttrs;

        int currentFlow = pathBottleneck(path, residual);

        child = path.getFirst();

        // Update residual graph and track cost
        for (int i = 1; i < path.size(); i++) {
            parent = path.get(i);

            // If reversed edge: flow in opposite direction than path
            if (residual.get(child).get(parent).flow != 0) {
                edgeAttrs = residual.get(child).get(parent);
                edgeAttrs.flow -= currentFlow;
            }

            // If original edge
            else {
                edgeAttrs = residual.get(parent).get(child);
                edgeAttrs.flow += currentFlow;
            }

            child = parent;
        }
        return currentFlow;
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public FlowCostProb maxFlow(Network network, String source, String sink){
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

        CostProb cp = costProbOfFlowInt(network, residual, source);
        return new FlowCostProb(maxFlow, cp.cost, cp.probability);
    }



    // Maximum flow in given path
    private int pathBottleneck(List<String> path, Map<String, Map<String, EdgeAttributes>> residual){
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




    // Functions to calculate totals based on a residual graph

    // Adapted functions for analysis
    public double costOfFlow(Network network, Map<String, Map<String, Integer>> residual){
        double cost = 0.0;
        String from;
        String to;
        EdgeAttributes edgeAttrs;

        for (Map.Entry<String, Map<String, EdgeAttributes>> outer : network.outEdges.entrySet()){
            from = outer.getKey();

            for (Map.Entry<String, EdgeAttributes> inner: outer.getValue().entrySet()){
                to = inner.getKey();
                edgeAttrs = inner.getValue();
                cost += (edgeAttrs.capacity - residual.get(from).get(to)) * edgeAttrs.cost;
            }
        }

        return cost;
    }


    public CostProb costProbOfFlowInt(Network network, Map<String, Map<String, Integer>> residual, String source){
        double totalCost = 0.0;
        double totalProbability = 0.0;

        String from;
        String to;
        int edgeFlow;
        double edgeProbability;
        EdgeAttributes edgeAttrs;


        for (Map.Entry<String, Map<String, EdgeAttributes>> outer : network.outEdges.entrySet()){
            from = outer.getKey();

            for (Map.Entry<String, EdgeAttributes> inner: outer.getValue().entrySet()){
                to = inner.getKey();
                edgeAttrs = inner.getValue();
                edgeFlow = edgeAttrs.capacity - residual.get(from).get(to);
                totalCost += edgeFlow * edgeAttrs.cost;

                // Include source risk in its outgoing edges
                // Like converting node risk to edge risk (incoming edges)
                if (from.equals(source)){
                    edgeProbability = Math.log((1 - network.getNode(from).risk) * (1 - network.getNode(to).risk));
                }

                else {
                    edgeProbability = Math.log(1 - network.getNode(to).risk);
                }
                totalProbability += edgeProbability * edgeFlow;
            }
        }

        return new CostProb(totalCost, totalProbability);
    }


    public CostProb costProbOfFlow(Network network, Map<String, Map<String, EdgeAttributes>> residual, String source){
        double totalCost = 0.0;
        double totalProbability = 0.0;

        String from;
        String to;
        int edgeFlow;
        double edgeProbability;
        EdgeAttributes edgeAttrs;


        for (Map.Entry<String, Map<String, EdgeAttributes>> outer : network.outEdges.entrySet()){
            from = outer.getKey();

            for (Map.Entry<String, EdgeAttributes> inner: outer.getValue().entrySet()){
                to = inner.getKey();
                edgeAttrs = inner.getValue();
                edgeFlow = residual.get(from).get(to).flow;
                totalCost += edgeFlow * edgeAttrs.cost;

                // Include source risk in its outgoing edges
                // Like converting node risk to edge risk (incoming edges)
                if (from.equals(source)){
                    edgeProbability = Math.log((1 - network.getNode(from).risk) * (1 - network.getNode(to).risk));
                }

                else {
                    edgeProbability = Math.log(1 - network.getNode(to).risk);
                }
                totalProbability += edgeProbability * edgeFlow;
            }
        }

        return new CostProb(totalCost, totalProbability);
    }

    // Function for analysis: without use of reversed edges
    public FlowCost minCostFlow(Network network, String source, String sink){
        Map<String, Map<String, EdgeAttributes>> residual = network.generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeAttributes edgeAttrs;

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
                edgeAttrs = residual.get(from).get(to);
                totalCost += edgeAttrs.cost * currentFlow;
                edgeAttrs.flow += currentFlow;
                to = from;
            }

            path = pathFinder.minCostPath(network, source, sink, residual);
        }

        return new FlowCost(totalFlow, totalCost);
    }
}
