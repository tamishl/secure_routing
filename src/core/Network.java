package core;

import core.utils.Algorithm;
import core.utils.CostToNode;
import core.utils.FlowCost;

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




    public List<String> minCostPath(String source, String destination, Algorithm algorithm, Map<String, Map<String, EdgeWeights>> residual){
        if (!isPath(source, destination)) {
            return new ArrayList<>();
        }

        List<String> path = new ArrayList<>();

        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord(source);
                    case Algorithm.DIJKSTRA -> dijkstra(source);
                    case Algorithm.DIJKSTRA_JOHNSON -> dijkstraJohnson(source, residual);

                }
        ;

        if (parents.isEmpty()){
            return path;
        }

        // Get path from given list by tracing backwards from target
        for (String node = destination; node != null; node = parents.get(node)){
            path.add(node);
        }

        return path;
    }

    public List<String> minCostPath(String source, String destination, Algorithm algorithm){
        return minCostPath(source, destination, algorithm, generateResidualMap());
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
        Map<String, Double> costTo = generateCostMap(source);
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
        Map<String, Double> costTo = generateCostMap(source);
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
    public Map<String, String> dijkstraJohnson(String source, Map<String, Map<String, EdgeWeights>> edges) {
        PriorityQueue<CostToNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costTo = generateCostMap(source);         // Cost from source node to given node
        Map<String, String> parent = new HashMap<>();            // toId, fromId that leads to lowest cost from source

        // Track Johnson potentials
        Map<String, Integer> potentials = new HashMap<>();

        for (String node: nodes.keySet()){
            potentials.put(node, 0);
        }

        queue.add(new CostToNode(source, 0.0));
        double cost = 0;
        String from;
        String to;
        EdgeWeights ew;

        boolean canFlow = false;
        double cheapest;

        while (!queue.isEmpty()) {
            from = queue.poll().nodeId;
            visited.add(from);
            cheapest = Double.POSITIVE_INFINITY;

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

    public Map<String, String> dijkstraJohnson(String source){
        Map<String, Map<String, EdgeWeights>> residual = generateResidualMap();
        return dijkstraJohnson(source, residual);
    }


    public FlowCost minCostFlow(String source, String destination){
        Map<String, Map<String, EdgeWeights>> residual = generateResidualMap();

        int totalFlow = 0;
        int currentFlow;
        double totalCost = 0.0;

        String from;
        String to;
        EdgeWeights ew;

        List<String> path = minCostPath(source, destination, Algorithm.DIJKSTRA_JOHNSON, residual);

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

            path = minCostPath(source, destination, Algorithm.DIJKSTRA_JOHNSON, residual);
        }

        return new FlowCost(totalFlow, totalCost);
    }


    // Basic Ford-Fulkerson: maximum possible flow from S to D
    public int maxFlow(String source, String destination){
        Map<String, Map<String, Integer>> rNetwork = generateResidualFlowMap();
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




    // Generators
    private  Map<String, Double> generateCostMap(String source){
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

    private Map<String, Map<String, EdgeWeights>> generateCopyMap(){
        Map<String, Map<String, EdgeWeights>> rNetwork = new HashMap<>();

        String from;
        String to;
        EdgeWeights ew;

        for(Map.Entry<String, Map<String, EdgeWeights>> outerEntry: outEdges.entrySet()){
            from = outerEntry.getKey();
            rNetwork.put(from, new HashMap<>());
            for(Map.Entry<String, EdgeWeights> innerEntry: outerEntry.getValue().entrySet()) {
                to = innerEntry.getKey();
                ew = innerEntry.getValue();
                rNetwork.get(from).put(to, new EdgeWeights(ew.capacity, ew.cost));
            }
        }

        return rNetwork;
    }

    private Map<String, Map<String, Integer>> generateResidualFlowMap(){
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

    private Map<String, Map<String, EdgeWeights>> generateResidualMap(){
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


}
