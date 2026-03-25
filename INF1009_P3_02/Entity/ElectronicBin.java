package INF1009_P3_02.Entity;

public class ElectronicBin extends Bin {

    public ElectronicBin(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, BinType.ELECTRONIC);
    }

    @Override
    protected String getTexturePath() {
        return "bins_image/electronic_Bin.png";
    }
}