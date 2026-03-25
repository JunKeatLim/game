package INF1009_P3_02.Entity;

public class TrashBin extends Bin {

    public TrashBin(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, BinType.TRASH);
    }

    @Override
    protected String getTexturePath() {
        return "bins_image/trash_Bin.png";
    }
}