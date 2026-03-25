package INF1009_P3_02.Engine.collision;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.entity.EntityManager;
import INF1009_P3_02.Engine.observer.GameEventManager;
import INF1009_P3_02.Engine.util.CollisionCounter;
import INF1009_P3_02.Game.collision.BotBinCollision;
import INF1009_P3_02.Game.collision.PlayerBinCollision;
import INF1009_P3_02.Game.collision.PlayerBinDepositCollision;
import INF1009_P3_02.Game.collision.PlayerBotCollision;
import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Bot;
import INF1009_P3_02.Game.entity.Player;

import java.util.List;
import java.util.Map;

public class CollisionManager {
    private final CollisionHandler playerBin;
    private final CollisionHandler botBin    = new BotBinCollision();
    private final CollisionHandler playerBot;
    private final CollisionHandler playerBinDeposit;

    private final CollisionCounter collisionCounter = new CollisionCounter();
    private final Speaker speaker;

    public CollisionManager(Speaker speaker, EntityManager entityManager,
                            GameEventManager eventManager) {
        this.speaker = speaker;
        this.playerBin   = new PlayerBinCollision(eventManager);
        this.playerBot        = new PlayerBotCollision(entityManager, eventManager);
        this.playerBinDeposit = new PlayerBinDepositCollision(entityManager, eventManager);
    }

    public CollisionContext resolve(
        Player player,
        List<Bot> bots,
        List<Bin> bins,
        float pOldX, float pOldY,
        Map<Bot, float[]> botOldPositions,
        float dt
    ) {
        CollisionContext ctx = new CollisionContext(pOldX, pOldY, 0f, 0f, dt);

        // Player vs obstacles
        for (Bin o : bins) {
            playerBinDeposit.handle(player, o, ctx, speaker);
            playerBin.handle(player, o, ctx, speaker);
        }

        // Bot vs obstacles + player vs bot
        if (bots != null) {
            for (Bot bot : bots) {
                float[] oldPos = botOldPositions != null ? botOldPositions.get(bot) : null;
                float bOldX = oldPos != null ? oldPos[0] : bot.getX();
                float bOldY = oldPos != null ? oldPos[1] : bot.getY();

                CollisionContext botCtx = new CollisionContext(pOldX, pOldY, bOldX, bOldY, dt);
                for (Bin o : bins) {
                    botBin.handle(bot, o, botCtx, speaker);
                }
                playerBot.handle(player, bot, botCtx, speaker);

                ctx.botCollidedWithBin = ctx.botCollidedWithBin || botCtx.botCollidedWithBin;
                ctx.botCollidedWithPlayer = ctx.botCollidedWithPlayer || botCtx.botCollidedWithPlayer;
                ctx.playerBotOverlappingNow = ctx.playerBotOverlappingNow || botCtx.playerBotOverlappingNow;
            }
        }

        ctx.updateBinOverlapState();
        collisionCounter.update(ctx, dt);

        return ctx;
    }

    public int getPlayerBotCollisionCount() {
        return collisionCounter.getCount();
    }
}