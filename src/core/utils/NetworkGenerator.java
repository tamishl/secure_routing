//package core;
//
//import java.util.*;
//
//// Requirements:
//// Directed graph (can not send to/through source node)
//// |E| > |N|
//// Each node must have at least one connection to another node
//// No node should have a connection to itself
//// There must be at least one path from a source node to the destination node
//// There must be at least |N| * 0.5 edges between the source and destination node
//
//public class NetworkGeneratorX {
//    int innerNodeCnt;
//    int sourceCnt;
//    int sinkCnt;
//
//    double minRisk;
//    double maxRisk;
//
//    // Assuming 0 as minimum
//    double maxCost;
//    int maxCapacity;
//
//    Set<String> sourceChild = new HashSet<>();
//    Set<String> sinkParent = new HashSet<>();
//
//    public NetworkGeneratorX(int innerNodeCnt, int sourceCnt, int sinkCnt, double minRisk, double maxRisk, int maxCapacity, double  maxCost){
//        this.innerNodeCnt = Math.max(3, innerNodeCnt); // at least 4 nodes
//        this.sourceCnt = sourceCnt;
//        this.sinkCnt = sinkCnt;
//        this.minRisk = minRisk;
//        this.maxRisk = maxRisk;
//        this.maxCapacity = maxCapacity;
//        this.maxCost = maxCost;
//    }
//
//    public Network generate(int seed){
//        Network network = new Network();
//        Set<String> edges= new HashSet<>();
//        Random random = new Random(seed);
//
//        // Create nodes
//        // Source nodes S
//        List<String> sources =  createNodes(NodeType.SOURCE.getPrefix(), sourceCnt);
//
//        // Sink nodes D (destination)
//        List<String> sinks =  createNodes(NodeType.DESTINATION.getPrefix(), sinkCnt);
//
//        // Internal nodes N
//        List<String> nodes =  createNodes(NodeType.INNER.getPrefix(), innerNodeCnt);
//
//
//        // Add nodes to network
//        addNodes(sources, network);
//        addNodes(nodes, network);
//        addNodes(sinks, network);
//
//
//        // Add edges
//        for (String source: sources) {
//            for (String sink: sinks) {
//                createBasePath(source, sink, nodes, random);
//            }
//        }
//
//        // 2 to 4 outgoing connections for sources
//        addEdges(network, sources, nodes, 1, Math.min(4, innerNodeCnt-1), random);
//
//        // 1 to 4 incoming connections for sinks
//        addEdges(network, sinks, nodes, 1, Math.min(4, innerNodeCnt-1), random, true);
//
//        // 2 to (innerNode*0.5) outgoing connections for internal nodes
//        addEdges(network, nodes, nodes, 2, (int)Math.ceil(innerNodeCnt*0.5), random);
//
//
//        return network;
//
//        }
//
//    private void createBasePath(String source, String sink, List<String> nodes, Random random){
//        // Amount of hops is 50-75% of |N|
//        int hops = (int)Math.ceil(nodes.size()*0.5) + random.nextInt(nodes.size()/4);
//        Set<String> visited = new HashSet<>();
//
//        visited.add(source);
//    }
//
//
//    private ArrayList<String> createNodes(String prefix, int count){
//        ArrayList<String> nodes = new ArrayList<>();
//        for(int i = 1; i <= count; i++){
//            nodes.add(prefix+i);
//        }
//
//        return nodes;
//    }
//
//    private void addNodes(Collection<String> nodes, Network network){
//        for (String s: nodes){
//            network.addNodeIfAbsent(s, 0.5);
//        }
//    }
//
//    private void addEdges(Network network, List<String> groupA, List<String> groupB, int minEdge, int maxEdge, Random random){
//        addEdges(network, groupA, groupB, minEdge, maxEdge, random, false);
//    }
//
//
//    // For each element in groupA connect with one element in groupB (direction is either from A to B or reversed)
//    private void addEdges(Network network, List<String> groupA, List<String> groupB, int minEdge, int maxEdge, Random random, Boolean reversed) {
//        String from;
//        String to;
//        int outEdgeCnt;
//        int count;
//
//        for (String n : groupA) {
//            // outEdgeCnt to limit the amount of outgoing edges per node
//            outEdgeCnt = minEdge + ((maxEdge > minEdge) ? random.nextInt(maxEdge-minEdge) : 0);
//            count = 0;
//            while (count < outEdgeCnt) {
//                // Swap direction
//                if (reversed){
//                    from = groupB.get(random.nextInt(groupB.size()));
//                    to = n;
//                }
//                else {
//                    from = n;
//                    to = groupB.get(random.nextInt(groupB.size()));
//                }
//
//                // No self-loops in case nodes appear in both groups
//                if (from.equals(to)){
//                    continue;
//                }
//
//                // Ensure no duplicates (no parallel edges in the same direction)
//                if (!network.outEdges.get(from).containsKey(to)) {
//                    network.insertEdge(from, to, 100, 0.5);
//                    count += 1;
//                    if (from.startsWith(NodeType.SOURCE.getPrefix())){
//                        sourceChild.add(to);
//                    }
//                    else if (to.startsWith(NodeType.DESTINATION.getPrefix())){
//                        sinkParent.add(to);
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//}
