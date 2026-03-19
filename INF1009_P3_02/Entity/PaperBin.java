package INF1009_P3_02.Entity;

public class PaperBin extends Obstacle {

    public PaperBin(float x, float y, float boundsW, float boundsH, float drawH) {
        super(x, y, boundsW, boundsH, drawH, ObstacleType.PAPER);
    }

    @Override
    protected String getTexturePath() {
        return "bins_image/paper_Bin.png";
    }
}