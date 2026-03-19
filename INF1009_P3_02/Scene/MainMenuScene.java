package INF1009_P3_02.Scene;

import INF1009_P3_02.SettingsData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainMenuScene extends Scene {

    private final SceneManager sceneManager;
    private final SettingsData settings;

    public MainMenuScene(SceneManager sceneManager, SettingsData settings) {
        this.sceneManager = sceneManager;
        this.settings = settings;
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/main_menu.jpg";
    }

    @Override
    protected void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center().padTop(150);
        stage.addActor(root);

        Table topRight = new Table();
        topRight.setFillParent(true);
        topRight.top().right().padTop(20).padRight(20);
        stage.addActor(topRight);

        float w = stage.getViewport().getWorldWidth() * 0.35f; // 35% of screen width
        float h = 80f;

        TextButton start = new TextButton("Start", skin);
        TextButton leaderboardBtn = new TextButton("Leaderboard", skin);
        TextButton settingsBtn = new TextButton("Settings", skin);
        TextButton helpBtn = new TextButton("?", skin);
        TextButton quit = new TextButton("Quit", skin);

        topRight.add(helpBtn).width(70f).height(70f);

        root.add(start).width(w).height(h).pad(18).row();
        root.add(leaderboardBtn).width(w).height(h).pad(18).row();
        root.add(settingsBtn).width(w).height(h).pad(18).row();
        root.add(quit).width(w).height(h).pad(18).row();

        start.getLabel().setFontScale(1.5f);
        leaderboardBtn.getLabel().setFontScale(1.5f);
        settingsBtn.getLabel().setFontScale(1.5f);
        helpBtn.getLabel().setFontScale(1.8f);
        quit.getLabel().setFontScale(1.5f);

        start.addListener(e -> {
            if (!start.isPressed()) return false;
            System.out.println("Button pressed: Start");
            sceneManager.goToCustomization();
            return true;
        });

        leaderboardBtn.addListener(e -> {
            if (!leaderboardBtn.isPressed()) return false;
            System.out.println("Button pressed: Leaderboard");
            sceneManager.goToLeaderboard(SceneManager.LeaderboardSource.MAIN_MENU);
            return true;
        });

        settingsBtn.addListener(e -> {
            if (!settingsBtn.isPressed()) return false;
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

        quit.addListener(e -> {
            if (!quit.isPressed()) return false;
            System.out.println("Button pressed: Quit");
            Gdx.app.exit();
            return true;
        });
    }
}
