package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture gooseTexture;
    private SpriteBatch spriteBatch;
    private FitViewport viewport;


    @Override
    public void create() {
        batch = new SpriteBatch();
        gooseTexture = new Texture("goose.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);



    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(gooseTexture, 210, 140, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        gooseTexture.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        
}



    }

}
