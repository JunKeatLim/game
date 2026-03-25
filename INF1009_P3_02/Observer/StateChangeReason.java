package INF1009_P3_02.Observer;

/**
 * The reason why the player's state changed.
 * Used by observers to determine the appropriate response.
 */
public enum StateChangeReason {
    // Carry state change reasons
    PICKUP,              // Player picked up trash from the ground
    DEPOSITED_CORRECT,   // Player deposited trash into the correct bin
    DEPOSITED_WRONG,     // Player deposited trash into the wrong bin
    DROPPED,             // Player was hit by a bot and dropped trash

    // Player collision reasons
    HIT_OBSTACLE,        // Player collided with an obstacle (bin)
    HIT_BOT              // Player collided with a bot
}