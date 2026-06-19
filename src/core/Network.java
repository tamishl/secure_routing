package core;

import core.models.CostProbLabel;

import java.util.*;

public class Network {
    public HashMap<String, Node> nodes = new HashMap<>();
    
    public Map<String, Map<String, EdgeAttributes>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per node
    public double maxCost = 0.0;

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
        outEdges.get(fromId).put(toId, new EdgeAttributes(capacity, cost));
        if (cost > maxCost){
            maxCost = cost;
        }
    }

    public void printGraph(){
        for (String nodeId : nodes.keySet()) {
            System.out.println(nodeId);
            for (Map.Entry<String, EdgeAttributes> entry: outEdges.get(nodeId).entrySet()){
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
            costMap.put(node, Double.POSITIVE_INFINITY);
        }

        return costMap;
    }

    public  Map<String, Double> generateProbabilityMap(String source){
        Map<String, Double> probabilityMap = new HashMap<>(); // Probability of successfully reaching destination from source node to given node

        for (String node : nodes.keySet()) {
            if (node.equals(source)){
                probabilityMap.put(node, Math.log(1 - nodes.get(source).risk));
                continue;
            }
            // Path probability will be calculated with logarithms so negative infinity
            probabilityMap.put(node, Double.NEGATIVE_INFINITY);
        }

        return probabilityMap;
    }

    public  Map<String, HashSet<CostProbLabel>> generateCostProbMap(String source) {
        Map<String, HashSet<CostProbLabel>> costProbMap = new HashMap<>(); // Probability of successfully reaching destination from source node to given node

        for (String node : nodes.keySet()) {
            costProbMap.put(node, new HashSet<>());

            if (node.equals(source)) {
                List<String> path = new ArrayList<>();
                path.add("S");
                costProbMap.get(source).add(new CostProbLabel(0.0, 1 - nodes.get(source).risk, path));
            }
        }
        return costProbMap;
    }

    public Map<String, Map<String, Integer>> generateResidualFlowMap(){
        Map<String, Map<String, Integer>> residual = new HashMap<>();

        for (String from: nodes.keySet()) {
            residual.put(from, new HashMap<>());
        }

        String from;
        String to;
        int capacity;

        for (Map.Entry<String, Map<String, EdgeAttributes>> outer : outEdges.entrySet()){
            from = outer.getKey();
            for (Map.Entry<String, EdgeAttributes> inner: outer.getValue().entrySet()){
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

    public Map<String, Map<String, EdgeAttributes>> generateResidualMap(){
        Map<String, Map<String, EdgeAttributes>> residual = new HashMap<>();

        for (String from: nodes.keySet()) {
            residual.put(from, new HashMap<>());
        }

        String from;
        String to;
        EdgeAttributes ew;
        for (Map.Entry<String, Map<String, EdgeAttributes>> outer : outEdges.entrySet()){
                from = outer.getKey();
                for (Map.Entry<String, EdgeAttributes> inner: outer.getValue().entrySet()){
                    to = inner.getKey();
                    ew = inner.getValue();
                    residual.get(from).put(to, new EdgeAttributes(ew.capacity, ew.cost));
                    // Add reversed edge if no antiparallel edge exists
                    if (!outEdges.get(to).containsKey(from)){
                        // Depends cost can also be -ew.cost, depends on usage
                        residual.get(to).put(from, new EdgeAttributes(0, 0.0));
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


    public Map<String, Map<String, EdgeAttributes>> generateCopyMap(){
        Map<String, Map<String, EdgeAttributes>> rNetwork = new HashMap<>();

        String from;
        String to;
        EdgeAttributes ew;

        for(Map.Entry<String, Map<String, EdgeAttributes>> outerEntry: outEdges.entrySet()){
            from = outerEntry.getKey();
            rNetwork.put(from, new HashMap<>());
            for(Map.Entry<String, EdgeAttributes> innerEntry: outerEntry.getValue().entrySet()) {
                to = innerEntry.getKey();
                ew = innerEntry.getValue();
                rNetwork.get(from).put(to, new EdgeAttributes(ew.capacity, ew.cost));
            }
        }

        return rNetwork;
    }


    // Getters
    public Node getNode(String str){
        return nodes.get(str);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public Map<String, EdgeAttributes> getEdges(String nodeId){
        return outEdges.get(nodeId);
    }


}
