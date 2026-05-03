package core;

// Host to or String toId?
public class Edge {
    String fromId;
    String toId;
    final int capacity;

    double time;
    int residual;

    public Edge (String fromId, String toId, int capacity, double time) {
        this.fromId = fromId;
        this.toId = toId;
        this.capacity = capacity;
        this.residual = capacity;
        this.time = time;
    }

    public String toString() {
        return fromId + " -> " + toId + ": capacity=" + capacity + ", time=" + time;
    }
}
