package INF1009_P3_02.Engine.collision;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.entity.Entity;

public interface CollisionHandler {
    void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker);
}
