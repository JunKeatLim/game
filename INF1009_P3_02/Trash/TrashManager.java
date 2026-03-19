/*package INF1009_P3_02.Trash;

import INF1009_P3_02.Entity.*;

public class TrashManager {

    private final EntityManager em;
    private final float worldW;
    private final float worldH;

    // Tunables (separate from collision system)
    private final float trashBoundsW;
    private final float trashBoundsH;
    private final float trashDrawH;

    // Optional: cap attempts so we never infinite-loop in a crowded map
    private static final int MAX_SPAWN_ATTEMPTS = 300;

    public TrashManager(EntityManager em, float worldW, float worldH,
                        float trashBoundsW, float trashBoundsH, float trashDrawH) {
        this.em = em;
        this.worldW = worldW;
        this.worldH = worldH;
        this.trashBoundsW = trashBoundsW;
        this.trashBoundsH = trashBoundsH;
        this.trashDrawH = trashDrawH;
    }

    public void spawnInitialTrash() {
        TrashType[] types = {
            TrashType.PAPER,
            TrashType.PLASTIC,
            TrashType.TRASHBAG,
            TrashType.ELECTRONIC
        };

        for (TrashType type : types) {
            for (int i = 0; i < 3; i++) {
                Trash t = spawnNonOverlapping(type);
                if (t != null) em.addEntity(t);
            }
        }
    }

    public void update(float dt) {
        Player player = em.getPlayer();
        if (player == null) return;

        // 1) carrying one already → no pickup
        if (player.getCarryState() != CarryState.NONE) return;

        // Find one collected trash (collect at most 1 per frame)
        Trash collected = null;
        for (Trash t : em.getTrashItems()) {
            if (player.getBounds().overlaps(t.getBounds())) {
                collected = t;
                break;
            }
        }

        if (collected == null) return;

        TrashType type = collected.getType();

        em.removeEntity(collected);

        player.setCarriedTrashType(type);

        applyCarryState(player, type);
    }

    private void applyCarryState(Player player, TrashType type) {
        // TRASHBAG changes player to Trash_* sprites
        if (type == TrashType.TRASHBAG) {
            player.setCarryState(CarryState.TRASH);
        } else {
            // PAPER/PLASTIC/ELECTRONIC (and bottle later) => Recycle_* sprites
            player.setCarryState(CarryState.RECYCLE);
        }
    }

    /** Spawn trash of a given type in a valid location (no overlap with obstacles/trash/player/bot). */
    /*private Trash spawnNonOverlapping(TrashType type) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {

            Trash candidate = Trash.spawnRandom(
                worldW, worldH,
                trashBoundsW, trashBoundsH,
                trashDrawH,
                type
            );

            if (isSpawnValid(candidate)) {
                return candidate;
            }
        }

        // Map too crowded; fail gracefully
        System.out.println("TrashManager: Failed to spawn " + type + " after attempts.");
        return null;
    }

    private boolean isSpawnValid(Trash candidate) {
        // Avoid player & bot
        Player p = em.getPlayer();
        if (p != null && candidate.getBounds().overlaps(p.getBounds())) return false;

        Bot b = em.getBot();
        if (b != null && candidate.getBounds().overlaps(b.getBounds())) return false;

        // Avoid obstacles
        for (Obstacle o : em.getObstacles()) {
            if (candidate.getBounds().overlaps(o.getBounds())) return false;
        }

        // Avoid other trash
        for (Trash t : em.getTrashItems()) {
            if (candidate.getBounds().overlaps(t.getBounds())) return false;
        }

        return true;
    }

    public void respawnTrash(TrashType type) {
        Trash t = spawnNonOverlapping(type);
        if (t != null) em.addEntity(t);
    }
}*/
