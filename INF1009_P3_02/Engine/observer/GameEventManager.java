package INF1009_P3_02.Engine.observer;

import INF1009_P3_02.Game.enumFolder.CarryState;
import INF1009_P3_02.Game.enumFolder.StateChangeReason;
import INF1009_P3_02.Game.enumFolder.TrashType;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject (Observable) that monitors the player's state.
 *
 * Tracks two kinds of state changes:
 *   1. Carry state — whether the player is holding trash or not
 *   2. Collision state — when the player collides with an obstacle or bot
 *
 * When a state change is detected, all registered observers are notified.
 */
public class GameEventManager {

    private final List<IObserver> listeners = new ArrayList<>();

    // The last known carry state
    private CarryState currentCarryState = CarryState.NONE;

    // ── Observer management ──────────────────────────────────────────

    public void addListener(IObserver listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(IObserver listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    // ── Carry state tracking ─────────────────────────────────────────

    public CarryState getCurrentCarryState() {
        return currentCarryState;
    }

    /**
     * Called when the player's carry state changes (pickup, deposit, drop).
     * Stores the new state and notifies all observers.
     */
    public void notifyCarryStateChanged(CarryState newState, TrashType trashType,
                                        StateChangeReason reason, int points) {
        CarryState oldState = this.currentCarryState;
        this.currentCarryState = newState;

        for (IObserver listener : listeners) {
            listener.onCarryStateChanged(oldState, newState, trashType, reason, points);
        }
    }

    // ── Player collision tracking ────────────────────────────────────

    /**
     * Called when the player collides with an obstacle or bot.
     * Notifies all observers so they can respond (sync movement, play sound, log, etc).
     */
    public void notifyPlayerCollision(StateChangeReason reason) {
        for (IObserver listener : listeners) {
            listener.onPlayerCollision(reason);
        }
    }
}