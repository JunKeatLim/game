package INF1009_P3_02.Entity;

import com.badlogic.gdx.math.MathUtils;

// Single factory that creates all types of Trash based on TrashType
public class TrashFactory {
    private final float worldW, worldH, boundsW, boundsH, drawH;

    public TrashFactory(float worldW, float worldH, float boundsW, float boundsH, float drawH) {
        this.worldW = worldW;
        this.worldH = worldH;
        this.boundsW = boundsW;
        this.boundsH = boundsH;
        this.drawH = drawH;
    }

    public Trash createTrash(TrashType type) {
        float x = MathUtils.random(0f, worldW - boundsW);
        float y = MathUtils.random(0f, worldH - boundsH);

        switch (type) {
            case PAPER:
                return new Paper(x, y, boundsW, boundsH, drawH);
            case PLASTIC:
                return new Plastic(x, y, boundsW, boundsH, drawH);
            case ELECTRONIC:
                return new Electronic(x, y, boundsW, boundsH, drawH);
            case TRASHBAG:
                return new Trashbag(x, y, boundsW, boundsH, drawH);
            default:
                throw new IllegalArgumentException("Unknown TrashType: " + type);
        }
    }
}