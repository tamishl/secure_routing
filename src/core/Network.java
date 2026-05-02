package core;

import java.util.*;

public class Network {
    private TrustSystem trustSystem;
    private HashMap<String, Host> hosts = new HashMap<>();
    
    Map<String, List<Edge>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per host

    public Network(TrustSystem trustSystem) {
        this.trustSystem = trustSystem;
    }

    public void createHost(String id) {
        hosts.put(id, new Host(id));
        outEdges.put(id, new ArrayList<>());
        trustSystem.addHost(id);
    }

    public void insertEdge(String fromId, String toId, int capacity, double time){
        outEdges.get(fromId).add(new Edge (toId, capacity, time));
    }

    public Host getOrCreate(String str) {
        // Create new host if it does not exist already
        if (!hosts.containsKey(str)) {
            hosts.put(str, new Host(str));
        }
        return hosts.get(str);
    }

    public void printGraph(){
        for (String hostId : hosts.keySet()) {
            System.out.println(hostId);
            for (Edge edge: outEdges.get(hostId)){
                System.out.println("-> " + edge.toId + ": C=" + edge.capacity + " T=" + edge.time);
            }
        }
    }

//    // Depth first graph traversal (recursive)
//    public void visitDepthFirst(Host h, Set<Host> visited) {
//        // End if the host is already visited
//        if (visited.contains(h)) {
//            return;
//        }
//        visited.add(h);
//        // Repeat the method for each host that is connected to the current host
//        for (Edge e : getEdges(h))
//            visitDepthFirst(e.to, visited);
//    }



    // Getters
    public Host getHost(String str){
        hosts.get(str);
        return hosts.get(str);
    }

    public Collection<Host> getHosts() {
        return hosts.values();
    }

    public List<Edge> getEdges(String hostId){
        return outEdges.get(hostId);
    }

//    public int getCapacity(String fromId, String toId){
//        return outEdges.get(fromId).get(toId);
//    }


}
