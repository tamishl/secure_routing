package core.algorithms;
import core.EdgeWeights;
import core.Network;

import java.util.*;

public class PathFinder {
    BellmanFord bellmanFord;
    Dijkstra dijkstra;


    public PathFinder(BellmanFord bellmanFord, Dijkstra dijkstra){
        this.bellmanFord = bellmanFord;
        this.dijkstra = dijkstra;
    }

    public List<String> minCostPath(Network network, String source, String destination, Algorithm algorithm, Map<String, Map<String, EdgeWeights>> residual){
        if (!isPath(network, source, destination)) {
            return new ArrayList<>();
        }

        List<String> path = new ArrayList<>();

        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord.bellmanFord(network, source);
                    case Algorithm.DIJKSTRA -> dijkstra.dijkstra(network, source);
                    case Algorithm.DIJKSTRA_JOHNSON -> dijkstra.dijkstraJohnson(network, source);
                }
                ;

        if (parents.isEmpty()){
            return path;
        }

        // Get path from given list by tracing backwards from target
        for (String node = destination; node != null; node = parents.get(node)){
            path.add(node);
        }

        return path;
    }

    public List<String> minCostPath(Network network, String source, String destination, Algorithm algorithm){
        return minCostPath(network, source, destination, algorithm, network.generateResidualMap());
    }

    // BFS: Check if destination can be reached from given node without checks for flow
    public boolean isPath(Network network, String source, String destination){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(source);

        while (!queue.isEmpty()){
            String current = queue.poll();
            visited.add(current);
            for (String toId: network.getEdges(current).keySet()){
                if (toId.equals(destination)){
                    return true;
                }
                if (!visited.contains(toId)){
                    queue.add(toId);
                }
            }
        }

        return false;
    }

    // DFS traversal to check connectivity (recursive)
    public void visitDepthFirst(Network network, String nodeId, Set<String> visited) {
        // End if the node is already visited
        if (visited.contains(nodeId)) {
            return;
        }
        visited.add(nodeId);
        // Repeat the method for each node that is connected to the current node
        for (String toId: network.getEdges(nodeId).keySet())
            visitDepthFirst(network, toId, visited);
    }
}
