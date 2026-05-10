package core;

import java.util.*;

public class Network {
    private HashMap<String, Node> nodes = new HashMap<>();
    
    Map<String, Map<String, EdgeWeights>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per node


    public void addNodeIfAbsent(String id, NodeType type, double probability) {
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

    public Integer maxFlow(String source, String target){
        List<String> path = minCostPath(source, target);
        String parent = path.getFirst();
        String current;
        Integer maxFlow = Integer.MAX_VALUE;

        // No outer while-loop because last element is target node
        for (int i = 1; i < path.size(); i++){
            current = path.get(i);
            maxFlow = Math.min(maxFlow, outEdges.get(parent).get(current).capacity);
            parent = current;
        }

        return maxFlow;
    }


    public List<String> minCostPath(String source, String target) {
        if (!isPath(source, target)) {
            return null;
        }

        Map<String, String> minCosts = minCosts(source);

        List<String> minPath = new ArrayList<>();

        // Get path from given list by tracing backwards from target
        for (String nodeId = target; nodeId != null; nodeId = minCosts.get(nodeId)){
            minPath.add(nodeId);
        }

        Collections.reverse(minPath);
        return minPath;
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
                cost = ew.capacity + costTo.get(current);
                if (cost < costTo.get(entry.getKey())) {
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
    public boolean isPath(String source, String target, Set<String> excluded){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>(excluded);
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

    // BFS: Check if target can be reached from given node
    // Overload to allow search without excluded nodes
    public boolean isPath(String source, String target){
        return isPath(source, target, new HashSet<>(){});
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
        EdgeWeights ew;
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
