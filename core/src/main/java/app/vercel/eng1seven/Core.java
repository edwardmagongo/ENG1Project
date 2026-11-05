package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
   
    private ShapeRenderer shapeRenderer;
    private float playerX = 320;
    private float playerY = 240;
    private float playerSpeed = 200;
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.6f, 0.2f, 1f);
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.UP)) {
                playerY += playerSpeed * Gdx.graphics.getDeltaTime();
}
        if (Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                playerY -= playerSpeed * Gdx.graphics.getDeltaTime();
}
        if (Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                playerX -= playerSpeed * Gdx.graphics.getDeltaTime();
}
        if (Gdx.input.isKeyPressed(Input.Keys.D) ||
                Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                playerX += playerSpeed * Gdx.graphics.getDeltaTime();
}
        playerX = Math.max(15, Math.min(playerX, Gdx.graphics.getWidth() - 15));
        playerY = Math.max(15, Math.min(playerY, Gdx.graphics.getHeight() - 15));


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.rect(250, 0, 140, 480);
        shapeRenderer.rect(0, 200, 640, 80);
        shapeRenderer.setColor(0.6f, 0.3f, 0.1f, 1f);
        shapeRenderer.rect(50, 320, 120, 70);
        shapeRenderer.rect(460, 320, 120, 70);
        shapeRenderer.rect(50, 80, 120, 70);
        shapeRenderer.rect(460, 80, 120, 70);
        shapeRenderer.setColor(0f, 0f, 1f, 1f);
        shapeRenderer.rect(70, 340, 20, 15);
        shapeRenderer.rect(120, 340, 20, 15);
        shapeRenderer.rect(480, 340, 20, 15);
        shapeRenderer.rect(530, 340, 20, 15);
        shapeRenderer.rect(70, 100, 20, 15);
        shapeRenderer.rect(120, 100, 20, 15);
        shapeRenderer.rect(480, 100, 20, 15);
        shapeRenderer.rect(530, 100, 20, 15);
        shapeRenderer.setColor(0f, 0.35f, 0f, 1f);
        shapeRenderer.circle(210, 350, 18);
        shapeRenderer.circle(430, 350, 18);
        shapeRenderer.circle(210, 120, 18);
        shapeRenderer.circle(430, 120, 18);
        shapeRenderer.setColor(0.4f, 0.8f, 1f, 1f);
        shapeRenderer.circle(playerX, playerY, 15);
        shapeRenderer.end();
        
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
