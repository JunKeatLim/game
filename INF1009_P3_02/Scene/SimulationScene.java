package INF1009_P3_02.Scene;


import INF1009_P3_02.BackgroundChoice;
import INF1009_P3_02.Collision.CollisionManager;
import INF1009_P3_02.Entity.*;
import INF1009_P3_02.GameMaster;
import INF1009_P3_02.InputOutput.InputOutputManager;
import INF1009_P3_02.Logging.GameEngineLogger;
import INF1009_P3_02.Movement.MovementManager;
import INF1009_P3_02.Observer.AudioEventListener;
import INF1009_P3_02.Observer.IObserver;
import INF1009_P3_02.Observer.GameEventManager;
import INF1009_P3_02.Observer.LoggingEventListener;
import INF1009_P3_02.Observer.MovementEventListener;
import INF1009_P3_02.Observer.StateChangeReason;
import INF1009_P3_02.SettingsData;
import INF1009_P3_02.SimulationConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationScene extends Scene {
    private static final float PLAYER_COLLISION_SIZE = 40f;
    private static final float PLAYER_DRAW_SIZE = 240f;
    private static final float POPUP_DURATION = 1.2f;
    private static final float WRONG_BIN_POPUP_DURATION = 0.6f;
    private static final float PICKUP_POPUP_DURATION = 0.7f;
    private static final float DROP_POPUP_DURATION = 1.5f;
    private static final float PLAYER_POPUP_OFFSET_Y = -34f;
    private static final float BIN_RESULT_ICON_SIZE = 96f;
    private static final float MONSTER_WARNING_ICON_SIZE = 400f;
    private static final float PICKUP_POPUP_SCALE = 1.4f;
    private static final float RESUME_COUNTDOWN_TOTAL = 3f;
    private static final float RECYCLE_FLASH_SECONDS = 0.8f;
    private static final float COLLISION_SHAKE_SECONDS = 0.22f;
    private static final float COLLISION_SHAKE_INTENSITY = 8f;
    private FunFact funFact;

    private final SceneManager sceneManager;
    private final SettingsData settings;
    private final SimulationConfig config;

    // rendering
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture basketballBackground;
    private Texture canteenBackground;
    private Texture parkBackground;
    private Texture clockIcon;
    private Texture trashbagIcon;
    private Texture electronicIcon;
    private Texture paperIcon;
    private Texture plasticIcon;
    private Texture pauseButtonIcon;
    private Texture tickIcon;
    private Texture crossIcon;
    private Texture monsterWarningIcon;
    private float pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH;
    private com.badlogic.gdx.InputAdapter pauseInputAdapter;

    // popup
    private String popupText = null;
    private Texture popupImage = null;
    private float popupTimer = 0f;
    private float popupDuration = POPUP_DURATION;
    private Color popupColor = Color.WHITE.cpy();
    private boolean popupAbovePlayer = false;

    private static final float TICK_OFFSET_X = 0f, TICK_OFFSET_Y = 0f;
    private static final float CROSS_OFFSET_X = 0f, CROSS_OFFSET_Y = 0f;
    private static final float ICON_SIZE = 22f;

    private float heartbeatTimer = 0f;

    // managers
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private InputOutputManager inputOutputManager;
    private final GameEngineLogger logger;
    private GameEventManager eventManager;

    // entities
    private Player player;
    private final List<Bot> bots = new ArrayList<>();
    private boolean simulationEnded = false;

    // world / state
    private float WORLD_W, WORLD_H;
    private float timeLeft;
    private int playerBotCollisionCount = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int score = 0;

    private boolean initialized = false;
    private boolean pendingWrongFunFact = false;
    private boolean resumeCountdownActive = false;
    private float resumeCountdownTimer = 0f;
    private boolean recycleFlashActive = false;
    private float recycleFlashTimer = 0f;
    private int lastCountdownNumberShown = -1;
    private float collisionShakeTimer = 0f;
    private float collisionShakeOffsetX = 0f;
    private float collisionShakeOffsetY = 0f;

    public SimulationScene(SceneManager sceneManager, SettingsData settings, SimulationConfig config, GameEngineLogger logger) {
        this.sceneManager = sceneManager;
        this.settings = settings;
        this.config = config;
        this.logger = logger;
        this.funFact = new FunFact();
    }

    public SimulationConfig getConfig() { return config; }

    public void startResumeCountdown(float seconds) {
        float safeSeconds = seconds <= 0f ? RESUME_COUNTDOWN_TOTAL : seconds;
        resumeCountdownActive = true;
        resumeCountdownTimer = safeSeconds;
        recycleFlashActive = false;
        recycleFlashTimer = 0f;
        lastCountdownNumberShown = -1;
    }

    @Override protected void buildUI() {}

    @Override
    public void show() {
        if (initialized) {
            simulationEnded = false;
            Gdx.input.setInputProcessor(pauseInputAdapter);
            if (inputOutputManager != null) inputOutputManager.setControlScheme(settings.controlScheme);
            return;
        }
        initialized = true;

        GameMaster gameMaster = sceneManager.getGameMaster();
        eventManager = gameMaster.getEventManager();

        pauseInputAdapter = new com.badlogic.gdx.InputAdapter() {
            @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = Gdx.graphics.getHeight() - screenY;
                if (funFact.isVisible()) return funFact.handleTouch(screenX, y);
                if (screenX >= pauseBtnX && screenX <= pauseBtnX + pauseBtnW &&
                    y >= pauseBtnY && y <= pauseBtnY + pauseBtnH) {
                    sceneManager.goToPauseScene();
                    return true;
                }
                return false;
            }
            @Override public boolean keyDown(int keycode) {
                if (funFact.isVisible()) {
                    if (keycode == com.badlogic.gdx.Input.Keys.ENTER
                        || keycode == com.badlogic.gdx.Input.Keys.SPACE
                        || keycode == com.badlogic.gdx.Input.Keys.ESCAPE) funFact.dismiss();
                    return true;
                }
                if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) { sceneManager.goToPauseScene(); return true; }
                return false;
            }
        };
        Gdx.input.setInputProcessor(pauseInputAdapter);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();
        basketballBackground = new Texture(Gdx.files.internal("backgrounds/school_basketball.jpg"));
        canteenBackground = new Texture(Gdx.files.internal("backgrounds/school_canteen.jpg"));
        parkBackground = new Texture(Gdx.files.internal("backgrounds/school_park.jpg"));
        clockIcon = new Texture(Gdx.files.internal("backgrounds/clock.png"));
        trashbagIcon = new Texture(Gdx.files.internal("trash_recyclable/trashbag.png"));
        electronicIcon = new Texture(Gdx.files.internal("trash_recyclable/electronic.png"));
        paperIcon = new Texture(Gdx.files.internal("trash_recyclable/paper.png"));
        plasticIcon = new Texture(Gdx.files.internal("trash_recyclable/plastic.png"));
        pauseButtonIcon = new Texture(Gdx.files.internal("backgrounds/pause.png"));
        tickIcon = new Texture(Gdx.files.internal("backgrounds/tick.png"));
        crossIcon = new Texture(Gdx.files.internal("backgrounds/cross.png"));
        monsterWarningIcon = new Texture(Gdx.files.internal("backgrounds/monster_warning.png"));

        WORLD_W = Gdx.graphics.getWidth();
        WORLD_H = Gdx.graphics.getHeight();
        pauseBtnW = 48f; pauseBtnH = 54f;
        pauseBtnX = WORLD_W - pauseBtnW - 18f;
        pauseBtnY = WORLD_H - pauseBtnH - 16f;
        timeLeft = config.durationSeconds;

        entityManager = new EntityManager();
        entityManager.setLogger(logger);
        entityManager.setWorldDimensions(WORLD_W, WORLD_H);
        EntityFactory entityFactory = new EntityFactory(WORLD_W, WORLD_H);
        entityManager.setEntityFactory(entityFactory);
        player = entityFactory.createPlayer(400, 300, 300, PLAYER_COLLISION_SIZE, PLAYER_DRAW_SIZE);

        int botCount = Math.max(0, config.getBotCount());
        float[][] botStartPositions = { {500f, 300f}, {700f, 430f} };
        bots.clear();
        for (int i = 0; i < botCount; i++) {
            float[] pos = botStartPositions[Math.min(i, botStartPositions.length - 1)];
            bots.add(new Bot(pos[0], pos[1], 150, 200, 150, WORLD_W, WORLD_H));
        }

        entityManager.addEntity(player);
        for (Bot bot : bots) entityManager.addEntity(bot);

        BinType[] obstacleTypes = { BinType.TRASH, BinType.ELECTRONIC, BinType.PAPER, BinType.PLASTIC };
        entityManager.spawnBinBottomRow(obstacleTypes);
        entityManager.spawnInitialTrash(config.getInitialTrashPerType());

        movementManager = new MovementManager(entityManager);

        inputOutputManager = new InputOutputManager(settings, movementManager);
        inputOutputManager.setControlScheme(settings.controlScheme);
        inputOutputManager.setEscapeListener(() -> sceneManager.goToPauseScene());

        collisionManager = new CollisionManager(inputOutputManager.getSpeaker(), entityManager, eventManager);

        // ── Register all Observers ───────────────────────────────────────
        eventManager.clearListeners();

        // Observer 1: Audio — plays sounds on carry state changes and player collisions
        eventManager.addListener(new AudioEventListener(inputOutputManager.getSpeaker()));

        // Observer 2: Logging — logs carry state changes and player collisions
        eventManager.addListener(new LoggingEventListener(logger));

        // Observer 3: Movement — syncs player movement on player collisions
        eventManager.addListener(new MovementEventListener(movementManager));

        // Observer 4: Scene UI — handles popups, score, fun facts on carry state changes
        eventManager.addListener(new SceneEventListener());

        movementManager.syncPlayerMovementFromEntity();

        logger.info("=== Simulation Started === Duration: " + config.durationSeconds + "s | Background: " + config.background);
    }

    private class SceneEventListener implements IObserver {

        @Override
        public void onCarryStateChanged(CarryState oldState, CarryState newState,
                                        TrashType trashType, StateChangeReason reason, int points) {
            switch (reason) {
                case PICKUP:
                    popupText = "Picked up: " + formatTrashType(trashType);
                    popupImage = null;
                    popupTimer = PICKUP_POPUP_DURATION;
                    popupDuration = PICKUP_POPUP_DURATION;
                    popupColor.set(1f, 0.95f, 0.2f, 1f);
                    popupAbovePlayer = true;
                    break;
                case DEPOSITED_CORRECT:
                    correctCount++;
                    score += points;
                    popupText = null;
                    popupImage = tickIcon;
                    popupTimer = POPUP_DURATION;
                    popupDuration = POPUP_DURATION;
                    popupAbovePlayer = false;
                    break;
                case DEPOSITED_WRONG:
                    wrongCount++;
                    score += points;
                    popupText = null;
                    popupImage = crossIcon;
                    popupTimer = WRONG_BIN_POPUP_DURATION;
                    popupDuration = WRONG_BIN_POPUP_DURATION;
                    popupAbovePlayer = false;
                    pendingWrongFunFact = true;
                    break;
                case DROPPED:
                    popupText = null;
                    popupImage = monsterWarningIcon;
                    popupTimer = DROP_POPUP_DURATION;
                    popupDuration = DROP_POPUP_DURATION;
                    popupColor.set(Color.WHITE);
                    popupAbovePlayer = false;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPlayerCollision(StateChangeReason reason) {
            // Track collision count when player hits a bot
            if (reason == StateChangeReason.HIT_BOT) {
                playerBotCollisionCount = collisionManager.getPlayerBotCollisionCount();
                collisionShakeTimer = COLLISION_SHAKE_SECONDS;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (resumeCountdownActive) {
            int numberToShow = Math.max(1, (int)Math.ceil(resumeCountdownTimer));
            if (numberToShow != lastCountdownNumberShown) {
                lastCountdownNumberShown = numberToShow;
                if (inputOutputManager != null) {
                    inputOutputManager.playCountdownNumber(numberToShow);
                }
            }

            resumeCountdownTimer -= dt;
            if (resumeCountdownTimer <= 0f) {
                resumeCountdownTimer = 0f;
                resumeCountdownActive = false;
                recycleFlashActive = true;
                recycleFlashTimer = RECYCLE_FLASH_SECONDS;
                if (inputOutputManager != null) {
                    inputOutputManager.playCountdownRecycle();
                }
            }
            return;
        }
        if (recycleFlashActive) {
            recycleFlashTimer -= dt;
            if (recycleFlashTimer <= 0f) {
                recycleFlashTimer = 0f;
                recycleFlashActive = false;
            }
            return;
        }

        if (collisionShakeTimer > 0f) {
            collisionShakeTimer -= dt;
            float progress = Math.max(0f, collisionShakeTimer / COLLISION_SHAKE_SECONDS);
            float strength = COLLISION_SHAKE_INTENSITY * progress;
            collisionShakeOffsetX = (float)((Math.random() * 2.0 - 1.0) * strength);
            collisionShakeOffsetY = (float)((Math.random() * 2.0 - 1.0) * strength);
            if (collisionShakeTimer <= 0f) {
                collisionShakeTimer = 0f;
                collisionShakeOffsetX = 0f;
                collisionShakeOffsetY = 0f;
            }
        }

        if (funFact.isVisible()) {
            funFact.update(dt);
            if (funFact.isVisible()) return;
        }

        timeLeft -= dt;

        heartbeatTimer += dt;
        if (heartbeatTimer >= 10f) {
            heartbeatTimer = 0f;
            logger.info("Heartbeat — Player pos: (" + (int)player.getX() + "," + (int)player.getY() + ") | Time: " + (int)timeLeft + "s | Score: " + score);
        }

        inputOutputManager.setControlScheme(settings.controlScheme);

        float pOldX = player.getX();
        float pOldY = player.getY();
        Map<Bot, float[]> botOldPositions = new HashMap<>();
        for (Bot bot : bots) {
            botOldPositions.put(bot, new float[] { bot.getX(), bot.getY() });
        }

        inputOutputManager.update(dt);
        movementManager.update(dt);
        entityManager.updateAll(dt);

        List<Bin> obstacles = entityManager.getBins();

        // All player collision responses (sound, logging, movement sync)
        // are handled by observers automatically via GameEventManager
        collisionManager.resolve(player, bots, obstacles, pOldX, pOldY, botOldPositions, dt);

        // Trash pickup — notifies observers of carry state change
        handleTrashPickup();

        updatePopup(dt);

        if (timeLeft <= 0f && !simulationEnded) {
            simulationEnded = true;
            logger.info("=== Simulation Ended === Total Collisions: " + playerBotCollisionCount + " Total Score: " + score);
            sceneManager.goToEndScene(score);
        }
    }

    private void handleTrashPickup() {
        if (player.getCarryState() == CarryState.NONE) {
            for (Trash t : entityManager.getTrashItems()) {
                if (player.getBounds().overlaps(t.getBounds())) {
                    TrashType type = t.getType();
                    entityManager.removeEntity(t);

                    CarryState newState = (type == TrashType.TRASHBAG) ? CarryState.TRASH : CarryState.RECYCLE;
                    player.setCarriedTrashType(type);
                    player.setCarryState(newState);

                    // Notify observers: carry state changed NONE → carrying (PICKUP)
                    eventManager.notifyCarryStateChanged(newState, type, StateChangeReason.PICKUP, 0);
                    break;
                }
            }
        }
    }

    private void updatePopup(float dt) {
        if (popupTimer > 0f) {
            popupTimer -= dt;
            if (popupTimer <= 0f) {
                popupTimer = 0f;
                popupDuration = POPUP_DURATION;
                popupText = null;
                popupImage = null;
                popupColor.set(Color.WHITE);
                popupAbovePlayer = false;
                if (pendingWrongFunFact) {
                    funFact.showWrongBinFact();
                    pendingWrongFunFact = false;
                }
            }
        }
    }

    private void drawPopup() {
        if (popupTimer <= 0f) return;
        float alpha = popupDuration <= 0f ? 0f : popupTimer / popupDuration;
        if (popupImage == crossIcon) alpha = 1f;
        if (popupImage != null) {
            float sz = popupImage == monsterWarningIcon ? MONSTER_WARNING_ICON_SIZE : BIN_RESULT_ICON_SIZE;
            batch.begin();
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(popupImage, (WORLD_W - sz) / 2f, (WORLD_H - sz) / 2f, sz, sz);
            batch.setColor(Color.WHITE);
            batch.end();
            return;
        }
        if (popupText == null) return;
        float fontScale = popupAbovePlayer ? PICKUP_POPUP_SCALE : 1f;
        font.getData().setScale(fontScale);
        layout.setText(font, popupText);
        float px, py;
        if (popupAbovePlayer) {
            px = player.getX() - layout.width / 2f;
            py = player.getY() + PLAYER_DRAW_SIZE / 2f + PLAYER_POPUP_OFFSET_Y;
        } else {
            px = (WORLD_W - layout.width) / 2f;
            py = WORLD_H * 0.62f;
        }
        batch.begin();
        font.setColor(popupColor.r, popupColor.g, popupColor.b, alpha);
        font.draw(batch, layout, px, py);
        font.setColor(Color.WHITE);
        batch.end();
        font.getData().setScale(1f);
    }

    private String formatTrashType(TrashType type) {
        switch (type) {
            case PAPER: return "Paper"; case PLASTIC: return "Plastic";
            case ELECTRONIC: return "Electronic"; case TRASHBAG: return "Trash Bag";
            default: return type.name();
        }
    }

    private Texture getTrashIcon(TrashType type) {
        switch (type) {
            case PAPER: return paperIcon; case PLASTIC: return plasticIcon;
            case ELECTRONIC: return electronicIcon; case TRASHBAG: return trashbagIcon;
            default: return null;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Matrix4 originalTransform = new Matrix4(batch.getTransformMatrix());
        if (collisionShakeOffsetX != 0f || collisionShakeOffsetY != 0f) {
            Matrix4 shaken = new Matrix4(originalTransform);
            shaken.translate(collisionShakeOffsetX, collisionShakeOffsetY, 0f);
            batch.setTransformMatrix(shaken);
        }

        drawBackground(config.background);

        batch.begin();
        player.draw(batch);
        for (Bot bot : bots) bot.draw(batch);
        for (Bin o : entityManager.getBins()) o.draw(batch);
        for (Trash t : entityManager.getTrashItems()) t.draw(batch);
        batch.end();
        batch.setTransformMatrix(originalTransform);

        drawPopup();
        drawHUD();
        funFact.draw(shapeRenderer, batch, font, layout, WORLD_W, WORLD_H);
        drawResumeCountdown();
        drawRecycleFlash();
        renderBrightnessOverlay();
    }

    private void drawResumeCountdown() {
        if (!resumeCountdownActive) return;

        String countdownText = String.valueOf((int) Math.ceil(resumeCountdownTimer));
        if ("0".equals(countdownText)) {
            countdownText = "1";
        }

        // Keep gameplay visible underneath with a soft tinted overlay.
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.07f, 0.10f, 0.34f);
        shapeRenderer.rect(0f, 0f, WORLD_W, WORLD_H);
        // second pass gives a subtle frosted effect instead of flat black
        shapeRenderer.setColor(0.16f, 0.20f, 0.26f, 0.12f);
        shapeRenderer.rect(0f, 0f, WORLD_W, WORLD_H);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        float pulse = 1f + 0.12f * (float)Math.sin((RESUME_COUNTDOWN_TOTAL - resumeCountdownTimer) * 8f);

        batch.begin();
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        font.getData().setScale(5.0f * pulse);

        layout.setText(font, countdownText);
        float x = (WORLD_W - layout.width) / 2f;
        float y = (WORLD_H + layout.height) / 2f;

        // Shadow/outline pass
        font.setColor(0f, 0f, 0f, 0.9f);
        font.draw(batch, layout, x + 4f, y - 4f);
        // Main number pass
        font.setColor(1f, 0.95f, 0.35f, 1f);
        font.draw(batch, layout, x, y);

        font.getData().setScale(1.35f);
        font.setColor(0.95f, 0.98f, 1f, 1f);
        layout.setText(font, "Get ready...");
        float readyX = (WORLD_W - layout.width) / 2f;
        float readyY = y - 70f;
        font.setColor(0f, 0f, 0f, 0.75f);
        font.draw(batch, layout, readyX + 2f, readyY - 2f);
        font.setColor(0.95f, 0.98f, 1f, 1f);
        font.draw(batch, layout, readyX, readyY);

        font.getData().setScale(oldScaleX, oldScaleY);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawRecycleFlash() {
        if (!recycleFlashActive) return;

        float alpha = Math.min(1f, recycleFlashTimer / RECYCLE_FLASH_SECONDS);
        float t = 1f - alpha;
        float pulse = 1f + 0.16f * (float)Math.sin(t * 12f);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.04f, 0.20f, 0.06f, 0.28f * alpha);
        shapeRenderer.rect(0f, 0f, WORLD_W, WORLD_H);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;

        font.getData().setScale(4.6f * pulse);
        layout.setText(font, "RECYCLE!");
        float x = (WORLD_W - layout.width) / 2f;
        float y = (WORLD_H + layout.height) / 2f;

        // Glow/shadow then bright main text
        font.setColor(0f, 0f, 0f, 0.85f * alpha);
        font.draw(batch, layout, x + 4f, y - 4f);
        font.setColor(0.55f, 1f, 0.45f, alpha);
        font.draw(batch, layout, x + 1.5f, y + 1.5f);
        font.setColor(0.90f, 1f, 0.85f, alpha);
        font.draw(batch, layout, x, y);

        font.getData().setScale(oldScaleX, oldScaleY);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawBackground(BackgroundChoice bg) {
        Texture sel;
        switch (bg) {
            case SCHOOL_BASKETBALL: sel = basketballBackground; break;
            case SCHOOL_CANTEEN: sel = canteenBackground; break;
            case SCHOOL_PARK: sel = parkBackground; break;
            default: sel = basketballBackground; break;
        }
        batch.begin(); batch.draw(sel, 0, 0, WORLD_W, WORLD_H); batch.end();
    }

    private void drawHUD() {
        float padding = 10f;
        float timerBoxX = padding, timerBoxY = pauseBtnY, timerBoxWidth = 72f, timerBoxHeight = pauseBtnH;
        Texture carryIcon = player.getCarriedTrashType() != null ? getTrashIcon(player.getCarriedTrashType()) : null;
        String pickupText = player.getCarriedTrashType() != null ? formatTrashType(player.getCarriedTrashType()) : "Empty";
        float pickupBoxX = timerBoxX + timerBoxWidth + padding, pickupBoxY = pauseBtnY, pickupBoxHeight = pauseBtnH;
        float iconH = carryIcon != null ? pauseBtnH + 4f : 0f;
        float iconW = (carryIcon != null) ? iconH * ((float) carryIcon.getWidth() / carryIcon.getHeight()) : 0f;
        font.getData().setScale(1.2f);
        layout.setText(font, pickupText);
        float iconTextGap = carryIcon != null ? -24f : 0f;
        float pickupBoxWidth = iconW + iconTextGap + layout.width + 10f;

        float barHeight = pauseBtnH, barY = pauseBtnY, barInnerPad = 14f, sectionGap = 16f;
        String correctStr = String.valueOf(correctCount), wrongStr = String.valueOf(wrongCount);
        String scoreStr = score > 0 ? "+" + score : String.valueOf(score);
        font.getData().setScale(1f);
        layout.setText(font, correctStr); float correctTextW = layout.width;
        layout.setText(font, wrongStr); float wrongTextW = layout.width;
        font.getData().setScale(1.3f);
        layout.setText(font, scoreStr); float scoreTextW = layout.width;
        font.getData().setScale(1f);
        float leftW = ICON_SIZE + 4f + correctTextW, rightW = wrongTextW + 4f + ICON_SIZE;
        float barWidth = barInnerPad + leftW + sectionGap + scoreTextW + sectionGap + rightW + barInnerPad;
        float barX = (WORLD_W - barWidth) / 2f;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        drawRoundedRect(shapeRenderer, timerBoxX, timerBoxY, timerBoxWidth, timerBoxHeight, timerBoxHeight / 2f);
        drawRoundedRect(shapeRenderer, pickupBoxX, pickupBoxY, pickupBoxWidth, pickupBoxHeight, pickupBoxHeight / 2f);
        drawRoundedRect(shapeRenderer, barX, barY, barWidth, barHeight, barHeight / 2f);
        drawRoundedRect(shapeRenderer, pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH, pauseBtnH / 2f);
        shapeRenderer.end();

        batch.begin();
        float clockSize = 28f;
        batch.draw(clockIcon, timerBoxX + 6, timerBoxY + (timerBoxHeight - clockSize) / 2f, clockSize, clockSize);
        float oldSX = font.getData().scaleX, oldSY = font.getData().scaleY;
        font.setColor(Color.BLACK);
        font.getData().setScale(1.5f);
        layout.setText(font, String.valueOf(Math.max(0, (int) timeLeft)));
        float numAreaX = timerBoxX + clockSize + 10, numAreaW = timerBoxWidth - (clockSize + 16);
        font.draw(batch, layout, numAreaX + (numAreaW - layout.width) / 2f, timerBoxY + (timerBoxHeight + layout.height) / 2f - 2);
        font.getData().setScale(oldSX, oldSY);

        font.setColor(Color.BLACK); font.getData().setScale(1.2f);
        if (carryIcon != null) {
            layout.setText(font, pickupText);
            float startX = pickupBoxX + (pickupBoxHeight / 2f) - (iconW / 2f);
            float iconX = startX;
            float iconY = pickupBoxY + (pickupBoxHeight - iconH) / 2f;
            float textX = iconX + iconW + iconTextGap;
            float textY = pickupBoxY + (pickupBoxHeight + layout.height) / 2f - 2f;

            batch.draw(carryIcon, iconX, iconY, iconW, iconH);
            font.draw(batch, layout, textX, textY);
        } else {
            layout.setText(font, pickupText);
            font.draw(batch, layout, pickupBoxX + (pickupBoxWidth - layout.width) / 2f, pickupBoxY + (pickupBoxHeight + layout.height) / 2f - 2);
        }
        font.getData().setScale(1f); font.setColor(Color.WHITE);

        font.setColor(Color.BLACK);
        float textBaseY = barY + (barHeight + layout.height) / 2f - 2;
        float tickX = barX + barInnerPad, tickY = barY + (barHeight - ICON_SIZE) / 2f;
        batch.draw(tickIcon, tickX + TICK_OFFSET_X, tickY + TICK_OFFSET_Y, ICON_SIZE, ICON_SIZE);
        layout.setText(font, correctStr);
        font.draw(batch, layout, tickX + ICON_SIZE + 4f, textBaseY);

        font.getData().setScale(1.3f); layout.setText(font, scoreStr);
        if (score > 0) font.setColor(0f, 0.55f, 0.1f, 1f);
        else if (score < 0) font.setColor(0.85f, 0.1f, 0.1f, 1f);
        else font.setColor(Color.BLACK);
        font.draw(batch, layout, barX + (barWidth - layout.width) / 2f, barY + (barHeight + layout.height) / 2f - 2);
        font.getData().setScale(1f);

        font.setColor(Color.BLACK); layout.setText(font, wrongStr);
        float crossX = barX + barWidth - barInnerPad - ICON_SIZE, crossY = barY + (barHeight - ICON_SIZE) / 2f;
        font.draw(batch, layout, crossX - 4f - layout.width, textBaseY);
        batch.draw(crossIcon, crossX + CROSS_OFFSET_X, crossY + CROSS_OFFSET_Y, ICON_SIZE, ICON_SIZE);

        font.setColor(Color.WHITE);
        batch.draw(pauseButtonIcon, pauseBtnX + 2f, pauseBtnY + 2f, pauseBtnW - 4f, pauseBtnH - 4f);
        batch.end();
    }

    private void drawRoundedRect(ShapeRenderer sr, float x, float y, float w, float h, float r) {
        sr.rect(x + r, y, w - 2*r, h); sr.rect(x, y + r, r, h - 2*r); sr.rect(x + w - r, y + r, r, h - 2*r);
        int s = 18;
        sr.arc(x + r, y + r, r, 180, 90, s); sr.arc(x + w - r, y + r, r, 270, 90, s);
        sr.arc(x + w - r, y + h - r, r, 0, 90, s); sr.arc(x + r, y + h - r, r, 90, 90, s);
    }

    public int getFinalCollisionCount() { return collisionManager.getPlayerBotCollisionCount(); }

    @Override public void resize(int w, int h) {
        WORLD_W = w; WORLD_H = h;
        pauseBtnX = WORLD_W - pauseBtnW - 18f; pauseBtnY = WORLD_H - pauseBtnH - 16f;
    }

    @Override public void hide() {}

    @Override public void dispose() {
        if (eventManager != null) eventManager.clearListeners();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (basketballBackground != null) basketballBackground.dispose();
        if (canteenBackground != null) canteenBackground.dispose();
        if (parkBackground != null) parkBackground.dispose();
        if (clockIcon != null) clockIcon.dispose();
        if (trashbagIcon != null) trashbagIcon.dispose();
        if (electronicIcon != null) electronicIcon.dispose();
        if (paperIcon != null) paperIcon.dispose();
        if (plasticIcon != null) plasticIcon.dispose();
        if (pauseButtonIcon != null) pauseButtonIcon.dispose();
        if (tickIcon != null) tickIcon.dispose();
        if (crossIcon != null) crossIcon.dispose();
        if (monsterWarningIcon != null) monsterWarningIcon.dispose();
        funFact.dispose();
        if (player != null) player.disposeTextures();
        for (Bot bot : bots) bot.disposeTextures();
        Bin.disposeAllTextures();
        Trash.disposeAllTextures();
    }
}
