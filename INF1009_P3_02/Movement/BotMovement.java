// BotMovement.java
package INF1009_P3_02.Movement;

import INF1009_P3_02.Entity.Bot;
import com.badlogic.gdx.math.MathUtils;

public class BotMovement {

    private final MovementManager movementManager;

    public BotMovement(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    private float[] randomDirection() {
        float angle = MathUtils.random(0f, 360f);
        return new float[] { MathUtils.cosDeg(angle), MathUtils.sinDeg(angle) };
    }

    public void updateBot(float dt, Bot bot) {
        float currentX = bot.getX();
        float currentY = bot.getY();
        float dirX = bot.getDirX();
        float dirY = bot.getDirY();
        float speed = bot.getSpeed();
        float worldW = bot.getWorldW();
        float worldH = bot.getWorldH();
        float botWidth = bot.getBotWidth();
        float botHeight = bot.getBotHeight();

        if (dirX == 0 && dirY == 0) {
            float[] randomDir = randomDirection();
            dirX = randomDir[0];
            dirY = randomDir[1];
        }

        //Calculate new XY position
        currentX += dirX * speed * dt;
        currentY += dirY * speed * dt;

        boolean hitEdge = false;

        //Keeps bot within screen bounds
        if (currentX < 0) {
            currentX = 0;
            hitEdge = true;
        }
        if (currentX + botWidth > worldW) {
            currentX = worldW - botWidth;
            hitEdge = true;
        }
        if (currentY < 0) {
            currentY = 0;
            hitEdge = true;
        }
        if (currentY + botHeight > worldH) {
            currentY = worldH - botHeight;
            hitEdge = true;
        }

        if (hitEdge) {
            float[] randomDir = randomDirection();
            dirX = randomDir[0];
            dirY = randomDir[1];
        }

        bot.setDirection(dirX, dirY);
        movementManager.onBotPositionCalculated(bot, currentX, currentY);
    }
}
