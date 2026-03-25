package INF1009_P3_02.Game.scene;

import INF1009_P3_02.Game.entity.Player;
import INF1009_P3_02.Game.enumFolder.CarryState;
import INF1009_P3_02.Game.enumFolder.TrashType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all visual feedback during the simulation:
 *  - Center-screen tick/cross flash when a bin is scored
 *  - Carry icon drawn above the player while holding trash
 *  - Pickup popup text that fades out after pickup
 */
public class FeedbackManager {

    private static final float FEEDBACK_DURATION = 1.2f;
    private static final float POPUP_DURATION    = 1.5f;
    private static final float CARRY_ICON_SIZE   = 28f;

    private final SpriteBatch batch;
    private final BitmapFont  font;
    private final GlyphLayout layout;

    // Center-screen flash (tick or cross)
    private Texture tickFlashIcon;
    private Texture crossFlashIcon;
    private Texture feedbackIcon  = null;
    private float   feedbackTimer = 0f;

    // Carry indicator and pickup popup
    private Map<TrashType, Texture> trashIconMap;
    private String pickupPopupText  = null;
    private float  pickupPopupTimer = 0f;

    public FeedbackManager(SpriteBatch batch) {
        this.batch  = batch;
        this.font   = new BitmapFont();
        this.layout = new GlyphLayout();
        this.font.setColor(Color.WHITE);
    }

    /** Load all textures. Call once after SpriteBatch is ready. */
    public void init() {
        tickFlashIcon  = new Texture(Gdx.files.internal("backgrounds/tick.png"));
        crossFlashIcon = new Texture(Gdx.files.internal("backgrounds/cross.png"));

        trashIconMap = new HashMap<>();
        trashIconMap.put(TrashType.PAPER,      new Texture(Gdx.files.internal("trash_recyclable/paper.png")));
        trashIconMap.put(TrashType.PLASTIC,    new Texture(Gdx.files.internal("trash_recyclable/plastic.png")));
        trashIconMap.put(TrashType.ELECTRONIC, new Texture(Gdx.files.internal("trash_recyclable/electronic.png")));
        trashIconMap.put(TrashType.TRASHBAG,   new Texture(Gdx.files.internal("trash_recyclable/trashbag.png")));
    }

    // ── Event triggers ────────────────────────────────────────────────

    public void onCorrect() {
        feedbackIcon  = tickFlashIcon;
        feedbackTimer = FEEDBACK_DURATION;
    }

    public void onWrong() {
        feedbackIcon  = crossFlashIcon;
        feedbackTimer = FEEDBACK_DURATION;
    }

    public void onPickup(TrashType type) {
        pickupPopupText  = "Picked up: " + formatTrashType(type);
        pickupPopupTimer = POPUP_DURATION;
    }

    // ── Update ────────────────────────────────────────────────────────

    public void update(float dt) {
        if (feedbackTimer    > 0f) feedbackTimer    -= dt;
        if (pickupPopupTimer > 0f) pickupPopupTimer -= dt;
    }

    // ── Draw ──────────────────────────────────────────────────────────

    public void draw(Player player, float worldW, float worldH) {
        drawCarryIndicator(player);
        drawPickupPopup(player);
        drawFeedbackFlash(worldW, worldH);
    }

    private void drawCarryIndicator(Player player) {
        if (player.getCarryState() == CarryState.NONE) return;
        TrashType held = player.getCarriedTrashType();
        if (held == null) return;
        Texture icon = trashIconMap.get(held);
        if (icon == null) return;

        Rectangle b = player.getBounds();
        float iconX = b.x + (b.width - CARRY_ICON_SIZE) / 2f;
        float iconY = b.y + b.height + 4f;

        batch.begin();
        batch.draw(icon, iconX, iconY, CARRY_ICON_SIZE, CARRY_ICON_SIZE);
        batch.end();
    }

    private void drawPickupPopup(Player player) {
        if (pickupPopupTimer <= 0f || pickupPopupText == null) return;

        float alpha = Math.min(1f, pickupPopupTimer / 0.5f); // fade in last 0.5 s

        Rectangle b = player.getBounds();
        float popupY = b.y + b.height + CARRY_ICON_SIZE + 10f;

        font.getData().setScale(1f);
        layout.setText(font, pickupPopupText);
        float popupX = b.x + b.width / 2f - layout.width / 2f;

        batch.begin();
        font.setColor(1f, 1f, 1f, alpha);
        font.draw(batch, layout, popupX, popupY);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawFeedbackFlash(float worldW, float worldH) {
        if (feedbackTimer <= 0f || feedbackIcon == null) return;
        float size = 96f;
        batch.begin();
        batch.draw(feedbackIcon, (worldW - size) / 2f, (worldH - size) / 2f, size, size);
        batch.end();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private String formatTrashType(TrashType type) {
        switch (type) {
            case PAPER:      return "Paper";
            case PLASTIC:    return "Plastic";
            case ELECTRONIC: return "Electronic";
            case TRASHBAG:   return "Trash Bag";
            default:         return type.name();
        }
    }

    public void dispose() {
        if (font           != null) font.dispose();
        if (tickFlashIcon  != null) tickFlashIcon.dispose();
        if (crossFlashIcon != null) crossFlashIcon.dispose();
        if (trashIconMap   != null) {
            for (Texture t : trashIconMap.values()) t.dispose();
        }
    }
}
