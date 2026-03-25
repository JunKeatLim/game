package INF1009_P3_02.Game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import INF1009_P3_02.Engine.entity.Entity;

public class Bot extends Entity {
    private float worldW, worldH;
    private float width, height;
    private float collisionWidth, collisionHeight;
    private float dirX, dirY;

    private Texture leftTexture;
    private Texture rightTexture;

    //Animation timer
    private float swapTimer = 0f;
    private boolean showLeft = true;
    

    public Bot(float x, float y, float speed,
               float width, float height,
               float worldW, float worldH) {
        super(x, y, speed);
        this.width = width;
        this.height = height;
        this.collisionWidth = width * 0.4f;
        this.collisionHeight = height * 0.4f;
        this.worldW = worldW;
        this.worldH = worldH;

        //Load textures
        leftTexture = new Texture("EnemyAvatar/Enemy_left.png");
        rightTexture = new Texture("EnemyAvatar/Enemy_right.png");
        updateBounds();
    }

    //Strategy can set direction
    public void setDirection(float dx, float dy) {
        this.dirX = dx;
        this.dirY = dy;
    }

    public float getDirX() { return dirX; }
    public float getDirY() { return dirY; }

    public float getWorldW() { return worldW; }
    public float getWorldH() { return worldH; }

    public float getBotWidth() { return width; }
    public float getBotHeight() { return height; }

    @Override
    protected void updateBounds() {

        float offsetX = (width - collisionWidth) / 2f;
        float offsetY = (height - collisionHeight) / 2f;

        bounds.set(
            getX() + offsetX,
            getY() + offsetY,
            collisionWidth,
            collisionHeight
        );
    }

    @Override
    public void update(float dt) {
        swapTimer += dt;

        // swap every 10 seconds
        if (swapTimer >= 10f) {
            showLeft = !showLeft;
            swapTimer = 0f;
        }

        updateBounds();
    }

    public void draw(SpriteBatch batch) {

        Texture current;
        if (showLeft) {
            current = leftTexture;
        } else {
            current = rightTexture;
        }

        batch.draw(
            current,
            getX(),
            getY(),
            width,
            height
        );
    }

    public void disposeTextures() {
        if (leftTexture != null) leftTexture.dispose();
        if (rightTexture != null) rightTexture.dispose();
    }
}