package INF1009_P3_02.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {
    private final float worldW, worldH;
    private final float collisionSize;
    private final float drawSize;
    private Facing facing = Facing.DOWN;
    private Texture texUp, texDown, texLeft, texRight;
    private Texture texRecycleUp, texRecycleDown, texRecycleLeft, texRecycleRight;
    private Texture texTrashUp, texTrashDown, texTrashLeft, texTrashRight;
    private TextureRegion[] upFrames;
    private TextureRegion[] downFrames;
    private TextureRegion[] leftFrames;
    private TextureRegion[] rightFrames;
    private TextureRegion[] recycleUpFrames;
    private TextureRegion[] recycleDownFrames;
    private TextureRegion[] recycleLeftFrames;
    private TextureRegion[] recycleRightFrames;
    private TextureRegion[] trashUpFrames;
    private TextureRegion[] trashDownFrames;
    private TextureRegion[] trashLeftFrames;
    private TextureRegion[] trashRightFrames;
    private CarryState carryState = CarryState.NONE;
    private TrashType carriedTrashType = null;

    private float animTime = 0f;
    private float frameDuration = 0.18f; // speed of switching frames

    public Player(float x, float y, float speed,
                  float collisionSize, float drawSize,
                  float worldW, float worldH) {
        super(x, y, speed);
        this.collisionSize = collisionSize;
        this.drawSize = drawSize;
        this.worldW = worldW;
        this.worldH = worldH;
        updateBounds();
    }

    public float getCollisionSize() { return collisionSize; }

    public float getWorldW() { return worldW; }
    public float getWorldH() { return worldH; }
    public CarryState getCarryState() { return carryState; }
    public void setCarryState(CarryState s) { carryState = s; }

    // Keep clamp in Player (it's a Player/world rule)
    public void clampToWorld() {
        float half = collisionSize / 2f;
        setX(Math.max(half, Math.min(getX(), worldW - half)));
        setY(Math.max(half, Math.min(getY(), worldH - half)));
    }


    public void loadTextures() {
        //Normal
        texUp = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player/Up.png"));
        texDown = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player/Down.png"));
        texLeft = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player/Left.png"));
        texRight = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player/Right.png"));
        //with recycle bag
        texRecycleUp = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_recycle/Recycle_Up.png"));
        texRecycleDown = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_recycle/Recycle_Down.png"));
        texRecycleLeft = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_recycle/Recycle_Left.png"));
        texRecycleRight = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_recycle/Recycle_Right.png"));
        //with trash bag
        texTrashUp = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_trash/Trash_Up.png"));
        texTrashDown = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_trash/Trash_Down.png"));
        texTrashLeft = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_trash/Trash_Left.png"));
        texTrashRight = new Texture(Gdx.files.internal("PlayerMovementAvatar/Player_trash/Trash_Right.png"));

        texUp.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texDown.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texLeft.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texRight.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        texRecycleUp.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texRecycleDown.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texRecycleLeft.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texRecycleRight.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        texTrashUp.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texTrashDown.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texTrashLeft.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        texTrashRight.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        upFrames = split2Vertical(texUp);
        downFrames = split2Vertical(texDown);
        leftFrames = split2Vertical(texLeft);
        rightFrames = split2Vertical(texRight);

        recycleUpFrames = split2Vertical(texRecycleUp);
        recycleDownFrames = split2Vertical(texRecycleDown);
        recycleLeftFrames = split2Vertical(texRecycleLeft);
        recycleRightFrames = split2Vertical(texRecycleRight);

        trashUpFrames = split2Vertical(texTrashUp);
        trashDownFrames = split2Vertical(texTrashDown);
        trashLeftFrames = split2Vertical(texTrashLeft);
        trashRightFrames = split2Vertical(texTrashRight);
    }

    private TextureRegion[] split2Vertical(Texture tex) {
        int frameW = tex.getWidth();
        int frameH = tex.getHeight() / 2;
        return new TextureRegion[] {
            new TextureRegion(tex, 0, frameH, frameW, frameH), // top
            new TextureRegion(tex, 0, 0, frameW, frameH)       // bottom
        };
    }

    public void setFacing(Facing facing) {
        this.facing = facing;
    }

    private TextureRegion getCurrentFrame() {

        TextureRegion[] frames;

        if (carryState == CarryState.RECYCLE) {
            switch (facing) {
                case UP: frames = recycleUpFrames; break;
                case DOWN: frames = recycleDownFrames; break;
                case LEFT: frames = recycleLeftFrames; break;
                case RIGHT: frames = recycleRightFrames; break;
                default: frames = recycleDownFrames;
            }
        }
        else if (carryState == CarryState.TRASH) {
            switch (facing) {
                case UP: frames = trashUpFrames; break;
                case DOWN: frames = trashDownFrames; break;
                case LEFT: frames = trashLeftFrames; break;
                case RIGHT: frames = trashRightFrames; break;
                default: frames = trashDownFrames;
            }
        }
        else {
            switch (facing) {
                case UP: frames = upFrames; break;
                case DOWN: frames = downFrames; break;
                case LEFT: frames = leftFrames; break;
                case RIGHT: frames = rightFrames; break;
                default: frames = downFrames;
            }
        }

        if (frames == null) return null;

        int idx = ((int)(animTime / frameDuration)) % 2;
        return frames[idx];
    }

    public void setCarriedTrashType(TrashType type) {
        this.carriedTrashType = type;
    }

    public TrashType getCarriedTrashType() {
        return carriedTrashType;
    }

    public void clearCarriedTrash() {
        carriedTrashType = null;
    }

    public void onMove(float dt) {
        animTime += dt;
    }

    @Override
    protected void updateBounds() {
        bounds.set(getX() - collisionSize / 2f, getY() - collisionSize / 2f, collisionSize, collisionSize);
    }

    @Override
    public void update(float dt) {
        updateBounds();
    }


    public void draw(SpriteBatch batch) {
        TextureRegion frame = getCurrentFrame();
        if (frame == null) return;

        float drawH = drawSize;
        float aspect = (float) frame.getRegionWidth() / (float) frame.getRegionHeight();
        float drawW = drawH * aspect;

        batch.draw(frame,
            getX() - drawW / 2f,
            getY() - drawH / 2f,
            drawW,
            drawH
        );
    }

    public void disposeTextures() {
        if (texUp != null) texUp.dispose();
        if (texDown != null) texDown.dispose();
        if (texLeft != null) texLeft.dispose();
        if (texRight != null) texRight.dispose();

        if (texRecycleUp != null) texRecycleUp.dispose();
        if (texRecycleDown != null) texRecycleDown.dispose();
        if (texRecycleLeft != null) texRecycleLeft.dispose();
        if (texRecycleRight != null) texRecycleRight.dispose();

        if (texTrashUp != null) texTrashUp.dispose();
        if (texTrashDown != null) texTrashDown.dispose();
        if (texTrashLeft != null) texTrashLeft.dispose();
        if (texTrashRight != null) texTrashRight.dispose();
    }
}
