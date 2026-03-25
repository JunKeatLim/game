package INF1009_P3_02.Game.collision;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.collision.CollisionContext;
import INF1009_P3_02.Engine.collision.CollisionHandler;
import INF1009_P3_02.Engine.entity.Entity;
import INF1009_P3_02.Engine.entity.EntityManager;
import INF1009_P3_02.Engine.observer.GameEventManager;
import INF1009_P3_02.Engine.util.CollisionUtil;
import INF1009_P3_02.Game.entity.Bot;
import INF1009_P3_02.Game.entity.Player;
import INF1009_P3_02.Game.enumFolder.CarryState;
import INF1009_P3_02.Game.enumFolder.StateChangeReason;
import INF1009_P3_02.Game.enumFolder.TrashType;

public class PlayerBotCollision implements CollisionHandler {

    private final EntityManager entityManager;
    private final GameEventManager eventManager;

    public PlayerBotCollision(EntityManager entityManager, GameEventManager eventManager) {
        this.entityManager = entityManager;
        this.eventManager = eventManager;
    }

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Player) || !(b instanceof Bot)) return;

        Player player = (Player) a;
        Bot bot = (Bot) b;
        TrashType carriedType = player.getCarriedTrashType();

        boolean overlapping = CollisionUtil.intersects(player, bot);
        ctx.playerBotOverlappingNow = overlapping;

        if (!overlapping) return;

        // If the player was carrying trash, drop it and notify carry state observers
        if (player.getCarryState() != CarryState.NONE && carriedType != null) {
            player.setCarryState(CarryState.NONE);
            player.clearCarriedTrash();

            // Notify observers: carry state changed (DROPPED)
            if (eventManager != null) {
                eventManager.notifyCarryStateChanged(CarryState.NONE, carriedType, StateChangeReason.DROPPED, 0);
            }

            entityManager.respawnTrash(carriedType);
        }

        // Block player from passing through the bot
        CollisionUtil.revert(player, ctx.pOldX, ctx.pOldY);

        // Nudge player away to avoid sticking on the bot edge
        float dx = player.getX() - bot.getX();
        float dy = player.getY() - bot.getY();
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0f) {
            dx /= len;
            dy /= len;
            float push = Math.max(2f, player.getSpeed() * ctx.dt);
            player.setX(player.getX() + dx * push);
            player.setY(player.getY() + dy * push);
            player.clampToWorld();
            player.update(0);
        }

        // Block bot: revert bot so it bounces like hitting an obstacle
        CollisionUtil.revert(bot, ctx.bOldX, ctx.bOldY);

        ctx.botCollidedWithPlayer = true;

        // Notify observers: player collision (HIT_BOT)
        // Audio, logging, movement all respond through observer
        if (eventManager != null) {
            eventManager.notifyPlayerCollision(StateChangeReason.HIT_BOT);
        }
    }
}