package INF1009_P3_02.Scene;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

public class EndScene extends Scene {

    private final SceneManager sceneManager;
    private final int collisionCount;

    private Table root;
    private Table saveOverlay;
    private Table saveDialog;
    private boolean HandleSavedScore;
    private boolean scoreSaved;

    public EndScene(SceneManager sceneManager, int collisionCount) {
        this.sceneManager = sceneManager;
        this.collisionCount = collisionCount;
    }

    @Override
    protected void buildUI() {
        root = new Table();
        root.setFillParent(true);
        root.top().padTop(40);
        stage.addActor(root);

        Label title = new Label("GAME OVER", skin, "title");

        Label score = new Label("Score: " + collisionCount, skin);
        score.setFontScale(2.5f);

        TextButton restart = new TextButton("Restart", skin);
        TextButton leaderboard = new TextButton("Leaderboard", skin);
        TextButton exitToMenu = new TextButton("Exit to Menu", skin);

        restart.getLabel().setFontScale(1.5f);
        leaderboard.getLabel().setFontScale(1.5f);
        exitToMenu.getLabel().setFontScale(1.5f);

        root.add(title).pad(40).row();
        root.add(score).padTop(40).row();

        root.add(restart).width(520).height(90).padTop(40).row();
        root.add(leaderboard).width(520).height(90).padTop(15).row();
        root.add(exitToMenu).width(520).height(90).padTop(15).row();

        restart.addListener(e -> {
            if (!restart.isPressed()) return false;
            System.out.println("Button pressed: Restart");
            sceneManager.goToCustomization();
            return true;
        });

        leaderboard.addListener(e -> {
            if (!leaderboard.isPressed()) return false;
            System.out.println("Button pressed: Leaderboard");
            sceneManager.goToLeaderboard(SceneManager.LeaderboardSource.END_SCENE);
            return true;
        });

        exitToMenu.addListener(e -> {
            if (!exitToMenu.isPressed()) return false;
            System.out.println("Button pressed: Exit to Menu");
            sceneManager.goToMainMenu();
            return true;
        });

        if (!HandleSavedScore) {
            root.setVisible(false);
            createSaveScorePopup();
        }
    }

    private void createSaveScorePopup() {
        saveOverlay = new Table();
        saveOverlay.setFillParent(true);
        stage.addActor(saveOverlay);

        saveDialog = new Table(skin);
        saveDialog.pad(20);
        saveOverlay.add(saveDialog).expand().center();

        showSavePrompt();
    }

    private void showSavePrompt() {
        saveDialog.clear();

        Label prompt = new Label("Save your score?", skin);
        prompt.setFontScale(2f);
        prompt.setAlignment(Align.center);

        TextButton yesButton = new TextButton("Yes", skin);
        TextButton noButton = new TextButton("No", skin);

        yesButton.getLabel().setFontScale(1.5f);
        noButton.getLabel().setFontScale(1.5f);

        saveDialog.add(prompt).pad(20).colspan(2).row();
        saveDialog.add(yesButton).width(200).height(70).pad(10);
        saveDialog.add(noButton).width(200).height(70).pad(10);

        yesButton.addListener(e -> {
            if (!yesButton.isPressed()) return false;
            showNameInput();
            return true;
        });

        noButton.addListener(e -> {
            if (!noButton.isPressed()) return false;
            closeSavePopup();
            return true;
        });
    }

    private void showNameInput() {
        saveDialog.clear();

        Label nameLabel = new Label("Enter your name:", skin);
        nameLabel.setFontScale(1.8f);
        nameLabel.setAlignment(Align.center);

        final TextField nameField = new TextField("", skin);
        nameField.setMessageText("Player");

        TextButton saveButton = new TextButton("Save", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        saveButton.getLabel().setFontScale(1.5f);
        cancelButton.getLabel().setFontScale(1.5f);

        saveDialog.add(nameLabel).pad(10).colspan(2).row();
        saveDialog.add(nameField).width(360).pad(10).colspan(2).row();
        saveDialog.add(saveButton).width(180).height(60).pad(8);
        saveDialog.add(cancelButton).width(180).height(60).pad(8);

        saveButton.addListener(e -> {
            if (!saveButton.isPressed()) return false;
            if (scoreSaved) return true;

            String name = nameField.getText();
            scoreSaved = true;
            sceneManager.getLeaderboardManager().addEntry(name, collisionCount);
            System.out.println("Saved score to leaderboard for: " + name);
            closeSavePopup();
            return true;
        });

        cancelButton.addListener(e -> {
            if (!cancelButton.isPressed()) return false;
            closeSavePopup();
            return true;
        });
    }

    private void closeSavePopup() {
        HandleSavedScore = true;
        if (saveOverlay != null) {
            saveOverlay.remove();
            saveOverlay = null;
            saveDialog = null;
        }
        if (root != null) {
            root.setVisible(true);
        }
    }
}
