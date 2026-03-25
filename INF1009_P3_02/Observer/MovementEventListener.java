package INF1009_P3_02.Observer;

import INF1009_P3_02.Entity.CarryState;
import INF1009_P3_02.Entity.TrashType;
import INF1009_P3_02.Movement.MovementManager;

/**
 * Observer that handles movement sync when the player collides.
 * Replaces the direct movementManager.syncPlayerMovementFromEntity()
 * call that was in SimulationScene.update().
 */
public class MovementEventListener implements IObserver {

    private final MovementManager movementManager;

    public MovementEventListener(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    @Override
    public void onCarryStateChanged(CarryState oldState, CarryState newState,
                                    TrashType trashType, StateChangeReason reason, int points) {
        // No movement response needed for carry state changes
    }

    @Override
    public void onPlayerCollision(StateChangeReason reason) {
        switch (reason) {
            case HIT_OBSTACLE:
            case HIT_BOT:
                // Sync player movement after being blocked
                movementManager.syncPlayerMovementFromEntity();
                break;
            default:
                break;
        }
    }
}