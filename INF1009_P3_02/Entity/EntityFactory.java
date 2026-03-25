package INF1009_P3_02.Entity;

public class EntityFactory implements AbstractEntityFactory{
    private final TrashFactory trashFactory;
    private final BinFactory binFactory;
    private final PlayerFactory playerFactory;

    public EntityFactory(float worldW, float worldH) {
        this.trashFactory = new TrashFactory(worldW, worldH, 40f, 40f, 90f);
        this.binFactory   = new BinFactory(worldW, worldH, 100f, 35f, 150f);
        this.playerFactory = new PlayerFactory(worldW, worldH);
    }

    @Override
    public Trash createTrash(TrashType type) {
        return trashFactory.createTrash(type);
    }

    @Override
    public Bin createBin(BinType type) {
        return binFactory.createBin(type);
    }

    @Override
    public Bin createBin(BinType type, float x, float y) {
        return binFactory.createBin(type, x, y);
    }

    @Override
    public Player createPlayer(float x, float y, float speed, float size, float drawH) {
        return playerFactory.createPlayer(x, y, speed, size, drawH);
    }

    // Expose for EntityManager internal use
    public BinFactory getBinFactory() { return binFactory; }
}
