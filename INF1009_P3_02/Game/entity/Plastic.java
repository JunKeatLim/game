package INF1009_P3_02.Game.entity;

import INF1009_P3_02.Game.enumFolder.TrashType;

public class Plastic extends Trash {

    public Plastic(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, TrashType.PLASTIC);
    }

    @Override
    protected String getTexturePath() {
        return "trash_recyclable/plastic.png";
    }
}
