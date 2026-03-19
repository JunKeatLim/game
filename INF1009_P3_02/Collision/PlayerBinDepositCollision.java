package INF1009_P3_02.Collision;

import INF1009_P3_02.Entity.*;
import INF1009_P3_02.InputOutput.Speaker;

public class PlayerBinDepositCollision implements CollisionHandler {
    private final EntityManager entityManager;

    public PlayerBinDepositCollision(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Player)) return;
        if (!(b instanceof Obstacle)) return;

        Player player = (Player) a;
        Obstacle bin = (Obstacle) b;

        if (!player.getBounds().overlaps(bin.getBounds())) return;

        ctx.setIsOverlappingBin(true);
        if (ctx.wasOverlappingBin()) return;

        CarryState carry = player.getCarryState();
        TrashType carriedType = player.getCarriedTrashType();

        // If not carrying anything, depositing should do nothing
        if (carry == CarryState.NONE || carriedType == null) return;

        ObstacleType binType = bin.getType();

        // Determine score based on trash type and bin type
        int points = calculateScore(carriedType, binType);

        ctx.addScore(points);
        if (points < 0) {
            ctx.wrongBin = true;
        }

        // Respawn ONLY after deposit attempt
        entityManager.respawnTrash(carriedType);

        // Clear player's carried state
        player.setCarryState(CarryState.NONE);
        player.clearCarriedTrash();
    }

    /**
     * Scoring rules:
     *   Correct matching bin (paper→paper, plastic→plastic, electronic→electronic, trashbag→trash): +3
     *   Wrong recyclable bin (e.g. paper→plastic, paper→electronic): +1
     *   Recyclable into trash bin: -1
     *   Trashbag into any recyclable bin: -1
     */
    private int calculateScore(TrashType trashType, ObstacleType binType) {
        switch (trashType) {
            case PAPER:
                if (binType == ObstacleType.PAPER)      return 3;   // correct bin
                if (binType == ObstacleType.PLASTIC)     return 1;   // wrong recyclable bin
                if (binType == ObstacleType.ELECTRONIC)  return 1;   // wrong recyclable bin
                if (binType == ObstacleType.TRASH)       return -1;  // recyclable into trash bin
                break;

            case PLASTIC:
                if (binType == ObstacleType.PLASTIC)     return 3;
                if (binType == ObstacleType.PAPER)       return 1;
                if (binType == ObstacleType.ELECTRONIC)  return 1;
                if (binType == ObstacleType.TRASH)       return -1;
                break;

            case ELECTRONIC:
                if (binType == ObstacleType.ELECTRONIC)  return 3;
                if (binType == ObstacleType.PAPER)       return 1;
                if (binType == ObstacleType.PLASTIC)     return 1;
                if (binType == ObstacleType.TRASH)       return -1;
                break;

            case TRASHBAG:
                if (binType == ObstacleType.TRASH)       return 3;   // correct bin
                // trashbag into any recyclable bin
                return -1;
        }

        return 0;
    }
}