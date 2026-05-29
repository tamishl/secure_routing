package core;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public enum NodeType {
    SOURCE("S"),
    INNER("N"),
    SINK("T");

    private final String prefix;

    // Reverse lookup
    // https://stackoverflow.com/questions/5316311/java-enum-reverse-look-up-best-practice
    private static final Map<String, NodeType> BY_PREFIX = Arrays.stream(values()).collect(Collectors.toMap(NodeType::getPrefix, t -> t));

    private NodeType(String prefix){
        this.prefix = prefix;
    }


    public String getPrefix(){
        return prefix;
    }
}
