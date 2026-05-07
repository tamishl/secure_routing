package core;

import java.util.*;

public class NetworkGenerator {
    int innerNodeCnt;
    int sourceCnt;
    int sinkCnt;

    double minRisk;
    double maxRisk;

    // Assuming 0 as minimum
    int maxCapacity;
    double maxCost;



    public NetworkGenerator(int innerNodeCnt, int sourceCnt, int sinkCnt, double minRisk, double maxRisk, int maxCapacity, double  maxCost){
        this.innerNodeCnt = Math.max(3, innerNodeCnt); // at least 4 nodes
        this.sourceCnt = sourceCnt;
        this.sinkCnt = sinkCnt;
        this.minRisk = minRisk;
        this.maxRisk = maxRisk;
        this.maxCapacity = maxCapacity;
        this.maxCost = maxCost;
    }

    public Network generate(int seed){
        Network network = new Network();
        Set<String> edges= new HashSet<>();
        Random random = new Random(seed);

        // Create nodes
        // Source nodes S
        List<String> sources =  createNodes("S", sourceCnt);

        // Internal nodes N
        List<String> nodes =  createNodes("N", innerNodeCnt);

        // Sink nodes D (destination)
        List<String> sinks =  createNodes("D", sinkCnt);

        // Add nodes to network
        addNodes(sources, network);
        addNodes(nodes, network);
        addNodes(sinks, network);


        // Add edges
        // 2 to 4 outgoing connections for sources
        addEdges(network,sources, nodes, 1, Math.min(4, innerNodeCnt-1), random);

        // 2 to (innerNode*0.5) outgoing connections for internal nodes
        addEdges(network, nodes, nodes, 2, (int)Math.ceil((double)((innerNodeCnt*0.5))), random);

        // 1 to 4 incoming connections for sinks
        addEdges(network, nodes, sinks, 1, Math.min(4, innerNodeCnt-1), random, true);

        return network;

        }


    private ArrayList<String> createNodes(String prefix, int count){
        ArrayList<String> nodes = new ArrayList<>();
        for(int i = 1; i <= count; i++){
            nodes.add(prefix+i);
        }

        return nodes;
    }

    private void addNodes(Collection<String> nodes, Network network){
        for (String s: nodes){
            network.createNode(s);
        }
    }

    private void addEdges(Network network, List<String> groupA, List<String> groupB, int minEdge, int maxEdge, Random random){
        addEdges(network, groupA, groupB, minEdge, maxEdge, random, false);
    }


    // For each element in groupA connect with one element in groupB (direction is either from A to B or reversed)
    private void addEdges(Network network, List<String> groupA, List<String> groupB, int minEdge, int maxEdge, Random random, Boolean reversed) {
        // Ensure no duplicate edges
        Set<String> edges;

        String from;
        String to;
        String edge;
        int outEdgeCnt;

        for (String n : groupA) {
            edges = new HashSet<>();

            // outEdgeCnt to limit the amount of outgoing edges per node
            outEdgeCnt = minEdge + ((maxEdge > minEdge) ? random.nextInt(maxEdge-minEdge) : 0);

            while (edges.size() < outEdgeCnt) {
                // Swap direction
                if (reversed){
                    from = groupB.get(random.nextInt(groupB.size()));
                    to = n;
                }
                else {
                    from = n;
                    to = groupB.get(random.nextInt(groupB.size()));
                }

                // No self-loops
                if (from.equals(to)){
                    continue;
                }

                edge = from + "->" + to;

                if (!edges.contains(edge)) {
                    network.insertEdge(from, to, 100, 0.5);
                    edges.add(edge);
                }
            }
        }
    }





        // Requirements:
        // Directed acyclic graph (can not send to/through source node)
        // |E| > |N|
        // Each edge must have at least one connection to another edge
        // No edge should have a connection to itself
        // There must be at lost one path from each source node to each destination node
        // There must be at least |N| * 0.5 edges between the source and destination node.
}
