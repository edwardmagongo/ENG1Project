
package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import app.vercel.eng1seven.map.GameMap;
import app.vercel.eng1seven.map.MapRenderer;

public class Core extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private GameMap map;
    private MapRenderer mapRenderer;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();

        map = GameMap.campus();
        mapRenderer = new MapRenderer(map);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        player = new Player(map);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        player.update();

        // Follow the player
        camera.position.set(player.getX(), player.getY(), 0);

        clampCameraToMap();
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.render(batch, camera);
        player.render(batch);

        batch.end();
    }

    private void clampCameraToMap() {
        float halfWidth = camera.viewportWidth * 0.5f * camera.zoom;
        float halfHeight = camera.viewportHeight * 0.5f * camera.zoom;

        float worldWidth = map.getWorldWidth();
        float worldHeight = map.getWorldHeight();

        camera.position.x = halfWidth * 2f >= worldWidth
                ? worldWidth * 0.5f
                : MathUtils.clamp(camera.position.x, halfWidth,
                        worldWidth - halfWidth);

        camera.position.y = halfHeight * 2f >= worldHeight
                ? worldHeight * 0.5f
                : MathUtils.clamp(camera.position.y, halfHeight,
                        worldHeight - halfHeight);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        clampCameraToMap();
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        mapRenderer.dispose();
        player.dispose();
    }
}
