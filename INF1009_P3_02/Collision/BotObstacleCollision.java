package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.Bot;
import INF1009_P3_02.Entity.Entity;
import INF1009_P3_02.Entity.Obstacle;
import INF1009_P3_02.InputOutput.Speaker;

public class BotObstacleCollision implements CollisionHandler {

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Bot) || !(b instanceof Obstacle)) return;

        Bot bot = (Bot) a;
        Obstacle obstacle = (Obstacle) b;

        if (!CollisionUtil.intersects(bot, obstacle)) return; //check if bot and obstacle overlap each other

        // Block bot: revert bot
        CollisionUtil.revert(bot, ctx.bOldX, ctx.bOldY);
        bot.setDirection(-bot.getDirX(), -bot.getDirY());


        ctx.botCollidedWithObstacle = true; // Tell movement system to pick a new direction
        speaker.playCollisionSound(); //Play collision sound
    }
}
