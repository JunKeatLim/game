package INF1009_P3_02.Game.collision;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.collision.CollisionContext;
import INF1009_P3_02.Engine.collision.CollisionHandler;
import INF1009_P3_02.Engine.entity.Entity;
import INF1009_P3_02.Engine.util.CollisionUtil;
import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Bot;

public class BotBinCollision implements CollisionHandler {

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Bot) || !(b instanceof Bin)) return;

        Bot bot = (Bot) a;
        Bin obstacle = (Bin) b;

        if (!CollisionUtil.intersects(bot, obstacle)) return; //check if bot and obstacle overlap each other

        // Block bot: revert bot
        CollisionUtil.revert(bot, ctx.bOldX, ctx.bOldY);
        bot.setDirection(-bot.getDirX(), -bot.getDirY());


        ctx.botCollidedWithBin = true; // Tell movement system to pick a new direction
        speaker.playCollisionSound(); //Play collision sound
    }
}