package INF1009_P3_02.Entity;

public class Electronic extends Trash {

    public Electronic(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, TrashType.ELECTRONIC);
    }

    @Override
    protected String getTexturePath() {
        return "trash_recyclable/electronic.png";
    }
}
