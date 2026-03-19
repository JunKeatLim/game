package INF1009_P3_02.Entity;

public class Paper extends Trash {

    public Paper(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, TrashType.PAPER);
    }

    @Override
    protected String getTexturePath() {
        return "trash_recyclable/paper.png";
    }
}
