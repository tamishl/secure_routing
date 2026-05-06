package core;

import java.util.*;

public class Network {
    private TrustSystem trustSystem;
    private HashMap<String, Node> nodes = new HashMap<>();
    
    Map<String, Map<String, EdgeWeights>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per node

    public Network(TrustSystem trustSystem) {
        this.trustSystem = trustSystem;
    }

    public void createNode(String id) {
        nodes.put(id, new Node(id));
        outEdges.put(id, new HashMap<>());
        trustSystem.addNode(id);
    }

    public void insertEdge(String fromId, String toId, int capacity, double time){
        outEdges.get(fromId).put(toId, new EdgeWeights(capacity, time));
    }

//    public Integer minFlow(String sourceId, String targetId){
//        String current = targetId;
//        Integer maxFlow = Integer.MAX_VALUE;
//        while (!current.equals(sourceId)){
//            for (String v: minCostPath(sourceId, targetId)){
//                maxFlow = Math.min(maxFlow, )
//            }
//        }
//    }


    public List<String> minCostPath(String sourceId, String targetId) {
        if (!isPath(sourceId, targetId)) {
            return null;
        }

        Map<String, String> minCosts = minCosts(sourceId);

        List<String> minPath = new ArrayList<>();

        // Get path from given list by tracing backwards from target
        for (String nodeId = targetId; nodeId != null; nodeId = minCosts.get(nodeId)){
            minPath.add(nodeId);
        }

        Collections.reverse(minPath);
        return minPath;
    }

    // DSP to get the parent list of nodes with the min cost from the source
    public Map<String, String> minCosts(String sourceId) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        Map<String, Double> costTo = new HashMap<>(); // Cost from source node to given node
        Map<String, String> parents = new HashMap<>(); // toId, fromId that leads to lowest cost from sourceId

        // Q: Initialize all or containsKey() in while-loop?
        for (String nodeId : nodes.keySet()) {
            costTo.put(nodeId, Double.MAX_VALUE);
        }
        costTo.put(sourceId, 0.0);
        queue.add(sourceId);
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
                cost = ew.time + costTo.get(current);
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
    public boolean isPath(String sourceId, String targetId, Set<String> excluded){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>(excluded);
        queue.add(sourceId);

        while (!queue.isEmpty()){
            String current = queue.poll();
            visited.add(current);
            for (String toId: getEdges(current).keySet()){
                if (toId.equals(targetId)){
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
    public boolean isPath(String sourceId, String targetId){
        return isPath(sourceId, targetId, new HashSet<>(){});
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
            System.out.println(nodeId + ":");
            for (Map.Entry<String, EdgeWeights> entry: outEdges.get(nodeId).entrySet()){
                ew = entry.getValue();
                System.out.println(entry.getKey());
                System.out.print(ew.toString());
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
