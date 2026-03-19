package INF1009_P3_02.Scene;

import INF1009_P3_02.InputOutput.Brightness;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public abstract class Scene {

    protected Stage stage;
    protected Skin skin;
    protected SpriteBatch batch;
    protected Texture uiBackground;
protected Brightness brightness;
    protected ShapeRenderer shapeRenderer;

    /**
     * Returns the background image path for this scene.
     * Override to use a different background.
     */
    protected String getBackgroundPath() {
        return "backgrounds/default_bg.jpg";
    }

    public void show() {
        stage = new Stage(new com.badlogic.gdx.utils.viewport.ExtendViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui.craftacular/craftacular-ui.json")); // path must exist
        buildUI();

        batch = new SpriteBatch();
        uiBackground = new Texture(Gdx.files.internal(getBackgroundPath()));
    }

    protected abstract void buildUI();

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getCamera().combined);

        batch.begin();
        batch.draw(uiBackground, 0, 0,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight());
        batch.end();

        stage.draw();

        // Render brightness overlay if available
        renderBrightnessOverlay();
    }

    protected void renderBrightnessOverlay() {
        if (brightness != null && shapeRenderer != null) {
            brightness.render(shapeRenderer);
        }
    }

    public void setBrightness(Brightness brightness) {
        this.brightness = brightness;
    }

    public void setShapeRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void hide() { }

    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (batch != null) batch.dispose();
        if (uiBackground != null) uiBackground.dispose();
    }
}

