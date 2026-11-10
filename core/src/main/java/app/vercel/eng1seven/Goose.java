package app.vercel.eng1seven;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import app.vercel.eng1seven.map.GameMap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import app.vercel.eng1seven.items.Bread;
import app.vercel.eng1seven.items.Trap;

public class Goose {

    private static final int FRAME_WIDTH = 240;
    private static final int FRAME_HEIGHT = 224;
    private static final int FRAME_COLUMNS = 5;
    private static final float FRAME_DURATION = 0.15f;

    private static final float SPRITE_HEIGHT = 96f;
    private static final float SPRITE_WIDTH = SPRITE_HEIGHT * FRAME_WIDTH / FRAME_HEIGHT;

    // How close the goose's centre must get to the player's centre to count as "caught".
    private static final float HIT_RADIUS = 80f;

    // Half the side of the square that collides with the map. Kept under half a tile wide
    // so checking the four corners is enough, and so the goose fits through one-tile gaps.
    private static final float COLLISION_HALF = 14f;

    // Geese spawn up to this far beyond the edge of the screen, so they walk into view soon.
    private static final float SPAWN_BAND = 4f * GameMap.TILE_SIZE;
    private static final int SPAWN_ATTEMPTS = 30;

    // If nowhere off-screen is free, spawn at least this far from the player.
    private static final float MIN_SPAWN_DISTANCE = 6f * GameMap.TILE_SIZE;

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
    private int damage;

    private final Player player;
    private final GameMap map;
    private final boolean collideWithMap;

    private final Vector2 dir = new Vector2();
    private static final float BREAD_RANGE = 250f;
    private static final float EATING_DISTANCE = 30f;
    private static final float FEEDING_TIME = 4f;

    private Bread targetBread;
    private boolean eating;
    private float feedingTimer;
    private boolean trapped;

    public Goose(float x, float y, Texture texture, Player player, GameMap map, boolean collideWithMap) {
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
        this.damage = 5;
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

        // A goose eats for a limited time before leaving the bread.
        if (eating) {
            if (targetBread == null || !breads.contains(targetBread, true)) {
                targetBread = null;
                eating = false;
                feedingTimer = 0f;
            } else {
                feedingTimer += delta;

                if (feedingTimer >= FEEDING_TIME) {
                    breads.removeValue(targetBread, true);
                    targetBread = null;
                    eating = false;
                    feedingTimer = 0f;
                } else {
                    return;
                }
            }
        }

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
        if (!collideWithMap || canStandAt(map, x + dir.x, y)) x += dir.x;
        if (!collideWithMap || canStandAt(map, x, y + dir.y)) y += dir.y;
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
        return Vector2.dst(x, y, player.getX(), player.getY()) < HIT_RADIUS;
    }

    // Whether a goose centred here is on walkable ground. Off-map tiles are never walkable,
    // so this also keeps the goose inside the map.
    private static boolean canStandAt(GameMap map, float x, float y) {
        return map.isWalkable(GameMap.toTile(x - COLLISION_HALF), GameMap.toTile(y - COLLISION_HALF))
                && map.isWalkable(GameMap.toTile(x + COLLISION_HALF), GameMap.toTile(y - COLLISION_HALF))
                && map.isWalkable(GameMap.toTile(x - COLLISION_HALF), GameMap.toTile(y + COLLISION_HALF))
                && map.isWalkable(GameMap.toTile(x + COLLISION_HALF), GameMap.toTile(y + COLLISION_HALF));
    }

    /**
     * Spawns a goose on walkable ground inside the map, just off-screen where possible.
     * Returns null if there is nowhere suitable.
     */
    public static Goose spawn(OrthographicCamera camera, Texture texture, Player player, GameMap map,
            boolean collideWithMap) {
        // The visible area, grown by half the sprite so a goose on its edge is fully hidden.
        float halfW = camera.viewportWidth * 0.5f * camera.zoom + SPRITE_WIDTH * 0.5f;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom + SPRITE_HEIGHT * 0.5f;
        float cx = camera.position.x;
        float cy = camera.position.y;
        Rectangle hidden = new Rectangle(cx - halfW, cy - halfH, halfW * 2f, halfH * 2f);

        // First choice: a band just beyond a random edge of the screen.
        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            float out = MathUtils.random(0f, SPAWN_BAND);
            float x, y;
            switch (MathUtils.random(3)) {
                case 0: // top
                    x = cx + MathUtils.random(-halfW, halfW);
                    y = cy + halfH + out;
                    break;
                case 1: // right
                    x = cx + halfW + out;
                    y = cy + MathUtils.random(-halfH, halfH);
                    break;
                case 2: // bottom
                    x = cx + MathUtils.random(-halfW, halfW);
                    y = cy - halfH - out;
                    break;
                default: // left
                    x = cx - halfW - out;
                    y = cy + MathUtils.random(-halfH, halfH);
                    break;
            }
            if (!hidden.contains(x, y) && canStandAt(map, x, y)) {
                return new Goose(x, y, texture, player, map, collideWithMap);
            }
        }

        // The screen covers most of the map: try anywhere off-screen, and otherwise
        // anywhere a fair distance from the player.
        float fallbackX = 0f, fallbackY = 0f;
        boolean haveFallback = false;
        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            float x = MathUtils.random(0f, map.getWorldWidth());
            float y = MathUtils.random(0f, map.getWorldHeight());
            if (!canStandAt(map, x, y)) continue;
            if (!hidden.contains(x, y)) {
                return new Goose(x, y, texture, player, map, collideWithMap);
            }
            if (!haveFallback && Vector2.dst(x, y, player.getX(), player.getY()) >= MIN_SPAWN_DISTANCE) {
                fallbackX = x;
                fallbackY = y;
                haveFallback = true;
            }
        }
        return haveFallback ? new Goose(fallbackX, fallbackY, texture, player, map, collideWithMap) : null;
    }

    public int getDamage() {
        return damage;
    }
}
