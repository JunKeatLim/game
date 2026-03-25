// MovementManager.java
package INF1009_P3_02.Engine.movement;

import INF1009_P3_02.Engine.entity.EntityManager;
import INF1009_P3_02.Game.entity.Bot;
import INF1009_P3_02.Game.entity.Player;
import INF1009_P3_02.Game.enumFolder.Facing;
import INF1009_P3_02.Game.movement.BotMovement;
import INF1009_P3_02.Game.movement.PlayerMovement;

import java.util.List;

public class MovementManager {

    private final PlayerMovement playerMovement;
    private final BotMovement botMovement;
    private final EntityManager entityManager;
    private float mouseTargetX = -1f;
    private float mouseTargetY = -1f;
    private boolean hasMouseTarget = false;
    private static final float MOUSE_ARRIVAL_THRESHOLD = 5f;

    public MovementManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.playerMovement = new PlayerMovement(this);
        this.botMovement = new BotMovement(this);
    }

    public void update(float dt) {
        Player player = entityManager.getPlayer();
        if (player != null) {
            playerMovement.setWorldBounds(player.getWorldW(), player.getWorldH(), player.getCollisionSize());
            if (hasMouseTarget) {
                float dx = mouseTargetX - player.getX();
                float dy = mouseTargetY - player.getY();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist <= MOUSE_ARRIVAL_THRESHOLD) {
                    hasMouseTarget = false;
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        player.setFacing(dx > 0 ? Facing.RIGHT : Facing.LEFT);
                        playerMovement.onKeyInput(dx > 0 ? 'D' : 'A', dt);
                    } else {
                        player.setFacing(dy > 0 ? Facing.UP : Facing.DOWN);
                        playerMovement.onKeyInput(dy > 0 ? 'W' : 'S', dt);
                    }
                    player.onMove(dt);
                }
            } else {
                playerMovement.update(dt);
            }
        }

        List<Bot> bots = entityManager.getBots();
        for (Bot bot : bots) {
            botMovement.updateBot(dt, bot);
        }
    }


    //Sends received key input to playerMovement to calculate
    public void onKeyInput(char key, float dt) {
        hasMouseTarget = false;
        Player player = entityManager.getPlayer();
        if (player != null) {
            switch (key) {
                case 'W':
                    player.setFacing(Facing.UP);
                    break;
                case 'A':
                    player.setFacing(Facing.LEFT);
                    break;
                case 'S':
                    player.setFacing(Facing.DOWN);
                    break;
                case 'D':
                    player.setFacing(Facing.RIGHT);
                    break;
            }
            player.onMove(dt);
        }
        playerMovement.onKeyInput(key, dt);
    }

    //Sends the new XY player position to entityManager
    public void onPositionCalculated(float newX, float newY) {
        entityManager.updatePlayerPosition(newX, newY);
    }

    //Sends the new XY bot position to entityManager
    public void onBotPositionCalculated(Bot bot, float newX, float newY) {
        entityManager.updateBotPosition(bot, newX, newY);
    }

    //Synchronises X Y position of player
    public void syncPlayerMovementFromEntity() {
        Player p = entityManager.getPlayer();
        if (p != null) {
            playerMovement.syncFromEntity(p.getX(), p.getY());
        }
    }

    public void onMouseTarget(float worldX, float worldY) {
        mouseTargetX = worldX;
        mouseTargetY = worldY;
        hasMouseTarget = true;
    }
}
