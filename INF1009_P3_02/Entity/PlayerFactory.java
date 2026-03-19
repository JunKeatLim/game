package INF1009_P3_02.Entity;

public class PlayerFactory {
    private final float worldW, worldH;

    public PlayerFactory(float worldW, float worldH) {
        this.worldW = worldW;
        this.worldH = worldH;
    }

    public Player createPlayer(float x, float y, float speed, float size, float drawH) {
        Player player = new Player(x, y, speed, size, drawH, worldW, worldH);
        player.loadTextures();
        return player;
    }
}