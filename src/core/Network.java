package core;

import java.util.*;

public class Network {
    public HashMap<String, Node> nodes = new HashMap<>();
    
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

    public void printGraph(){
        for (String nodeId : nodes.keySet()) {
            System.out.println(nodeId);
            for (Map.Entry<String, EdgeWeights> entry: outEdges.get(nodeId).entrySet()){
                System.out.println("-> " + entry.getKey() + ": " + entry.getValue().toString());
            }
        }
    }


    // Generators
    public  Map<String, Double> generateCostMap(String source){
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

    public Map<String, Map<String, EdgeWeights>> generateCopyMap(){
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

    public Map<String, Map<String, Integer>> generateResidualFlowMap(){
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

    public Map<String, Map<String, EdgeWeights>> generateResidualMap(){
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
