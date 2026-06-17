package core.algorithms;

import core.EdgeWeights;

import java.util.*;

import core.Network;
import core.models.CostProbability;

public class BellmanFord {

    public Map<String, HashSet<CostProbability>> computePareto(Network network, String source, String sink, Map<String, Map<String, EdgeWeights>> edges, Map<String, Double> potentials){
        // Cost and probability from source node to given node
        Map<String, HashSet<CostProbability>> paretoPaths = network.generateCostProbMap(source);

        String from;
        String to;
        EdgeWeights ew;
        double cost = Double.POSITIVE_INFINITY;
        double probability;

        boolean changed = true;

        // No neg cycle checks necessary due to reweighting
        for (int i = 0; i < network.nodes.size(); i++){
            // Prevent futile iterations: stop when no improvement happened in previous cycle.
            if (!changed){
                break;
            }
            changed = false;

            for(Map.Entry<String, Map<String, EdgeWeights>> outerEntry: edges.entrySet()){
                from = outerEntry.getKey();
                if (from.equals(sink)){
                    continue;
                }
                for(Map.Entry<String, EdgeWeights> innerEntry: outerEntry.getValue().entrySet()){
                    to = innerEntry.getKey();
                    ew = innerEntry.getValue();

                    // Only loop if flow is possible
                    if (ew.flow < ew.capacity || edges.get(to).get(from).flow != 0){
                        changed = true;
                        for (CostProbability cp: paretoPaths.get(from)){
                            // If flow is left or can be undone, calculate probability (Johnson reweighting)
                            // Check if previous flow to current node can be undone
                            probability = cp.probability;

                            if (edges.get(to).get(from).flow != 0){
                                cost = cp.cost - edges.get(to).get(from).cost + potentials.get(from) - potentials.get(to);
                                probability -= Math.log(1-network.getNode(from).risk);
                            }

                            // Check if can flow over original edge
                            else if (ew.flow < ew.capacity){
                                cost = cp.cost + ew.cost + potentials.get(from) - potentials.get(to);
                                probability += Math.log(1-network.getNode(to).risk);
                            }

                            updateParetoPaths(paretoPaths.get(to), cp.path, to, cost, probability);
                        }
                    }
                }
            }
        }

       return paretoPaths;
    }

    private void updateParetoPaths(HashSet<CostProbability> nodePaths, List<String> predecessors, String nodeId, double cost, double probability){
        boolean isPareto = true;

        List<String> path = new ArrayList<>(predecessors);
        path.add(nodeId);

        if (!nodePaths.isEmpty()) {
            isPareto = false;
            HashSet<CostProbability> dominated = new HashSet<>();

            for (CostProbability cp : nodePaths) {
                // Path is dominated by already existing path: at least one objective is worse and none is better
                if ((cost > cp.cost || probability < cp.probability) && (cost >= cp.cost && probability <= cp.probability)) {
                        isPareto = false;
                        break;
                }

                // Same values as other path
                // Can end loop because if a path with the same values is added, it is not dominated
                else if (cost == cp.cost && probability == cp.probability) {
                    // Don't add if it's the same path
                    isPareto = !path.equals(cp.path);
                    break;
                }

                // At least one objective is better
                else if (cost < cp.cost || probability > cp.probability) {
                    isPareto = true;

                    // No objective is worse
                    if (cost <= cp.cost && probability >= cp.probability) {
                        dominated.add(cp);
                    }
                }
            }

            // Remove dominated paths
            for (CostProbability cp: dominated){
                nodePaths.remove(cp);
            }
        }

        if (isPareto){
            nodePaths.add(new CostProbability(cost, probability, path));
        }
    }

    public Map<String, String> compute(Network network, String source){
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
                break;
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

//
//probability = probabilityFromSource.get(from);
//
//// If flow is left or can be undone, calculate probability (Johnson reweighting)
//// Check if previous flow to current node can be undone
//                    if (edges.get(to).get(from).flow != 0){
//cost = costFromSource.get(from) - edges.get(to).get(from).cost + potentials.get(from) - potentials.get(to);
//probability = probabilityFromSource.get(from) - Math.log(1-network.getNode(from).risk);
//canFlow = true;
//        }
//
//        // Check if can flow over original edge
//        else if (ew.flow < ew.capacity){
//cost = costFromSource.get(from) + ew.cost + potentials.get(from) - potentials.get(to);
//probability = probability + Math.log(1-network.getNode(to).risk);
//canFlow = true;
//        }
//
//        if (canFlow) {
//        if (cost < costFromSource.get(to)) {
//changed = true;
//        costFromSource.put(to, cost);
//                            parents.put(to, from);
//                        }
//                                }