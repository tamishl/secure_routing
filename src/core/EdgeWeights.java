package core;

// Host to or String toId?
public class EdgeWeights {
    final int capacity;

    double cost;
    int residual;

    public EdgeWeights(int capacity, double cost) {
        this.capacity = capacity;
        this.residual = capacity;
        this.cost = cost;
    }

    public String toString() {
        return "capacity:" + capacity + ", cost:" + cost;
    }
}
