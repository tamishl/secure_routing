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



    public List<String> minCostPath(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> residual, Map<String, Double> potentials){
        List<String> path = new ArrayList<>();

        Map<String, String> parents = dijkstra.computeDijkstraJohnson(network, source, sink, residual, potentials);

        // If the target has not been reached
        if (!parents.containsKey(sink)){
            return path;
        }

        for (String nodeId = sink; nodeId != null; nodeId = parents.get(nodeId)){
            path.add(nodeId);
        }
        return path;
    }

    public List<String> minCostPathFlow(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> residual){
        List<String> path = new ArrayList<>();

        Map<String, String> parents = dijkstra.computeDijkstraFlow(network, source, sink, residual);

        // If the target has not been reached
        if (!parents.containsKey(sink)){
            return path;
        }

        for (String nodeId = sink; nodeId != null; nodeId = parents.get(nodeId)){
            path.add(nodeId);
        }
        return path;
    }


    public List<String> path(Network network, String source, String sink, Map<String, Map<String, Integer>> residual){
        List<String> path = new ArrayList<>();

        Map<String, String> parents = bfs.compute(network, source, sink, residual);

        // If the target has not been reached
        if (!parents.containsKey(sink)){
            return path;
        }

        for (String nodeId = sink; nodeId != null; nodeId = parents.get(nodeId)){
            path.add(nodeId);
        }

        return path;
    }
}
