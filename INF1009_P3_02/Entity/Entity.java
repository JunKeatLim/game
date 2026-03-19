package INF1009_P3_02.Entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public abstract class Entity {
    private float x;
    private float y;
    private float speed;
    protected Rectangle bounds = new Rectangle();

    public Entity(){
        this.x = 0;
        this.y = 0;
        this.speed = 0;
    }

    public Entity(float x, float y, float speed){
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public float getX() {

        return x;
    }
    public void setX(float x) {

        this.x = x;
    }

    public float getY() {

        return y;
    }
    public void setY(float y) {

        this.y = y;
    }
    public float getSpeed() {

        return speed;
    }
    public void setSpeed(float speed) {

        this.speed = speed;
    }

    public void draw(SpriteBatch batch) {

    }
    public Rectangle getBounds() {
        return bounds;
    }
    protected abstract void updateBounds();
    public abstract void update(float dt);
}
