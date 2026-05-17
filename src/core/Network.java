package core;

import core.utils.Algorithm;
import core.utils.CostToNode;

import java.util.*;

public class Network {
    private HashMap<String, Node> nodes = new HashMap<>();
    
    public Map<String, Map<String, EdgeWeights>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per node


    public void addNodeIfAbsent(String id, double probability, NodeType type) {
        if (!nodes.containsKey(id)) {
            nodes.put(id, new Node(id, type, probability));
            outEdges.put(id, new HashMap<>());
        }
    }

    // Infer NodeType by ID prefix
    public void addNodeIfAbsent(String id, double probability) {
        if (!nodes.containsKey(id)) {
            NodeType type;
            if (id.startsWith(NodeType.SOURCE.getPrefix())){
                type = NodeType.SOURCE;
            }
            else if (id.startsWith(NodeType.DESTINATION.getPrefix())){
                type = NodeType.DESTINATION;
            }
            else {
                type = NodeType.INNER_NODE;
            }

            nodes.put(id, new Node(id, type, probability));
            outEdges.put(id, new HashMap<>());
        }
    }

    public void insertEdge(String fromId, String toId, int capacity, double cost){
        outEdges.get(fromId).put(toId, new EdgeWeights(capacity, cost));
    }




    public void minCostPath(String source, String destination, List<String> path, Algorithm algorithm){
        if (!isPath(source, destination)) {
            return;
        }

        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord(source);
                    case Algorithm.DIJKSTRA -> dijkstra(source);
                    case Algorithm.DIJKSTRA_JOHNSON -> dijkstraJohnson(source);

                }
        ;

            // Get path from given list by tracing backwards from target
        for (String node = destination; node != null; node = parents.get(node)){
            path.add(node);
        }
    }

    // Same implementation but without changing state (no List arg)
    public List<String> minCostPath(String source, String target, Algorithm algorithm) {
        ArrayList<String> path = new ArrayList<>();
        minCostPath(source, target, path,algorithm);
        return path;
    }



    // BFS: Return first found path if target can be reached from given node, checking for residual flow.
    public Map<String, String> getParents(String source, String target, Map<String, Map<String, Integer>> rNetwork){
        Map<String, String> parent = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);
        visited.add(source);

        String from;
        while (!queue.isEmpty()){
            from = queue.poll();
            for (String to : getEdges(from).keySet()){
                // Skip if no capacity on edge
                if (rNetwork.get(from).get(to) == 0){
                    continue;
                }

                if (to.equals(target)){
                    parent.put(to, from);
                    return parent;
                }
                if (!visited.contains(to)){
                    queue.add(to);
                    parent.put(to, from);
                    visited.add(to);
                }
            }
        }

        return new HashMap<>();
    }

    // BFS: Check if destination can be reached from given node without checks for flow
    public boolean isPath(String source, String destination){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);

        while (!queue.isEmpty()){
            String current = queue.poll();
            visited.add(current);
            for (String toId: getEdges(current).keySet()){
                if (toId.equals(destination)){
                    return true;
                }
                if (!visited.contains(toId)){
                    queue.add(toId);
                }
            }
        }

        return false;
    }


    private Map<String, Map<String, Integer>> getResidualFlowMap(){
        Map<String, Map<String, Integer>> rNetwork = new HashMap<>();
        int capacity;

        for (String from: nodes.keySet()){
            rNetwork.put(from, new HashMap<>());
            for (String to: nodes.keySet()){
                capacity = outEdges.get(from).containsKey(to)
                        ? outEdges.get(from).get(to).capacity
                        : 0;
                rNetwork.get(from).put(to, capacity);
            }
        }

        return rNetwork;
    }

    private Map<String, Map<String, EdgeWeights>> getResidualMap(){
        Map<String, Map<String, EdgeWeights>> rNetwork = new HashMap<>();
        int capacity;
        double cost;

        for (String from: nodes.keySet()){
            rNetwork.put(from, new HashMap<>());
            for (String to: nodes.keySet()){
                capacity = outEdges.get(from).containsKey(to)
                        ? outEdges.get(from).get(to).capacity
                        : 0;
                cost = outEdges.get(from).containsKey(to)
                        ? outEdges.get(from).get(to).cost
                        : 0.0;
                rNetwork.get(from).put(to, new EdgeWeights(capacity, cost));
            }
        }

        return rNetwork;
    }



    public void printGraph(){
        for (String nodeId : nodes.keySet()) {
            System.out.println(nodeId);
            for (Map.Entry<String, EdgeWeights> entry: outEdges.get(nodeId).entrySet()){
                System.out.println("-> " + entry.getKey() + ": " + entry.getValue().toString());
            }
        }
    }


    // ---------------------------- Algorithms ----------------------------
    // Bellman-Ford
    public Map<String, String> bellmanFord(String source){
        Map<String, Double> costTo = getCostMap(source);
        Map<String, String> parents = new HashMap<>();

        // Can add sets to avoid calculation for inaccessible nodes, but also leads to additional checks in each iteration
//        Set<String> visitable  = new HashSet<>();
//        visitable.add(source);

        String from;
        String to;
        double cost;

        boolean changed = true;

        // Iterate N times and check for negative cycles during last loop (instead of creating 2 for-loops)
        for (int i = 0; i < nodes.size(); i++){
            // Prevent futile iterations: stop when no improvement happened in previous cycle.
            if (!changed){
                return parents;
            }
            changed = false;

            for(Map.Entry<String, Map<String, EdgeWeights>> outerEntry: outEdges.entrySet()){
                from = outerEntry.getKey();
                for(Map.Entry<String, EdgeWeights> innerEntry: outerEntry.getValue().entrySet()){
                    to = innerEntry.getKey();
                    cost = costTo.get(from) + innerEntry.getValue().cost;
                    if (cost < costTo.get(to)){

                        // If still improvement after all edges have been seen, network has a negative cycle.
                        // Return empty map
                        if (i == nodes.size()-1){
                            return new HashMap<>();
                            // To find nodes affected by the negative cycle:
//                            costTo.put(to, Double.NEGATIVE_INFINITY);

                        }
                        changed = true;
                        costTo.put(to, cost);
                        parents.put(to, from);
                    }
                }
            }
        }

        return parents;
    }


    // DSP
    public Map<String, String> dijkstra(String source) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Cost from source node to given node
        Map<String, Double> costTo = getCostMap(source);
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
            for (Map.Entry<String, EdgeWeights> entry : getEdges(current).entrySet()) {
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



    // DSP
    public Map<String, String> dijkstraJohnson(String source) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Map<String, EdgeWeights>> edges = getResidualMap();
        Map<String, Double> costTo = getCostMap(source);         // Cost from source node to given node
        Map<String, String> parent = new HashMap<>();            // toId, fromId that leads to lowest cost from source

        // Track Johnson potentials
        Map<String, Integer> potentials = new HashMap<>();

        for (String node: nodes.keySet()){
            potentials.put(node, 0);
        }

        queue.add(new CostToNode(source, 0.0));
        double cost = 0;
        String from;
        EdgeWeights ew;
        String to;
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
                // Check if previous flow to current node can be undone
                if (edges.get(to).get(from).flow != 0){
                    cost = costTo.get(from) + potentials.get(from) - potentials.get(to) - ew.cost;
                    canFlow = true;
                }

                // Check if can flow over original edge
                if (ew.flow < ew.capacity){
                    cost = costTo.get(from) + potentials.get(from) - potentials.get(to) + ew.cost;
                    canFlow = true;
                }

                if (canFlow && cost < costTo.get(to)) {
                    costTo.put(to, cost);
                    parent.put(to, from);
                    canFlow = false;
                }
                if (!visited.contains(to)) {
                    queue.add(new CostToNode(to, costTo.get(to)));
                }
            }
        }
        return parent;
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public int maxFlow(String source, String destination){
        Map<String, Map<String, Integer>> rNetwork = getResidualFlowMap();
        Map<String, String> parent = getParents(source, destination, rNetwork);

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
            parent = getParents(source, destination, rNetwork);
        }

        return maxFlow;
    }


    // Maximum flow in the cheapest path
    public int maxFlowMinPath(String source, String destination, Algorithm algorithm){
        List<String> path = minCostPath(source, destination, algorithm);
        String current = path.getFirst();
        String parent;
        int maxFlow = Integer.MAX_VALUE;

        for (int i = 1; i < path.size(); i++){
            parent = path.get(i);
            maxFlow = Math.min(maxFlow, outEdges.get(parent).get(current).capacity);
            current = parent;
        }

        return maxFlow;
    }

    // DFS traversal to check connectivity (recursive)
    public void visitDepthFirst(String nodeId, Set<String> visited) {
        // End if the node is already visited
        if (visited.contains(nodeId)) {
            return;
        }
        visited.add(nodeId);
        // Repeat the method for each node that is connected to the current node
        for (String toId: getEdges(nodeId).keySet())
            visitDepthFirst(toId, visited);
    }




    // Getters
    public Node getNode(String str){
        nodes.get(str);
        return nodes.get(str);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public Map<String, EdgeWeights> getEdges(String nodeId){
        return outEdges.get(nodeId);
    }


    private  Map<String, Double> getCostMap(String source){
        Map<String, Double> costMap = new HashMap<>(); // Cost from source node to given node

        for (String node : nodes.keySet()) {
            if (node.equals(source)){
                costMap.put(node, 0.0);
                continue;
            }
            costMap.put(node, Double.MAX_VALUE);
        }

        return costMap;
    }

}
