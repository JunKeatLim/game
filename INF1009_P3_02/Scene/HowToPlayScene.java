package INF1009_P3_02.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class HowToPlayScene extends Scene {

    private static final float CONTENT_WIDTH = 980f;

    private final SceneManager sceneManager;

    private Table content;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private int currentPage = 0;
    private boolean prevHandled = false;
    private boolean nextHandled = false;
    private Texture prevIconTexture;
    private Texture nextIconTexture;
    private Texture paperBinTexture;
    private Texture plasticBinTexture;
    private Texture electronicBinTexture;
    private Texture trashBinTexture;
    private Texture paperItemTexture;
    private Texture plasticItemTexture;
    private Texture electronicItemTexture;
    private Texture trashItemTexture;
    private Texture helpPanelTexture;
    private Texture hintPanelTexture;

    public HowToPlayScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    protected void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.pad(32f);
        stage.addActor(root);

        content = new Table();
        content.defaults().left().top();

        // Semi-transparent panel behind text for readability.
        Table contentCard = new Table();
        contentCard.pad(30f);
        contentCard.setBackground(createHelpPanelDrawable());
        contentCard.add(content).width(CONTENT_WIDTH).left().top();

        Label title = new Label("HOW TO PLAY", skin, "title");

        TextButton back = new TextButton("Back", skin);
        back.getLabel().setFontScale(1.4f);

        // Custom bin icons for previous / next page
        prevIconTexture = new Texture(Gdx.files.internal("bins_image/HowTo_prevBin.png"));
        nextIconTexture = new Texture(Gdx.files.internal("bins_image/HowTo_nextBin.png"));
        paperBinTexture = new Texture(Gdx.files.internal("bins_image/paper_Bin.png"));
        plasticBinTexture = new Texture(Gdx.files.internal("bins_image/plastic_Bin.png"));
        electronicBinTexture = new Texture(Gdx.files.internal("bins_image/electronic_Bin.png"));
        trashBinTexture = new Texture(Gdx.files.internal("bins_image/trash_Bin.png"));
        paperItemTexture = new Texture(Gdx.files.internal("trash_recyclable/paper.png"));
        plasticItemTexture = new Texture(Gdx.files.internal("trash_recyclable/plastic.png"));
        electronicItemTexture = new Texture(Gdx.files.internal("trash_recyclable/electronic.png"));
        trashItemTexture = new Texture(Gdx.files.internal("trash_recyclable/trashbag.png"));

        TextureRegionDrawable prevDrawable = new TextureRegionDrawable(new TextureRegion(prevIconTexture));
        TextureRegionDrawable nextDrawable = new TextureRegionDrawable(new TextureRegion(nextIconTexture));

        ImageButton.ImageButtonStyle prevStyle = new ImageButton.ImageButtonStyle();
        prevStyle.imageUp = prevDrawable;
        prevButton = new ImageButton(prevStyle);

        ImageButton.ImageButtonStyle nextStyle = new ImageButton.ImageButtonStyle();
        nextStyle.imageUp = nextDrawable;
        nextButton = new ImageButton(nextStyle);

        Label pageHint = new Label("Click the trash can to flip pages", skin);
        pageHint.setFontScale(1.12f);
        pageHint.setAlignment(Align.center);
        pageHint.setColor(1f, 1f, 1f, 1f);

        Table hintCard = new Table();
        hintCard.setBackground(createHintPanelDrawable());
        hintCard.pad(8f, 18f, 8f, 18f);
        hintCard.add(pageHint).center();

        root.add(title).padBottom(24f).row();
        root.add(contentCard).expand().top().row();

        Table navRow = new Table();
        navRow.add(prevButton).width(64f).height(64f).padRight(20f);
        navRow.add(hintCard).expandX().center();
        navRow.add(nextButton).width(64f).height(64f).padLeft(20f);
        root.add(navRow).padTop(12f).padBottom(50f).row();

        root.add(back).width(420f).height(80f).padTop(10f).padBottom(52f);

        back.addListener(e -> {
            if (!back.isPressed()) return false;
            System.out.println("Button pressed: Back");
            Scene source = sceneManager.getHowToPlaySourceScene();
            sceneManager.clearHowToPlaySourceScene();
            if (source != null) {
                sceneManager.switchToPrevious(source);
            } else {
                sceneManager.goToMainMenu();
            }
            return true;
        });

        prevButton.addListener(e -> {
            if (!prevButton.isPressed()) {
                prevHandled = false;
                return false;
            }
            if (prevHandled) return false;
            prevHandled = true;
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
            return true;
        });

        nextButton.addListener(e -> {
            if (!nextButton.isPressed()) {
                nextHandled = false;
                return false;
            }
            if (nextHandled) return false;
            nextHandled = true;
            if (currentPage < 1) {
                currentPage++;
                updatePage();
            }
            return true;
        });

        updatePage();
    }

    private TextureRegionDrawable createHelpPanelDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.06f, 0.09f, 0.13f, 0.74f);
        pixmap.fill();
        helpPanelTexture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(helpPanelTexture));
    }

    private TextureRegionDrawable createHintPanelDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.03f, 0.05f, 0.08f, 0.82f);
        pixmap.fill();
        hintPanelTexture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(hintPanelTexture));
    }

    private void styleBodyLabel(Label label) {
        label.setWrap(true);
        label.setFontScale(0.98f);
        label.setColor(0.96f, 0.98f, 1f, 1f);
        label.setAlignment(Align.topLeft);
    }

    private void styleHeaderLabel(Label label) {
        label.setFontScale(1.25f);
        label.setColor(1f, 0.94f, 0.72f, 1f);
    }

    private void styleSubHeaderLabel(Label label) {
        label.setFontScale(1.12f);
        label.setColor(0.86f, 0.94f, 1f, 1f);
    }

    private Table createBinGuideRow(Texture itemTexture, Texture binTexture, String title, float r, float g, float b) {
        Table row = new Table();
        row.defaults().left().center();

        Image binImage = new Image(new TextureRegionDrawable(new TextureRegion(binTexture)));

        Label titleLabel = new Label(title, skin);
        styleSubHeaderLabel(titleLabel);
        titleLabel.setColor(r, g, b, 1f);

        Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(itemTexture)));

        row.add(binImage).width(180f).height(100f);
        row.add(titleLabel);
        row.add(itemImage).width(180f).height(100f);

        return row;
    }

    private void updatePage() {
        content.clearChildren();

        if (currentPage == 0) {
            Label header = new Label("Your Mission", skin);
            styleHeaderLabel(header);
            header.setAlignment(Align.center);
            Label missionText = new Label(
                "Sort as much recyclables and trash before time runs out.\n\n" +
                "1) Pick up an item on the map.\n" +
                "2) Identify its type and move to the matching bin.\n" +
                "3) Deposit the item to score points.\n" +
                "4) Sort out as much items as you can before time ends.",
                skin
            );
            styleBodyLabel(missionText);

            Label scoringHeader = new Label("Scoring:", skin);
            styleSubHeaderLabel(scoringHeader);

            Label positiveScore = new Label("- Correct bin deposit: +3 points.", skin);
            styleBodyLabel(positiveScore);
            positiveScore.setColor(0.58f, 1f, 0.6f, 1f);

            Label OtherpositiveScore = new Label("- Recyclable but Wrong bin deposit: +1 point.", skin);
            styleBodyLabel(OtherpositiveScore);
            OtherpositiveScore.setColor(0.58f, 1f, 0.6f, 1f);

            Label negativeScore = new Label("- Wrong bin deposit: -1 point.", skin);
            styleBodyLabel(negativeScore);
            negativeScore.setColor(1f, 0.55f, 0.55f, 1f);

            Label collisionPenalty = new Label("- Collisions with the bot will cause you drop your item.", skin);
            styleBodyLabel(collisionPenalty);
            collisionPenalty.setColor(1f, 0.86f, 0.55f, 1f);

            content.add(header).width(CONTENT_WIDTH).center().padBottom(12f).row();
            content.add(missionText).width(CONTENT_WIDTH).left().padBottom(12f).row();
            content.add(scoringHeader).width(CONTENT_WIDTH).left().padBottom(6f).row();
            content.add(positiveScore).width(CONTENT_WIDTH).left().padBottom(4f).row();
            content.add(OtherpositiveScore).width(CONTENT_WIDTH).left().padBottom(4f).row();
            content.add(negativeScore).width(CONTENT_WIDTH).left().padBottom(4f).row();
            content.add(collisionPenalty).width(CONTENT_WIDTH).left();
            content.row();
        } else if (currentPage == 1) {
            Label header = new Label("How to Bin It", skin);
            styleHeaderLabel(header);
            header.setAlignment(Align.center);

            Label intro = new Label("Sort each item into the correct bin:", skin);
            styleBodyLabel(intro);

            Table binTable = new Table();
            binTable.defaults().pad(0.5f, 0f, 0.5f, 0f).left().top();

            binTable.add(createBinGuideRow(paperItemTexture, paperBinTexture, "Blue Bin - Paper", 0.5f, 0.7f, 1f)).left().row();
            binTable.add(createBinGuideRow(plasticItemTexture, plasticBinTexture, "Yellow Bin - Plastic", 0.6f, 1f, 0.5f)).left().row();
            binTable.add(createBinGuideRow(electronicItemTexture, electronicBinTexture, "Red Bin - E-Waste", 1f, 0.5f, 0.5f)).left().row();
            binTable.add(createBinGuideRow(trashItemTexture, trashBinTexture, "Black Bin - General Trash", 0.8f, 0.8f, 0.8f)).left().row();

            content.add(header).width(CONTENT_WIDTH).center().padBottom(8f).row();
            content.add(intro).width(CONTENT_WIDTH).left().padBottom(10f).row();
            content.add(binTable).left();
        }

        prevButton.setDisabled(currentPage == 0);
        nextButton.setDisabled(currentPage == 1);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (prevIconTexture != null) {
            prevIconTexture.dispose();
            prevIconTexture = null;
        }
        if (nextIconTexture != null) {
            nextIconTexture.dispose();
            nextIconTexture = null;
        }
        if (paperBinTexture != null) {
            paperBinTexture.dispose();
            paperBinTexture = null;
        }
        if (plasticBinTexture != null) {
            plasticBinTexture.dispose();
            plasticBinTexture = null;
        }
        if (electronicBinTexture != null) {
            electronicBinTexture.dispose();
            electronicBinTexture = null;
        }
        if (trashBinTexture != null) {
            trashBinTexture.dispose();
            trashBinTexture = null;
        }
        if (paperItemTexture != null) {
            paperItemTexture.dispose();
            paperItemTexture = null;
        }
        if (plasticItemTexture != null) {
            plasticItemTexture.dispose();
            plasticItemTexture = null;
        }
        if (electronicItemTexture != null) {
            electronicItemTexture.dispose();
            electronicItemTexture = null;
        }
        if (trashItemTexture != null) {
            trashItemTexture.dispose();
            trashItemTexture = null;
        }
        if (helpPanelTexture != null) {
            helpPanelTexture.dispose();
            helpPanelTexture = null;
        }
        if (hintPanelTexture != null) {
            hintPanelTexture.dispose();
            hintPanelTexture = null;
        }
    }
}