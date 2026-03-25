package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.Bot;
import INF1009_P3_02.Entity.EntityManager;
import INF1009_P3_02.Entity.Bin;
import INF1009_P3_02.Entity.Player;
import INF1009_P3_02.InputOutput.Speaker;
import INF1009_P3_02.Observer.GameEventManager;

import java.util.List;
import java.util.Map;

public class CollisionManager {
    private final CollisionHandler playerObstacle;
    private final CollisionHandler botObstacle    = new BotObstacleCollision();
    private final CollisionHandler playerBot;
    private final CollisionHandler playerBinDeposit;

    private final CollisionCounter collisionCounter = new CollisionCounter();
    private final Speaker speaker;

    public CollisionManager(Speaker speaker, EntityManager entityManager,
                            GameEventManager eventManager) {
        this.speaker = speaker;
        this.playerObstacle   = new PlayerObstacleCollision(eventManager);
        this.playerBot        = new PlayerBotCollision(entityManager, eventManager);
        this.playerBinDeposit = new PlayerBinDepositCollision(entityManager, eventManager);
    }

    public CollisionContext resolve(
        Player player,
        List<Bot> bots,
        List<Bin> obstacles,
        float pOldX, float pOldY,
        Map<Bot, float[]> botOldPositions,
        float dt
    ) {
        CollisionContext ctx = new CollisionContext(pOldX, pOldY, 0f, 0f, dt);

        // Player vs obstacles
        for (Bin o : obstacles) {
            playerBinDeposit.handle(player, o, ctx, speaker);
            playerObstacle.handle(player, o, ctx, speaker);
        }

        // Bot vs obstacles + player vs bot
        if (bots != null) {
            for (Bot bot : bots) {
                float[] oldPos = botOldPositions != null ? botOldPositions.get(bot) : null;
                float bOldX = oldPos != null ? oldPos[0] : bot.getX();
                float bOldY = oldPos != null ? oldPos[1] : bot.getY();

                CollisionContext botCtx = new CollisionContext(pOldX, pOldY, bOldX, bOldY, dt);
                for (Bin o : obstacles) {
                    botObstacle.handle(bot, o, botCtx, speaker);
                }
                playerBot.handle(player, bot, botCtx, speaker);

                ctx.botCollidedWithObstacle = ctx.botCollidedWithObstacle || botCtx.botCollidedWithObstacle;
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