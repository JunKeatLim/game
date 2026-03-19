package INF1009_P3_02.Entity;

import com.badlogic.gdx.math.MathUtils;

public class ObstacleFactory {
    private final float worldW, worldH;
    private final float boundsW, boundsH, drawH;

    public ObstacleFactory(float worldW, float worldH, float boundsW, float boundsH, float drawH) {
        this.worldW = worldW;
        this.worldH = worldH;
        this.boundsW = boundsW;
        this.boundsH = boundsH;
        this.drawH = drawH;
    }

    public Obstacle createObstacle(ObstacleType type) {
        float x = MathUtils.random(0f, worldW - boundsW);
        float y = MathUtils.random(0f, worldH - boundsH);

        switch (type) {
            case TRASH:
                return new TrashBin(x, y, boundsW, boundsH, drawH);
            case ELECTRONIC:
                return new ElectronicBin(x, y, boundsW, boundsH, drawH);
            case PAPER:
                return new PaperBin(x, y, boundsW, boundsH, drawH);
            case PLASTIC:
                return new PlasticBin(x, y, boundsW, boundsH, drawH);
            default:
                throw new IllegalArgumentException("Unknown ObstacleType: " + type);
        }
    }
}