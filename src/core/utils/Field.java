package core.utils;

public enum Field {
    ID("id"),
    FROM("from"),
    TO("to"),
    PROBABILITY("probability"),
    CAPACITY("capacity"),
    COST("cost");

    private final String value;

    Field(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }


}
