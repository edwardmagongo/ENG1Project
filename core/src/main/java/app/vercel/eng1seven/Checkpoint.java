package app.vercel.eng1seven;

import com.badlogic.gdx.math.Rectangle;

public class Checkpoint {
    private final String name;
    private final Rectangle bounds;
    private boolean reached;

    public Checkpoint(String name, float x, float y, float width, float height) {
        this.name = name;
        this.bounds = new Rectangle(x, y, width, height);
        this.reached = false;
    }

    public boolean checkPlayer(float playerX, float playerY) {
        if (!reached && bounds.contains(playerX, playerY)) {
            reached = true;
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isReached() {
        return reached;
    }

    public void reset() {
        reached = false;
    }
}