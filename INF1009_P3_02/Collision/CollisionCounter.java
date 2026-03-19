package INF1009_P3_02.Collision;

public class CollisionCounter {
    private boolean wasOverlapping = false;
    private int count = 0;

    private float cooldown = 0f;
    private static final float COUNT_COOLDOWN = 0.35f; //The minimum time (in seconds) between two valid collision counts.

    public void update(CollisionContext ctx, float dt) {
        cooldown = Math.max(0f, cooldown - dt);
        boolean overlapping = ctx.playerBotOverlappingNow; //bot and player collide

        if (overlapping && !wasOverlapping && cooldown <= 0f) {
            count++;
            cooldown = COUNT_COOLDOWN; //Reset cooldown to prevent immediate recount/prevents multiple collision counts
        }

        wasOverlapping = overlapping; // Update the previous state for the next frame comparison.
    }

    public int getCount() {
        return count;
    }
}
