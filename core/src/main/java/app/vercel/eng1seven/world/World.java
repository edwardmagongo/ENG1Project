package app.vercel.eng1seven.world;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import app.vercel.eng1seven.Checkpoint;

// The five zones and which one the player is in. The campus is the hub; North, East, South
// and West each connect only to it. South is the final zone.
public class World {
    private final EnumMap<ZoneId, Zone> zones = new EnumMap<>(ZoneId.class);
    private Zone current;

    public World() {
        Map<Direction, ZoneId> campusExits = new EnumMap<>(Direction.class);
        campusExits.put(Direction.NORTH, ZoneId.NORTH);
        campusExits.put(Direction.EAST, ZoneId.EAST);
        campusExits.put(Direction.SOUTH, ZoneId.SOUTH);
        campusExits.put(Direction.WEST, ZoneId.WEST);

        add(new Zone(ZoneId.CAMPUS, "Campus East", ZoneLayouts.CAMPUS,
                new Checkpoint("David Kato College", 860f, 60f, 120f, 120f), campusExits));
        add(new Zone(ZoneId.NORTH, "Goodricke College", ZoneLayouts.NORTH,
                new Checkpoint("Goodricke Courtyard", 580f, 560f, 120f, 120f), backToCampus(Direction.SOUTH)));
        add(new Zone(ZoneId.EAST, "The Lake", ZoneLayouts.EAST,
                new Checkpoint("Lake Pier", 1060f, 320f, 120f, 120f), backToCampus(Direction.WEST)));
        add(new Zone(ZoneId.SOUTH, "Ron Cooke Hub", ZoneLayouts.SOUTH,
                new Checkpoint("The Piazza", 580f, 40f, 120f, 120f), backToCampus(Direction.NORTH)));
        add(new Zone(ZoneId.WEST, "Anne Lister College", ZoneLayouts.WEST,
                new Checkpoint("Anne Lister Quad", 120f, 520f, 120f, 120f), backToCampus(Direction.EAST)));

        current = zones.get(ZoneId.CAMPUS);
    }

    private void add(Zone zone) {
        zones.put(zone.getId(), zone);
    }

    private static Map<Direction, ZoneId> backToCampus(Direction edge) {
        Map<Direction, ZoneId> exits = new EnumMap<>(Direction.class);
        exits.put(edge, ZoneId.CAMPUS);
        return exits;
    }

    public Zone current() {
        return current;
    }

    // Moves to the zone behind this exit and returns where the player's feet arrive,
    // or returns null and stays put if there is no exit there.
    public Vector2 travel(Direction exit) {
        ZoneId target = current.getExitTarget(exit);
        if (target == null) return null;
        current = zones.get(target);
        return current.entryPoint(exit.opposite());
    }

    public int getCheckpointsReached() {
        int reached = 0;
        for (Zone zone : zones.values()) {
            if (zone.getCheckpoint().isReached()) reached++;
        }
        return reached;
    }

    public int getCheckpointCount() {
        return zones.size();
    }

    // Back to the campus with every checkpoint cleared, for a restart.
    public void reset() {
        for (Zone zone : zones.values()) {
            zone.getCheckpoint().reset();
        }
        current = zones.get(ZoneId.CAMPUS);
    }
}
