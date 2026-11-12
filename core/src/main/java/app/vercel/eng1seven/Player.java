package app.vercel.eng1seven;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


import app.vercel.eng1seven.map.GameMap;

// The player character, walking the campus with WASD or the arrow keys.
public class Player {
    // Where the walk cycles sit in player.png.
    private static final int FIRST_COLUMN_X = 230;
    private static final int FRAME_WIDTH = 265;
    private static final int FRAME_HEIGHT = 221;
    private static final int FRAME_COLUMNS = 5;
    private static final float FRAME_DURATION = 0.15f;

    // Drawn size in world units. The width follows the frame's shape, so he is not
    // squashed.
    private static final float SPRITE_HEIGHT = 120f;
    private static final float SPRITE_WIDTH = SPRITE_HEIGHT * FRAME_WIDTH / FRAME_HEIGHT;

    // Only the feet collide; a box the size of the sprite would be four tiles
    // across.
    private static final float FEET_WIDTH = 24f;
    private static final float FEET_HEIGHT = 16f;

    // World units per second.
    private static final float SPEED = 200f;
    private static final int MAX_BREAD = 3;
    private static final int MAX_TRAPS = 3;

    // Which way the character faces, and the row of player.png that animates it.
    private enum Facing {
        DOWN(0), UP(1), LEFT(2), RIGHT(3);

        private final int row;

        Facing(int row) {
            this.row = row;
        }
    }

    private GameMap map;
    private final Texture playerTexture;
    private final TextureRegion[][] animationFrames;
    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkUp;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkRight;

    private float x = 320f;
    private float y = 240f;
    private float stateTime;
    private boolean isMoving;
    private Facing facing = Facing.DOWN;
    private int health = 100;
    private int breadCount = 0;
    private int trapCount = 0;

    public Player(GameMap map) {
        this.map = map;
        playerTexture = new Texture("player/player.png");

        animationFrames = new TextureRegion[Facing.values().length][FRAME_COLUMNS];
        for (int row = 0; row < animationFrames.length; row++) {
            for (int col = 0; col < FRAME_COLUMNS; col++) {
                animationFrames[row][col] = new TextureRegion(playerTexture,
                        FIRST_COLUMN_X + col * FRAME_WIDTH, row * FRAME_HEIGHT,
                        FRAME_WIDTH, FRAME_HEIGHT);
            }
        }

        walkDown = createWalkCycle(Facing.DOWN);
        walkUp = createWalkCycle(Facing.UP);
        walkLeft = createWalkCycle(Facing.LEFT);
        walkRight = createWalkCycle(Facing.RIGHT);
    }

    // Moves by the keys held this frame. Each axis is tested alone, so he slides along walls.
    public void update(float delta, Array<Goose> geese) {
        int inputX = 0;
        int inputY = 0;
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))
            inputY++;
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
            inputY--;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
            inputX--;
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
            inputX++;

        isMoving = false;
        if (inputX == 0 && inputY == 0)
            return;

        if (inputY > 0)
            facing = Facing.UP;
        else if (inputY < 0)
            facing = Facing.DOWN;
        if (inputX < 0)
            facing = Facing.LEFT;
        else if (inputX > 0)
            facing = Facing.RIGHT;

        // Otherwise a diagonal covers root two times the ground of a straight line.
        float length = (float) Math.sqrt(inputX * inputX + inputY * inputY);
        float stepX = inputX / length * SPEED * delta;
        float stepY = inputY / length * SPEED * delta;

        if (stepX != 0f && canWalkTo(x + stepX, y) && !blockedByGeese(x + stepX, y, geese))  {
            x += stepX;
            isMoving = true;
        }
        if (stepY != 0f && canWalkTo(x, y + stepY) && !blockedByGeese(x, y + stepY, geese)) {
            y += stepY;
            isMoving = true;
        }

        if (isMoving)
            stateTime += delta;
    }

    private boolean blockedByGeese(float newX, float newY, Array<Goose> geese) {
        float min = Goose.PLAYER_CONTACT_DISTANCE;
        for (int i = 0; i < geese.size; i++) {
            Goose g = geese.get(i);
            float newDist = Vector2.dst2(newX, newY, g.getX(), g.getY());
            float oldDist = Vector2.dst2(x, y, g.getX(), g.getY());
            if (newDist < min * min && newDist < oldDist) return true;
        }
        return false;
    }
    public void render(SpriteBatch batch) {
        TextureRegion frame = isMoving
                ? currentWalkCycle().getKeyFrame(stateTime)
                : animationFrames[facing.row][0];
        batch.draw(frame, x - SPRITE_WIDTH * 0.5f, y - SPRITE_HEIGHT * 0.5f,
                SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    public void dispose() {
        playerTexture.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean pickUpBread() {
        if (breadCount >= MAX_BREAD)
            return false;
        breadCount++;
        return true;
    }

    public boolean pickUpTrap() {
        if (trapCount >= MAX_TRAPS)
            return false;
        trapCount++;
        return true;
    }

    // Collision and the zone exits work at the feet: the bottom centre of the sprite.
    public float getFeetY() {
        return y - SPRITE_HEIGHT * 0.5f;
    }

    // Moves the player so their feet stand at this point, for example when entering a zone.
    public void placeFeetAt(float feetX, float feetY) {
        x = feetX;
        y = feetY + SPRITE_HEIGHT * 0.5f;
        isMoving = false;
    }

    // The map the player walks and collides on, swapped when they change zone.
    public void setMap(GameMap map) {
        this.map = map;
    }

    public int getBreadCount() {
        return breadCount;
    }

    public int getTrapCount() {
        return trapCount;
    }

    public boolean useBread() {
        if (breadCount <= 0)
            return false;
        breadCount--;
        return true;
    }

    public boolean useTrap() {
        if (trapCount <= 0)
            return false;
        trapCount--;
        return true;
    }

    // Direction the player is facing, used to place items in front of him.
    public int getFacingX() {
        if (facing == Facing.LEFT)
            return -1;
        if (facing == Facing.RIGHT)
            return 1;
        return 0;
    }

    public int getFacingY() {
        if (facing == Facing.UP)
            return 1;
        if (facing == Facing.DOWN)
            return -1;
        return 0;
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

    // Whether he may stand here: feet on walkable tiles, and the whole sprite
    // inside the map.
    private boolean canWalkTo(float newX, float newY) {
        float halfSpriteWidth = SPRITE_WIDTH * 0.5f;
        float halfSpriteHeight = SPRITE_HEIGHT * 0.5f;
        if (newX - halfSpriteWidth < 0f || newX + halfSpriteWidth > map.getWorldWidth())
            return false;
        if (newY - halfSpriteHeight < 0f || newY + halfSpriteHeight > map.getWorldHeight())
            return false;

        float left = newX - FEET_WIDTH * 0.5f;
        float right = newX + FEET_WIDTH * 0.5f;
        float bottom = newY - SPRITE_HEIGHT * 0.5f;
        float top = bottom + FEET_HEIGHT;

        for (int tileX = GameMap.toTile(left); tileX <= GameMap.toTile(right); tileX++) {
            for (int tileY = GameMap.toTile(bottom); tileY <= GameMap.toTile(top); tileY++) {
                if (!map.isWalkable(tileX, tileY))
                    return false;
            }
        }
        return true;
    }

    public int getHealth() {
        return health;
    }

    public void damage(int damage) {
        health -= damage;
    }

}
