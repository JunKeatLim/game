package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.Entity;

public class CollisionUtil {

    public static boolean intersects(Entity a, Entity b) {
        return a.getBounds().overlaps(b.getBounds());     //Check for intersection between two entities
    }

    public static void revert(Entity entity, float oldX, float oldY) { //Reverts an entity back to its previous position when it moved into an invalid state
        entity.setX(oldX);
        entity.setY(oldY);
        entity.update(0); // sync bounds
    }
}
