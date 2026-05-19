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
        Map<String, Double> costTo = network.generateCostMap(source);
        Map<String, String> parent = new HashMap<>(); // toId, fromId that leads to lowest cost from source

        queue.add(new CostToNode(source, 0.0));
        double cost;
        String current;
        EdgeWeights ew;
        String to;

        while (!queue.isEmpty()) {
            current = queue.poll().nodeId;
            visited.add(current);

            // Update total costTo if lower cost is found
            // Save parent to keep track of path
            for (Map.Entry<String, EdgeWeights> entry : network.getEdges(current).entrySet()) {
                to = entry.getKey();
                ew = entry.getValue();
                cost = ew.cost + costTo.get(current);
                if (cost < costTo.get(to)) {
                    costTo.put(to, cost);
                    parent.put(to, current);
                }
                if (!visited.contains(to)) {
                    queue.add(new CostToNode(to, costTo.get(to)));
                }
            }
        }
        return parent;
    }




    public Map<String, String> computeDijkstraJohnson(Network network, String source, Map<String, Map<String, EdgeWeights>> edges) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costTo = network.generateCostMap(source);         // Cost from source node to given node
        Map<String, String> parent = new HashMap<>();            // toId, fromId that leads to lowest cost from source

        // Track Johnson potentials
        Map<String, Integer> potentials = new HashMap<>();

        for (String node: network.nodes.keySet()){
            potentials.put(node, 0);
        }

        queue.add(new CostToNode(source, 0.0));
        double cost = 0;
        String from;
        String to;
        EdgeWeights ew;

        boolean canFlow = false;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);

            // Update total costTo if lower cost is found
            // Save parent to keep track of path
            for (Map.Entry<String, EdgeWeights> edge : edges.get(from).entrySet()) {
                to = edge.getKey();
                ew = edge.getValue();

                // If flow is left or can be undone, calculate cost (Johnson)
                // Check if can flow over original edge
                if (ew.flow < ew.capacity){
                    cost = costTo.get(from) + potentials.get(from) - potentials.get(to) + ew.cost;
                    canFlow = true;
                }
                // Check if previous flow to current node can be undone
                else if (edges.get(to).get(from).flow != 0){
                    cost = costTo.get(from) + potentials.get(from) - potentials.get(to) - ew.cost;
                    canFlow = true;
                }


                if (canFlow && cost < costTo.get(to)) {
                    costTo.put(to, cost);
                    parent.put(to, from);
                }

                if (canFlow && !visited.contains(to)) {
                    queue.add(new CostToNode(to, costTo.get(to)));
                }
                canFlow = false;
            }
        }
        return parent;
    }
}
