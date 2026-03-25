package INF1009_P3_02.Game.movement;

import INF1009_P3_02.Engine.movement.MovementManager;

public class PlayerMovement {
    private final MovementManager movementManager;
    private float currentX = 400;  // Default start
    private float currentY = 300;

    private float worldW = 1280f;
    private float worldH = 720f;
    private float playerSize = 50f;

    public PlayerMovement(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    public void setWorldBounds(float worldW, float worldH, float playerSize) {
        this.worldW = worldW;
        this.worldH = worldH;
        this.playerSize = playerSize;
    }

    public void onKeyInput(char key, float dt) {
        float moveSpeed = 300 * dt;
        float newX = currentX;
        float newY = currentY;

        //Calculate new XY position based on key input received
        switch (key) {
            case 'A':
                newX -= moveSpeed; break;
            case 'D':
                newX += moveSpeed; break;
            case 'W':
                newY += moveSpeed; break;
            case 'S':
                newY -= moveSpeed; break;
        }

        //Keeps player entity within screen bounds
        float half = playerSize / 2f;
        boolean hitEdge = false;

        if (newX < half) {
            newX = half;
            hitEdge = true;
        }
        if (newX > worldW - half) {
            newX = worldW - half;
            hitEdge = true;
        }
        if (newY < half) {
            newY = half;
            hitEdge = true;
        }
        if (newY > worldH - half) {
            newY = worldH - half;
            hitEdge = true;
        }

        currentX = newX;
        currentY = newY;

        movementManager.onPositionCalculated(newX, newY);
    }

    public void syncFromEntity(float x, float y) {
        this.currentX = x;
        this.currentY = y;
    }

    public void update(float dt) {}
}
