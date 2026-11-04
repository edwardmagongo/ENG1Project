package app.vercel.eng1seven.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

// Draws a GameMap as flat coloured tiles: one white pixel, tinted per tile.
public class MapRenderer implements Disposable {
    private final GameMap map;
    private final Texture blank;

    public MapRenderer(GameMap map) {
        this.map = map;
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        blank = new Texture(pixmap);
        pixmap.dispose();
    }

    // Draws the tiles the camera can see. The batch must already be begun.
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float halfWidth = camera.viewportWidth * 0.5f * camera.zoom;
        float halfHeight = camera.viewportHeight * 0.5f * camera.zoom;
        int minX = Math.max(0, GameMap.toTile(camera.position.x - halfWidth));
        int maxX = Math.min(map.getWidth() - 1, GameMap.toTile(camera.position.x + halfWidth));
        int minY = Math.max(0, GameMap.toTile(camera.position.y - halfHeight));
        int maxY = Math.min(map.getHeight() - 1, GameMap.toTile(camera.position.y + halfHeight));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                batch.setColor(map.getTile(x, y).getColor());
                batch.draw(blank, x * GameMap.TILE_SIZE, y * GameMap.TILE_SIZE,
                    GameMap.TILE_SIZE, GameMap.TILE_SIZE);
            }
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        blank.dispose();
    }
}
