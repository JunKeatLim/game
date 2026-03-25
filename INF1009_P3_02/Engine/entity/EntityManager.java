package INF1009_P3_02.Engine.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import INF1009_P3_02.Engine.logging.GameEngineLogger;
import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Bot;
import INF1009_P3_02.Game.entity.Player;
import INF1009_P3_02.Game.entity.Trash;
import INF1009_P3_02.Game.enumFolder.BinType;
import INF1009_P3_02.Game.enumFolder.TrashType;
import INF1009_P3_02.Game.factory.BinFactory;
import INF1009_P3_02.Game.factory.EntityFactory;

public class EntityManager {
    private Player player;
    private Bot bot;
    private final List<Bot> bots = new ArrayList<>();
    private float worldW, worldH;

    private final List<Bin> bins = new ArrayList<>();
    private final List<Trash> trashItems = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    // Single factories instead of per-type registries
    private EntityFactory entityFactory;
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
        if (e instanceof Bin) bins.add((Bin) e);
        if (e instanceof Trash) trashItems.add((Trash) e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
        if (e == player) player = null;
        if (e instanceof Bot) {
            bots.remove(e);
            bot = bots.isEmpty() ? null : bots.get(0);
        }
        if (e instanceof Bin) bins.remove(e);
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

    public List<Bin> getBins() {
        return Collections.unmodifiableList(bins);
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
        public void setEntityFactory(EntityFactory factory) {
        this.entityFactory = factory;
    }

    // ── Private helper factory check ─────────────────────────────────
    private void ensureFactorySet() {
        if (entityFactory == null) {
            throw new IllegalStateException("EntityFactory has not been set on EntityManager");
        }
    }

    // ── Trash creation via single factory ─────────────────────────────
    public Trash createTrash(TrashType type) {
        ensureFactorySet();
        return entityFactory.createTrash(type);
    }

    // ── Obstacle creation via factory ─────────────────────────────────
    public Bin createBin(BinType type) {
         ensureFactorySet();
        return entityFactory.createBin(type);
    }

    public Bin createBin(BinType type, float x, float y) {
        ensureFactorySet();
        return entityFactory.createBin(type, x, y);
    }

    // ── Spawning ──────────────────────────────────────────────────────
    public void spawnInitialTrash() {
        spawnInitialTrash(3);
    }

    public void spawnInitialTrash(int trashPerType) {
        int safeTrashPerType = Math.max(1, trashPerType);
        for (TrashType type : TrashType.values()) {
            for (int i = 0; i < safeTrashPerType; i++) {
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

    public void spawnBinBottomRow(BinType[] types) {
        ensureFactorySet();
        if (types == null || types.length == 0) return;
        BinFactory binFactory = entityFactory.getBinFactory();
        float boundsW = binFactory.getBoundsW();
        float boundsH = binFactory.getBoundsH();
        float drawH = binFactory.getDrawH();
        float gap = 44f;
        float totalWidth = types.length * boundsW + (types.length - 1) * gap;
        float startX = Math.max(0f, (worldW - totalWidth) / 2f);
        float bottomPadding = 12f;
        float y = Math.max(0f, bottomPadding + (drawH - boundsH) / 2f);

        for (int i = 0; i < types.length; i++) {
            float x = startX + i * (boundsW + gap);
            Bin bin = createBin(types[i], x, y);
            if (noOverlapWithEntities(bin)) {
                addEntity(bin);
            } else {
                logWarning("Failed to place bin " + types[i] + " in bottom row.");
            }
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

    private Bin spawnNonOverlappingBin(BinType type) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            Bin candidate = createBin(type);
            if (noOverlapWithEntities(candidate)) {
                return candidate;
            }
        }
        logWarning("Failed to spawn bin " + type + " after " + MAX_SPAWN_ATTEMPTS + " attempts.");
        return null;
    }

    /** Shared base check: no overlap with player, bot, or existing obstacles. */
    private boolean noOverlapWithEntities(Entity candidate) {
        if (player != null && candidate.getBounds().overlaps(player.getBounds())) return false;
        for (Bot b : bots)
            if (candidate.getBounds().overlaps(b.getBounds())) return false;
        for (Bin o : bins)
            if (candidate.getBounds().overlaps(o.getBounds())) return false;
        return true;
    }

    private void logWarning(String message) {
        if (logger != null) logger.warning("EntityManager: " + message);
        else System.out.println("EntityManager: " + message);
    }
}
