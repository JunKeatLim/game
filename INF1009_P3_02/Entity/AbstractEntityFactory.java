package INF1009_P3_02.Entity;

public interface AbstractEntityFactory {
    Trash createTrash(TrashType type);
    Bin createBin(BinType type);
    Bin createBin(BinType type, float x, float y);
    Player createPlayer(float x, float y, float speed, float size, float drawH);
}
