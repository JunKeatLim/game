package INF1009_P3_02.InputOutput;

import INF1009_P3_02.ControlScheme;
import INF1009_P3_02.Movement.MovementManager;
import INF1009_P3_02.SettingsData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class InputOutputManager {
    private final KeyboardListener keyboardListener;
    private final MouseListener mouseListener;
    private final MovementManager movementManager;
    private final Speaker speaker;
    private final Brightness brightness;
    private final ShapeRenderer shapeRenderer;
    private final SettingsData settings;
    private EscapeListener escapeListener;
    private ControlScheme controlScheme;

    public InputOutputManager(SettingsData settings, MovementManager movementManager) {
        this.settings = settings;
        this.movementManager = movementManager;
        this.keyboardListener = new KeyboardListener(this);
        this.mouseListener = new MouseListener(this);
        this.speaker = new Speaker();
        this.brightness = new Brightness();
        this.shapeRenderer = new ShapeRenderer();

        // Load audio content and sync settings
        this.speaker.loadContent();
        syncSettingsToOutputs();
    }

    public void setControlScheme(ControlScheme scheme) {
        this.controlScheme = scheme;
    }

    public ControlScheme getControlScheme() {
        return controlScheme;
    }

    private void syncSettingsToOutputs() {
        speaker.setVolume(settings.volume);
        brightness.setLevel(settings.brightness);
        this.controlScheme = settings.controlScheme;
    }

    public void applySettingsChanges() {
        syncSettingsToOutputs();
    }

    public void update(float dt) {
        keyboardListener.update(dt);
        mouseListener.update(dt);
    }

    // Called by KeyboardListener for movement keys
    public void onKeyDetected(char key, float dt) {
        if (movementManager != null) {
            movementManager.onKeyInput(key, dt);
        }
    }

    public void onMouseClick(float worldX, float worldY) {
        if (movementManager != null) {
            movementManager.onMouseTarget(worldX, worldY);
        }
    }

    // Called by KeyboardListener for ESC key
    public void onEscapePressed() {
        if (escapeListener != null) {
            escapeListener.onEscapePressed();
        }
    }

    // Set the listener that will handle ESC events
    public void setEscapeListener(EscapeListener listener) {
        this.escapeListener = listener;
    }

    public void playMenuMusic() {
        speaker.playMenuMusic();
    }

    public void playGameMusic() {
        speaker.playGameMusic();
    }

    public void stopMusic() {
        speaker.stopMusic();
    }

    public void playPickupSound() {
        speaker.playPickupSound();
    }

    public void playCorrectSound() {
        speaker.playCorrectSound();
    }

    public void playWrongSound() {
        speaker.playWrongSound();
    }

    public void playGameEndSound() {
        speaker.stopMusic();
        speaker.playGameEndSound();
    }

    public void playCountdownTick() {
        speaker.playCountdownTick();
    }

    public void playCountdownRecycle() {
        speaker.playCountdownRecycle();
    }

    public void playCountdownNumber(int number) {
        speaker.playCountdownNumber(number);
    }

    public void playUiWhoosh() {
        speaker.playUiWhoosh();
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public Brightness getBrightness() {
        return brightness;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public void dispose() {
        if (speaker != null) speaker.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    // Interface for ESC key handling
    public interface EscapeListener {
        void onEscapePressed();
    }
}