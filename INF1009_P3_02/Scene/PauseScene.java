package INF1009_P3_02.Scene;

import INF1009_P3_02.SettingsData;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class PauseScene extends Scene {

    private final SceneManager sceneManager;
    private final SettingsData settings;

    public PauseScene(SceneManager sceneManager, SettingsData settings) {
        this.sceneManager = sceneManager;
        this.settings = settings;
    }

    @Override
    protected void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table topRight = new Table();
        topRight.setFillParent(true);
        topRight.top().right().padTop(20).padRight(20);
        stage.addActor(topRight);

        Label title = new Label("PAUSED", skin, "title");

        TextButton resume = new TextButton("Resume", skin);
        TextButton restart = new TextButton("Restart", skin);
        TextButton settings = new TextButton("Settings", skin);
        TextButton helpBtn = new TextButton("?", skin);
        TextButton exitToMenu = new TextButton("Exit to Menu", skin);

        topRight.add(helpBtn).width(70f).height(70f);

        resume.getLabel().setFontScale(1.5f);
        restart.getLabel().setFontScale(1.5f);
        settings.getLabel().setFontScale(1.5f);
        helpBtn.getLabel().setFontScale(1.8f);
        exitToMenu.getLabel().setFontScale(1.5f);

        root.add(title).pad(20).row();
        root.add(resume).width(520).height(90).padTop(40).row();
        root.add(restart).width(520).height(90).padTop(15).row();
        root.add(settings).width(520).height(90).padTop(15).row();
        root.add(exitToMenu).width(520).height(90).padTop(15).row();

        resume.addListener(e -> {
            if (!resume.isPressed()) return false;
            System.out.println("Button pressed: Resume");
            sceneManager.resumeFromPause();
            return true;
        });

        restart.addListener(e -> {
            if (!restart.isPressed()) return false;
            System.out.println("Button pressed: Restart");
            sceneManager.goToCustomization();
            return true;
        });

        settings.addListener(e -> {
            if (!settings.isPressed()) return false;
            System.out.println("Button pressed: Settings");
            sceneManager.goToSettings();
            return true;
        });

        helpBtn.addListener(e -> {
            if (!helpBtn.isPressed()) return false;
            System.out.println("Button pressed: Help");
            sceneManager.goToHowToPlay();
            return true;
        });

        exitToMenu.addListener(e -> {
            if (!exitToMenu.isPressed()) return false;
            System.out.println("Button pressed: Exit to Menu");
            sceneManager.goToMainMenu();
            return true;
        });
    }
}
