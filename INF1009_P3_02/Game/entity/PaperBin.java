package INF1009_P3_02.Game.entity;

import INF1009_P3_02.Game.enumFolder.BinType;

public class PaperBin extends Bin {

    public PaperBin(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, BinType.PAPER);
    }

    @Override
    protected String getTexturePath() {
        return "bins_image/paper_Bin.png";
    }
}