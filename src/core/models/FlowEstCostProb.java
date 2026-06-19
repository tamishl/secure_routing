package core.models;

public class FlowEstCostProb {
    public int flow;
    public double estimateOfReceival;
    public double cost;
    public double probability;

    public FlowEstCostProb(int flow, double estimateOfReceival, double cost, double probability){
        this.flow = flow;
        this.estimateOfReceival = estimateOfReceival;
        this.cost= cost;
        this.probability = probability;
    }
}
