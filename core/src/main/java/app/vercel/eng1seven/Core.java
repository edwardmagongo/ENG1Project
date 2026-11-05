package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    @Override
   public void create() {
        shapeRenderer = new ShapeRenderer();
        player = new Player();
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        
    }

    /** Moves and zooms the camera from the keyboard, then keeps it over the map. */
    private void panCamera(float delta) {
        if (Gdx.input.isKeyPressed(Keys.Q)) camera.zoom += ZOOM_SPEED * delta;
        if (Gdx.input.isKeyPressed(Keys.E)) camera.zoom -= ZOOM_SPEED * delta;
        camera.zoom = MathUtils.clamp(camera.zoom, MIN_ZOOM, MAX_ZOOM);

        float step = PAN_SPEED * camera.zoom * delta;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) camera.position.x -= step;
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) camera.position.x += step;
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) camera.position.y -= step;
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) camera.position.y += step;

        clampCameraToMap();
        camera.update();
    }

    /** Keeps the view inside the map, or centred on it when the map is smaller than the view. */
    private void clampCameraToMap() {
        float halfWidth = camera.viewportWidth * 0.5f * camera.zoom;
        float halfHeight = camera.viewportHeight * 0.5f * camera.zoom;
        float worldWidth = map.getWorldWidth();
        float worldHeight = map.getWorldHeight();

        camera.position.x = halfWidth * 2f >= worldWidth
            ? worldWidth * 0.5f
            : MathUtils.clamp(camera.position.x, halfWidth, worldWidth - halfWidth);
        camera.position.y = halfHeight * 2f >= worldHeight
            ? worldHeight * 0.5f
            : MathUtils.clamp(camera.position.y, halfHeight, worldHeight - halfHeight);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        player.dispose();
        image.dispose();
    }
}
