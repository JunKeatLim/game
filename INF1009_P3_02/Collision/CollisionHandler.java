package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.Entity;
import INF1009_P3_02.InputOutput.Speaker;

public interface CollisionHandler {
    void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker);
}
