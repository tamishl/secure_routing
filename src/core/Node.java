package core;

import java.util.Objects;

public class Node {
    public String id;
    public NodeType type;
    public double risk;


    public Node(String id, NodeType type, double risk) {
        this.id = id;
        this.type = type;
        this.risk = risk;
    }

    public String toString() {
        return id;
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