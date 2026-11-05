package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import app.vercel.eng1seven.map.GameMap;
import app.vercel.eng1seven.map.MapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private static final float PAN_SPEED = 400f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2f;
    private static final float ZOOM_SPEED = 1f;
    private static final float SCROLL_ZOOM_STEP = 0.1f;

    private SpriteBatch batch;
    private GameMap map;
    private MapRenderer mapRenderer;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Texture gooseTexture;
    private SpriteBatch gooseBatch;
    private FitViewport gooseViewport;



    @Override
    public void create() {
        batch = new SpriteBatch();
        map = GameMap.campus();
        mapRenderer = new MapRenderer(map);

        gooseTexture = new Texture("goose.png");
        gooseBatch = new SpriteBatch();
        gooseViewport = new FitViewport(8, 5);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(640, 480, camera);
        camera.position.set(map.getWorldWidth() * 0.5f, map.getWorldHeight() * 0.5f, 0f);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                camera.zoom = MathUtils.clamp(
                    camera.zoom + amountY * SCROLL_ZOOM_STEP, MIN_ZOOM, MAX_ZOOM);
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        panCamera(Gdx.graphics.getDeltaTime());

        ScreenUtils.clear(0.08f, 0.1f, 0.14f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mapRenderer.render(batch, camera);
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
        batch.dispose();
        mapRenderer.dispose();
    }
}
