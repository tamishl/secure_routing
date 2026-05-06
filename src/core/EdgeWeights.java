package core;

// Host to or String toId?
public class EdgeWeights {
    final int capacity;

    double time;
    int residual;

    public EdgeWeights(int capacity, double time) {
        this.capacity = capacity;
        this.residual = capacity;
        this.time = time;
    }

    public String toString() {
        return "capacity:" + capacity + ", time:" + time;
    }
}
