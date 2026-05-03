package core;

import java.util.*;

public class Network {
    private TrustSystem trustSystem;
    private HashMap<String, Host> hosts = new HashMap<>();
    
    Map<String, Set<Edge>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per host

    public Network(TrustSystem trustSystem) {
        this.trustSystem = trustSystem;
    }

    public void createHost(String id) {
        hosts.put(id, new Host(id));
        outEdges.put(id, new HashSet<>());
        trustSystem.addHost(id);
    }

    public void insertEdge(String fromId, String toId, int capacity, double time){
        outEdges.get(fromId).add(new Edge (fromId, toId, capacity, time));
    }


    public List<Edge> minCostPath(String fromId, String toId, Set<String> visited) {
            // End if the host is already visited
            if (visited.contains(hostId)) {
                return;
            }
            visited.add(hostId);
            // Repeat the method for each host that is connected to the current host
            for (Edge e: getEdges(hostId)) {
                minCostPath(e.toId, visited);
        }

    }



    // BFS: Check if target can be reached from given node
    public boolean isPath(String sourceId, String targetId, Set<String> excluded){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>(excluded);
        queue.add(sourceId);

        while (!queue.isEmpty()){
            String current = queue.poll();
            visited.add(current);
            for (Edge e: getEdges(current)){
                if (e.toId.equals(targetId)){
                    return true;
                }
                if (!visited.contains(e.toId)){
                    queue.add(e.toId);
                }
            }
        }

        return false;
    }

    // BFS: Check if target can be reached from given node
    // Overload to allow search without excluded hosts
    public boolean isPath(String sourceId, String targetId){
        return isPath(sourceId, targetId, new HashSet<>(){});
    }

    // DFS traversal to check connectivity (recursive)
    public void visitDepthFirst(String hostId, Set<String> visited) {
        // End if the host is already visited
        if (visited.contains(hostId)) {
            return;
        }
        visited.add(hostId);
        // Repeat the method for each host that is connected to the current host
        for (Edge e: getEdges(hostId))
            visitDepthFirst(e.toId, visited);
    }

    public void printGraph(){
        for (String hostId : hosts.keySet()) {
            System.out.println(hostId + ":");
            for (Edge edge: outEdges.get(hostId)){
                System.out.println(edge.toString());
//                System.out.println("-> " + edge.toId + ": C=" + edge.capacity + " T=" + edge.time);
            }
        }
    }

    // Getters
    public Host getHost(String str){
        hosts.get(str);
        return hosts.get(str);
    }

    public Collection<Host> getHosts() {
        return hosts.values();
    }

    public Set<Edge> getEdges(String hostId){
        return outEdges.get(hostId);
    }

}
