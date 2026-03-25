package INF1009_P3_02.Observer;

import INF1009_P3_02.Entity.CarryState;
import INF1009_P3_02.Entity.TrashType;
import INF1009_P3_02.InputOutput.Speaker;

/**
 * Observer that handles audio responses to player state changes.
 */
public class AudioEventListener implements IObserver {

    private final Speaker speaker;

    public AudioEventListener(Speaker speaker) {
        this.speaker = speaker;
    }

    @Override
    public void onCarryStateChanged(CarryState oldState, CarryState newState,
                                    TrashType trashType, StateChangeReason reason, int points) {
        switch (reason) {
            case PICKUP:
                speaker.playPickupSound();
                break;
            case DEPOSITED_CORRECT:
                speaker.playCorrectSound();
                break;
            case DEPOSITED_WRONG:
                speaker.playWrongSound();
                break;
            case DROPPED:
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerCollision(StateChangeReason reason) {
        switch (reason) {
            case HIT_BOT:
                speaker.playCollisionSound();
                break;
            case HIT_OBSTACLE:
                // No sound for hitting an obstacle currently
                break;
            default:
                break;
        }
    }
}