package INF1009_P3_02.Game.scene;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import INF1009_P3_02.Engine.scene.Scene;
import INF1009_P3_02.Game.config.ControlScheme;
import INF1009_P3_02.Game.config.SettingsData;

public class ControlSettingsScene extends Scene {

    private final SceneManager sceneManager;
    private final SettingsData settings;
    private ControlScheme previousControlScheme; // Track previous scheme to avoid duplicate logs

    public ControlSettingsScene(SceneManager sceneManager, SettingsData settings) {
        this.sceneManager = sceneManager;
        this.settings = settings;
        this.previousControlScheme = settings.controlScheme;
    }

    @Override
    protected void buildUI() {
        // Reset previous scheme on each build to avoid stale comparisons
        previousControlScheme = settings.controlScheme;
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("CONTROL SETTINGS", skin, "title");

        Label controlLabel = new Label("Control Scheme", skin);
        controlLabel.setFontScale(2f);

        // Create dropdown for control schemes
        SelectBox<ControlScheme> controlSchemeBox = new SelectBox<>(skin);
        controlSchemeBox.setItems(ControlScheme.values());
        controlSchemeBox.setSelected(settings.controlScheme);
        controlSchemeBox.setMaxListCount(ControlScheme.values().length);
        controlSchemeBox.getStyle().font.getData().setScale(1.5f);
        controlSchemeBox.getStyle().listStyle.font.getData().setScale(1.5f);
        controlSchemeBox.getStyle().listStyle.background = skin.getDrawable("button-hover");
        controlSchemeBox.getStyle().listStyle.selection = skin.getDrawable("button-disabled");

        TextButton back = new TextButton("Back", skin);
        back.getLabel().setFontScale(1.5f);

        // Layout
        root.add(title).pad(20).row();
        root.add(controlLabel).padTop(40).row();
        root.add(controlSchemeBox).width(520).height(80).pad(20).row();
        root.add(back).width(520).height(90).padTop(30).row();

        // Live update control scheme - only log on actual selection change
        controlSchemeBox.addListener(e -> {
            ControlScheme newScheme = controlSchemeBox.getSelected();
            if (!newScheme.equals(previousControlScheme)) {
                previousControlScheme = newScheme;
                settings.controlScheme = newScheme;
                System.out.println("Control scheme changed to: " + newScheme);
            }
            return false;
        });

        // Navigation
        back.addListener(e -> {
            if (!back.isPressed()) return false;
            System.out.println("Button pressed: Back");
            sceneManager.goToSettings();
            return true;
        });
    }
}
