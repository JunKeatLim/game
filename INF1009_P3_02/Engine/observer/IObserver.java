package INF1009_P3_02.Engine.observer;

import INF1009_P3_02.Game.enumFolder.CarryState;
import INF1009_P3_02.Game.enumFolder.StateChangeReason;
import INF1009_P3_02.Game.enumFolder.TrashType;

/**
 * Observer interface (IObserver) for player state changes.
 *
 * Observers implement this interface and register with GameEventManager.
 * They are notified when:
 *   1. The player's carry state changes (pickup, deposit, drop)
 *   2. The player collides with something (obstacle or bot)
 */
public interface IObserver {

    /**
     * Called when the player's carry state changes.
     *
     * @param oldState   the carry state before the change
     * @param newState   the carry state after the change
     * @param trashType  the type of trash involved (e.g. PAPER, PLASTIC)
     * @param reason     why the state changed (PICKUP, DEPOSITED_CORRECT, DEPOSITED_WRONG, DROPPED)
     * @param points     score impact (positive for correct, negative for wrong, 0 for pickup/drop)
     */
    void onCarryStateChanged(CarryState oldState, CarryState newState,
                             TrashType trashType, StateChangeReason reason, int points);

    /**
     * Called when the player collides with an obstacle or bot.
     *
     * @param reason  what the player collided with (HIT_OBSTACLE or HIT_BOT)
     */
    void onPlayerCollision(StateChangeReason reason);
}