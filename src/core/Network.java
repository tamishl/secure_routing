package core;

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


    private Map<String, Map<String, Integer>> getRFlowNetwork(){
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


    public int maxFlowInPath(String source, String destination){
        List<String> path = minCostPath(source, destination);
        String current = path.getFirst();
        String parent;
        int maxFlow = Integer.MAX_VALUE;

        // No outer while-loop because last element is target node
        for (int i = 1; i < path.size(); i++){
            parent = path.get(i);
            maxFlow = Math.min(maxFlow, outEdges.get(parent).get(current).capacity);
            current = parent;
        }

        return maxFlow;
    }

    // Basic Ford-Fulkerson
    public int maxFlow(String source, String destination){
        Map<String, Map<String, Integer>> rNetwork = getRFlowNetwork();
        Map<String, String> parent = getPath(source, destination, rNetwork);

        int maxFlow = 0;

        String to;
        String from;
        int pathFlow;

        while (parent != null) {
            to = destination;
            pathFlow = Integer.MAX_VALUE;

            // Get maximum flow over given path / bottleneck
            // Skip first since to is already initialized
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
            parent = getPath(source, destination, rNetwork);
        }

        return maxFlow;
    }

    public void minCostPath(String source, String target, List<String> parent){
        if (!isPath(source, target)) {
            return;
        }

        Map<String, String> minCosts = minCosts(source);

        // Get path from given list by tracing backwards from target
        for (String nodeId = target; nodeId != null; nodeId = minCosts.get(nodeId)){
            parent.add(nodeId);
        }
    }

    // Same implementation but without changing state
    public List<String> minCostPath(String source, String target) {
        ArrayList<String> parent = new ArrayList<>();
        minCostPath(source, target, parent);
        return parent;
    }

    // DSP to get the parent list of nodes with the min cost from the source
    public Map<String, String> minCosts(String source) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costTo = new HashMap<>(); // Cost from source node to given node
        Map<String, String> parents = new HashMap<>(); // toId, fromId that leads to lowest cost from source

        // Q: Initialize all or containsKey() in while-loop?
        for (String nodeId : nodes.keySet()) {
            costTo.put(nodeId, Double.MAX_VALUE);
        }
        costTo.put(source, 0.0);
        queue.add(source);
        double cost;
        EdgeWeights ew;
        String e;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            visited.add(current);

            // Update total costTo if lower cost is found
            // Save parent to keep track of path
            for (Map.Entry<String, EdgeWeights> entry : getEdges(current).entrySet()) {
                e = entry.getKey();
                ew = entry.getValue();
                cost = ew.cost + costTo.get(current);
                if (cost < costTo.get(e)) {
                    costTo.put(e, cost);
                    parents.put(e, current);
                }
                if (!visited.contains(e)) {
                    queue.add(e);
                }
            }
        }
        return parents;
    }



    // BFS: Check if target can be reached from given node
    // If so, return path
    public Map<String, String> getPath(String source, String target, Map<String, Map<String, Integer>> rNetwork){
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

        return null;
    }

    // BFS: Check if target can be reached from given node
    public boolean isPath(String source, String target){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);

        while (!queue.isEmpty()){
            String current = queue.poll();
            visited.add(current);
            for (String toId: getEdges(current).keySet()){
                if (toId.equals(target)){
                    return true;
                }
                if (!visited.contains(toId)){
                    queue.add(toId);
                }
            }
        }

        return false;
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

    public void printGraph(){
        for (String nodeId : nodes.keySet()) {
            System.out.println(nodeId);
            for (Map.Entry<String, EdgeWeights> entry: outEdges.get(nodeId).entrySet()){
                System.out.println("-> " + entry.getKey() + ": " + entry.getValue().toString());
            }
        }
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
