package INF1009_P3_02.Game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import java.util.Random;

public class FunFact {

    private static final float FADE_IN_SECONDS = 0.25f;
    private static final float POPUP_WIDTH = 360f;
    private static final float POPUP_HEIGHT = 420f;
    private static final float PADDING = 24f;
    private static final float BUTTON_HEIGHT = 56f;

    private static final String[] WRONG_BIN_FACTS = {
        "Singapore generates over 7 million tonnes of waste every year. That is roughly about 1,000 kg per person annually (about the weight of a small car).",
        "All non-recyclable waste ends up at Semakau Landfill",
        "If recyclables are dirty or mixed with food, they often cannot be processed.",
        "Public housing (HDB flats) has recycling chutes or bins on every floor. It's super convenient! ",
        "The 3Rs stand for Reduce, Reuse, and Recycle.",
        "A plastic bottle can take hundreds of years to break down in the environment.",
        "Recycling one aluminium can saves enough energy to power a TV for a few hours.",
        "Paper can usually be recycled multiple times before the fibers become too short.",
        "Rinsing containers before recycling helps prevent entire batches from getting contaminated.",
        "E-waste such as old phones and chargers should not be thrown into regular trash bins.",
        "In Singapore, clean paper, plastic, metal, and glass recyclables can go into the blue recycling bins.",
        "National Environment Agency (NEA) runs campaigns and programmes to build recycling habits across Singapore.",
        "Singapore's Zero Waste Masterplan aims to reduce waste sent to Semakau Landfill and raise recycling rates.",
        "When items are sorted correctly, recycling facilities can process them faster and better.",
        "Small daily actions, like sorting waste correctly, make a big long-term environmental impact."
    };

    private final Random random = new Random();
    private final Color shadowColor = new Color(0.02f, 0.08f, 0.12f, 0.58f);
    private final Color cardColor = new Color(0.04f, 0.10f, 0.16f, 1f);
    private final Color panelInnerColor = new Color(0.05f, 0.18f, 0.28f, 1f);
    private final Color neonBorderColor = new Color(0.43f, 1.00f, 0.53f, 1f);
    private final Color neonHeaderColor = new Color(0.77f, 1.00f, 0.45f, 1f);
    private final Color artColor = new Color(0.08f, 0.22f, 0.34f, 1f);
    private final Color artAccent = new Color(0.10f, 0.55f, 0.68f, 1f);
    private final Color titleColor = new Color(0.77f, 1.00f, 0.45f, 1f);
    private final Color bodyColor = new Color(0.90f, 0.95f, 0.98f, 1f);
    private final Color buttonColor = new Color(0.20f, 0.66f, 0.25f, 1f);
    private final Color buttonTextColor = new Color(1f, 1f, 1f, 1f);

    private String currentFact;
    private float timer;
    private float pulseTime;
    private float buttonX;
    private float buttonY;
    private float buttonW;
    private float buttonH;

    private Texture whitePixel;
    private Texture playerHeroTexture;

    public FunFact() {
    }

    public void showWrongBinFact() {
        currentFact = WRONG_BIN_FACTS[random.nextInt(WRONG_BIN_FACTS.length)];
        timer = 0f;
        pulseTime = 0f;
    }

    public void update(float dt) {
        if (currentFact != null) {
            timer = Math.min(FADE_IN_SECONDS, timer + dt);
            pulseTime += dt;
        }
    }

    public boolean isVisible() {
        return currentFact != null;
    }

    public void dismiss() {
        timer = 0f;
        currentFact = null;
    }

    public boolean handleTouch(float worldX, float worldY) {
        if (!isVisible()) {
            return false;
        }
        if (worldX >= buttonX && worldX <= buttonX + buttonW
            && worldY >= buttonY && worldY <= buttonY + buttonH) {
            dismiss();
        }
        return true;
    }

    public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, GlyphLayout layout, float worldW, float worldH) {
        if (!isVisible()) {
            return;
        }

        ensureTexturesLoaded();

        float alpha = Math.min(1f, timer / FADE_IN_SECONDS);
        float glowPulse = 0.86f + 0.14f * (float) Math.sin(pulseTime * 5.5f);

        float panelW = Math.min(POPUP_WIDTH, worldW - 40f);
        float panelH = Math.min(POPUP_HEIGHT, worldH - 44f);
        float panelX = (worldW - panelW) / 2f;
        float panelY = (worldH - panelH) / 2f;

        float framePad = 14f;
        float innerX = panelX + framePad;
        float innerY = panelY + framePad;
        float innerW = panelW - framePad * 2f;
        float innerH = panelH - framePad * 2f;

        float headerX = innerX + 8f;
        float headerW = innerW - 16f;
        float headerY = innerY + innerH - 44f;

        float artX = innerX + 10f;
        float artY = innerY + innerH * 0.43f;
        float artW = innerW - 20f;
        float artH = innerH * 0.43f;

        buttonW = Math.min(160f, panelW - 96f);
        buttonH = BUTTON_HEIGHT;
        buttonX = panelX + (panelW - buttonW) / 2f;
        buttonY = panelY + 26f;

        batch.begin();
        batch.setColor(shadowColor.r, shadowColor.g, shadowColor.b, shadowColor.a * alpha);
        batch.draw(whitePixel, panelX + 6f, panelY - 6f, panelW, panelH);

        batch.setColor(neonBorderColor.r, neonBorderColor.g, neonBorderColor.b, 0.42f * alpha * glowPulse);
        batch.draw(whitePixel, panelX - 2f, panelY - 2f, panelW + 4f, panelH + 4f);

        batch.setColor(cardColor.r, cardColor.g, cardColor.b, alpha);
        batch.draw(whitePixel, panelX, panelY, panelW, panelH);

        batch.setColor(neonBorderColor.r, neonBorderColor.g, neonBorderColor.b, 0.82f * alpha * glowPulse);
        batch.draw(whitePixel, innerX - 2f, innerY - 2f, innerW + 4f, innerH + 4f);
        batch.setColor(panelInnerColor.r, panelInnerColor.g, panelInnerColor.b, alpha);
        batch.draw(whitePixel, innerX, innerY, innerW, innerH);

        // Pixel-corner accents.
        float c = 16f;
        float t = 3f;
        batch.setColor(neonBorderColor.r, neonBorderColor.g, neonBorderColor.b, 0.95f * alpha * glowPulse);
        // Top-left
        batch.draw(whitePixel, innerX, innerY + innerH - t, c, t);
        batch.draw(whitePixel, innerX, innerY + innerH - c, t, c);
        // Top-right
        batch.draw(whitePixel, innerX + innerW - c, innerY + innerH - t, c, t);
        batch.draw(whitePixel, innerX + innerW - t, innerY + innerH - c, t, c);
        // Bottom-left
        batch.draw(whitePixel, innerX, innerY, c, t);
        batch.draw(whitePixel, innerX, innerY, t, c);
        // Bottom-right
        batch.draw(whitePixel, innerX + innerW - c, innerY, c, t);
        batch.draw(whitePixel, innerX + innerW - t, innerY, t, c);

        batch.setColor(neonHeaderColor.r, neonHeaderColor.g, neonHeaderColor.b, 0.18f * alpha);
        batch.draw(whitePixel, headerX, headerY, headerW, 28f);

        batch.setColor(neonHeaderColor.r, neonHeaderColor.g, neonHeaderColor.b, 0.78f * alpha * glowPulse);
        batch.draw(whitePixel, headerX, headerY + 27f, headerW, 1.5f);

        batch.setColor(artColor.r, artColor.g, artColor.b, alpha);
        batch.draw(whitePixel, artX, artY, artW, artH);
        batch.setColor(artAccent.r, artAccent.g, artAccent.b, 0.65f * alpha);
        batch.draw(whitePixel, artX, artY, artW, artH * 0.14f);

        batch.setColor(1f, 1f, 1f, alpha);
        float heroH = artH * 0.72f;
        float heroW = heroH * 0.88f;
        float heroX = artX + (artW - heroW) / 2f;
        float heroY = artY + (artH - heroH) / 2f + artH * 0.04f;
        batch.draw(playerHeroTexture, heroX, heroY, heroW, heroH);

        batch.setColor(buttonColor.r, buttonColor.g, buttonColor.b, alpha);
        batch.draw(whitePixel, buttonX, buttonY, buttonW, buttonH);
        batch.setColor(neonBorderColor.r, neonBorderColor.g, neonBorderColor.b, 0.95f * alpha);
        batch.draw(whitePixel, buttonX + 2f, buttonY + 2f, buttonW - 4f, 2f);
        batch.draw(whitePixel, buttonX + 2f, buttonY + buttonH - 4f, buttonW - 4f, 2f);

        font.getData().setScale(1.20f);
        font.setColor(titleColor.r, titleColor.g, titleColor.b, alpha);
        layout.setText(font, "DID YOU KNOW?");
        float titleX = innerX + (innerW - layout.width) / 2f;
        float titleY = headerY + 22f;
        font.draw(batch, layout, titleX, titleY);

        font.getData().setScale(1.04f);
        font.setColor(bodyColor.r, bodyColor.g, bodyColor.b, alpha);
        float bodyWidth = innerW - (PADDING * 1.6f);
        layout.setText(font, currentFact, bodyColor, bodyWidth, Align.left, true);
        float bodyX = innerX + (innerW - bodyWidth) / 2f;
        float bodyY = artY - 16f;
        font.draw(batch, layout, bodyX, bodyY);

        font.getData().setScale(1.10f);
        font.setColor(buttonTextColor.r, buttonTextColor.g, buttonTextColor.b, alpha);
        layout.setText(font, "Got it!");
        float buttonTextX = buttonX + (buttonW - layout.width) / 2f;
        float buttonTextY = buttonY + (buttonH + layout.height) / 2f - 2f;
        font.draw(batch, layout, buttonTextX, buttonTextY);

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
        batch.end();
    }

    private void ensureTexturesLoaded() {
        if (whitePixel != null) {
            return;
        }

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        playerHeroTexture = new Texture("PlayerMovementAvatar/Player_Hero/player_hero.png");
    }

    public void dispose() {
        if (whitePixel != null) {
            whitePixel.dispose();
            whitePixel = null;
        }
        if (playerHeroTexture != null) {
            playerHeroTexture.dispose();
            playerHeroTexture = null;
        }
    }
}
