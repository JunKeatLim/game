package INF1009_P3_02.Scene;

import INF1009_P3_02.LeaderboardEntry;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class LeaderboardScene extends Scene {

    private final SceneManager sceneManager;

    public LeaderboardScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    protected void buildUI() {

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(40);
        stage.addActor(root);

        Label title = new Label("LEADERBOARD", skin, "title");

        Label rankHeader = new Label("Rank", skin);
        Label nameHeader = new Label("Player", skin);
        Label scoreHeader = new Label("Score", skin);

        rankHeader.setFontScale(1.8f);
        nameHeader.setFontScale(1.8f);
        scoreHeader.setFontScale(1.8f);

        rankHeader.setAlignment(Align.center);
        nameHeader.setAlignment(Align.center);
        scoreHeader.setAlignment(Align.center);

        TextButton back = new TextButton("Back", skin);
        back.getLabel().setFontScale(1.5f);

        root.add(title).pad(20).colspan(3).row();

        root.add(rankHeader).width(200).pad(10);
        root.add(nameHeader).width(400).pad(10);
        root.add(scoreHeader).width(200).pad(10).row();

        List<LeaderboardEntry> entries = sceneManager.getLeaderboardManager().getEntries();

        if (entries.isEmpty()) {
            Label emptyLabel = new Label("No scores saved yet.", skin);
            emptyLabel.setFontScale(1.5f);
            emptyLabel.setAlignment(Align.center);
            root.add(emptyLabel).padTop(20).colspan(3).row();
        } else {
            int rank = 1;
            for (LeaderboardEntry entry : entries) {
                Label rankLabel = new Label(String.valueOf(rank), skin);
                Label nameLabel = new Label(entry.name, skin);
                Label scoreLabel = new Label(String.valueOf(entry.score), skin);

                rankLabel.setFontScale(1.5f);
                nameLabel.setFontScale(1.5f);
                scoreLabel.setFontScale(1.5f);

                rankLabel.setAlignment(Align.center);
                nameLabel.setAlignment(Align.center);
                scoreLabel.setAlignment(Align.center);

                root.add(rankLabel).pad(8);
                root.add(nameLabel).pad(8);
                root.add(scoreLabel).pad(8).row();

                rank++;
            }
        }

        root.add(back).width(520).height(90).padTop(25).colspan(3);

        back.addListener(e -> {
            if (!back.isPressed()) return false;
            System.out.println("Button pressed: Back");
            sceneManager.EndLeaderboard();
            return true;
        });
    }
}