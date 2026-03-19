package INF1009_P3_02.Scene;


import INF1009_P3_02.BackgroundChoice;
import INF1009_P3_02.Collision.CollisionContext;
import INF1009_P3_02.Collision.CollisionManager;
import INF1009_P3_02.Entity.*;
import INF1009_P3_02.InputOutput.InputOutputManager;
import INF1009_P3_02.Logging.GameEngineLogger;
import INF1009_P3_02.Movement.MovementManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationScene extends Scene {
    private static final float PLAYER_COLLISION_SIZE = 40f;
    private static final float PLAYER_DRAW_SIZE = 240f;
    private static final float TRASH_BOUNDS_SIZE = 40f;
    private static final float TRASH_DRAW_SIZE = 90f;
    private static final float POPUP_DURATION = 1.2f;
    private static final float PICKUP_POPUP_DURATION = 0.7f;
    private static final float DROP_POPUP_DURATION = 1.5f;
    private static final float PLAYER_POPUP_OFFSET_Y = -34f;
    private static final float BIN_RESULT_ICON_SIZE = 96f;
    private static final float MONSTER_WARNING_ICON_SIZE = 400f;
    private static final float PICKUP_POPUP_SCALE = 1.4f;
    private static final FunFact FUN_FACT_POPUP = FunFact.getInstance();

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

    // popup text feedback
    private String popupText = null;
    private Texture popupImage = null;
    private float popupTimer = 0f;
    private Color popupColor = Color.WHITE.cpy();
    private boolean popupAbovePlayer = false;

    // Per-icon draw offsets — adjust these to compensate for different PNG crops.
    // Positive X moves right, positive Y moves up (LibGDX Y-up).
    private static final float TICK_OFFSET_X  =  0f;
    private static final float TICK_OFFSET_Y  =  0f;
    private static final float CROSS_OFFSET_X =  0f;
    private static final float CROSS_OFFSET_Y =  0f;
    private static final float ICON_SIZE      = 22f; // shared draw size for both icons

    private float heartbeatTimer = 0f;

    // managers
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private InputOutputManager inputOutputManager;
    private final GameEngineLogger logger;

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

    private CarryState previousCarryState = CarryState.NONE;

public SimulationScene(SceneManager sceneManager, SettingsData settings, SimulationConfig config, GameEngineLogger logger) {
        this.sceneManager = sceneManager;
        this.settings = settings;
        this.config = config;
        this.logger = logger;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    @Override
    protected void buildUI() {
        // No Scene2D UI for this scene.
    }

    @Override
    public void show() {
        if (initialized) {
            simulationEnded = false;
            Gdx.input.setInputProcessor(pauseInputAdapter);
            if (inputOutputManager != null) {
                inputOutputManager.setControlScheme(settings.controlScheme);
            }
            return;
        }
        initialized = true;

        // Set up input processor for pause button
        pauseInputAdapter = new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = Gdx.graphics.getHeight() - screenY; // invert y
                if (FUN_FACT_POPUP.isVisible()) {
                    return FUN_FACT_POPUP.handleTouch(screenX, y);
                }
                if (screenX >= pauseBtnX && screenX <= pauseBtnX + pauseBtnW &&
                    y >= pauseBtnY && y <= pauseBtnY + pauseBtnH) {
                    sceneManager.goToPauseScene();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (FUN_FACT_POPUP.isVisible()) {
                    if (keycode == com.badlogic.gdx.Input.Keys.ENTER
                        || keycode == com.badlogic.gdx.Input.Keys.SPACE
                        || keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                        FUN_FACT_POPUP.dismiss();
                    }
                    return true;
                }
                if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    sceneManager.goToPauseScene();
                    return true;
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(pauseInputAdapter);

        logger.info("=== Simulation Started === Duration: " + config.durationSeconds + "s | Background: " + config.background);

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

        pauseBtnW = 36f;
        pauseBtnH = 36f;
        pauseBtnX = WORLD_W - pauseBtnW - 18f;
        pauseBtnY = WORLD_H - pauseBtnH - 16f;

        timeLeft = config.durationSeconds;

        // Set up EntityManager with factories
        entityManager = new EntityManager();
        entityManager.setLogger(logger);
        entityManager.setWorldDimensions(WORLD_W, WORLD_H);
        entityManager.setTrashFactory(new TrashFactory(
            WORLD_W,
            WORLD_H,
            TRASH_BOUNDS_SIZE,
            TRASH_BOUNDS_SIZE,
            TRASH_DRAW_SIZE
        ));
        entityManager.setObstacleFactory(new ObstacleFactory(WORLD_W, WORLD_H, 80f, 25f, 120f));

        // Create Player using PlayerFactory
        PlayerFactory playerFactory = new PlayerFactory(WORLD_W, WORLD_H);
        player = playerFactory.createPlayer(400, 300, 300, PLAYER_COLLISION_SIZE, PLAYER_DRAW_SIZE);

        int botCount = Math.max(0, config.getBotCount());
        float[][] botStartPositions = {
            {500f, 300f},
            {700f, 430f}
        };
        bots.clear();
        for (int i = 0; i < botCount; i++) {
            float[] pos = botStartPositions[Math.min(i, botStartPositions.length - 1)];
            bots.add(new Bot(pos[0], pos[1], 150, 200, 150, WORLD_W, WORLD_H));
        }

        // Add player and bots first so obstacle/trash spawning can check overlap
        entityManager.addEntity(player);
        for (Bot bot : bots) {
            entityManager.addEntity(bot);
        }

        // Spawn obstacles via factory (through EntityManager)
        ObstacleType[] obstacleTypes = {
            ObstacleType.TRASH,
            ObstacleType.ELECTRONIC,
            ObstacleType.PAPER,
            ObstacleType.PLASTIC
        };
        entityManager.spawnObstacles(obstacleTypes);

        // Spawn initial trash
        entityManager.spawnInitialTrash();

        movementManager = new MovementManager(entityManager);

        inputOutputManager = new InputOutputManager(settings, movementManager);
        inputOutputManager.setControlScheme(settings.controlScheme);
        inputOutputManager.setEscapeListener(() -> {
            sceneManager.goToPauseScene();
        });

        // CollisionManager
        collisionManager = new CollisionManager(inputOutputManager.getSpeaker(), entityManager, sceneManager.getLogger());

        movementManager.syncPlayerMovementFromEntity();

        previousCarryState = player.getCarryState();
    }

    @Override
    public void update(float dt) {
        if (FUN_FACT_POPUP.isVisible()) {
            FUN_FACT_POPUP.update(dt);
            if (FUN_FACT_POPUP.isVisible()) {
                return;
            }
        }

        timeLeft -= dt;

        heartbeatTimer += dt;
        if (heartbeatTimer >= 10f) {
            heartbeatTimer = 0f;
            logger.info("Heartbeat — Player position: (" + (int)player.getX() + ", " + (int)player.getY() + ") | Time left: " + (int)timeLeft + "s");
            logger.info("Total Collisions: " + playerBotCollisionCount);
            logger.info("Total Score: " + score);        
        }

        inputOutputManager.setControlScheme(settings.controlScheme);

        float pOldX = player.getX();
        float pOldY = player.getY();
        Map<Bot, float[]> botOldPositions = new HashMap<>();
        for (Bot bot : bots) {
            botOldPositions.put(bot, new float[] { bot.getX(), bot.getY() });
        }
        TrashType carriedBeforeCollision = player.getCarriedTrashType();

        inputOutputManager.update(dt);
        movementManager.update(dt);
        entityManager.updateAll(dt);

        List<Obstacle> obstacles = entityManager.getObstacles();

        CollisionContext ctx = collisionManager.resolve(player, bots, obstacles, pOldX, pOldY, botOldPositions, dt);

        handleTrashPickup();

        if (ctx.getScore() > 0) {
            correctCount++;
            score++;
            onCorrect();
            inputOutputManager.playCorrectSound();
        }

        if (ctx.wrongBin) {
            wrongCount++;
            score--;
            onWrong();
            inputOutputManager.playWrongSound();
        }

        if (ctx.playerBotOverlappingNow && carriedBeforeCollision != null) {
            onDrop();
        }

        updatePopup(dt);

        CarryState currentCarryState = player.getCarryState();
        if (previousCarryState == CarryState.NONE && currentCarryState != CarryState.NONE) {
            inputOutputManager.playPickupSound();
        }
        previousCarryState = currentCarryState;

        if (ctx.playerCollidedWithObstacle || ctx.botCollidedWithPlayer) {
            movementManager.syncPlayerMovementFromEntity();
        }

        int prevCount = playerBotCollisionCount;
        playerBotCollisionCount = collisionManager.getPlayerBotCollisionCount();

        if (playerBotCollisionCount > prevCount) {
            logger.info("Player-Bot collision #" + playerBotCollisionCount + " at Player(" + (int) player.getX() + "," + (int) player.getY() + ")");
        }

        if (timeLeft <= 0f && !simulationEnded) {
            simulationEnded = true;
            logger.info("=== Simulation Ended === Total Collisions: " + playerBotCollisionCount + " Total Score: " + score);
            sceneManager.goToEndScene(score);
            return;
        }
    }

    private void onPickup(TrashType type) {
        popupText = "Picked up: " + formatTrashType(type);
        popupImage = null;
        popupTimer = PICKUP_POPUP_DURATION;
        popupColor.set(1f, 0.95f, 0.2f, 1f);
        popupAbovePlayer = true;
    }

    private void onDrop() {
        popupText = null;
        popupImage = monsterWarningIcon;
        popupTimer = DROP_POPUP_DURATION;
        popupColor.set(Color.WHITE);
        popupAbovePlayer = false;
    }

    private void onCorrect() {
        popupText = null;
        popupImage = tickIcon;
        popupTimer = POPUP_DURATION;
        popupAbovePlayer = false;
    }

    private void onWrong() {
        popupText = null;
        popupImage = crossIcon;
        popupTimer = POPUP_DURATION;
        popupAbovePlayer = false;
        pendingWrongFunFact = true;
    }

    private void updatePopup(float dt) {
        if (popupTimer > 0f) {
            popupTimer -= dt;
            if (popupTimer <= 0f) {
                popupTimer = 0f;
                popupText = null;
                popupImage = null;
                popupColor.set(Color.WHITE);
                popupAbovePlayer = false;
                if (pendingWrongFunFact) {
                    FUN_FACT_POPUP.showWrongBinFact();
                    pendingWrongFunFact = false;
                }
            }
        }
    }

    private void drawPopup() {
        if (popupTimer <= 0f) return;

        float alpha = popupTimer / POPUP_DURATION;
        if (popupImage != null) {
            float popupImageSize = popupImage == monsterWarningIcon
                ? MONSTER_WARNING_ICON_SIZE
                : BIN_RESULT_ICON_SIZE;
            batch.begin();
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(
                popupImage,
                (WORLD_W - popupImageSize) / 2f,
                (WORLD_H - popupImageSize) / 2f,
                popupImageSize,
                popupImageSize
            );
            batch.setColor(Color.WHITE);
            batch.end();
            return;
        }

        if (popupText == null) return;

        float fontScale = popupAbovePlayer ? PICKUP_POPUP_SCALE : 1f;
        font.getData().setScale(fontScale);
        layout.setText(font, popupText);
        float popupX;
        float popupY;

        if (popupAbovePlayer) {
            float playerCenterX = player.getX();
            float playerTopY = player.getY() + PLAYER_DRAW_SIZE / 2f;
            popupX = playerCenterX - layout.width / 2f;
            popupY = playerTopY + PLAYER_POPUP_OFFSET_Y;
        } else {
            popupX = (WORLD_W - layout.width) / 2f;
            popupY = WORLD_H * 0.62f;
        }

        batch.begin();
        font.setColor(popupColor.r, popupColor.g, popupColor.b, alpha);
        font.draw(batch, layout, popupX, popupY);
        font.setColor(Color.WHITE);
        batch.end();
        font.getData().setScale(1f);
    }

    private String formatTrashType(TrashType type) {
        switch (type) {
            case PAPER:      return "Paper";
            case PLASTIC:    return "Plastic";
            case ELECTRONIC: return "Electronic";
            case TRASHBAG:   return "Trash Bag";
            default:         return type.name();
        }
    }

    private Texture getTrashIcon(TrashType type) {
    switch (type) {
        case PAPER:      return paperIcon;
        case PLASTIC:    return plasticIcon;
        case ELECTRONIC: return electronicIcon;
        case TRASHBAG:   return trashbagIcon;
        default:         return null;
    }
}

    private void handleTrashPickup() {
        if (player.getCarryState() == CarryState.NONE) {
            for (Trash t : entityManager.getTrashItems()) {
                if (player.getBounds().overlaps(t.getBounds())) {
                    TrashType type = t.getType();
                    entityManager.removeEntity(t);
                    player.setCarriedTrashType(type);
                    player.setCarryState(type == TrashType.TRASHBAG ? CarryState.TRASH : CarryState.RECYCLE);
                    onPickup(type);
                    break;
                }
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground(config.background);

        batch.begin();
        player.draw(batch);
        for (Bot bot : bots) {
            bot.draw(batch);
        }
        for (Obstacle o : entityManager.getObstacles()) {
            o.draw(batch);
        }
        for (Trash t : entityManager.getTrashItems()) {
            t.draw(batch);
        }
        batch.end();

        drawPopup();
        drawHUD();
        FUN_FACT_POPUP.draw(shapeRenderer, batch, font, layout, WORLD_W, WORLD_H);
        renderBrightnessOverlay();
    }

    private void drawBackground(BackgroundChoice bg) {
        Texture selectedBackground;
        switch (bg) {
            case SCHOOL_BASKETBALL: selectedBackground = basketballBackground; break;
            case SCHOOL_CANTEEN:    selectedBackground = canteenBackground;    break;
            case SCHOOL_PARK:       selectedBackground = parkBackground;       break;
            default:                selectedBackground = basketballBackground; break;
        }
        batch.begin();
        batch.draw(selectedBackground, 0, 0, WORLD_W, WORLD_H);
        batch.end();
    }

    private void drawHUD() {
        float padding = 10f;

        // Timer box dimensions
        float timerBoxX = padding;
        float timerBoxY = pauseBtnY;
        float timerBoxWidth = 72f;
        float timerBoxHeight = pauseBtnH;

        // Pickup box dimensions
        float pickupBoxX = timerBoxX + timerBoxWidth + padding;;
        float pickupBoxY = pauseBtnY;
        float pickupBoxHeight = pauseBtnH;
        Texture carryIcon = player.getCarriedTrashType() != null ? getTrashIcon(player.getCarriedTrashType()) : null;
        String pickupText = player.getCarriedTrashType() != null ? formatTrashType(player.getCarriedTrashType()) : "Empty";

        float iconH = pauseBtnH - 4f;
        float iconW = 0f;
        if (carryIcon != null) {
            float aspect = (float) carryIcon.getWidth() / carryIcon.getHeight();
            iconW = iconH * aspect;
        }
        font.getData().setScale(1f);
        layout.setText(font, pickupText);
        float pickupBoxWidth = 6f + iconW + (iconW > 0 ? 4f : 0f) + layout.width + 6f; 

        // ── Centered score bar ───────────────────────────────────────────
        float barHeight = pauseBtnH;
        float barY = pauseBtnY;
        float barInnerPad = 14f;
        float sectionGap = 16f;

        String correctStr = String.valueOf(correctCount);
        String wrongStr   = String.valueOf(wrongCount);
        String scoreStr   = score > 0 ? "+" + score : String.valueOf(score);

        font.getData().setScale(1f);
        layout.setText(font, correctStr); float correctTextW = layout.width;
        layout.setText(font, wrongStr);   float wrongTextW   = layout.width;
        font.getData().setScale(1.3f);
        layout.setText(font, scoreStr);   float scoreTextW = layout.width;
        font.getData().setScale(1f);

        float leftW  = ICON_SIZE + 4f + correctTextW;
        float rightW = wrongTextW + 4f + ICON_SIZE;
        float barWidth = barInnerPad + leftW + sectionGap + scoreTextW + sectionGap + rightW + barInnerPad;
        float barX = (WORLD_W - barWidth) / 2f;

        // Draw all white backgrounds in one ShapeRenderer pass
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        drawRoundedRect(shapeRenderer, timerBoxX, timerBoxY, timerBoxWidth, timerBoxHeight, timerBoxHeight / 2f);
        drawRoundedRect(shapeRenderer, pickupBoxX, pickupBoxY, pickupBoxWidth, pickupBoxHeight, pickupBoxHeight / 2f);
        drawRoundedRect(shapeRenderer, barX, barY, barWidth, barHeight, barHeight / 2f);
        drawRoundedRect(shapeRenderer, pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH, pauseBtnH / 2f);
        shapeRenderer.end();

        // Draw all icons and text in one batch pass
        batch.begin();

        // Clock icon
        float clockSize = 28f;
        float clockY = timerBoxY + (timerBoxHeight - clockSize) / 2f;
        batch.draw(clockIcon, timerBoxX + 6, clockY, clockSize, clockSize);

        // Timer number
        font.setColor(Color.BLACK);
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        font.getData().setScale(1.5f);
        String timeText = String.valueOf(Math.max(0, (int) timeLeft));
        layout.setText(font, timeText);
        float numberAreaX = timerBoxX + clockSize + 10;
        float numberAreaWidth = timerBoxWidth - (clockSize + 16);
        float numberX = numberAreaX + (numberAreaWidth - layout.width) / 2f;
        float numberY = timerBoxY + (timerBoxHeight + layout.height) / 2f - 2;
        font.draw(batch, layout, numberX, numberY);
        font.getData().setScale(oldScaleX, oldScaleY);

        // Pickup box text
        font.setColor(Color.BLACK);
        font.getData().setScale(1f);
        if (carryIcon != null) {
            float iconX = pickupBoxX + 6f;
            float iconY = pickupBoxY + (pickupBoxHeight - iconH) / 2f;
            batch.draw(carryIcon, iconX, iconY, iconW, iconH);
            layout.setText(font, pickupText);
            float textX = iconX + iconW + 4f;
            float textY = pickupBoxY + (pickupBoxHeight + layout.height) / 2f - 2;
            font.draw(batch, layout, textX, textY);
        } else {
            layout.setText(font, pickupText);
            float pickupNumberX = pickupBoxX + (pickupBoxWidth - layout.width) / 2f;
            float pickupNumberY = pickupBoxY + (pickupBoxHeight + layout.height) / 2f - 2;
            font.draw(batch, layout, pickupNumberX, pickupNumberY);
        }
        font.getData().setScale(oldScaleX, oldScaleY);
        font.setColor(Color.WHITE);
                
        // ── Score bar content ────────────────────────────────────────────
        font.setColor(Color.BLACK);
        float textBaseY = barY + (barHeight + layout.height) / 2f - 2;

        // Left: tick icon + correctCount
        float tickX = barX + barInnerPad;
        float tickY = barY + (barHeight - ICON_SIZE) / 2f;
        batch.draw(tickIcon, tickX + TICK_OFFSET_X, tickY + TICK_OFFSET_Y, ICON_SIZE, ICON_SIZE);
        layout.setText(font, correctStr);
        font.draw(batch, layout, tickX + ICON_SIZE + 4f, textBaseY);

        // Center: score (coloured, slightly larger)
        font.getData().setScale(1.3f);
        layout.setText(font, scoreStr);
        if (score > 0)      font.setColor(0f, 0.55f, 0.1f, 1f);
        else if (score < 0) font.setColor(0.85f, 0.1f, 0.1f, 1f);
        else                font.setColor(Color.BLACK);
        float scoreCenterY = barY + (barHeight + layout.height) / 2f - 2;
        font.draw(batch, layout, barX + (barWidth - layout.width) / 2f, scoreCenterY);
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
        batch.draw(pauseButtonIcon, pauseBtnX + 2f, pauseBtnY + 2f, pauseBtnW - 4f, pauseBtnH - 4f);

        batch.end();
    }

    // Helper to draw a filled rounded rectangle using ShapeRenderer
    private void drawRoundedRect(com.badlogic.gdx.graphics.glutils.ShapeRenderer sr, float x, float y, float w, float h, float r) {
        // Center rectangle
        sr.rect(x + r, y, w - 2*r, h);
        // Side rectangles
        sr.rect(x, y + r, r, h - 2*r);
        sr.rect(x + w - r, y + r, r, h - 2*r);
        // Four corners (quarter circles)
        int segments = 18;
        sr.arc(x + r, y + r, r, 180, 90, segments); // bottom left
        sr.arc(x + w - r, y + r, r, 270, 90, segments); // bottom right
        sr.arc(x + w - r, y + h - r, r, 0, 90, segments); // top right
        sr.arc(x + r, y + h - r, r, 90, 90, segments); // top left
    }
    
    public int getFinalCollisionCount() {
        return collisionManager.getPlayerBotCollisionCount();
    }

    @Override public void resize(int width, int height) {
        WORLD_W = width;
        WORLD_H = height;
        pauseBtnX = WORLD_W - pauseBtnW - 18f;
        pauseBtnY = WORLD_H - pauseBtnH - 16f;
    }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
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
        FUN_FACT_POPUP.dispose();
        if (player != null) player.disposeTextures();
        for (Bot bot : bots) {
            bot.disposeTextures();
        }
        Obstacle.disposeAllTextures();
        Trash.disposeAllTextures();
    }
}