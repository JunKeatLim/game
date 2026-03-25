package INF1009_P3_02.Game.scene;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import INF1009_P3_02.Engine.scene.Scene;
import INF1009_P3_02.Game.config.SettingsData;

public class SettingsScene extends Scene {

    private final SceneManager sceneManager;
    private final SettingsData settings;

    public SettingsScene(SceneManager sceneManager, SettingsData settings) {
        this.sceneManager = sceneManager;
        this.settings = settings;
    }

    @Override
    protected void buildUI() {

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // ===== TITLE =====
        Label title = new Label("SETTINGS", skin, "title");

        // ===== VOLUME =====
        Label volLabel = new Label("Volume", skin);
        volLabel.setFontScale(1.8f);

        Slider volSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volSlider.setValue(settings.volume);
        volSlider.setScaleY(1.5f);

        // ===== BRIGHTNESS =====
        Label brightLabel = new Label("Brightness", skin);
        brightLabel.setFontScale(1.8f);

        Slider brightSlider = new Slider(0f, 1f, 0.01f, false, skin);
        brightSlider.setValue(settings.brightness);
        brightSlider.setScaleY(1.5f);

        // ===== BUTTONS =====
        TextButton controlsBtn = new TextButton("Controls Setting", skin);
        TextButton backBtn = new TextButton("Back", skin);

        controlsBtn.getLabel().setFontScale(1.4f);
        backBtn.getLabel().setFontScale(1.4f);

        // ===== UI LAYOUT =====
        root.add(title).padBottom(30).row();

        root.add(volLabel).padTop(10).row();
        root.add(volSlider).width(650).height(60).padBottom(20).row();

        root.add(brightLabel).padTop(10).row();
        root.add(brightSlider).width(650).height(60).padBottom(30).row();

        root.add(controlsBtn).width(500).height(80).padTop(10).row();
        root.add(backBtn).width(500).height(80).padTop(15).row();

        // ===== LISTENERS =====
        volSlider.addListener(e -> {
            settings.volume = volSlider.getValue();

            if (!volSlider.isDragging() && sceneManager.getInputOutputManager() != null) {
                sceneManager.getInputOutputManager()
                        .getSpeaker()
                        .setVolume(settings.volume);
            }
            return false;
        });

        brightSlider.addListener(e -> {
            settings.brightness = brightSlider.getValue();

            if (!brightSlider.isDragging() && sceneManager.getInputOutputManager() != null) {
                sceneManager.getInputOutputManager()
                        .getBrightness()
                        .setLevel(settings.brightness);
            }
            return false;
        });

        controlsBtn.addListener(e -> {
            if (!controlsBtn.isPressed()) return false;

            System.out.println("Button pressed: Controls Setting");
            sceneManager.goToControlSettings();
            return true;
        });

        backBtn.addListener(e -> {
            if (!backBtn.isPressed()) return false;

            System.out.println("Button pressed: Back");

            // Apply all settings changes (control scheme, volume, brightness)
            if (sceneManager.getInputOutputManager() != null) {
                sceneManager.getInputOutputManager().applySettingsChanges();
            }

            Scene source = sceneManager.getSettingsSourceScene();
            sceneManager.clearSettingsSourceScene();

            if (source != null) {
                sceneManager.switchToPrevious(source);
            } else {
                sceneManager.goToMainMenu();
            }

            return true;
        });
    }
}