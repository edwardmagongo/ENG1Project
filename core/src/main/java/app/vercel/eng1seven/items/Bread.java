
package app.vercel.eng1seven.items;

/**
 * Represents a piece of bread dropped by the player.
 */
public class Bread {

    private final float x;
    private final float y;

    public Bread(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
