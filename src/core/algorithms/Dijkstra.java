package core.algorithms;

import core.EdgeAttributes;
import core.Network;
import core.models.CostToNode;
import core.models.ProbabilityToNode;

import java.util.*;

public class Dijkstra {

    public Map<String, String> computeDijkstraJohnson(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> edges, Map<String, Double> potentials) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Cost from source node to given node
        Map<String, Double> costFromSource = network.generateCostMap(source);

        Map<String, String> parents = new HashMap<>();                         // child with parent that has the lowest cost from source

        queue.add(new CostToNode(source, 0.0));
        double cost = Double.POSITIVE_INFINITY;
        String from;
        String to;
        EdgeAttributes edgeAttrs;

        boolean canFlow = false;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower cost is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeAttributes> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                edgeAttrs = edge.getValue();

                // If flow is left or can be cancelled
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    cost = costFromSource.get(from) - edges.get(to).get(from).cost + potentials.get(from) - potentials.get(to);
                    canFlow = true;
                }

                // Check if can flow over original edge
                else if (edgeAttrs.flow < edgeAttrs.capacity){
                    cost = costFromSource.get(from) + edgeAttrs.cost + potentials.get(from) - potentials.get(to);
                    canFlow = true;
                }

                 if (canFlow){
                    // Epsilon check to handle floating-point precision errors
                    if (Math.abs(cost) < 1e-9){
                        cost = 0.0;
                    }
                    if (cost < costFromSource.get(to)) {
                        costFromSource.put(to, cost);
                        parents.put(to, from);
                    }

                    if (!visited.contains(to) && !to.equals(sink)) {
                        queue.add(new CostToNode(to, costFromSource.get(to)));
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


    public Map<String, String> computeDijkstraProbability(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> edges) {
        PriorityQueue<ProbabilityToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> probabilityFromSource = network.generateProbabilityMap(source);         // Probability of successfully reaching given node from source node

        Map<String, String> parents = new HashMap<>();                         // child, parents that has the lowest probability from source

        queue.add(new ProbabilityToNode(source, probabilityFromSource.get(source)));
        double probability;
        String from;
        String to;
        EdgeAttributes edgeAttrs;

        boolean canFlow = false;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);
            // Update total probabilityFromSource if higher probability is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeAttributes> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                edgeAttrs = edge.getValue();
                probability = probabilityFromSource.get(from);

                // If flow is left or can be undone, calculate probability (Johnson reweighting)
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    // Don't include from and to in a reversed edge, because they are already used in a previous path
                    // Thus remove prob of from
                    // No need to remove risk of to, because it's only included in forwards edges, thus the probFromSource is already correct
                    probability -= Math.log(1-network.getNode(from).risk);
                    canFlow = true;
                }
                // Check if can flow over original edge
                else if (edgeAttrs.flow < edgeAttrs.capacity){
                    probability += Math.log(1-network.getNode(to).risk);
                    canFlow = true;
                }

                if (canFlow){
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

        return parents;
    }


    // For analysis
    public Map<String, String> computeDijkstraFlow(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> edges) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costFromSource = network.generateCostMap(source);         // Cost from source node to given node
        Map<String, String> parents = new HashMap<>();                         // child, parents that has the lowest probability from source

        queue.add(new CostToNode(source, 0.0));
        double cost;
        String from;
        String to;
        EdgeAttributes edgeAttrs;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower probability is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeAttributes> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                edgeAttrs = edge.getValue();

                // Check if can flow over edge
                if (edgeAttrs.flow < edgeAttrs.capacity){
                    cost = costFromSource.get(from) + edgeAttrs.cost;

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


    public void updatePotentials(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> edges, Map<String, Double> potentials) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Cost from source node to given node
        Map<String, Double> costFromSource = network.generateCostMap(source);

        queue.add(new CostToNode(source, 0.0));
        double cost = Double.POSITIVE_INFINITY;
        String from;
        String to;
        EdgeAttributes edgeAttrs;

        boolean canFlow = false;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower cost is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeAttributes> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                edgeAttrs = edge.getValue();

                // If flow is left or can be undone, calculate probability (Johnson reweighting)
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    cost = costFromSource.get(from) - edges.get(to).get(from).cost + potentials.get(from) - potentials.get(to);
                    canFlow = true;
                }

                // Check if can flow over original edge
                else if (edgeAttrs.flow < edgeAttrs.capacity){
                    cost = costFromSource.get(from) + edgeAttrs.cost + potentials.get(from) - potentials.get(to);
                    canFlow = true;
                }

                if (canFlow){
                    // Epsilon check to handle floating-point precision errors
                    if (Math.abs(cost) < 1e-9){
                        cost = 0.0;
                    }
                    if (cost < costFromSource.get(to)) {
                        costFromSource.put(to, cost);
                    }

                    if (!visited.contains(to) && !to.equals(sink)) {
                        queue.add(new CostToNode(to, costFromSource.get(to)));
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
    }

    // Regular dijkstra, no flow considerations
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
        EdgeAttributes edgeAttrs;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costFromSource if lower cost is found
            // Save parents to keep track of path
            for (Map.Entry<String, EdgeAttributes> entry : network.getEdges(from).entrySet()) {
                to = entry.getKey();
                edgeAttrs = entry.getValue();
                cost = edgeAttrs.cost + costFromSource.get(from);
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
}