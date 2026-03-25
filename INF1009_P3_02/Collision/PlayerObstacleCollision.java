package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.Entity;
import INF1009_P3_02.Entity.Bin;
import INF1009_P3_02.Entity.Player;
import INF1009_P3_02.InputOutput.Speaker;
import INF1009_P3_02.Observer.GameEventManager;
import INF1009_P3_02.Observer.StateChangeReason;

public class PlayerObstacleCollision implements CollisionHandler {

    private final GameEventManager eventManager;

    public PlayerObstacleCollision(GameEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Player) || !(b instanceof Bin)) return;

        Player player = (Player) a;
        Bin obstacle = (Bin) b;

        if (!CollisionUtil.intersects(player, obstacle)) return;

        CollisionUtil.revert(player, ctx.pOldX, ctx.pOldY);
        ctx.playerCollidedWithObstacle = true;

        // Notify observers of player collision
        if (eventManager != null) {
            eventManager.notifyPlayerCollision(StateChangeReason.HIT_OBSTACLE);
        }
    }
}