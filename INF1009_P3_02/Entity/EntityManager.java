package INF1009_P3_02.Entity;

import INF1009_P3_02.Logging.GameEngineLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityManager {
    private Player player;
    private Bot bot;
    private final List<Bot> bots = new ArrayList<>();
    private float worldW, worldH;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Trash> trashItems = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    // Single factories instead of per-type registries
    private TrashFactory trashFactory;
    private ObstacleFactory obstacleFactory;
    private GameEngineLogger logger;

    private static final int MAX_SPAWN_ATTEMPTS = 300;

    public void setLogger(GameEngineLogger logger) {
        this.logger = logger;
    }

    public void addEntity(Entity e) {
        entities.add(e);
        if (e instanceof Player) player = (Player) e;
        if (e instanceof Bot) {
            Bot botEntity = (Bot) e;
            bots.add(botEntity);
            bot = bots.isEmpty() ? null : bots.get(0);
        }
        if (e instanceof Obstacle) obstacles.add((Obstacle) e);
        if (e instanceof Trash) trashItems.add((Trash) e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
        if (e == player) player = null;
        if (e instanceof Bot) {
            bots.remove(e);
            bot = bots.isEmpty() ? null : bots.get(0);
        }
        if (e instanceof Obstacle) obstacles.remove(e);
        if (e instanceof Trash) trashItems.remove(e);
    }

    public List<Entity> getAll() {
        return Collections.unmodifiableList(entities);
    }

    public void updateAll(float dt) {
        for (Entity e : entities) {
            e.update(dt);
        }
    }

    public List<Trash> getTrashItems() {
        return Collections.unmodifiableList(trashItems);
    }

    public Player getPlayer() { return player; }

    public Bot getBot() { return bot; }

    public List<Bot> getBots() {
        return Collections.unmodifiableList(bots);
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    public void updatePlayerPosition(float newX, float newY) {
        if (player != null) {
            player.setX(newX);
            player.setY(newY);
            player.clampToWorld();
        }
    }

    public void updateBotPosition(float newX, float newY) {
        if (bot != null) {
            bot.setX(newX);
            bot.setY(newY);
        }
    }

    public void updateBotPosition(Bot bot, float newX, float newY) {
        if (bot != null && bots.contains(bot)) {
            bot.setX(newX);
            bot.setY(newY);
        }
    }

    // ── World dimensions ──────────────────────────────────────────────
    public void setWorldDimensions(float worldW, float worldH) {
        this.worldW = worldW;
        this.worldH = worldH;
    }

    // ── Factory setters ───────────────────────────────────────────────
    public void setTrashFactory(TrashFactory factory) {
        this.trashFactory = factory;
    }

    public void setObstacleFactory(ObstacleFactory factory) {
        this.obstacleFactory = factory;
    }

    // ── Trash creation via single factory ─────────────────────────────
    public Trash createTrash(TrashType type) {
        if (trashFactory == null) {
            throw new IllegalStateException("TrashFactory has not been set on EntityManager");
        }
        return trashFactory.createTrash(type);
    }

    // ── Obstacle creation via factory ─────────────────────────────────
    public Obstacle createObstacle(ObstacleType type) {
        if (obstacleFactory == null) {
            throw new IllegalStateException("ObstacleFactory has not been set on EntityManager");
        }
        return obstacleFactory.createObstacle(type);
    }

    // ── Spawning ──────────────────────────────────────────────────────
    public void spawnInitialTrash() {
        for (TrashType type : TrashType.values()) {
            for (int i = 0; i < 3; i++) {
                Trash t = spawnNonOverlapping(type);
                if (t != null) addEntity(t);
            }
        }
    }

    public void respawnTrash(TrashType type) {
        Trash t = spawnNonOverlapping(type);
        if (t != null) addEntity(t);
    }

    /**
     * Spawns obstacles using the ObstacleFactory, ensuring they don't
     * overlap with the player or bot.
     */
    public void spawnObstacles(ObstacleType[] types) {
        for (ObstacleType type : types) {
            Obstacle o = spawnNonOverlappingObstacle(type);
            if (o != null) addEntity(o);
        }
    }

    private Trash spawnNonOverlapping(TrashType type) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            Trash candidate = createTrash(type);
            if (noOverlapWithEntities(candidate) &&
                trashItems.stream().noneMatch(t -> candidate.getBounds().overlaps(t.getBounds()))) {
                return candidate;
            }
        }
        logWarning("Failed to spawn " + type + " after " + MAX_SPAWN_ATTEMPTS + " attempts.");
        return null;
    }

    private Obstacle spawnNonOverlappingObstacle(ObstacleType type) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            Obstacle candidate = createObstacle(type);
            if (noOverlapWithEntities(candidate)) {
                return candidate;
            }
        }
        logWarning("Failed to spawn obstacle " + type + " after " + MAX_SPAWN_ATTEMPTS + " attempts.");
        return null;
    }

    /** Shared base check: no overlap with player, bot, or existing obstacles. */
    private boolean noOverlapWithEntities(Entity candidate) {
        if (player != null && candidate.getBounds().overlaps(player.getBounds())) return false;
        for (Bot b : bots)
            if (candidate.getBounds().overlaps(b.getBounds())) return false;
        for (Obstacle o : obstacles)
            if (candidate.getBounds().overlaps(o.getBounds())) return false;
        return true;
    }

    private void logWarning(String message) {
        if (logger != null) logger.warning("EntityManager: " + message);
        else System.out.println("EntityManager: " + message);
    }
}