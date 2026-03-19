/*package INF1009_P3_02.Scene;


import INF1009_P3_02.BackgroundChoice;
import INF1009_P3_02.Collision.CollisionContext;
import INF1009_P3_02.Collision.CollisionManager;
import INF1009_P3_02.Entity.*;
import INF1009_P3_02.InputOutput.InputOutputManager;
import INF1009_P3_02.Logging.GameEngineLogger;
import INF1009_P3_02.Movement.MovementManager;
import INF1009_P3_02.SettingsData;
import INF1009_P3_02.SimulationConfig;
import INF1009_P3_02.Trash.TrashManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SimulationScene extends Scene {


    private final SceneManager sceneManager;
    private final SettingsData settings;
    private final SimulationConfig config;


    // rendering
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture basketballBackground;
    private Texture canteenBackground;
    private Texture parkBackground;


    private float heartbeatTimer = 0f;


    // managers
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private InputOutputManager inputOutputManager;
    private GameEngineLogger loggerManager;
    private TrashManager trashManager;



    // entities
    private Player player;
    private Bot bot;
    private List<Obstacle> obstacles;
    private boolean simulationEnded = false;


    // world / state
    private float WORLD_W, WORLD_H;
    private float timeLeft;
    private int playerBotCollisionCount = 0;
    private int score = 0;


    private static final int OBSTACLE_COUNT = 4;
    private boolean initialized = false;


    public SimulationScene(SceneManager sceneManager, SettingsData settings, SimulationConfig config) {
        this.sceneManager = sceneManager;
        this.settings = settings;
        this.config = config;
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
            // Clear UI input processor when resuming to gameplay.
            simulationEnded = false;


            Gdx.input.setInputProcessor(null);
            if (inputOutputManager != null) {
                inputOutputManager.setControlScheme(settings.controlScheme);
            }
            return;
        }
        initialized = true;


        loggerManager = new GameEngineLogger("engine_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt");
        loggerManager.info("=== Simulation Started === Duration: " + config.durationSeconds + "s | Background: " + config.background + " | Shape: " +
            config.playerShape);



        sr = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();
        basketballBackground = new Texture(Gdx.files.internal("backgrounds/school_basketball.jpg"));
        canteenBackground = new Texture(Gdx.files.internal("backgrounds/school_canteen.jpg"));
        parkBackground = new Texture(Gdx.files.internal("backgrounds/school_park.jpg"));


        WORLD_W = Gdx.graphics.getWidth();
        WORLD_H = Gdx.graphics.getHeight();


        timeLeft = config.durationSeconds; // make sure your config has this


        entityManager = new EntityManager();
        trashManager = new TrashManager(entityManager, WORLD_W, WORLD_H, 40f, 40f, 120f); //Load all trash entity
        trashManager.spawnInitialTrash();
        movementManager = new MovementManager(entityManager);


        inputOutputManager = new InputOutputManager(settings, movementManager);
        inputOutputManager.setControlScheme(settings.controlScheme);
        inputOutputManager.setEscapeListener(() -> {
            sceneManager.goToPauseScene();
        });
        collisionManager = new CollisionManager(
            inputOutputManager.getSpeaker(),
            trashManager
        );


        // Create Player
        player = new Player(
            400, 300,
            300, 40, 200,
            WORLD_W, WORLD_H
        );
        player.loadTextures();


        // Create Bot
        bot = new Bot(
            500, 300,
            150,
            40, 30,
            WORLD_W, WORLD_H
        );


        // Create Obstacles
        obstacles = new ArrayList<>();


        ObstacleType[] types = {
            ObstacleType.TRASH,
            ObstacleType.ELECTRONIC,
            ObstacleType.PAPER,
            ObstacleType.PLASTIC
        };


        float boundsW = 80;
        float boundsH = 25;
        float drawH = 120; // visual size; tune this


        for (int i = 0; i < OBSTACLE_COUNT; i++) {
            Obstacle o;
            do {
                o = Obstacle.spawnRandom(WORLD_W, WORLD_H, boundsW, boundsH, drawH, types[i]);
            } while (o.getBounds().overlaps(player.getBounds()) || o.getBounds().overlaps(bot.getBounds()));


            obstacles.add(o);
        }


        entityManager.addEntity(player);
        movementManager.syncPlayerMovementFromEntity();


        entityManager.addEntity(bot);
        for (Obstacle o : obstacles) entityManager.addEntity(o);
    }


    @Override
    public void update(float dt) {
        timeLeft -= dt;


        heartbeatTimer += dt;
        if (heartbeatTimer >= 10f) {
            heartbeatTimer = 0f;
            loggerManager.info("Heartbeat — Player position: (" + (int)player.getX() + ", " + (int)player.getY() + ") | Time left: " + (int)timeLeft + "s");
        }


        // Keep control scheme synced with settings (e.g., after pause changes)
        inputOutputManager.setControlScheme(settings.controlScheme);


        // Save old positions BEFORE moving (for collision resolution)
        float pOldX = player.getX();
        float pOldY = player.getY();
        float bOldX = bot.getX();
        float bOldY = bot.getY();


        inputOutputManager.update(dt);


        // 1) Update movement (player and bot)
        movementManager.update(dt);


        // 2) Update bounds
        entityManager.updateAll(dt);


        // 3) Resolve collisions
        CollisionContext ctx = collisionManager.resolve(player, bot, obstacles, pOldX, pOldY, bOldX, bOldY, dt);
        trashManager.update(dt);
        score += ctx.getScore();


        if (ctx.playerCollidedWithObstacle || ctx.botCollidedWithPlayer) {
            movementManager.syncPlayerMovementFromEntity();
        }



        if (ctx.botCollidedWithObstacle || ctx.botCollidedWithPlayer) {
            movementManager.handleCollision();
        }


        int prevCount = playerBotCollisionCount;
        playerBotCollisionCount = collisionManager.getPlayerBotCollisionCount();


        if (playerBotCollisionCount > prevCount && loggerManager != null) {
            loggerManager.info("Player-Bot collision #" + playerBotCollisionCount + " at Player(" + (int) player.getX() + "," + (int) player.getY() + ")");
        }


        if (loggerManager != null) {
            loggerManager.info("Total Collisions: " + playerBotCollisionCount);
        }


        if (loggerManager != null) {
            loggerManager.info("Total Score: " + score);
        }


        // End
        if (timeLeft <= 0f && !simulationEnded) {
            simulationEnded = true;
            if (loggerManager != null) {
                loggerManager.info("=== Simulation Ended === Total Collisions: " + playerBotCollisionCount + " Total Score: " + score);
            }
            sceneManager.goToEndScene(score);
            return;
        }
    }
    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        drawBackground(config.background);


        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entityManager.getAll()) {
            if (e instanceof Player) continue;
            if (e instanceof Obstacle) continue; // obstacles now sprite-based
            e.draw(sr);
        }
        sr.end();


        batch.begin();
        player.draw(batch);
        for (Obstacle o : obstacles) {
            o.draw(batch);
        }
        for (Trash t : entityManager.getTrashItems()) {
            t.draw(batch);
        }
        batch.end();


        drawHUD();
        renderBrightnessOverlay();
    }


    private void drawBackground(BackgroundChoice bg) {
        Texture selectedBackground;
        switch (bg) {
            case SCHOOL_BASKETBALL:
                selectedBackground = basketballBackground;
                break;


            case SCHOOL_CANTEEN:
                selectedBackground = canteenBackground;
                break;


            case SCHOOL_PARK:
                selectedBackground = parkBackground;
                break;


            default:
                selectedBackground = basketballBackground;
                break;
        }


        batch.begin();
        batch.draw(selectedBackground, 0, 0, WORLD_W, WORLD_H);
        batch.end();
    }


    private void drawHUD() {
        batch.begin();


        String text = "Score: " + score + " Collisions: " + playerBotCollisionCount + "  Time: " + Math.max(0, (int)timeLeft);
        layout.setText(font, text);


        float padding = 10f;
        font.draw(batch, layout, WORLD_W - layout.width - padding, WORLD_H - padding);


        layout.setText(font, "Press ESC to pause");
        font.draw(batch, layout, padding, WORLD_H - padding);


        batch.end();
    }


    public int getFinalCollisionCount() {
        return collisionManager.getPlayerBotCollisionCount();
    }


    @Override public void resize(int width, int height) {
        WORLD_W = width;
        WORLD_H = height;
    }


    @Override
    public void hide() { }


    @Override
    public void dispose() {
        if (loggerManager != null) loggerManager.close();
        if (sr != null) sr.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (basketballBackground != null) basketballBackground.dispose();
        if (canteenBackground != null) canteenBackground.dispose();
        if (parkBackground != null) parkBackground.dispose();
        if (player != null) player.disposeTextures();
        if (obstacles != null) {
            for (Obstacle o : obstacles) o.disposeTexture();
        }
        Trash.disposeAllTextures();
    }
}*/