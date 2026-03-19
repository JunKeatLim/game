package INF1009_P3_02.Collision;
import INF1009_P3_02.Entity.Entity;
import INF1009_P3_02.Entity.Obstacle;
import INF1009_P3_02.Entity.Player;
import INF1009_P3_02.InputOutput.Speaker;

public class PlayerObstacleCollision implements CollisionHandler {
    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Player) || !(b instanceof Obstacle)) return;

        Player player = (Player) a;
        Obstacle obstacle = (Obstacle) b;

        if (!CollisionUtil.intersects(player, obstacle)) return; //check if bot and obstacle overlap each other

        CollisionUtil.revert(player, ctx.pOldX, ctx.pOldY);
        ctx.playerCollidedWithObstacle = true;

    }
}
