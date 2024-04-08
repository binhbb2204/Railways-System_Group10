
package model;

public enum TrainType {
    SE("Express service between major cities, air-conditioned, 4-6 bunks per cabin"),
    SP("Superior services with more amenities and comfort options"),
    TN("Slower service with more stops, variety of classes, more affordable"),
    FIVE_STAR("Luxury service with higher level of comfort, operates on select routes");

    private final String description;

    TrainType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

