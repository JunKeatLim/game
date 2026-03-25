package INF1009_P3_02.Game.factory;

import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Trash;
import INF1009_P3_02.Game.enumFolder.BinType;
import INF1009_P3_02.Game.enumFolder.TrashType;

public interface AbstractEntityFactory {
    Trash createTrash(TrashType type);
    Bin createBin(BinType type);
    Bin createBin(BinType type, float x, float y);
}
