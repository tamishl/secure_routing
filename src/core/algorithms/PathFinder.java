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

    // Regular minCostPath, without considering flow
    public List<String> minCostPath(Network network, String source, String sink, Algorithm algorithm){
        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord.compute(network, source);
                    case Algorithm.DIJKSTRA -> dijkstra.computeDijkstra(network, source);
                    }
                ;
        return getPath(parents, sink);
    }



    public List<String> minCostFlowPath(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> residual, Map<String, Double> potentials){
        return getPath(dijkstra.computeDijkstraJohnson(network, source, sink, residual, potentials), sink);
    }

    public List<String> minCostFlowPath(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> residual){
        return getPath(dijkstra.computeDijkstraFlow(network, source, sink, residual), sink);
    }

    public List<String> minRiskFlowPath(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> residual){
        return getPath(dijkstra.computeDijkstraProbability(network, source, sink, residual), sink);
    }


    public List<String> path(Network network, String source, String sink, Map<String, Map<String, Integer>> residual){
        return getPath(bfs.compute(network, source, sink, residual), sink);
    }

    private List<String> getPath(Map<String, String> parents, String sink){
        List<String> path = new ArrayList<>();

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
