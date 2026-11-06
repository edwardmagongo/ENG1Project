package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import app.vercel.eng1seven.map.GameMap;
import app.vercel.eng1seven.map.MapRenderer;

// Draws the campus and keeps the camera on the player.
public class Core extends ApplicationAdapter {
    // The smallest world area the camera shows. ExtendViewport grows one axis to fill the window.
    private static final float MIN_VIEW_WIDTH = 640f;
    private static final float MIN_VIEW_HEIGHT = 480f;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private GameMap map;
    private MapRenderer mapRenderer;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();

        map = GameMap.campus();
        mapRenderer = new MapRenderer(map);
        player = new Player(map);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT, camera);
        camera.position.set(player.getX(), player.getY(), 0f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        clampCameraToMap();
        camera.update();
    }

    @Override
    public void render() {
        player.update(Gdx.graphics.getDeltaTime());

        camera.position.set(player.getX(), player.getY(), 0f);
        clampCameraToMap();
        camera.update();

        ScreenUtils.clear(0.08f, 0.1f, 0.14f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mapRenderer.render(batch, camera);
        player.render(batch);
        batch.end();
    }

    // Keeps the view inside the map, or centred on it when the map is smaller than the view.
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
        player.dispose();
    }
}
