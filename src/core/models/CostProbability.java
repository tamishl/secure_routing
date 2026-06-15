package core.models;

public class CostProbability implements Comparable<CostProbability> {
    public String nodeId;
    public double cost;
    public double probability;


    public CostProbability(String nodeId, double totalCost, double totalProbability){
        this.nodeId = nodeId;
        this.cost = totalCost;
        this.probability = totalProbability;
    }

    // has to be updated to include prob
    // Comparison method for priority queue
    public int compareTo(CostProbability other)
    {
        return Double.compare(this.cost, other.cost);
    }

    public boolean dominated(CostProbability other){
        boolean betterOrEqual = this.cost <= other.cost && this.probability >= other.probability;
        boolean dominating = this.cost < other.cost || this.probability > other.probability;

        return betterOrEqual && dominating;

    }
}

// this < other = -1
// this > other = 1
// this == other = 0
//
//        int cost = Double.compare(this.cost, other.cost); // -1 is better, 1 is worse
//        int probability = Double.compare(this.probability, other.probability); // 1 is better, -1 is worse
//        // Same values
//        if (cost == 0 && probability == 0){
//            return 0;
//        }
//        // Cost and/or probability is better, none of them is worse
//        if (cost <= 0 && probability >= 0){
//            return -1;
//        }
//        // Cost and probability are both worse
//        return 1;

