package app.vercel.eng1seven;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import app.vercel.eng1seven.items.Bread;
import app.vercel.eng1seven.items.Trap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import app.vercel.eng1seven.map.GameMap;
import app.vercel.eng1seven.map.MapRenderer;
import app.vercel.eng1seven.world.Direction;
import app.vercel.eng1seven.world.World;
import app.vercel.eng1seven.world.Zone;

// Draws the current zone and keeps the camera on the player.
public class Core extends ApplicationAdapter {
    // The smallest world area the camera shows. ExtendViewport grows one axis to
    // fill the window.
    private static final float MIN_VIEW_WIDTH = 640f;
    private static final float MIN_VIEW_HEIGHT = 480f;

    private static final float SPAWN_INTERVAL = 5f;
    private static final float PLAYER_DAMAGE_COOLDOWN = 1f;
    private static final int MAX_GEESE = 15;

    private static final float PICKUP_SPAWN_INTERVAL = 10f;
    private static final int MAX_PICKUPS_EACH = 3;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private World world;
    private GameMap map; // always the current zone's map
    private MapRenderer mapRenderer;
    private Player player;
    private Counter counter;

    private Array<Goose> geese;
    private Texture gooseTexture;
    private Texture bossTexture;
    private Texture breadTexture;
    private Texture trapTexture;
    private float spawnTimer;
    private float playerDamageCooldown;
    private Array<Bread> breads;
    private Array<Trap> traps;
    private Array<Bread> breadPickups;
    private Array<Trap> trapPickups;
    private float pickupSpawnTimer;

    private SpriteBatch hudBatch;
    private BitmapFont font;
    private Texture heartTexture;
    private final GlyphLayout textLayout = new GlyphLayout();

    private static final float TOTAL_TIME = 300f;
    private float timeRemaining = TOTAL_TIME;
    private boolean isTimeUp = false;
    private boolean isGameOver = false;
    private boolean bossGooseSpawned = false;
    private Texture overlayTexture;

    private String latestCheckpointName = "None";

    private boolean isPaused = false;

    // A banner near the top of the screen for checkpoints and zone changes, fading out over
    // its last half second.
    private static final float BANNER_TIME = 2.5f;
    private static final float BANNER_FADE = 0.5f;
    private String bannerText = "";
    private float bannerTimer = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        hudBatch = new SpriteBatch();
        heartTexture = new Texture(Gdx.files.internal("heart.png"));
        heartTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font = new BitmapFont();
        font.getData().setScale(1f);

        world = new World();
        map = world.current().getMap();
        mapRenderer = new MapRenderer(map);
        player = new Player(map);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT, camera);
        camera.position.set(player.getX(), player.getY(), 0f);

        gooseTexture = new Texture("geese/goose_spritesheet_1.png");
        bossTexture = new Texture("geese/goose_spritesheet_5.png");
        breadTexture = new Texture("golden-bread-roll.png");
        trapTexture = new Texture("open-metal-bear-trap.png");
        geese = new Array<>();
        breads = new Array<>();
        traps = new Array<>();
        breadPickups = new Array<>();
        trapPickups = new Array<>();

        spawnTimer = 0;
        pickupSpawnTimer = 0f;
        spawnInitialPickups();

        counter = new Counter();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        overlayTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        clampCameraToMap();
        camera.update();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // pause system
        if (!isGameOver && (Gdx.input.isKeyJustPressed(Keys.P) || Gdx.input.isKeyJustPressed(Keys.ESCAPE))) {
            isPaused = !isPaused;
        }

        // timer
        if (!isGameOver && !isPaused) {
            timeRemaining -= delta;
            if (timeRemaining <= 0f) {
                timeRemaining = 0f;
                isTimeUp = true;
                isGameOver = true;
            }
            // health game over
            if (player.getHealth() <= 0) {
                isGameOver = true;
            }
        }

        // only update gameplay if game is not over
        if (!isGameOver && !isPaused) {
            player.update(delta);
            updateZoneTransition();
            handleItemInput();
            updateGeese(delta);
            updateCheckpoint();
            bannerTimer = Math.max(0f, bannerTimer - delta);

            handleItemPickups();
            updatePickupSpawns(delta);

            camera.position.set(player.getX(), player.getY(), 0f);
            clampCameraToMap();
            camera.update();
        } else if (isGameOver && Gdx.input.isKeyJustPressed(Keys.R)) {
            restartGame();
        }

        ScreenUtils.clear(0.08f, 0.1f, 0.14f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mapRenderer.render(batch, camera);
        drawCheckpointZone();
        player.render(batch);
        float bobOffset = MathUtils.sin((TOTAL_TIME - timeRemaining) * 5f) * 4f;


        for (Goose g : geese) {
            g.render(batch);

        }
        for (Bread bread : breads) {
            batch.draw(breadTexture, bread.getX() - 16f, bread.getY() - 16f, 32f, 32f);
        }

        for (Trap trap : traps) {
            batch.draw(trapTexture, trap.getX() - 16f, trap.getY() - 16f, 32f, 32f);
        }

        for (Bread pickup : breadPickups) {
            batch.draw(breadTexture, pickup.getX() - 16f, pickup.getY() - 16f + bobOffset, 32f, 32f);
        }

        for (Trap pickup : trapPickups) {
            batch.draw(trapTexture, pickup.getX() - 16f, pickup.getY() - 16f + bobOffset, 32f, 32f);
        }

        batch.end();

        float margin = 16f, heartSize = 32f, gap = 8f;
        textLayout.setText(font, String.valueOf(player.getHealth()));
        float textX = Gdx.graphics.getWidth() - margin - textLayout.width;
        float heartX = textX - gap - heartSize;
        float heartY = Gdx.graphics.getHeight() - margin - heartSize;
        hudBatch.begin();
        hudBatch.draw(heartTexture, heartX, heartY, heartSize, heartSize);
        font.draw(hudBatch, textLayout, textX, heartY + heartSize / 2f + textLayout.height / 2f);

        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        // Built by hand: the web (GWT) build has no String.format.
        String timerText = "Time: " + (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds < 10 ? "0" : "") + seconds;
        font.setColor(timeRemaining <= 30f ? Color.RED : Color.WHITE);
        font.draw(hudBatch, timerText, 20f, Gdx.graphics.getHeight() - 20f); // draw timer top left for now

        font.setColor(Color.WHITE);
        font.draw(hudBatch, "Checkpoints: " + world.getCheckpointsReached() + "/" + world.getCheckpointCount()
                + " (" + latestCheckpointName + ")",
                20f, Gdx.graphics.getHeight() - 45f); // draw checkpoint number under the timer for now
        font.draw(hudBatch, "Zone: " + world.current().getName(), 20f, Gdx.graphics.getHeight() - 70f);
        font.draw(hudBatch, "Bread (1): " + player.getBreadCount() + "/3 | Traps (2): " + player.getTrapCount() + "/3",
                20f, Gdx.graphics.getHeight() - 95f);
        font.draw(hudBatch, "Items Used: " + counter.getItemsUsed(),
                20f, Gdx.graphics.getHeight() - 120f);

        if (bannerTimer > 0f && !isGameOver) {
            drawBanner();
        }

        if (isPaused && !isGameOver) {
            hudBatch.setColor(0f, 0f, 0f, 0.7f);
            hudBatch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            hudBatch.setColor(Color.WHITE);

            float centerX = Gdx.graphics.getWidth() * 0.5f;
            float centerY = Gdx.graphics.getHeight() * 0.5f;

            font.setColor(Color.YELLOW);
            drawCenteredText("PAUSED", centerX, centerY + 60f);

            font.setColor(Color.WHITE);
            drawCenteredText("Press P or ESC to Resume", centerX, centerY + 25f);
            drawCenteredText("WASD / Arrows: Move", centerX, centerY - 15f);
            drawCenteredText("1: Throw Bread   |   2: Place Trap", centerX, centerY - 40f);
            drawCenteredText("Walk over bobbing drops to pick them up", centerX, centerY - 65f);
        }

        if (isGameOver) {

            hudBatch.setColor(0f, 0f, 0f, 0.7f);
            hudBatch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            hudBatch.setColor(Color.WHITE);

            font.setColor(Color.RED);
            String gameOverText = isTimeUp ? "TIME'S UP! GAME OVER" : "YOU DIED! GAME OVER";
            font.draw(hudBatch, gameOverText, Gdx.graphics.getWidth() * 0.5f - 110f,
                    Gdx.graphics.getHeight() * 0.5f + 20f); // draw the game over if you guys dont like then

            font.setColor(Color.WHITE);
            font.draw(hudBatch, "Press R to Restart", Gdx.graphics.getWidth() * 0.5f - 80f,
                    Gdx.graphics.getHeight() * 0.5f - 15f);
        }

        hudBatch.end();
    }

    // Moves the player into the next zone when their feet reach an exit.
    private void updateZoneTransition() {
        Zone from = world.current();
        Direction exit = from.exitAt(player.getX(), player.getFeetY());
        if (exit == null) return;

        Vector2 arrival = world.travel(exit);
        if (arrival == null) return;

        Zone to = world.current();
        map = to.getMap();
        player.setMap(map);
        player.placeFeetAt(arrival.x, arrival.y);
        mapRenderer.dispose();
        mapRenderer = new MapRenderer(map);

        // Each zone starts without geese or dropped items.
        geese.clear();
        breads.clear();
        traps.clear();
        breadPickups.clear();
        trapPickups.clear();
        spawnTimer = 0;
        pickupSpawnTimer = 0f;
        spawnInitialPickups();

        camera.position.set(player.getX(), player.getY(), 0f);
        clampCameraToMap();
        camera.update();

        showBanner("Entering: " + to.getName());
        onZoneEntered(from, to);
    }

    // Called after every zone change. Hidden events plug in here: #6 heals the player on a
    // transition, and #7 spawns the boss goose on entering the final zone (South).
    private void onZoneEntered(Zone from, Zone to) {
    }

    private void updateCheckpoint() {
        Checkpoint checkpoint = world.current().getCheckpoint();
        if (checkpoint.checkPlayer(player.getX(), player.getFeetY())) {
            latestCheckpointName = checkpoint.getName();
            counter.chkpntsTriggered();
            showBanner("Checkpoint reached: " + checkpoint.getName()
                    + "  (" + world.getCheckpointsReached() + "/" + world.getCheckpointCount() + ")");
        }
    }

    private void showBanner(String text) {
        bannerText = text;
        bannerTimer = BANNER_TIME;
    }

    // Tints the current zone's checkpoint area: gold until reached, then green.
    private void drawCheckpointZone() {
        Checkpoint checkpoint = world.current().getCheckpoint();
        Rectangle bounds = checkpoint.getBounds();
        if (checkpoint.isReached()) {
            batch.setColor(0.3f, 0.9f, 0.4f, 0.25f);
        } else {
            batch.setColor(1f, 0.85f, 0.2f, 0.4f);
        }
        batch.draw(overlayTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
    }

    // Draws the banner near the top of the screen on a dark box.
    private void drawBanner() {
        float alpha = Math.min(1f, bannerTimer / BANNER_FADE);
        // GlyphLayout takes the font's colour when the text is set, so set the colour first.
        font.setColor(1f, 0.85f, 0.2f, alpha);
        textLayout.setText(font, bannerText);

        float padding = 12f;
        float boxWidth = textLayout.width + padding * 2f;
        float boxHeight = textLayout.height + padding * 2f;
        float boxX = (Gdx.graphics.getWidth() - boxWidth) * 0.5f;
        float boxY = Gdx.graphics.getHeight() * 0.75f;

        hudBatch.setColor(0f, 0f, 0f, 0.6f * alpha);
        hudBatch.draw(overlayTexture, boxX, boxY, boxWidth, boxHeight);
        hudBatch.setColor(Color.WHITE);

        font.draw(hudBatch, textLayout, boxX + padding, boxY + padding + textLayout.height);
        font.setColor(Color.WHITE);
    }

    private void restartGame() {
        world.reset();
        map = world.current().getMap();
        mapRenderer.dispose();
        mapRenderer = new MapRenderer(map);
        player.dispose();
        player = new Player(map);
        timeRemaining = TOTAL_TIME;
        isTimeUp = false;
        isGameOver = false;
        isPaused = false;
        bossGooseSpawned = false;
        latestCheckpointName = "None";
        bannerTimer = 0f;
        geese.clear();
        breads.clear();
        traps.clear();
        breadPickups.clear();
        trapPickups.clear();
        spawnTimer = 0;
        pickupSpawnTimer = 0f;
        counter = new Counter();
        spawnInitialPickups();
        playerDamageCooldown = 0;
        counter = new Counter();
    }

    private void updateGeese(float delta) {
        playerDamageCooldown = Math.max(0f, playerDamageCooldown - delta);
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL && geese.size < MAX_GEESE) {
            spawnTimer = 0;
            Goose spawned = Goose.spawn(camera, gooseTexture, player, map, true);
            if (spawned != null) geese.add(spawned);
        }

  
        if (!bossGooseSpawned && counter.getchkpntsTriggered() == 4) {
            bossGooseSpawned = true;
            counter.hiddenEventTriggered();
            //boss goose currently spawns in the bottom right, as the 4th checkpoint is in the top left
            geese.add(new Goose(
                map.getWorldWidth() * 0.8f, map.getWorldHeight() * 0.3f,
                bossTexture, player, map, true, 1.5f));
        }

        // Iterate backwards so removing is safe
        for (int i = geese.size - 1; i >= 0; i--) {
            Goose g = geese.get(i);
            g.update(delta, breads, traps);
            if (g.touchesPlayer()) {
                // nikosh's damage code
                if (g.isBoss() == true) {
                    if (playerDamageCooldown <= 0f) {
                        player.damage(g.getDamage());
                        playerDamageCooldown = PLAYER_DAMAGE_COOLDOWN;
                    }
                }
                else {
                    player.damage(g.getDamage());
                }

                // need to implement damage system later
                //nikosh made it so the boss doesnt despawn for now
                if (!g.isBoss()) {
                    geese.removeIndex(i);
                }
            }
        }
    }

    private void handleItemInput() {
        float placementDistance = 70f;

        if ((Gdx.input.isKeyJustPressed(Keys.NUM_1) || Gdx.input.isKeyJustPressed(Keys.B)) && player.useBread()) {
            float x = player.getX() + player.getFacingX() * placementDistance;
            float y = player.getY() + player.getFacingY() * placementDistance;
            breads.add(new Bread(x, y));
            counter.usedItem();
        }

        if ((Gdx.input.isKeyJustPressed(Keys.NUM_2) || Gdx.input.isKeyJustPressed(Keys.T)) && player.useTrap()) {
            float x = player.getX() + player.getFacingX() * placementDistance;
            float y = player.getY() + player.getFacingY() * placementDistance;
            traps.add(new Trap(x, y));
            counter.usedItem();
        }
    }

    private void handleItemPickups() {
        float pickupRadius = 32f;
        for (int i = breadPickups.size - 1; i >= 0; i--) {
            Bread pickup = breadPickups.get(i);
            float dx = player.getX() - pickup.getX();
            float dy = player.getY() - pickup.getY();
            if (dx * dx + dy * dy <= pickupRadius * pickupRadius) {
                if (player.pickUpBread()) {
                    breadPickups.removeIndex(i);
                }
            }
        }

        for (int i = trapPickups.size - 1; i >= 0; i--) {
            Trap pickup = trapPickups.get(i);
            float dx = player.getX() - pickup.getX();
            float dy = player.getY() - pickup.getY();
            if (dx * dx + dy * dy <= pickupRadius * pickupRadius) {
                if (player.pickUpTrap()) {
                    trapPickups.removeIndex(i);
                }
            }
        }
    }

    private void spawnInitialPickups() {
        for (int i = 0; i < 2; i++) {
            spawnRandomPickup(true);
            spawnRandomPickup(false);
        }
    }

    private void updatePickupSpawns(float delta) {
        pickupSpawnTimer += delta;
        if (pickupSpawnTimer < PICKUP_SPAWN_INTERVAL) {
            return;
        }
        pickupSpawnTimer = 0f;

        boolean canSpawnBread = breadPickups.size < MAX_PICKUPS_EACH;
        boolean canSpawnTrap = trapPickups.size < MAX_PICKUPS_EACH;
        if (!canSpawnBread && !canSpawnTrap) {
            return;
        }

        if (canSpawnBread && canSpawnTrap) {
            spawnRandomPickup(MathUtils.randomBoolean());
        } else {
            spawnRandomPickup(canSpawnBread);
        }
    }

    private void spawnRandomPickup(boolean isBread) {
        for (int attempt = 0; attempt < 50; attempt++) {
            int tileX = MathUtils.random(1, map.getWidth() - 2);
            int tileY = MathUtils.random(1, map.getHeight() - 2);
            if (!map.isWalkable(tileX, tileY)) {
                continue;
            }
            float wx = tileX * GameMap.TILE_SIZE + GameMap.TILE_SIZE * 0.5f;
            float wy = tileY * GameMap.TILE_SIZE + GameMap.TILE_SIZE * 0.5f;
            if (Math.abs(wx - player.getX()) < 64f && Math.abs(wy - player.getY()) < 64f) {
                continue;
            }
            if (isBread) {
                breadPickups.add(new Bread(wx, wy));
            } else {
                trapPickups.add(new Trap(wx, wy));
            }
            return;
        }
    }

    private void drawCenteredText(String text, float centerX, float y) {
        textLayout.setText(font, text);
        font.draw(hudBatch, textLayout, centerX - textLayout.width * 0.5f, y);
    }

    // Keeps the view inside the map, or centred on it when the map is smaller than
    // the view.
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
        hudBatch.dispose();
        heartTexture.dispose();
        font.dispose();
        mapRenderer.dispose();
        player.dispose();
        gooseTexture.dispose();
        bossTexture.dispose();
        breadTexture.dispose();
        trapTexture.dispose();
        overlayTexture.dispose();
    }
}
