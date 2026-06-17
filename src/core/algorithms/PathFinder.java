package core.algorithms;
import core.EdgeAttributes;
import core.Network;
import core.models.CostProbLabel;

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

    // Not considering flow
    public List<String> minCostPath(Network network, String source, String sink, Algorithm algorithm){
        Map<String, String> parents =
                switch(algorithm) {
                    case Algorithm.BELLMAN_FORD -> bellmanFord.compute(network, source);
                    case Algorithm.DIJKSTRA -> dijkstra.computeDijkstra(network, source);
                    }
                ;
        return getPath(parents, sink);
    }


    // Considering flow
    // Considering reversed edges
    public List<String> minCostFlowPath(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> residual, Map<String, Double> potentials){
        return getPath(dijkstra.computeDijkstraJohnson(network, source, sink, residual, potentials), sink);
    }

    public List<String> minRiskFlowPath(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> residual){
        return getPath(dijkstra.computeDijkstraProbability(network, source, sink, residual), sink);
    }

    public List<String> flowPath(Network network, String source, String sink, Map<String, Map<String, Integer>> residual){
        return getPath(bfs.compute(network, source, sink, residual), sink);
    }

    // Not considering reversed edges
    public List<String> minCostPath(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> residual){
        return getPath(dijkstra.computeDijkstraFlow(network, source, sink, residual), sink);
    }

    // Pareto optimalty
    public HashSet<List<String>> paretoPath(Network network, String source, String sink, Map<String, Map<String, EdgeAttributes>> residual, Map<String, Double> potentials){
//        dijkstra.updatePotentials(network, source, sink, residual, potentials);

        HashSet<List<String>> pathsToSink = new HashSet<>();
        Map<String, HashSet<CostProbLabel>> paretoPaths =  bellmanFord.computePareto(network, source, sink, residual, potentials);
        for (CostProbLabel cp: paretoPaths.get(sink)){
            pathsToSink.add(cp.path);
        }

        return pathsToSink;
    }


    // General pathfinder
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
