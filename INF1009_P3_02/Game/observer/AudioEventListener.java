package INF1009_P3_02.Game.observer;

import INF1009_P3_02.Engine.audio.Speaker;
import INF1009_P3_02.Engine.observer.IObserver;
import INF1009_P3_02.Game.enumFolder.CarryState;
import INF1009_P3_02.Game.enumFolder.StateChangeReason;
import INF1009_P3_02.Game.enumFolder.TrashType;

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