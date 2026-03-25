package INF1009_P3_02.Game.scene;

import INF1009_P3_02.GameMaster;
import INF1009_P3_02.Engine.io.InputOutputManager;
import INF1009_P3_02.Engine.logging.GameEngineLogger;
import INF1009_P3_02.Engine.scene.Scene;
import INF1009_P3_02.Game.Leaderboard.LeaderboardManager;
import INF1009_P3_02.Game.config.SettingsData;
import INF1009_P3_02.Game.config.SimulationConfig;

import java.text.SimpleDateFormat;
import java.util.Date;



public class SceneManager {

    public enum LeaderboardSource {
        MAIN_MENU,
        END_SCENE
    }

    // Shared data owned by the manager
    private final SettingsData settings = new SettingsData();
    private SimulationConfig currentConfig;
    private final GameEngineLogger logger;
    private final LeaderboardManager leaderboardManager = new LeaderboardManager("leaderboard.txt");

    private Scene settingsSourceScene;
    private Scene howToPlaySourceScene;

    // Scenes we reuse
    private final MainMenuScene mainMenuScene;
    private final CustomizationScene customizationScene;
    private final SettingsScene settingsScene;
    private final ControlSettingsScene controlSettingsScene;
    private final HowToPlayScene howToPlayScene;
    private final PauseScene pauseScene;
    private final LeaderboardScene leaderboardScene;

    // Scenes created fresh when needed
    private GameScene simulationScene;
    private EndScene endScene;

    // Current scene
    private Scene currentScene;

    // Optional external managers
    private InputOutputManager inputOutputManager;
    private GameMaster gameMaster;
    private LeaderboardSource leaderboardSource = LeaderboardSource.MAIN_MENU;

    public SceneManager() {
        // Create reusable scenes once
        logger = new GameEngineLogger("engine_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt");
        mainMenuScene = new MainMenuScene(this, settings);
        customizationScene = new CustomizationScene(this, settings);
        settingsScene = new SettingsScene(this, settings);
        controlSettingsScene = new ControlSettingsScene(this, settings);
        howToPlayScene = new HowToPlayScene(this);
        pauseScene = new PauseScene(this, settings);
        leaderboardScene = new LeaderboardScene(this);
        // Start at main menu
        switchToScene(mainMenuScene);
    }

    // =========================================
    // Core switching
    // =========================================
    private void switchToScene(Scene newScene) {
        if (newScene == null) return;

        if (currentScene != null && currentScene != newScene) {
            currentScene.hide();
            if (inputOutputManager != null) {
                inputOutputManager.playUiWhoosh();
            }

            // prevent stage leaks
            currentScene.clearStage();
        }

        currentScene = newScene;
        currentScene.show();
        updateSceneBrightness(currentScene);
    }

    private void updateSceneBrightness(Scene scene) {
        if (scene != null && inputOutputManager != null) {
            scene.setBrightness(inputOutputManager.getBrightness());
            scene.setShapeRenderer(inputOutputManager.getShapeRenderer());
        }
    }

    private void playMenuMusic() {
        if (inputOutputManager != null) {
            inputOutputManager.getSpeaker().setVolume(settings.volume);
            inputOutputManager.playMenuMusic();
        }
    }

    private void playGameMusic() {
        if (inputOutputManager != null) {
            inputOutputManager.getSpeaker().setVolume(settings.volume * 0.5f);
            inputOutputManager.playGameMusic();
        }
    }

    // =========================================
    // Navigation API
    // =========================================

    public void goToMainMenu() {
        switchToScene(mainMenuScene);
        playMenuMusic();
    }

    public void goToCustomization() {
        switchToScene(customizationScene);
        playMenuMusic();
    }

    public void goToSettings() {
        if (!(currentScene instanceof ControlSettingsScene)) {
            setSettingsSourceScene(currentScene);
        }
        switchToScene(settingsScene);
    }

    public void goToControlSettings() {
        switchToScene(controlSettingsScene);
    }

    public void goToHowToPlay() {
        howToPlaySourceScene = currentScene;
        switchToScene(howToPlayScene);
    }

    public void startSimulation(SimulationConfig config) {
        logger.info("Control scheme selected: " + getSettings().controlScheme);
        this.currentConfig = config;

        // Create fresh simulation every time
        this.simulationScene = new GameScene(this, settings, config, logger);
        switchToScene(simulationScene);
        playGameMusic();
    }

    /** Called by SimulationScene when time is up, etc. */
    public void goToEndScene(int collisionCount) {
        if (inputOutputManager != null) {
            inputOutputManager.playGameEndSound();
        }
        endScene = new EndScene(this, collisionCount);
        switchToScene(endScene);
    }

    public void goToPauseScene() {
        logger.info("Game paused");
        switchToScene(pauseScene);
    }

    public void resumeFromPause() {
        logger.info("Game resumed");
        if (simulationScene != null) {
            switchToScene(simulationScene);
            simulationScene.startResumeCountdown(3f);
            playGameMusic();
        } else {
            switchToScene(mainMenuScene);
            playMenuMusic();
        }
    }

    public void restartSimulation() {
        if (currentConfig != null) {
            startSimulation(currentConfig);
        } else {
            switchToScene(customizationScene);
        }
    }

    // Navigate to the leaderboard screen
    public void goToLeaderboard() {
        goToLeaderboard(LeaderboardSource.MAIN_MENU);
    }

    public void goToLeaderboard(LeaderboardSource source) {
        leaderboardSource = source;
        switchToScene(leaderboardScene);
        playMenuMusic();
    }

    public void EndLeaderboard() {
        if (leaderboardSource == LeaderboardSource.END_SCENE && endScene != null) {
            switchToScene(endScene);
            return;
        }

        switchToScene(mainMenuScene);
        playMenuMusic();
    }

    // =========================================
    // Helper for SettingsScene to know where to return after "Back"
    // =========================================
    public void switchToPrevious(Scene scene) {
        switchToScene(scene);
    }

    public void setHowToPlaySourceScene(Scene howToPlaySourceScene) {
        this.howToPlaySourceScene = howToPlaySourceScene;
    }

    public Scene getHowToPlaySourceScene() {
        return howToPlaySourceScene;
    }

    public void clearHowToPlaySourceScene() {
        this.howToPlaySourceScene = null;
    }

    public void setSettingsSourceScene(Scene settingsSourceScene) {
        this.settingsSourceScene = settingsSourceScene;
    }

    public Scene getSettingsSourceScene() {
        return settingsSourceScene;
    }

    public void clearSettingsSourceScene() {
        this.settingsSourceScene = null;
    }

    // =========================================
    // Getters / Setters used by scenes / GameMaster
    // =========================================

    public Scene getCurrentScene() {
        return currentScene;
    }

    public SettingsData getSettings() {
        return settings;
    }

    public SimulationConfig getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(SimulationConfig config) {
        this.currentConfig = config;
    }

    public InputOutputManager getInputOutputManager() {
        return inputOutputManager;
    }

    public void setInputOutputManager(InputOutputManager inputOutputManager) {
        this.inputOutputManager = inputOutputManager;

        if (inputOutputManager != null) {
            inputOutputManager.getSpeaker().setVolume(settings.volume);
            inputOutputManager.getBrightness().setLevel(settings.brightness);
            inputOutputManager.playMenuMusic();
            updateSceneBrightness(currentScene);
        }
    }

    public void setGameMaster(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }

    public GameMaster getGameMaster() {
        return gameMaster;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public GameEngineLogger getLogger() {
        return logger;
    }

    // =========================================
    // Forward lifecycle from GameMaster
    // =========================================

    public void update(float delta) {
        if (currentScene != null) currentScene.update(delta);
    }

    public void render() {
        if (currentScene != null) currentScene.render();
    }

    public void resize(int width, int height) {
        if (currentScene != null) currentScene.resize(width, height);
    }

    public void dispose() {
        if (currentScene != null) {
            currentScene.hide();
            currentScene.dispose();
            currentScene = null;
        }
        logger.close();
    }
}