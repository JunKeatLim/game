package INF1009_P3_02.Game.entity;

import INF1009_P3_02.Game.enumFolder.TrashType;

public class Trashbag extends Trash {

    public Trashbag(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, TrashType.TRASHBAG);
    }

    @Override
    protected String getTexturePath() {
        return "trash_recyclable/trashbag.png";
    }
}
