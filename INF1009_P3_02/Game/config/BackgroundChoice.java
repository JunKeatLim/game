package INF1009_P3_02.Game.config;

public enum BackgroundChoice {
    SCHOOL_BASKETBALL("Easy"),
    SCHOOL_CANTEEN("Medium"),
    SCHOOL_PARK("Hard");

    private final String title;

    BackgroundChoice(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
