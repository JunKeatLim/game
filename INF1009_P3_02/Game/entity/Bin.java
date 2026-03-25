package INF1009_P3_02.Game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import INF1009_P3_02.Engine.entity.Entity;
import INF1009_P3_02.Game.enumFolder.BinType;

import java.util.EnumMap;
import java.util.Map;

public abstract class Bin extends Entity {
    private final float boundsW, boundsH;
    private final float drawH;              // visual size (height)
    private final BinType type;

    private static final Map<BinType, Texture> TEX = new EnumMap<>(BinType.class);

    public Bin(float x, float y,
                    float boundsW, float boundsH,
                    float drawH,
                    BinType type) {
        super(x, y, 0);
        this.boundsW = boundsW; 
        this.boundsH = boundsH;
        this.drawH = drawH;
        this.type = type;

        ensureTextureLoaded();
        updateBounds();
    }

    protected abstract String getTexturePath();

    private void ensureTextureLoaded() {
        if (TEX.containsKey(type)) return;
        Texture t = new Texture(Gdx.files.internal(getTexturePath()));
        t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TEX.put(type, t);
    }

    public BinType getType() { return type; }

    @Override
    public void update(float dt) {
        updateBounds();
    }

    @Override
    protected void updateBounds() {
        bounds.set(getX(), getY(), boundsW, boundsH);
    }

    @Override
    public void draw(SpriteBatch batch) {
        Texture t = TEX.get(type);
        if (t == null) return;

        float aspect = (float) t.getWidth() / (float) t.getHeight();
        float drawW = drawH * aspect;

        float cx = getX() + boundsW / 2f; 
        float cy = getY() + boundsH / 2f;
        
        batch.draw(t, cx - drawW / 2f, cy - drawH / 2f, drawW, drawH);
    }

    public static void disposeAllTextures() {
        for (Texture t : TEX.values()) t.dispose();
        TEX.clear();
    }
}