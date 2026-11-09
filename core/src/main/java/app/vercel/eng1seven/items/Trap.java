
package app.vercel.eng1seven.items;

/**
 * Represents a trap deployed by the player.
 */
public class Trap {

    private final float x;
    private final float y;

    private boolean triggered = false;

    public Trap(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void trigger() {
        triggered = true;
    }
}
