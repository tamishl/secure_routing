package core;

import java.util.Objects;

public class Node {
    String id;
    int vulnerability;


    public Node(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }

    public void setVulnerability(int vulnerability) {
        this.vulnerability = vulnerability;
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