package org.openlmis.rnr.domain;

public enum RnrColumnType {
    UserInput("User Input"), Calculated("Calculated");

    private final String columnType;

    public static RnrColumnType getValueOf(String value){
        for (RnrColumnType  columnType : RnrColumnType.values()) {
            if(columnType.columnType.equalsIgnoreCase(value)) return columnType;
        }
        return null;
    }

    public  String toString(){
        return this.columnType;
    }

    RnrColumnType(String name) {
        this.columnType = name;
    }
}
