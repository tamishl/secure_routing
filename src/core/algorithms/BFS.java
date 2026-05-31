package core.algorithms;

import core.Network;

import java.util.*;

public class BFS {
    // Return first found path if sink can be reached from given node, checking for residual flow (i.e. min hops)
    public Map<String, String> compute(Network network, String source, String sink, Map<String, Map<String, Integer>> rNetwork){
        Map<String, String> parent = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);
        visited.add(source);

        String from;
        while (!queue.isEmpty()){
            from = queue.poll();
            for (String to : network.getEdges(from).keySet()){
                // Skip if no capacity on edge
                if (rNetwork.get(from).get(to) == 0){
                    continue;
                }

                if (to.equals(sink)){
                    parent.put(to, from);
                    return parent;
                }
                if (!visited.contains(to)){
                    queue.add(to);
                    parent.put(to, from);
                    visited.add(to);
                }
            }
        }

        return new HashMap<>();
    }
}
