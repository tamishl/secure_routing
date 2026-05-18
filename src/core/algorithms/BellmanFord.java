package core.algorithms;

import core.EdgeWeights;

import java.util.HashMap;
import java.util.Map;
import core.Network;

public class BellmanFord {
    public Map<String, String> bellmanFord(Network network, String source){
        Map<String, Double> costTo = network.generateCostMap(source);
        Map<String, String> parents = new HashMap<>();

        // Can add sets to avoid calculation for inaccessible nodes, but also leads to additional checks in each iteration
//        Set<String> visitable  = new HashSet<>();
//        visitable.add(source);

        String from;
        String to;
        double cost;

        boolean changed = true;

        // Iterate N times and check for negative cycles during last loop (instead of creating 2 for-loops)
        for (int i = 0; i < network.nodes.size(); i++){
            // Prevent futile iterations: stop when no improvement happened in previous cycle.
            if (!changed){
                return parents;
            }
            changed = false;

            for(Map.Entry<String, Map<String, EdgeWeights>> outerEntry: network.outEdges.entrySet()){
                from = outerEntry.getKey();
                for(Map.Entry<String, EdgeWeights> innerEntry: outerEntry.getValue().entrySet()){
                    to = innerEntry.getKey();
                    cost = costTo.get(from) + innerEntry.getValue().cost;
                    if (cost < costTo.get(to)){

                        // If still improvement after all edges have been seen, network has a negative cycle.
                        // Return empty map
                        if (i == network.nodes.size()-1){
                            return new HashMap<>();
                            // To find nodes affected by the negative cycle:
//                            costTo.put(to, Double.NEGATIVE_INFINITY);

                        }
                        changed = true;
                        costTo.put(to, cost);
                        parents.put(to, from);
                    }
                }
            }
        }

        return parents;
    }

}
