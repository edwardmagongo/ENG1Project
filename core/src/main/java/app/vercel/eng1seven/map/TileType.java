package app.vercel.eng1seven.map;

import com.badlogic.gdx.graphics.Color;

/** The kinds of terrain a {@link GameMap} tile can hold. */
public enum TileType {
    GRASS('.', 0x4c8b3aff, true, true),
    PATH('#', 0xb08b5eff, true, false),
    PLAZA('+', 0x9a9a95ff, true, false),
    TREE('t', 0x2c5e2aff, false, false),
    WATER('~', 0x2f6f9eff, false, false);

    private final char symbol;
    private final Color color;
    private final boolean walkable;
    private final boolean buildable;

    TileType(char symbol, int rgba8888, boolean walkable, boolean buildable) {
        this.symbol = symbol;
        this.color = new Color(rgba8888);
        this.walkable = walkable;
        this.buildable = buildable;
    }

    // The character this tile is written as in a map layout.
    public char getSymbol() {
        return symbol;
    }

    public Color getColor() {
        return color;
    }

    // Whether a character may stand on this tile.
    public boolean isWalkable() {
        return walkable;
    }

    // Whether a building may be placed on this tile.
    public boolean isBuildable() {
        return buildable;
    }

    // Throws IllegalArgumentException if no tile type uses the symbol.
    public static TileType fromSymbol(char symbol) {
        for (TileType type : values()) {
            if (type.symbol == symbol) return type;
        }
        throw new IllegalArgumentException("Unknown map symbol: '" + symbol + "'");
    }
}
