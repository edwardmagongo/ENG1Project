package app.vercel.eng1seven;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import app.vercel.eng1seven.map.GameMap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import app.vercel.eng1seven.items.Bread;
import app.vercel.eng1seven.items.Trap;

public class goose {

    private static final int FRAME_WIDTH = 240;
    private static final int FRAME_HEIGHT = 224;
    private static final int FRAME_COLUMNS = 5;
    private static final float FRAME_DURATION = 0.15f;

    private static final float SPRITE_HEIGHT = 96f;
    private static final float SPRITE_WIDTH = SPRITE_HEIGHT * FRAME_WIDTH / FRAME_HEIGHT;
    private static final float HALF = SPRITE_HEIGHT / 2f;
    private static final float HIT_RADIUS = 50f; // how close counts as "caught"

    // Which way the character faces, and the row of geese pic that animates it. ---
    // taken from Player.java
    private enum Facing {
        DOWN(0), UP(1), LEFT(3), RIGHT(2);

        private final int row;

        Facing(int row) {
            this.row = row;
        }
    }

    private float x, y; // center, same convention as Player
    private float speed = 110f; // slower than the player (200)

    private final Texture texture; // shared, owned by whoever loaded it
    private final TextureRegion[][] animationFrames;
    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkUp;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkRight;

    private float stateTime = 0f;
    private Facing facing = Facing.DOWN;

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

        animationFrames = new TextureRegion[Facing.values().length][FRAME_COLUMNS]; // taken from Player.java
        for (int row = 0; row < animationFrames.length; row++) {
            for (int col = 0; col < FRAME_COLUMNS; col++) {
                animationFrames[row][col] = new TextureRegion(texture,
                        col * FRAME_WIDTH, row * FRAME_HEIGHT,
                        FRAME_WIDTH, FRAME_HEIGHT);
            }
        }

        walkDown = createWalkCycle(Facing.DOWN);
        walkUp = createWalkCycle(Facing.UP);
        walkLeft = createWalkCycle(Facing.LEFT);
        walkRight = createWalkCycle(Facing.RIGHT);
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

        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            facing = (dir.x > 0) ? Facing.RIGHT : Facing.LEFT;
        } else {
            facing = (dir.y > 0) ? Facing.UP : Facing.DOWN;
        }

        stateTime += delta;

        // Move each axis separately so the goose slides along walls.
        if (!collideWithMap || canMoveTo(x + dir.x, y)) x += dir.x;
        if (!collideWithMap || canMoveTo(x, y + dir.y)) y += dir.y;
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = currentWalkCycle().getKeyFrame(stateTime);
        batch.draw(frame, x - SPRITE_WIDTH * 0.5f, y - SPRITE_HEIGHT * 0.5f,
                SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    private Animation<TextureRegion> createWalkCycle(Facing walkFacing) {
        Animation<TextureRegion> cycle = new Animation<>(FRAME_DURATION, animationFrames[walkFacing.row]);
        cycle.setPlayMode(Animation.PlayMode.LOOP);
        return cycle;
    }

    private Animation<TextureRegion> currentWalkCycle() {
        switch (facing) {
            case UP:
                return walkUp;
            case LEFT:
                return walkLeft;
            case RIGHT:
                return walkRight;
            default:
                return walkDown;
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
    public static goose spawn(OrthographicCamera camera, Texture texture, Player player, GameMap map,
            boolean collideWithMap) {
        float cx = camera.position.x;
        float cy = camera.position.y;
        float halfW = camera.viewportWidth * 0.5f * camera.zoom + HALF;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom + HALF;

        float x, y;
        switch (MathUtils.random(3)) {
            case 0:
                x = cx + MathUtils.random(-halfW, halfW);
                y = cy + halfH;
                break; // top
            case 1:
                x = cx + halfW;
                y = cy + MathUtils.random(-halfH, halfH);
                break; // right
            case 2:
                x = cx + MathUtils.random(-halfW, halfW);
                y = cy - halfH;
                break; // bottom
            default:
                x = cx - halfW;
                y = cy + MathUtils.random(-halfH, halfH);
                break; // left
        }
        return new goose(x, y, texture, player, map, collideWithMap);
    }
}
