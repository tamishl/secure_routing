package core;

// Host to or String toId?
public class EdgeWeights {
    final int capacity;
    int flow = 0;
    double cost;

    public EdgeWeights(int capacity, double cost) {
        this.capacity = capacity;
        this.cost = cost;
    }

    public String toString() {
        return "capacity = " + capacity + ", cost = " + cost;
    }
}
