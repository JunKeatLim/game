package INF1009_P3_02;

public class SimulationConfig {

    public BackgroundChoice background = BackgroundChoice.SCHOOL_BASKETBALL;
    public int durationSeconds = 60; // 60, 180, 300

    public int getBotCount() {
        switch (background) {
            case SCHOOL_BASKETBALL:
                return 0; // Easy
            case SCHOOL_CANTEEN:
                return 1; // Medium
            case SCHOOL_PARK:
                return 2; // Hard
            default:
                return 1;
        }
    }

}
