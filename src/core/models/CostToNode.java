package core.models;

//    https://www.geeksforgeeks.org/dsa/dijkstras-shortest-path-algorithm-using-priority_queue-stl/
// Class for PriorityQ in computeDijkstra to prioritize nodes with a lower cost from the source

public class CostToNode implements Comparable<CostToNode> {
    public String nodeId;
    public double cost;


    public CostToNode(String nodeId, double totalCost){
        this.nodeId = nodeId;
        this.cost = totalCost;
    }

    // Comparison method for priority queue
    public int compareTo(CostToNode other)
    {
        return Double.compare(this.cost, other.cost);
    }
}
