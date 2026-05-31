package core.algorithms;

import core.Network;

import java.util.*;

public class BFS {
    // Return first found path if sink can be reached from given node, checking for residual flow (i.e. min hops)
    public Map<String, String> compute(Network network, String source, String sink, Map<String, Map<String, Integer>> residual){
        Map<String, String> parents = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);
        visited.add(source);

        String from;
        while (!queue.isEmpty()){
            from = queue.poll();
            for (String to : network.getEdges(from).keySet()){
                // Skip if no capacity on edge
                if (residual.get(from).get(to) == 0){
                    continue;
                }

                if (to.equals(sink)){
                    parents.put(to, from);
                    return parents;
                }
                if (!visited.contains(to)){
                    queue.add(to);
                    parents.put(to, from);
                    visited.add(to);
                }
            }
        }
        return new HashMap<>();
    }


    // BFS: Check if sink can be reached from given node without checks for flow
    public boolean isPath(Network network, String source, String sink){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);

        while (!queue.isEmpty()){
            String current = queue.poll();
            visited.add(current);
            for (String toId: network.getEdges(current).keySet()){
                if (toId.equals(sink)){
                    return true;
                }
                if (!visited.contains(toId)){
                    queue.add(toId);
                }
            }
        }

        return false;
    }
}
