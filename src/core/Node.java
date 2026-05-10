package core;

import java.util.Objects;

public class Node {
    String id;
    NodeType type;
    double probability;


    public Node(String id, NodeType type, double probability) {
        this.id = id;
        this.type = type;
        this.probability = probability;
    }

    public String toString() {
        return id;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    //Override equals to make sure vertices with the same value are considered as equal
    //Code provided by IntelliJ IDE (Generate >> equals() and hashCode())
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;        //Direct comparison of hashCode
        if (object == null || getClass() != object.getClass()) return false;

        Node node = (Node) object;
        return Objects.equals(id, node.id);       //Comparison of values
    }

    @Override
    public int hashCode() {
        //Create hashCode based on all values (in this case only the name of the vertex)
        return Objects.hashCode(id);
    }
}