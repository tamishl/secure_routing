package core;

// Host to or String toId?
public class EdgeAttributes {
    public final int capacity;
    public int flow = 0;
    public double cost;

    public EdgeAttributes(int capacity, double cost) {
        this.capacity = capacity;
        this.cost = cost;
    }

    public String toString() {
        return "capacity = " + capacity + ", cost = " + cost;
    }
}
