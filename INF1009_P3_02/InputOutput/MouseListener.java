package INF1009_P3_02.InputOutput;

import INF1009_P3_02.ControlScheme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class MouseListener {
    private final InputOutputManager inputOutputManager;

    public MouseListener(InputOutputManager inputOutputManager) {
        this.inputOutputManager = inputOutputManager;
    }

    public void update(float dt) {
        if (inputOutputManager.getControlScheme() != ControlScheme.MOUSE) return;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float worldX = Gdx.input.getX();
            float worldY = Gdx.graphics.getHeight() - Gdx.input.getY();
            inputOutputManager.onMouseClick(worldX, worldY);
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            inputOutputManager.onEscapePressed();
        }
    }
}