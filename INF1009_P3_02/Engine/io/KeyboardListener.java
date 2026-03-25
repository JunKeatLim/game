package INF1009_P3_02.Engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import INF1009_P3_02.Game.config.ControlScheme;

public class KeyboardListener {
    private final InputOutputManager inputOutputManager;

    public KeyboardListener(InputOutputManager inputOutputManager) {
        this.inputOutputManager = inputOutputManager;
    }

    public void update(float dt) {
        ControlScheme scheme = inputOutputManager.getControlScheme();
        switch (scheme){
            case WASD:
                // get keys input and send back to io manager
                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    inputOutputManager.onKeyDetected('W', dt);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    inputOutputManager.onKeyDetected('A', dt);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    inputOutputManager.onKeyDetected('S', dt);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    inputOutputManager.onKeyDetected('D', dt);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    inputOutputManager.onEscapePressed();
                }
                break;
            case ARROW_KEYS:
                // get keys input and send back to io manager
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    inputOutputManager.onKeyDetected('W', dt);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    inputOutputManager.onKeyDetected('A', dt);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    inputOutputManager.onKeyDetected('S', dt);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    inputOutputManager.onKeyDetected('D', dt);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    inputOutputManager.onEscapePressed();
                }
                break;
            case MOUSE:
                // For mouse control, only handle ESC key
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    inputOutputManager.onEscapePressed();
                }
                break;
        }

    }
}
