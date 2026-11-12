package app.vercel.eng1seven.world;

// An edge of a zone, and the way an exit on that edge leads.
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            default: return EAST;
        }
    }
}
