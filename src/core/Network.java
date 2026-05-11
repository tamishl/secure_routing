package core;

import java.util.*;

public class Network {
    private HashMap<String, Node> nodes = new HashMap<>();
    
    Map<String, Map<String, EdgeWeights>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per node


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

    private Network getFullResidualNetwork(){
        Network residNetwork = new Network();

        String from;
        String to;
        // Initial flow value
        int flow = 0;

        // Include all objectives for future flexibility
        EdgeWeights weights;
        double cost;

        for (Map.Entry<String, Map<String, EdgeWeights>> outerEntry: outEdges.entrySet()){
            from = outerEntry.getKey();
            residNetwork.addNodeIfAbsent(from, nodes.get(from).probability);
            for(Map.Entry<String, EdgeWeights> innerEntry: outerEntry.getValue().entrySet()){
                // All nodes have a key in outEdges so no need to add to-nodes to the network
                to = innerEntry.getKey();
                cost = -innerEntry.getValue().cost;
                residNetwork.insertEdge(to, from, flow, cost );
            }
        }

        return residNetwork;
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

    public int maxFlow(String source, String destination){
        List<String> parent = new ArrayList<>();

        String to = parent.getFirst();
        String from;
        int maxFlow = Integer.MAX_VALUE;

        while(isPath(source, destination)) {
            minCostPath(source, destination, parent);
            for (int i = 1; i < parent.size(); i++) {
                from = parent.get(i);
                maxFlow = Math.min(maxFlow, outEdges.get(from).get(to).capacity);
                to = from;
            }
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
