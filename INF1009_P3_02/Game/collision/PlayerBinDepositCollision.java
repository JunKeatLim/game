package INF1009_P3_02.Game.collision;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.collision.CollisionContext;
import INF1009_P3_02.Engine.collision.CollisionHandler;
import INF1009_P3_02.Engine.entity.Entity;
import INF1009_P3_02.Engine.entity.EntityManager;
import INF1009_P3_02.Engine.observer.GameEventManager;
import INF1009_P3_02.Game.entity.Bin;
import INF1009_P3_02.Game.entity.Player;
import INF1009_P3_02.Game.enumFolder.BinType;
import INF1009_P3_02.Game.enumFolder.CarryState;
import INF1009_P3_02.Game.enumFolder.StateChangeReason;
import INF1009_P3_02.Game.enumFolder.TrashType;

public class PlayerBinDepositCollision implements CollisionHandler {
    private final EntityManager entityManager;
    private final GameEventManager eventManager;

    public PlayerBinDepositCollision(EntityManager entityManager, GameEventManager eventManager) {
        this.entityManager = entityManager;
        this.eventManager = eventManager;
    }

    @Override
    public void handle(Entity a, Entity b, CollisionContext ctx, Speaker speaker) {
        if (!(a instanceof Player)) return;
        if (!(b instanceof Bin)) return;

        Player player = (Player) a;
        Bin bin = (Bin) b;

        if (!player.getBounds().overlaps(bin.getBounds())) return;

        ctx.setIsOverlappingBin(true);
        if (ctx.wasOverlappingBin()) return;

        CarryState carry = player.getCarryState();
        TrashType carriedType = player.getCarriedTrashType();

        // If not carrying anything, depositing should do nothing
        if (carry == CarryState.NONE || carriedType == null) return;

        BinType binType = bin.getType();

        // Determine score based on trash type and bin type
        int points = calculateScore(carriedType, binType);

        ctx.addScore(points);

        // Determine if this is a correct deposit (exact bin match) vs wrong placement
        boolean isCorrectBin = isCorrectBin(carriedType, binType);
        if (!isCorrectBin) {
            ctx.wrongBin = true;
        }

        // Clear player's carried state
        player.setCarryState(CarryState.NONE);
        player.clearCarriedTrash();

        // Notify observers: carry state changed from carrying → NONE (deposit)
        if (eventManager != null) {
            StateChangeReason reason = isCorrectBin
                ? StateChangeReason.DEPOSITED_CORRECT
                : StateChangeReason.DEPOSITED_WRONG;
            eventManager.notifyCarryStateChanged(CarryState.NONE, carriedType, reason, points);
        }

        // Respawn ONLY after deposit attempt
        entityManager.respawnTrash(carriedType);
    }

    /**
     * Check if the trash was placed in its correct matching bin.
     */
    private boolean isCorrectBin(TrashType trashType, BinType binType) {
        switch (trashType) {
            case PAPER:      return binType == BinType.PAPER;
            case PLASTIC:    return binType == BinType.PLASTIC;
            case ELECTRONIC: return binType == BinType.ELECTRONIC;
            case TRASHBAG:   return binType == BinType.TRASH;
        }
        return false;
    }

    /**
     * Scoring rules:
     *   Correct matching bin (paper→paper, plastic→plastic, electronic→electronic, trashbag→trash): +3
     *   Any wrong bin: -1
     */
    private int calculateScore(TrashType trashType, BinType binType) {
        switch (trashType) {
            case PAPER:
                if (binType == BinType.PAPER)      return 3;
                if (binType == BinType.PLASTIC)     return -1;
                if (binType == BinType.ELECTRONIC)  return -1;
                if (binType == BinType.TRASH)       return -1;
                break;

            case PLASTIC:
                if (binType == BinType.PLASTIC)     return 3;
                if (binType == BinType.PAPER)       return -1;
                if (binType == BinType.ELECTRONIC)  return -1;
                if (binType == BinType.TRASH)       return -1;
                break;

            case ELECTRONIC:
                if (binType == BinType.ELECTRONIC)  return 3;
                if (binType == BinType.PAPER)       return -1;
                if (binType == BinType.PLASTIC)     return -1;
                if (binType == BinType.TRASH)       return -1;
                break;

            case TRASHBAG:
                if (binType == BinType.TRASH)       return 3;
                return -1;
        }

        return 0;
    }
}