package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.Bot;
import INF1009_P3_02.Entity.EntityManager;
import INF1009_P3_02.Entity.Obstacle;
import INF1009_P3_02.Entity.Player;
import INF1009_P3_02.InputOutput.Speaker;
import INF1009_P3_02.Logging.GameEngineLogger;
import java.util.List;
import java.util.Map;

public class CollisionManager {
    // Strategy objects responsible for handling specific collision types.
    private final CollisionHandler playerObstacle = new PlayerObstacleCollision();
    private final CollisionHandler botObstacle    = new BotObstacleCollision();
    private final CollisionHandler playerBot;

    // Tracks the number of valid player–bot collision events.
    private final CollisionCounter collisionCounter = new CollisionCounter();
    // Handles sound effects when collisions occur.
    private final Speaker speaker;
    private final GameEngineLogger logger;
    private final CollisionHandler playerBinDeposit;

    public CollisionManager(Speaker speaker, EntityManager entityManager, GameEngineLogger logger) {
        this.speaker = speaker;
        this.logger = logger;
        this.playerBinDeposit = new PlayerBinDepositCollision(entityManager);
        this.playerBot = new PlayerBotCollision(entityManager);
    }

    public CollisionContext resolve(
        Player player,
        List<Bot> bots,
        List<Obstacle> obstacles,
        float pOldX, float pOldY,
        Map<Bot, float[]> botOldPositions,
        float dt
    ) {
        CollisionContext ctx = new CollisionContext(pOldX, pOldY, 0f, 0f, dt);

        // Player vs obstacles
        for (Obstacle o : obstacles) {
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
                for (Obstacle o : obstacles) {
                    botObstacle.handle(bot, o, botCtx, speaker);
                }
                playerBot.handle(player, bot, botCtx, speaker);

                ctx.botCollidedWithObstacle = ctx.botCollidedWithObstacle || botCtx.botCollidedWithObstacle;
                ctx.botCollidedWithPlayer = ctx.botCollidedWithPlayer || botCtx.botCollidedWithPlayer;
                ctx.playerBotOverlappingNow = ctx.playerBotOverlappingNow || botCtx.playerBotOverlappingNow;
            }
        }

        // Count score
        ctx.updateBinOverlapState();
        // Count collision
        collisionCounter.update(ctx, dt);
        
        return ctx;
    }

    public int getPlayerBotCollisionCount() {
        return collisionCounter.getCount();
    }
}
