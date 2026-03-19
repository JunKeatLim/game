package INF1009_P3_02.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Draws the top HUD bar containing:
 *  - Timer pill (left)       — clock icon + countdown number
 *  - Score bar pill (center) — tick count | score | cross count
 *  - Pause button pill (right)
 */
public class SimulationHUD {

    // Adjust these to compensate for different PNG crops (positive = right/up)
    private static final float TICK_OFFSET_X  = 0f;
    private static final float TICK_OFFSET_Y  = 0f;
    private static final float CROSS_OFFSET_X = 0f;
    private static final float CROSS_OFFSET_Y = 0f;
    private static final float ICON_SIZE      = 22f;

    private static final float PAUSE_BTN_W = 36f;
    private static final float PAUSE_BTN_H = 36f;

    private final SpriteBatch   batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont    font;
    private final GlyphLayout   layout;

    private Texture clockIcon;
    private Texture pauseButtonIcon;
    private Texture tickIcon;
    private Texture crossIcon;

    private float worldW;
    private float pauseBtnX, pauseBtnY;

    public SimulationHUD(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        this.batch         = batch;
        this.shapeRenderer = shapeRenderer;
        this.font          = new BitmapFont();
        this.layout        = new GlyphLayout();
        this.font.setColor(Color.WHITE);
    }

    /** Load textures and calculate initial positions. */
    public void init(float worldW, float worldH) {
        this.worldW = worldW;
        pauseBtnX = worldW - PAUSE_BTN_W - 18f;
        pauseBtnY = worldH - PAUSE_BTN_H - 16f;

        clockIcon       = new Texture(Gdx.files.internal("backgrounds/clock.png"));
        pauseButtonIcon = new Texture(Gdx.files.internal("backgrounds/pause.png"));
        tickIcon        = new Texture(Gdx.files.internal("backgrounds/tick.png"));
        crossIcon       = new Texture(Gdx.files.internal("backgrounds/cross.png"));
    }

    public void resize(float worldW, float worldH) {
        this.worldW = worldW;
        pauseBtnX = worldW - PAUSE_BTN_W - 18f;
        pauseBtnY = worldH - PAUSE_BTN_H - 16f;
    }

    // Pause button bounds — used by SimulationScene for hit detection
    public float getPauseBtnX() { return pauseBtnX; }
    public float getPauseBtnY() { return pauseBtnY; }
    public float getPauseBtnW() { return PAUSE_BTN_W; }
    public float getPauseBtnH() { return PAUSE_BTN_H; }

    // ── Draw ──────────────────────────────────────────────────────────

    public void draw(GameStats stats, float timeLeft) {
        float padding = 10f;

        // Timer pill
        float timerBoxX      = padding;
        float timerBoxY      = pauseBtnY;
        float timerBoxWidth  = 72f;
        float timerBoxHeight = PAUSE_BTN_H;

        // Score bar — measure text first so we can size the pill dynamically
        float barHeight   = PAUSE_BTN_H;
        float barY        = pauseBtnY;
        float barInnerPad = 14f;
        float sectionGap  = 16f;

        int    score      = stats.getScore();
        String correctStr = String.valueOf(stats.getCorrectCount());
        String wrongStr   = String.valueOf(stats.getWrongCount());
        String scoreStr   = score > 0 ? "+" + score : String.valueOf(score);

        font.getData().setScale(1f);
        layout.setText(font, correctStr); float correctTextW = layout.width;
        layout.setText(font, wrongStr);   float wrongTextW   = layout.width;
        font.getData().setScale(1.3f);
        layout.setText(font, scoreStr);   float scoreTextW   = layout.width;
        font.getData().setScale(1f);

        float leftW    = ICON_SIZE + 4f + correctTextW;
        float rightW   = wrongTextW + 4f + ICON_SIZE;
        float barWidth = barInnerPad + leftW + sectionGap + scoreTextW + sectionGap + rightW + barInnerPad;
        float barX     = (worldW - barWidth) / 2f;

        // ── ShapeRenderer: all white pill backgrounds in one pass ─────
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        drawRoundedRect(timerBoxX, timerBoxY, timerBoxWidth, timerBoxHeight, timerBoxHeight / 2f);
        drawRoundedRect(barX,      barY,      barWidth,      barHeight,      barHeight      / 2f);
        drawRoundedRect(pauseBtnX, pauseBtnY, PAUSE_BTN_W,  PAUSE_BTN_H,   PAUSE_BTN_H    / 2f);
        shapeRenderer.end();

        // ── Batch: all icons and text in one pass ─────────────────────
        batch.begin();

        // Clock icon
        float clockSize = 28f;
        float clockY    = timerBoxY + (timerBoxHeight - clockSize) / 2f;
        batch.draw(clockIcon, timerBoxX + 6, clockY, clockSize, clockSize);

        // Timer number
        font.setColor(Color.BLACK);
        float savedScaleX = font.getData().scaleX;
        float savedScaleY = font.getData().scaleY;
        font.getData().setScale(1.5f);
        layout.setText(font, String.valueOf(Math.max(0, (int) timeLeft)));
        float numberAreaX    = timerBoxX + clockSize + 10;
        float numberAreaW    = timerBoxWidth - (clockSize + 16);
        float numberX        = numberAreaX + (numberAreaW - layout.width) / 2f;
        float numberY        = timerBoxY + (timerBoxHeight + layout.height) / 2f - 2;
        font.draw(batch, layout, numberX, numberY);
        font.getData().setScale(savedScaleX, savedScaleY);

        // Score bar content
        font.setColor(Color.BLACK);
        float textBaseY = barY + (barHeight + layout.height) / 2f - 2;

        // Left: tick icon + correctCount
        float tickX = barX + barInnerPad;
        float tickY = barY + (barHeight - ICON_SIZE) / 2f;
        batch.draw(tickIcon, tickX + TICK_OFFSET_X, tickY + TICK_OFFSET_Y, ICON_SIZE, ICON_SIZE);
        layout.setText(font, correctStr);
        font.draw(batch, layout, tickX + ICON_SIZE + 4f, textBaseY);

        // Center: score (coloured)
        font.getData().setScale(1.3f);
        layout.setText(font, scoreStr);
        if      (score > 0) font.setColor(0f, 0.55f, 0.1f, 1f);
        else if (score < 0) font.setColor(0.85f, 0.1f, 0.1f, 1f);
        else                font.setColor(Color.BLACK);
        font.draw(batch, layout, barX + (barWidth - layout.width) / 2f, barY + (barHeight + layout.height) / 2f - 2);
        font.getData().setScale(1f);

        // Right: wrongCount + cross icon
        font.setColor(Color.BLACK);
        layout.setText(font, wrongStr);
        float crossX = barX + barWidth - barInnerPad - ICON_SIZE;
        float crossY = barY + (barHeight - ICON_SIZE) / 2f;
        font.draw(batch, layout, crossX - 4f - layout.width, textBaseY);
        batch.draw(crossIcon, crossX + CROSS_OFFSET_X, crossY + CROSS_OFFSET_Y, ICON_SIZE, ICON_SIZE);

        font.setColor(Color.WHITE);

        // Pause icon
        batch.draw(pauseButtonIcon, pauseBtnX + 2f, pauseBtnY + 2f, PAUSE_BTN_W - 4f, PAUSE_BTN_H - 4f);

        batch.end();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void drawRoundedRect(float x, float y, float w, float h, float r) {
        shapeRenderer.rect(x + r, y, w - 2 * r, h);
        shapeRenderer.rect(x, y + r, r, h - 2 * r);
        shapeRenderer.rect(x + w - r, y + r, r, h - 2 * r);
        int seg = 18;
        shapeRenderer.arc(x + r,     y + r,     r, 180, 90, seg);
        shapeRenderer.arc(x + w - r, y + r,     r, 270, 90, seg);
        shapeRenderer.arc(x + w - r, y + h - r, r, 0,   90, seg);
        shapeRenderer.arc(x + r,     y + h - r, r, 90,  90, seg);
    }

    public void dispose() {
        if (font            != null) font.dispose();
        if (clockIcon       != null) clockIcon.dispose();
        if (pauseButtonIcon != null) pauseButtonIcon.dispose();
        if (tickIcon        != null) tickIcon.dispose();
        if (crossIcon       != null) crossIcon.dispose();
    }
}
