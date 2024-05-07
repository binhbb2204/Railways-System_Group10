
package model;


public enum StatusType {
    ON_TIME("On Time"),
    DELAYED("Delayed"), 
    CANCELLED("Cancelled");

    private final String description;

    StatusType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
