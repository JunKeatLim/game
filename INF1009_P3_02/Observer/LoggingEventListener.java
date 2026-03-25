package INF1009_P3_02.Observer;

import INF1009_P3_02.Entity.CarryState;
import INF1009_P3_02.Entity.TrashType;
import INF1009_P3_02.Logging.GameEngineLogger;

/**
 * Observer that handles logging responses to player state changes.
 */
public class LoggingEventListener implements IObserver {

    private final GameEngineLogger logger;

    public LoggingEventListener(GameEngineLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onCarryStateChanged(CarryState oldState, CarryState newState,
                                    TrashType trashType, StateChangeReason reason, int points) {
        switch (reason) {
            case PICKUP:
                logger.info("State changed: " + oldState + " -> " + newState
                    + " | Player picked up: " + trashType);
                break;
            case DEPOSITED_CORRECT:
                logger.info("State changed: " + oldState + " -> " + newState
                    + " | Correct deposit: " + trashType + " (+" + points + " pts)");
                break;
            case DEPOSITED_WRONG:
                logger.info("State changed: " + oldState + " -> " + newState
                    + " | Wrong deposit: " + trashType + " (" + points + " pts)");
                break;
            case DROPPED:
                logger.info("State changed: " + oldState + " -> " + newState
                    + " | Dropped: " + trashType + " (bot collision)");
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerCollision(StateChangeReason reason) {
        switch (reason) {
            case HIT_OBSTACLE:
                logger.info("Player collided with obstacle");
                break;
            case HIT_BOT:
                logger.info("Player collided with bot");
                break;
            default:
                break;
        }
    }
}