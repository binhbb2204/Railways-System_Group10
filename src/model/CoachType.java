package model;

public enum CoachType {
    SOFT_SLEEPER("Soft Sleeper"),
    HARD_SLEEPER("Hard Sleeper"),
    SOFT_SEAT("Soft Seat"),
    HARD_SEAT("Hard Seat"),
    VERY_HARD_SEAT("Very Hard Seat"),
    DINING_CAR("Dining Car"),
    BAGGAGE_VAN("Baggage Van"),
    POWER_CAR("Power Car"),
    DOUBLE_DECK_COACH("Double Deck Coach");

    private final String description;

    CoachType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
