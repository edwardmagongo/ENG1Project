package app.vercel.eng1seven;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import app.vercel.eng1seven.map.GameMap;
public class Player {

    private float x = 320;
    private float y = 240;
    private float speed = 200;
    private Texture playerTexture;
    private TextureRegion[][] animationFrames;
    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;
    private float stateTime;
    private boolean isMoving;
    private int direction = 0;
    private final GameMap map;

public Player(GameMap map) {
    this.map = map;
    playerTexture = new Texture("player/player.png");

    animationFrames = new TextureRegion[4][5];

    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 5; col++) {
            animationFrames[row][col] = new TextureRegion(
                playerTexture,
                230 + (col * 265),
                row * 221,
                265,
                221
            );
    }
}
    walkDown = new Animation<>(0.15f, animationFrames[0]);
    walkUp = new Animation<>(0.15f, animationFrames[1]);
    walkLeft = new Animation<>(0.15f, animationFrames[2]);
    walkRight = new Animation<>(0.15f, animationFrames[3]);

    walkDown.setPlayMode(Animation.PlayMode.LOOP);
    walkUp.setPlayMode(Animation.PlayMode.LOOP);
    walkLeft.setPlayMode(Animation.PlayMode.LOOP);
    walkRight.setPlayMode(Animation.PlayMode.LOOP);
}

public void update() {
    float delta = Gdx.graphics.getDeltaTime();
    isMoving = false;

    if (Gdx.input.isKeyPressed(Input.Keys.W) ||
        Gdx.input.isKeyPressed(Input.Keys.UP)) {

        if (canWalkTo(x, y + speed * delta)) {
            y += speed * delta;
            isMoving = true;
        }
        direction = 1;
    }

    if (Gdx.input.isKeyPressed(Input.Keys.S) ||
        Gdx.input.isKeyPressed(Input.Keys.DOWN)) {

        if (canWalkTo(x, y - speed * delta)) {
            y -= speed * delta;
            isMoving = true;
        }
        direction = 0;
    }

    if (Gdx.input.isKeyPressed(Input.Keys.A) ||
        Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

        if (canWalkTo(x - speed * delta, y)) {
            x -= speed * delta;
            isMoving = true;
        }
        direction = 2;
    }

    if (Gdx.input.isKeyPressed(Input.Keys.D) ||
        Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

        if (canWalkTo(x + speed * delta, y)) {
            x += speed * delta;
            isMoving = true;
        }
        direction = 3;
    }

    if (isMoving) {
        stateTime += delta;
    }
}

public void render(SpriteBatch batch) {

    TextureRegion currentFrame;

    if (isMoving) {
        switch (direction) {
            case 0:
                currentFrame = walkDown.getKeyFrame(stateTime);
                break;
            case 1:
                currentFrame = walkUp.getKeyFrame(stateTime);
                break;
            case 2:
                currentFrame = walkLeft.getKeyFrame(stateTime);
                break;
            default:
                currentFrame = walkRight.getKeyFrame(stateTime);
                break;
        }
    } else {
        currentFrame = animationFrames[direction][0];
    }

    batch.draw(currentFrame, x - 60, y - 60, 120, 120);
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
private boolean canWalkTo(float newX, float newY) {
    float halfSize = 60;

    // Four corners
    boolean corners =
        map.isWalkable(GameMap.toTile(newX - halfSize), GameMap.toTile(newY - halfSize)) &&
        map.isWalkable(GameMap.toTile(newX + halfSize), GameMap.toTile(newY - halfSize)) &&
        map.isWalkable(GameMap.toTile(newX - halfSize), GameMap.toTile(newY + halfSize)) &&
        map.isWalkable(GameMap.toTile(newX + halfSize), GameMap.toTile(newY + halfSize));

    // Middle of each side
    boolean sides =
        map.isWalkable(GameMap.toTile(newX), GameMap.toTile(newY - halfSize)) &&
        map.isWalkable(GameMap.toTile(newX), GameMap.toTile(newY + halfSize)) &&
        map.isWalkable(GameMap.toTile(newX - halfSize), GameMap.toTile(newY)) &&
        map.isWalkable(GameMap.toTile(newX + halfSize), GameMap.toTile(newY));

    return corners && sides;
}
}