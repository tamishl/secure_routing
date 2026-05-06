package core;

public class NetworkGenerator {
    int nodes;
    int edges;

    double minRisk;
    double maxRisk;

    // Assuming 0 as minimum
    int maxCapacity;
    double maxCost;



    public NetworkGenerator(int nodes, int edges, int minRisk, int maxRisk, int maxCapacity, int  maxCost){
        this.nodes = nodes;
        this.edges = edges;
        this.minRisk = minRisk;
        this.maxRisk = maxRisk;
        this.maxCapacity = maxCapacity;
        this.maxCost = maxCost;
    }
}
