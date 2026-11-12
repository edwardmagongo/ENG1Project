package app.vercel.eng1seven.world;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import app.vercel.eng1seven.Checkpoint;
import app.vercel.eng1seven.map.GameMap;

// One area of the game world: its map, its checkpoint and the exits that lead to other zones.
// Every position here is the player's feet (the bottom centre of the sprite), because
// collision and the tile map work at the feet.
public class Zone {
    // Exits are 2-tile gaps in the border: columns 19-20 on the top and bottom edges,
    // rows 11-12 on the left and right edges.
    static final int EXIT_FIRST_COLUMN = 19;
    static final int EXIT_LAST_COLUMN = 20;
    static final int EXIT_FIRST_ROW = 11;
    static final int EXIT_LAST_ROW = 12;

    // Feet this close to an edge, inside its exit gap, use the exit. The player's sprite keeps
    // the feet at least 120 units below the top edge, so this has to be deeper than that.
    static final float TRIGGER_DEPTH = 5f * GameMap.TILE_SIZE;
    // Arriving players stand this far in, clear of the trigger, so they don't bounce straight back.
    static final float ENTRY_DEPTH = 7f * GameMap.TILE_SIZE;

    private final ZoneId id;
    private final String name;
    private final GameMap map;
    private final Checkpoint checkpoint;
    private final EnumMap<Direction, ZoneId> exits = new EnumMap<>(Direction.class);

    public Zone(ZoneId id, String name, String[] layout, Checkpoint checkpoint, Map<Direction, ZoneId> exits) {
        this.id = id;
        this.name = name;
        this.map = new GameMap(layout);
        this.checkpoint = checkpoint;
        this.exits.putAll(exits);
    }

    public ZoneId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GameMap getMap() {
        return map;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    // The zone an exit on this edge leads to, or null if this edge has no exit.
    public ZoneId getExitTarget(Direction edge) {
        return exits.get(edge);
    }

    // The exit the player's feet are standing in, or null if they aren't in one.
    public Direction exitAt(float feetX, float feetY) {
        for (Direction edge : exits.keySet()) {
            if (inTrigger(edge, feetX, feetY)) return edge;
        }
        return null;
    }

    // Where the player's feet go when they arrive through the exit on this edge.
    public Vector2 entryPoint(Direction edge) {
        float pathX = (EXIT_FIRST_COLUMN + EXIT_LAST_COLUMN + 1) * 0.5f * GameMap.TILE_SIZE;
        float pathY = (EXIT_FIRST_ROW + 0.5f) * GameMap.TILE_SIZE;
        switch (edge) {
            case NORTH: return new Vector2(pathX, map.getWorldHeight() - ENTRY_DEPTH);
            case SOUTH: return new Vector2(pathX, ENTRY_DEPTH);
            case WEST: return new Vector2(ENTRY_DEPTH, pathY);
            default: return new Vector2(map.getWorldWidth() - ENTRY_DEPTH, pathY);
        }
    }

    private boolean inTrigger(Direction edge, float feetX, float feetY) {
        switch (edge) {
            case NORTH: return inExitColumns(feetX) && feetY >= map.getWorldHeight() - TRIGGER_DEPTH;
            case SOUTH: return inExitColumns(feetX) && feetY < TRIGGER_DEPTH;
            case WEST: return inExitRows(feetY) && feetX < TRIGGER_DEPTH;
            default: return inExitRows(feetY) && feetX >= map.getWorldWidth() - TRIGGER_DEPTH;
        }
    }

    private static boolean inExitColumns(float x) {
        return x >= EXIT_FIRST_COLUMN * GameMap.TILE_SIZE && x < (EXIT_LAST_COLUMN + 1) * GameMap.TILE_SIZE;
    }

    private static boolean inExitRows(float y) {
        return y >= EXIT_FIRST_ROW * GameMap.TILE_SIZE && y < (EXIT_LAST_ROW + 1) * GameMap.TILE_SIZE;
    }
}
