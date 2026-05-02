package core;

// Host to or String toId?
public class Edge {
    String toId;
    final int capacity;

    double time;
    int residual;

    public Edge (String toId, int capacity, double time) {
        this.toId = toId;
        this.capacity = capacity;
        this.residual = capacity;
        this.time = time;
    }

    public String toString() {
        return toId + "| capacity: " + capacity + "| time: " + time;
    }
}
