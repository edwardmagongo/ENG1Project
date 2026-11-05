package app.vercel.eng1seven.map;

// A rectangular grid of terrain, built from a character layout.
// Tile (0, 0) is the bottom-left tile, so tile coordinates line up with libGDX world coordinates.
public class GameMap {
    // Width and height of one tile, in world units.
    public static final int TILE_SIZE = 32;

    // The campus layout, written top row first.
    private static final String[] CAMPUS = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tt.................##.................tt",
        "tt.................##.....~~~~~~~~~~..tt",
        "tt.................##....~~~~~~~~~~~~.tt",
        "tt.................##....~~~~~~~~~~~~.tt",
        "tt.................##.....~~~~~~~~~~..tt",
        "tt.................##......~~~~~~~~...tt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "tt...............++++++...............tt",
        "tt####################################tt",
        "tt####################################tt",
        "tt...............++++++...............tt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "tttttt.............##.................tt",
        "tttttt.............##.................tt",
        "tttttt.............##.................tt",
        "tt.................##.................tt",
        "tt.................##.............tttttt",
        "tt.................##.............tttttt",
        "tt.................##.................tt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };

    private final TileType[][] tiles;
    private final int width;
    private final int height;

    public GameMap(String[] rows) {
        if (rows.length == 0 || rows[0].isEmpty()) {
            throw new IllegalArgumentException("Map layout is empty");
        }
        width = rows[0].length();
        height = rows.length;
        tiles = new TileType[width][height];
        for (int row = 0; row < height; row++) {
            if (rows[row].length() != width) {
                throw new IllegalArgumentException(
                    "Map row " + row + " is " + rows[row].length() + " tiles wide, expected " + width);
            }
            // Layouts read top-down, but tile y counts up from the bottom.
            int y = height - 1 - row;
            for (int x = 0; x < width; x++) {
                tiles[x][y] = TileType.fromSymbol(rows[row].charAt(x));
            }
        }
    }

    // The campus the game is played on.
    public static GameMap campus() {
        return new GameMap(CAMPUS);
    }

    // Width in tiles.
    public int getWidth() {
        return width;
    }

    // Height in tiles.
    public int getHeight() {
        return height;
    }

    // Width in world units.
    public float getWorldWidth() {
        return width * (float) TILE_SIZE;
    }

    // Height in world units.
    public float getWorldHeight() {
        return height * (float) TILE_SIZE;
    }

    public boolean contains(int tileX, int tileY) {
        return tileX >= 0 && tileX < width && tileY >= 0 && tileY < height;
    }

    // Returns the tile, or null if the coordinates are off the map.
    public TileType getTile(int tileX, int tileY) {
        return contains(tileX, tileY) ? tiles[tileX][tileY] : null;
    }

    // Off-map tiles are never walkable.
    public boolean isWalkable(int tileX, int tileY) {
        return contains(tileX, tileY) && tiles[tileX][tileY].isWalkable();
    }

    // Off-map tiles are never buildable.
    public boolean isBuildable(int tileX, int tileY) {
        return contains(tileX, tileY) && tiles[tileX][tileY].isBuildable();
    }

    // Converts a world coordinate to the tile index covering it.
    public static int toTile(float worldCoordinate) {
        return (int) Math.floor(worldCoordinate / TILE_SIZE);
    }
}
