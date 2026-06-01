package core.algorithms;

import core.EdgeWeights;
import core.Network;
import core.models.CostToNode;

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
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costFromSource = network.generateCostMap(source);         // Cost from source node to given node
        Map<String, String> parents = new HashMap<>();                         // child, parents that has the lowest cost from source

        queue.add(new CostToNode(source, 0.0));
        double cost = 0.0;
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

                // If flow is left or can be undone, calculate cost (Johnson reweighting)
                // Check if can flow over original edge
                if (ew.flow < ew.capacity){
                    cost = costFromSource.get(from) + ew.cost + potentials.get(from) - potentials.get(to);
                    canFlow = true;
                }
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    cost = costFromSource.get(from) - edges.get(to).get(from).cost + potentials.get(from) - potentials.get(to);
                    canFlow = true;
                }

                if (canFlow){
                    if (cost < 0.0){
                        System.out.println("cost: " + cost);
                        System.out.println("from= " + from + ", to =  " + to);
                        System.out.println("cost from source: " + costFromSource.get(from));
                        System.out.println("edge cost: " + edges.get(to).get(from).cost);
                        System.out.println("potential from: " + potentials.get(from));
                        System.out.println("potential to: " + potentials.get(to));
                    }
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
            if (potentials.get(nodeId) < 0.0){
                System.out.println("potential: " + potentials.get(nodeId));
            }
            if (costFromSource.get(nodeId) < 0.0){
                System.out.println("cfs: " + costFromSource.get(nodeId));
            }
            potential = potentials.get(nodeId) + costFromSource.get(nodeId);
            potentials.put(nodeId, potential);
        }

        return parents;
    }
}