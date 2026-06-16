package core.models;

public class FlowCostProb {
    public int flow;
    public double cost;
    public double probability;

    public FlowCostProb(int flow, double cost, double probability){
        this.flow = flow;
        this.cost= cost;
        this.probability = probability;
    }
}
