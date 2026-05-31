package core.algorithms;
import core.EdgeWeights;
import core.Network;

import java.util.*;

public class PathFinder {
    BellmanFord bellmanFord;
    Dijkstra dijkstra;
    BFS bfs;


    public PathFinder(BellmanFord bellmanFord, Dijkstra dijkstra, BFS bfs){
        this.bellmanFord = bellmanFord;
        this.dijkstra = dijkstra;
        this.bfs = bfs;
    }

    public List<String> minCostPath(Network network, String source, String sink, Algorithm algorithm){
        List<String> path = new ArrayList<>();

        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord.compute(network, source);
                    case Algorithm.DIJKSTRA -> dijkstra.computeDijkstra(network, source);
                    case Algorithm.BFS -> bfs.compute(network, source, sink, network.generateResidualFlowMap());
                    case Algorithm.DIJKSTRA_JOHNSON -> dijkstra.computeDijkstraJohnson(network, source, network.generateResidualMap(), new HashMap<>());
                }
                ;

        // If the target has not been reached
        if (!parents.containsKey(sink)){
            return path;
        }


        for (String nodeId = sink; nodeId != null; nodeId = parents.get(nodeId)){
            path.add(nodeId);

        }

        return path;
    }



    public List<String> minCostPath(Network network, String source, String sink, Algorithm algorithm, Map<String, Map<String, EdgeWeights>> residual, Map<String, Double> potentials){
        List<String> path = new ArrayList<>();

        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord.compute(network, source);
                    case Algorithm.DIJKSTRA -> dijkstra.computeDijkstra(network, source);
                    case Algorithm.BFS -> bfs.compute(network, source, sink, network.generateResidualFlowMap());
                    case Algorithm.DIJKSTRA_JOHNSON -> dijkstra.computeDijkstraJohnson(network, source, residual, potentials);
                }
                ;

        // If the target has not been reached
        if (!parents.containsKey(sink)){
            return path;
        }


        for (String nodeId = sink; nodeId != null; nodeId = parents.get(nodeId)){
            path.add(nodeId);

        }

        return path;
    }


    public List<String> minCostPath(Network network, String source, String sink, Algorithm algorithm, Map<String, Map<String, Integer>> residual){
        List<String> path = new ArrayList<>();

        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord.compute(network, source);
                    case Algorithm.DIJKSTRA -> dijkstra.computeDijkstra(network, source);
                    case Algorithm.BFS -> bfs.compute(network, source, sink, residual);
                    case Algorithm.DIJKSTRA_JOHNSON -> dijkstra.computeDijkstraJohnson(network, source, network.generateResidualMap(), new HashMap<>());
                }
                ;

        // If the target has not been reached
        if (!parents.containsKey(sink)){
            return path;
        }


        for (String nodeId = sink; nodeId != null; nodeId = parents.get(nodeId)){
            path.add(nodeId);

        }

        return path;
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
