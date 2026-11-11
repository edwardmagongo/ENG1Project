package app.vercel.eng1seven;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import app.vercel.eng1seven.map.GameMap;
import com.badlogic.gdx.utils.Array;

public class goose {
    private static final float SIZE = 96f;       // drawn size in pixels (player is 120)
    private static final float HALF = SIZE / 2f;

    private static final float RADIUS = 34f;
    private static final float PLAYER_RADIUS = 45f;
    private static final float TOUCH_MARGIN = 4f;

    private float x, y;                          // center, same convention as Player
    private float speed = 110f;                  // slower than the player (200)
    private boolean facingLeft;

    private final Texture texture;               // shared, owned by whoever loaded it
    private final Player player;
    private final GameMap map;
    private final boolean collideWithMap;

    private final Vector2 dir = new Vector2();

    public goose(float x, float y, Texture texture, Player player, GameMap map, boolean collideWithMap) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.player = player;
        this.map = map;
        this.collideWithMap = collideWithMap;
    }



    public void update(float delta, Array<goose> others) {
        // Direction towards the player, normalised so diagonals aren't faster
        dir.set(player.getX() - x, player.getY() - y);
        if (dir.len2() >= 1f) {            // already on top of the player
            dir.nor().scl(speed * delta);
            facingLeft = dir.x < 0;
            moveBy(dir.x, dir.y);
        }

        separateFrom(player.getX(), player.getY(), RADIUS + PLAYER_RADIUS, 1f);

        for (int i = 0; i < others.size; i++) {
            goose o = others.get(i);
            if (o != this) {
                separateFrom(o.x, o.y, RADIUS * 2f, 0.5f);
            }
        }

    }

    private void separateFrom(float ox, float oy, float minDist, float share) {
        float dx = x - ox;
        float dy = y - oy;
        float distSq = dx * dx + dy * dy;
        if (distSq >= minDist * minDist) return;

        float dist = (float) Math.sqrt(distSq);
        if (dist < 0.001f) {                     // exactly on top: pick a random direction
            float angle = MathUtils.random(MathUtils.PI2);
            dx = MathUtils.cos(angle);
            dy = MathUtils.sin(angle);
            dist = 1f;
        }

        float push = (minDist - dist) * share;
        moveBy(dx / dist * push, dy / dist * push);
    }

    private void moveBy(float mx, float my) {
        if (!collideWithMap || canMoveTo(x + mx, y)) x += mx;
        if (!collideWithMap || canMoveTo(x, y + my)) y += my;
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
        float r = RADIUS + PLAYER_RADIUS + TOUCH_MARGIN;
        return Vector2.dst(x, y, player.getX(), player.getY()) < r * r;
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
