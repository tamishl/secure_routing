package core;

public enum NodeType {
    SOURCE("S"),
    INNER_NODE("N"),
    DESTINATION("D");

    private final String prefix;

    private NodeType(String prefix){
        this.prefix = prefix;
    }

    public String getPrefix(){
        return prefix;
    }
}
