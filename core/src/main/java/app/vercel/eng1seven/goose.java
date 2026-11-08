package app.vercel.eng1seven;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import app.vercel.eng1seven.map.GameMap;
import com.badlogic.gdx.utils.Array;
import app.vercel.eng1seven.items.Bread;
import app.vercel.eng1seven.items.Trap;

public class goose {
    private static final float SIZE = 96f;       // drawn size in pixels (player is 120)
    private static final float HALF = SIZE / 2f;
    private static final float HIT_RADIUS = 50f; // how close counts as "caught"

    private float x, y;                          // center, same convention as Player
    private float speed = 110f;                  // slower than the player (200)
    private boolean facingLeft;

    private final Texture texture;               // shared, owned by whoever loaded it
    private final Player player;
    private final GameMap map;
    private final boolean collideWithMap;

    private final Vector2 dir = new Vector2();
    private static final float BREAD_RANGE = 250f;
    private static final float EATING_DISTANCE = 30f;

    private Bread targetBread;
    private boolean eating;
    private boolean trapped;

    public goose(float x, float y, Texture texture, Player player, GameMap map, boolean collideWithMap) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.player = player;
        this.map = map;
        this.collideWithMap = collideWithMap;
    }

    public void update(float delta, Array<Bread> breads, Array<Trap> traps) {

        // A trapped goose cannot move again.
        if (trapped) return;

        // Check whether this goose has touched a trap.
        for (Trap trap : traps) {
            if (!trap.isTriggered()
                && Vector2.dst(x, y, trap.getX(), trap.getY()) < 45f) {
                trap.trigger();
                trapped = true;
                return;
            }
        }

        // A goose that is already eating stays at its bread.
        if (eating) return;

        // Find the nearest bread within attraction range.
        float nearestDistance = BREAD_RANGE;
        Bread nearestBread = null;

        for (Bread bread : breads) {
            float distance = Vector2.dst(x, y, bread.getX(), bread.getY());

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestBread = bread;
            }
        }

        if (nearestBread != null) {
            targetBread = nearestBread;

            // Stop when the goose reaches the bread.
            if (nearestDistance <= EATING_DISTANCE) {
                eating = true;
                return;
            }

            dir.set(targetBread.getX() - x, targetBread.getY() - y);
        } else {
            targetBread = null;

            // If no bread is nearby, continue chasing the player.
            dir.set(player.getX() - x, player.getY() - y);
        }

        if (dir.len2() < 1f) return;

        dir.nor().scl(speed * delta);

        facingLeft = dir.x < 0;

        // Move each axis separately so the goose slides along walls.
        if (!collideWithMap || canMoveTo(x + dir.x, y)) x += dir.x;
        if (!collideWithMap || canMoveTo(x, y + dir.y)) y += dir.y;
    }

    public void render(SpriteBatch batch) {
        // Flip horizontally by drawing with negative width
        if (facingLeft) {
            batch.draw(texture, x + HALF, y - HALF, -SIZE, SIZE);
        } else {
            batch.draw(texture, x - HALF, y - HALF, SIZE, SIZE);
        }
    }

    public boolean touchesPlayer() {
        return Vector2.dst(x, y, player.getX(), player.getY()) < HIT_RADIUS + 30f;
    }

    private boolean canMoveTo(float nx, float ny) {
        float h = HALF * 0.6f; // smaller hitbox than the sprite so it doesn't snag
        return map.isWalkable(GameMap.toTile(nx - h), GameMap.toTile(ny - h))
            && map.isWalkable(GameMap.toTile(nx + h), GameMap.toTile(ny - h))
            && map.isWalkable(GameMap.toTile(nx - h), GameMap.toTile(ny + h))
            && map.isWalkable(GameMap.toTile(nx + h), GameMap.toTile(ny + h));
    }

    /** Spawns just outside the camera's visible area on a random side. */
    public static goose spawn(OrthographicCamera camera, Texture texture, Player player, GameMap map, boolean collideWithMap) {
        float cx = camera.position.x;
        float cy = camera.position.y;
        float halfW = camera.viewportWidth * 0.5f * camera.zoom + HALF;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom + HALF;

        float x, y;
        switch (MathUtils.random(3)) {
            case 0:  x = cx + MathUtils.random(-halfW, halfW); y = cy + halfH; break; // top
            case 1:  x = cx + halfW; y = cy + MathUtils.random(-halfH, halfH); break; // right
            case 2:  x = cx + MathUtils.random(-halfW, halfW); y = cy - halfH; break; // bottom
            default: x = cx - halfW; y = cy + MathUtils.random(-halfH, halfH); break; // left
        }
        return new goose(x, y, texture, player, map, collideWithMap);
    }
}
