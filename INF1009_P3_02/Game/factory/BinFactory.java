package INF1009_P3_02.Game.factory;

import com.badlogic.gdx.math.MathUtils;

import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.ElectronicBin;
import INF1009_P3_02.Game.entity.PaperBin;
import INF1009_P3_02.Game.entity.PlasticBin;
import INF1009_P3_02.Game.entity.TrashBin;
import INF1009_P3_02.Game.enumFolder.BinType;

public class BinFactory {
    private final float worldW, worldH;
    private final float boundsW, boundsH, drawH;

    public BinFactory(float worldW, float worldH, float boundsW, float boundsH, float drawH) {
        this.worldW = worldW;
        this.worldH = worldH;
        this.boundsW = boundsW;
        this.boundsH = boundsH;
        this.drawH = drawH;
    }

    public Bin createBin(BinType type) {
        float x = MathUtils.random(0f, worldW - boundsW);
        float y = MathUtils.random(0f, worldH - boundsH);
        return createBin(type, x, y);
    }

    public Bin createBin(BinType type, float x, float y) {
        float clampedX = MathUtils.clamp(x, 0f, worldW - boundsW);
        float clampedY = MathUtils.clamp(y, 0f, worldH - boundsH);

        switch (type) {
            case TRASH:
                return new TrashBin(clampedX, clampedY, boundsW, boundsH, drawH);
            case ELECTRONIC:
                return new ElectronicBin(clampedX, clampedY, boundsW, boundsH, drawH);
            case PAPER:
                return new PaperBin(clampedX, clampedY, boundsW, boundsH, drawH);
            case PLASTIC:
                return new PlasticBin(clampedX, clampedY, boundsW, boundsH, drawH);
            default:
                throw new IllegalArgumentException("Unknown ObstacleType: " + type);
        }
    }

    public float getBoundsW() {
        return boundsW;
    }

    public float getBoundsH() {
        return boundsH;
    }

    public float getDrawH() {
        return drawH;
    }
}
