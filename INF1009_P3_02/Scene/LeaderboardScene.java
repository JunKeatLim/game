package INF1009_P3_02.Scene;

import INF1009_P3_02.LeaderboardEntry;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class LeaderboardScene extends Scene {

    private final SceneManager sceneManager;
    private Texture panelTexture;
    private Texture subtitlePanelTexture;
    private String selectedMode = "ALL";

    public LeaderboardScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    protected void buildUI() {

        Table root = new Table();
        root.setFillParent(true);
        root.pad(30f);
        stage.addActor(root);

        Label title = new Label("LEADERBOARD", skin, "title");
        Label subtitle = new Label(buildSubtitle(), skin);
        subtitle.setFontScale(1.0f);
        subtitle.setColor(0.08f, 0.12f, 0.18f, 1f);

        Table card = new Table();
        card.pad(22f);
        card.setBackground(createPanelDrawable(0.06f, 0.10f, 0.16f, 0.74f));

        Label rankHeader = new Label("Rank", skin);
        Label nameHeader = new Label("Player", skin);
        Label scoreHeader = new Label("Score", skin);

        rankHeader.setFontScale(1.5f);
        nameHeader.setFontScale(1.5f);
        scoreHeader.setFontScale(1.5f);
        rankHeader.setColor(0.75f, 0.92f, 1f, 1f);
        nameHeader.setColor(0.75f, 0.92f, 1f, 1f);
        scoreHeader.setColor(0.75f, 0.92f, 1f, 1f);
        rankHeader.setAlignment(Align.center);
        nameHeader.setAlignment(Align.center);
        scoreHeader.setAlignment(Align.center);

        TextButton back = new TextButton("Back", skin);
        back.getLabel().setFontScale(1.5f);

        TextButton allBtn = new TextButton("All", skin);
        TextButton easyBtn = new TextButton("Easy", skin);
        TextButton mediumBtn = new TextButton("Medium", skin);
        TextButton hardBtn = new TextButton("Hard", skin);
        allBtn.getLabel().setFontScale(1.15f);
        easyBtn.getLabel().setFontScale(1.15f);
        mediumBtn.getLabel().setFontScale(1.15f);
        hardBtn.getLabel().setFontScale(1.15f);
        applyModeButtonHighlight(allBtn, easyBtn, mediumBtn, hardBtn);

        root.add(title).padTop(10f).padBottom(8f).row();
        Table subtitleCard = new Table();
        subtitleCard.setBackground(createSubtitlePanelDrawable(1f, 1f, 1f, 0.62f));
        subtitleCard.pad(6f, 14f, 6f, 14f);
        subtitleCard.add(subtitle).center();
        root.add(subtitleCard).padBottom(14f).row();
        Table filters = new Table();
        filters.add(allBtn).width(150f).height(56f).pad(4f);
        filters.add(easyBtn).width(150f).height(56f).pad(4f);
        filters.add(mediumBtn).width(150f).height(56f).pad(4f);
        filters.add(hardBtn).width(150f).height(56f).pad(4f);
        root.add(filters).padBottom(10f).row();
        root.add(card).width(930f).padBottom(16f).row();

        List<LeaderboardEntry> entries = sceneManager.getLeaderboardManager().getEntriesByMode(selectedMode);
        int maxRows = Math.min(10, entries.size());

        card.add(rankHeader).width(180).padBottom(10f);
        card.add(nameHeader).width(430).padBottom(10f);
        card.add(scoreHeader).width(180).padBottom(10f).row();

        if (maxRows == 0) {
            Label emptyLabel = new Label("No scores saved for " + modeDisplayName(selectedMode) + " yet.", skin);
            emptyLabel.setFontScale(1.35f);
            emptyLabel.setAlignment(Align.center);
            emptyLabel.setColor(0.95f, 0.97f, 1f, 1f);
            card.add(emptyLabel).padTop(12f).colspan(3).row();
        } else {
            for (int i = 0; i < maxRows; i++) {
                LeaderboardEntry entry = entries.get(i);
                int rank = i + 1;

                Label rankLabel = new Label(String.valueOf(rank), skin);
                Label nameLabel = new Label(formatPlayerName(entry.name), skin);
                Label scoreLabel = new Label(String.valueOf(entry.score), skin);

                rankLabel.setFontScale(1.35f);
                nameLabel.setFontScale(1.35f);
                scoreLabel.setFontScale(1.35f);
                rankLabel.setAlignment(Align.center);
                nameLabel.setAlignment(Align.center);
                scoreLabel.setAlignment(Align.center);

                if (rank == 1) {
                    rankLabel.setColor(1f, 0.88f, 0.35f, 1f);
                    nameLabel.setColor(1f, 0.95f, 0.65f, 1f);
                    scoreLabel.setColor(1f, 0.88f, 0.35f, 1f);
                } else if (rank == 2) {
                    rankLabel.setColor(0.86f, 0.89f, 0.95f, 1f);
                    nameLabel.setColor(0.92f, 0.95f, 1f, 1f);
                    scoreLabel.setColor(0.86f, 0.89f, 0.95f, 1f);
                } else if (rank == 3) {
                    rankLabel.setColor(0.93f, 0.76f, 0.58f, 1f);
                    nameLabel.setColor(1f, 0.89f, 0.78f, 1f);
                    scoreLabel.setColor(0.93f, 0.76f, 0.58f, 1f);
                } else {
                    float rowColor = (rank % 2 == 0) ? 0.86f : 1.0f;
                    rankLabel.setColor(rowColor, rowColor, rowColor, 1f);
                    nameLabel.setColor(rowColor, rowColor, rowColor, 1f);
                    scoreLabel.setColor(rowColor, rowColor, rowColor, 1f);
                }

                card.add(rankLabel).pad(4f);
                card.add(nameLabel).pad(4f);
                card.add(scoreLabel).pad(4f).row();
            }
        }

        root.add(back).width(520).height(90);

        back.addListener(e -> {
            if (!back.isPressed()) return false;
            System.out.println("Button pressed: Back");
            sceneManager.EndLeaderboard();
            return true;
        });

        allBtn.addListener(e -> {
            if (!allBtn.isPressed()) return false;
            selectedMode = "ALL";
            rebuildUI();
            return true;
        });
        easyBtn.addListener(e -> {
            if (!easyBtn.isPressed()) return false;
            selectedMode = "EASY";
            rebuildUI();
            return true;
        });
        mediumBtn.addListener(e -> {
            if (!mediumBtn.isPressed()) return false;
            selectedMode = "MEDIUM";
            rebuildUI();
            return true;
        });
        hardBtn.addListener(e -> {
            if (!hardBtn.isPressed()) return false;
            selectedMode = "HARD";
            rebuildUI();
            return true;
        });
    }

    private TextureRegionDrawable createPanelDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        panelTexture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(panelTexture));
    }

    private TextureRegionDrawable createSubtitlePanelDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        subtitlePanelTexture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(subtitlePanelTexture));
    }

    private String formatPlayerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Player";
        }
        String trimmed = name.trim();
        if (trimmed.length() <= 14) {
            return trimmed;
        }
        return trimmed.substring(0, 14) + "...";
    }

    private String buildSubtitle() {
        return "Top recyclers - " + modeDisplayName(selectedMode);
    }

    private String modeDisplayName(String mode) {
        if ("EASY".equals(mode)) return "Easy";
        if ("MEDIUM".equals(mode)) return "Medium";
        if ("HARD".equals(mode)) return "Hard";
        return "All Modes";
    }

    private void applyModeButtonHighlight(TextButton allBtn, TextButton easyBtn, TextButton mediumBtn, TextButton hardBtn) {
        styleModeButton(allBtn, "ALL".equals(selectedMode));
        styleModeButton(easyBtn, "EASY".equals(selectedMode));
        styleModeButton(mediumBtn, "MEDIUM".equals(selectedMode));
        styleModeButton(hardBtn, "HARD".equals(selectedMode));
    }

    private void styleModeButton(TextButton button, boolean selected) {
        if (selected) {
            button.getLabel().setColor(1f, 0.95f, 0.45f, 1f);
        } else {
            button.getLabel().setColor(1f, 1f, 1f, 1f);
        }
    }

    private void rebuildUI() {
        if (stage != null) {
            stage.clear();
        }
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
        if (subtitlePanelTexture != null) {
            subtitlePanelTexture.dispose();
            subtitlePanelTexture = null;
        }
        buildUI();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
        if (subtitlePanelTexture != null) {
            subtitlePanelTexture.dispose();
            subtitlePanelTexture = null;
        }
    }
}