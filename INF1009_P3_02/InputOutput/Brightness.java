package INF1009_P3_02.InputOutput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Brightness {
    private float brightnessLevel;
    private float previousLevel = -1f; // Track previous level to avoid duplicate logs

    public Brightness() {
        this.brightnessLevel = 1.0f;
    }

    public void setLevel(float level) {
        float newLevel = Math.max(0, Math.min(1, level));
        
        // Only log if the value actually changed (rounded to 2 decimals to avoid floating point noise)
        if (Math.abs(newLevel - previousLevel) > 0.01f) {
            previousLevel = newLevel;
            System.out.println("Brightness changed to: " + String.format("%.2f", newLevel));
        }
        
        this.brightnessLevel = newLevel;
    }

    public float getLevel() {
        return brightnessLevel;
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (brightnessLevel >= 1.0f) return;

        float alpha = 1.0f - brightnessLevel;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
