package app.vercel.eng1seven;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import app.vercel.eng1seven.map.GameMap;

public class goose {
    private static final float SIZE = 96f;       // drawn size in pixels (player is 120)
    private static final float HALF = SIZE / 2f;
    private static final float HIT_RADIUS = 50f; // how close counts as "caught"

    private float x, y;                          // center, same convention as Player
    private float speed = 110f;                  // slower than the player (200)
    private boolean facingLeft;
    private int damage;

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
        this.damage = 5;
    }

    public void update(float delta) {
        // Direction towards the player, normalised so diagonals aren't faster
        dir.set(player.getX() - x, player.getY() - y);
        if (dir.len2() < 1f) return;             // already on top of the player
        dir.nor().scl(speed * delta);

        facingLeft = dir.x < 0;

        // Move each axis separately so the goose slides along walls
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

    public int getDamage() {
        return damage;
    }
}
