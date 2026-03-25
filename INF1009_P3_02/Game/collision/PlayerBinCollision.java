package INF1009_P3_02.Game.collision;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.collision.CollisionContext;
import INF1009_P3_02.Engine.collision.CollisionHandler;
import INF1009_P3_02.Engine.entity.Entity;
import INF1009_P3_02.Engine.observer.GameEventManager;
import INF1009_P3_02.Engine.util.CollisionUtil;
import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Player;
import INF1009_P3_02.Game.enumFolder.StateChangeReason;

public class PlayerBinCollision implements CollisionHandler {

    private final GameEventManager eventManager;

    public PlayerBinCollision(GameEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Player) || !(b instanceof Bin)) return;

        Player player = (Player) a;
        Bin obstacle = (Bin) b;

        if (!CollisionUtil.intersects(player, obstacle)) return;

        CollisionUtil.revert(player, ctx.pOldX, ctx.pOldY);
        ctx.playerCollidedWithBin = true;

        // Notify observers of player collision
        if (eventManager != null) {
            eventManager.notifyPlayerCollision(StateChangeReason.HIT_OBSTACLE);
        }
    }
}