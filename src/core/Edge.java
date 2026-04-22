package core;

import java.util.Objects;

public class Edge {
    Host from, to;
    int weight;

    public Edge(Host from, Host to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String toString() {
        return from.id + " - " + weight + " -> " + to.id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Edge edge = (Edge) object;
        return weight == edge.weight && Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }
}
