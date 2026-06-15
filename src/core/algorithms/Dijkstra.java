package core.algorithms;

import core.EdgeWeights;
import core.Network;
import core.models.CostProbability;
import core.models.CostToNode;
import core.models.ProbabilityToNode;

import java.util.*;

public class Dijkstra {

    public Map<String, String> computeDijkstra(Network network, String source) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Cost from source node to given node
        Map<String, Double> costFromSource = network.generateCostMap(source);
        Map<String, String> parents = new HashMap<>(); // toId, fromId that leads to lowest cost from source

        queue.add(new CostToNode(source, 0.0));
        double cost;
        String from;
        String to;
        EdgeWeights ew;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower cost is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeWeights> entry : network.getEdges(from).entrySet()) {
                to = entry.getKey();
                ew = entry.getValue();
                cost = ew.cost + costFromSource.get(from);
                if (cost < costFromSource.get(to)) {
                    costFromSource.put(to, cost);
                    parents.put(to, from);
                }
                if (!visited.contains(to)) {
                    queue.add(new CostToNode(to, costFromSource.get(to)));
                }
            }
        }
        return parents;
    }




    public Map<String, String> computeDijkstraJohnson(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> edges, Map<String, Double> potentials) {
        PriorityQueue<CostProbability> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Cost amd probability of successfully reaching from source node to given node
        Map<String, Double> costFromSource = network.generateCostMap(source);
        Map<String, Double> probabilityFromSource = network.generateProbabilityMap(source);

        Map<String, String> parents = new HashMap<>();                         // child with parent that has the lowest cost from source

        queue.add(new CostProbability(source, 0.0, probabilityFromSource.get(source)));
        double cost = 0.0;
        double probability;
        String from;
        String to;
        EdgeWeights ew;

        boolean canFlow = false;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower cost is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeWeights> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                ew = edge.getValue();
                probability = probabilityFromSource.get(from);

                // If flow is left or can be undone, calculate probability (Johnson reweighting)
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    cost = costFromSource.get(from) - edges.get(to).get(from).cost + potentials.get(from) - potentials.get(to);

                    // Risk of from and to in reversed edge are already used in a previous path
                    // Thus remove probability from and don't add probability of to
                    probability = probabilityFromSource.get(from) - Math.log(1-network.getNode(from).risk);
                    canFlow = true;
                }

                // Check if can flow over original edge
                else if (ew.flow < ew.capacity){
                    cost = costFromSource.get(from) + ew.cost + potentials.get(from) - potentials.get(to);
                    probability = probability + Math.log(1-network.getNode(to).risk);
                    canFlow = true;
                }

                 if (canFlow){
                    // Epsilon check to handle floating-point precision errors
                    if (Math.abs(cost) < 1e-9){
                        cost = 0.0;
                    }
                    if (cost < costFromSource.get(to)) {
                        costFromSource.put(to, cost);
                        probabilityFromSource.put(to, probability);
                        parents.put(to, from);
                    }

                    if (!visited.contains(to) && !to.equals(sink)) {
                        queue.add(new CostProbability(to, costFromSource.get(to), probabilityFromSource.get(to)));
                    }
                    canFlow = false;
                }
            }
        }

        // Update potentials
        double potential;
        for (String nodeId: potentials.keySet()){
            potential = potentials.get(nodeId) - costFromSource.get(nodeId);
            potentials.put(nodeId, potential);
        }

        return parents;
    }


    public Map<String, String> computeDijkstraJohnsonProb(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> edges, Map<String, Double> potentials) {
        PriorityQueue<ProbabilityToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> probabilityFromSource = network.generateProbabilityMap(source);         // Probability of successfully reaching given node from source node

        Map<String, String> parents = new HashMap<>();                         // child, parents that has the lowest probability from source

        queue.add(new ProbabilityToNode(source, probabilityFromSource.get(source)));
        double probability;
        String from;
        String to;
        EdgeWeights ew;

        boolean canFlow = false;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);
            // Update total probabilityFromSource if higher probability is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeWeights> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                ew = edge.getValue();
                probability = probabilityFromSource.get(from);

                // If flow is left or can be undone, calculate probability (Johnson reweighting)
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    // Don't include from and to in a reversed edge, because they are already used in a previous path
                    // Thus remove log(prob) of from
                    probability = probabilityFromSource.get(from) - Math.log(1-network.getNode(from).risk);
                    canFlow = true;
                }
                // Check if can flow over original edge
                else if (ew.flow < ew.capacity){
                    probability = probability + Math.log(1-network.getNode(to).risk);
                    canFlow = true;
                }

                if (canFlow){
//                    // Epsilon check to handle floating-point precision errors
//                    if (Math.abs(probability) < 1e-9){
//                        probability = 0.0;
//                    }
                    // Using logarithms for numbers [0,1.0] so all values will be 0 or negative
                    // The closer to 0, the higher the probability, so >
                    if (probability > probabilityFromSource.get(to)) {
                        probabilityFromSource.put(to, probability);
                        parents.put(to, from);
                    }

                    if (!visited.contains(to) && !to.equals(sink)) {
                        queue.add(new ProbabilityToNode(to, probabilityFromSource.get(to)));
                    }
                    canFlow = false;
                }
            }
        }

        // Update potentials
        double potential;
        for (String nodeId: potentials.keySet()){
            potential = potentials.get(nodeId) - probabilityFromSource.get(nodeId);
            potentials.put(nodeId, potential);
        }

        return parents;
    }


    // For analysis
    public Map<String, String> computeDijkstraFlow(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> edges) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costFromSource = network.generateCostMap(source);         // Cost from source node to given node
        Map<String, String> parents = new HashMap<>();                         // child, parents that has the lowest probability from source

        queue.add(new CostToNode(source, 0.0));
        double cost = 0.0;
        String from;
        String to;
        EdgeWeights ew;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower probability is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeWeights> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                ew = edge.getValue();

                // Check if can flow over edge
                if (ew.flow < ew.capacity){
                    cost = costFromSource.get(from) + ew.cost;

                    if (cost < costFromSource.get(to)) {
                        costFromSource.put(to, cost);
                        parents.put(to, from);
                    }

                    if (!visited.contains(to) && !to.equals(sink)) {
                        queue.add(new CostToNode(to, costFromSource.get(to)));
                    }
                }
            }
        }
        return parents;
    }


    public boolean dominates(CostProbability cp){
        return false;
    }
}