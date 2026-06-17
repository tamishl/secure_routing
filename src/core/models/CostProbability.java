package core.models;

import core.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CostProbability {
    public double cost;
    public double probability;
    public List<String> path = new ArrayList<>();


    public CostProbability(double totalCost, double totalProbability, List<String> path){
        this.cost = totalCost;
        this.probability = totalProbability;
        this.path = path;
    }


    public boolean dominated(CostProbability other){
        boolean betterOrEqual = this.cost <= other.cost && this.probability >= other.probability;
        boolean dominating = this.cost < other.cost || this.probability > other.probability;

        return betterOrEqual && dominating;

    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;        //Direct comparison of hashCode
        if (object == null || getClass() != object.getClass()) return false;

        CostProbability cp = (CostProbability) object;
        return Objects.equals(path, cp.path);       //Comparison of values
    }

    @Override
    public int hashCode() {
        //Create hashCode based on all values (in this case only the name of the vertex)
        return Objects.hashCode(path);
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

