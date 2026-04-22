package core;

import java.util.*;

public class Network {
    // Keep track of hosts
    private HashMap<String, Host> hosts = new HashMap<>();
    Map<Host, Map<Host, Integer>> outEdges = new HashMap<>(); // Adjacency map: outgoing edges per host

    public Host createHost(String str) {
            hosts.put(str, new Host(str));
        return hosts.get(str);
    }

    public void insertEdge(String fromH, String toH, int costE){
        Host fHost = hosts.get(fromH);
        Host tHost = hosts.get(toH);

        // Add host as source if it doesn't exist already
        if(!outEdges.containsKey(fHost))
            outEdges.put(fHost, new HashMap<Host, Integer>());

        outEdges.get(fHost).put(tHost,costE);
    }

    public Host getOrCreate(String str) {
        // Create new host if it does not exist already
        if (!hosts.containsKey(str)) {
            hosts.put(str, new Host(str));
        }
        return hosts.get(str);
    }


    public void printGraph(){
        for (Map.Entry<Host, Map<Host, Integer>> edge: outEdges.entrySet()){
            for (Map.Entry<Host, Integer> out: edge.getValue().entrySet()){
                System.out.println(edge.getKey() + " -> " + out.getKey() + ": " + out.getValue());
            }

        }
    }

//    // Depth first graph traversal
//    public void visitDepthFirst(Host v, Set<Host> visited) {
//        // End if the host is already visited
//        if (visited.contains(v)) {
//            return;
//        }
//        visited.add(v);
//        // Repeat the method for each host that is connected to the current host
//        for (Edge e : getEdges(v))
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

    public Map<Host, Integer> getEdges(Host h){
        return outEdges.get(h);
    };

    public int getCost(Host fromH, Host toH){
        return outEdges.get(fromH).get(toH);
    }


}
