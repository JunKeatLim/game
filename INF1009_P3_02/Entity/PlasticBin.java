package INF1009_P3_02.Entity;

public class PlasticBin extends Bin {

    public PlasticBin(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, BinType.PLASTIC);
    }

    @Override
    protected String getTexturePath() {
        return "bins_image/plastic_Bin.png";
    }
}