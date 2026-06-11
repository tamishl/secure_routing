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
            else if (id.startsWith(NodeType.SINK.getPrefix())){
                type = NodeType.SINK;
            }
            else {
                type = NodeType.INNER;
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

    // Converters
    public void convertSingleSourceSink(){

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
        Map<String, Map<String, Integer>> residual = new HashMap<>();

        for (String from: nodes.keySet()) {
            residual.put(from, new HashMap<>());
        }

        String from;
        String to;
        int capacity;

        for (Map.Entry<String, Map<String, EdgeWeights>> outer : outEdges.entrySet()){
            from = outer.getKey();
            for (Map.Entry<String, EdgeWeights> inner: outer.getValue().entrySet()){
                to = inner.getKey();
                capacity = inner.getValue().capacity;
                residual.get(from).put(to, capacity);
                // Add reversed edge if no antiparallel edge exists
                if (!outEdges.get(to).containsKey(from)){
                    residual.get(to).put(from, 0);
                }
            }
        }
        return residual;
    }

    public Map<String, Map<String, EdgeWeights>> generateResidualMap(){
        Map<String, Map<String, EdgeWeights>> residual = new HashMap<>();

        for (String from: nodes.keySet()) {
            residual.put(from, new HashMap<>());
        }

        String from;
        String to;
        EdgeWeights ew;
        for (Map.Entry<String, Map<String, EdgeWeights>> outer : outEdges.entrySet()){
                from = outer.getKey();
                for (Map.Entry<String, EdgeWeights> inner: outer.getValue().entrySet()){
                    to = inner.getKey();
                    ew = inner.getValue();
                    residual.get(from).put(to, new EdgeWeights(ew.capacity, ew.cost));
                    // Add reversed edge if no antiparallel edge exists
                    if (!outEdges.get(to).containsKey(from)){
                        // Depends cost can also be -ew.cost, depends on usage
                        residual.get(to).put(from, new EdgeWeights(0, 0.0));
                    }
                }
        }
        return residual;
    }

    public Map<String, Double> generatePotentials() {
        Map<String, Double> potentials = new HashMap<>();

        for (String node : nodes.keySet()) {
            potentials.put(node, 0.0);
        }
        return potentials;
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
