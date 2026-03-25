package INF1009_P3_02.Game.factory;

import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Trash;
import INF1009_P3_02.Game.enumFolder.BinType;
import INF1009_P3_02.Game.enumFolder.TrashType;

public class EntityFactory implements AbstractEntityFactory{
    private final TrashFactory trashFactory;
    private final BinFactory binFactory;

    public EntityFactory(float worldW, float worldH) {
        this.trashFactory = new TrashFactory(worldW, worldH, 40f, 40f, 90f);
        this.binFactory   = new BinFactory(worldW, worldH, 100f, 35f, 150f);
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

    // Expose for EntityManager internal use
    public BinFactory getBinFactory() { return binFactory; }
}
