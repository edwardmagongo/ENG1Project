
package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import app.vercel.eng1seven.map.GameMap;
import app.vercel.eng1seven.map.MapRenderer;


public class Core extends ApplicationAdapter {

    private static final float SPAWN_INTERVAL = 5f;
    private static final int MAX_GEESE = 15;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private GameMap map;
    private MapRenderer mapRenderer;
    private Player player;

    private Texture gooseTexture;
    private Array<goose> geese;
    private float spawnTimer;


    @Override
    public void create() {
        batch = new SpriteBatch();

        map = GameMap.campus();
        mapRenderer = new MapRenderer(map);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        player = new Player(map);

        gooseTexture = new Texture("goose.png");
        geese = new Array<>();
        spawnTimer = 0;

    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        player.update();
        updateGeese(delta);

        // Follow the player
        camera.position.set(player.getX(), player.getY(), 0);

        clampCameraToMap();
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.render(batch, camera);
        player.render(batch);
        for (goose g : geese) {
            g.render(batch);
        }

        batch.end();
    }

    private void updateGeese(float delta) {
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL && geese.size < MAX_GEESE) {
            spawnTimer = 0;
            geese.add(goose.spawn(camera, gooseTexture, player, map, false));
        }

        // Iterate backwards so removing is safe
        for (int i = geese.size - 1; i >= 0; i--) {
            goose g = geese.get(i);
            g.update(delta);
            if (g.touchesPlayer()) {
                // need to implement damage system later
                geese.removeIndex(i);
            }
        }
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
        gooseTexture.dispose();
    }


}
