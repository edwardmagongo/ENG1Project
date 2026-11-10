package app.vercel.eng1seven.world;

// The five zone layouts, written top row first in GameMap's symbols:
// '.' grass, '#' path, '+' plaza, 't' tree, '~' water. Every layout is 40x24 tiles.
// Exit gaps sit at tile columns 19-20 (top and bottom edges) and tile rows 11-12 (left and right).
final class ZoneLayouts {
    private ZoneLayouts() {
    }

    // The hub: the original campus with plaza borders along the paths, and a gap in the tree border
    // at the middle of every edge.
    static final String[] CAMPUS = {
        "ttttttttttttttttttt##ttttttttttttttttttt",
        "tt................+##+................tt",
        "tt................+##+....~~~~~~~~~~..tt",
        "tt................+##+...~~~~~~~~~~~~.tt",
        "tt................+##+...~~~~~~~~~~~~.tt",
        "tt................+##+....~~~~~~~~~~..tt",
        "tt................+##+.....~~~~~~~~...tt",
        "tt................+##+................tt",
        "tt................+##+................tt",
        "tt................+##+................tt",
        "tt+++++++++++++++++##+++++++++++++++++tt",
        "########################################",
        "########################################",
        "tt+++++++++++++++++##+++++++++++++++++tt",
        "tt................+##+................tt",
        "tt................+##+................tt",
        "tttttt............+##+................tt",
        "tttttt............+##+................tt",
        "tttttt............+##+................tt",
        "tt................+##+................tt",
        "tt................+##+............tttttt",
        "tt................+##+............tttttt",
        "tt................+##+................tt",
        "ttttttttttttttttttt##ttttttttttttttttttt"
    };

    // North Lake: a lake crossed by bridges, with a plaza island at the top. Exit: south.
    static final String[] NORTH = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tt....................................tt",
        "tt..............++++++++..............tt",
        "tt..~~~~~~~~~~~~++++++++~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~++++++++~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~++++++++~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~+++~~~~~~~~~~##~~~~~~~~~~+++~~..tt",
        "tt..~~+++######################+++~~..tt",
        "tt..~~+++######################+++~~..tt",
        "tt..~~+++~~~~~~~~~~##~~~~~~~~~~+++~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt..~~~~~~~~~~~~~~~##~~~~~~~~~~~~~~~..tt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "ttttttttttttttttttt##ttttttttttttttttttt"
    };

    // East Fields: open grass with a few tree clumps and a plaza in the far east. Exit: west.
    static final String[] EAST = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tt....................................tt",
        "tt....................................tt",
        "tt......ttt...........................tt",
        "tt......ttt...........ttt.............tt",
        "tt......ttt...........ttt.............tt",
        "tt....................ttt.............tt",
        "tt....................................tt",
        "tt.............tt...............++++++tt",
        "tt.............tt...............++++++tt",
        "tt..............................++++++tt",
        "################################++++++tt",
        "################################++++++tt",
        "tt..............................++++++tt",
        "tt...........................tt.++++++tt",
        "tt...........................tt.++++++tt",
        "tt......................ttt...........tt",
        "tt.......tttt...........ttt...........tt",
        "tt.......tttt...........ttt...........tt",
        "tt.......tttt.........................tt",
        "tt....................................tt",
        "tt....................................tt",
        "tt....................................tt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };

    // South Courtyard (final zone): a plaza arena with tree pillars. Exit: north.
    static final String[] SOUTH = {
        "ttttttttttttttttttt##ttttttttttttttttttt",
        "tt.................##.................tt",
        "tt.................##.................tt",
        "tt...++++++++++++++##++++++++++++++...tt",
        "tt...++++++++++++++##++++++++++++++...tt",
        "tt...++++++++++++++##++++++++++++++...tt",
        "tt...++++tt++++++++##+++++++++tt+++...tt",
        "tt...++++tt++++++++##+++++++++tt+++...tt",
        "tt...++++++++++++++++++++++++++++++...tt",
        "tt...+++++++++tt+++++++++tt++++++++...tt",
        "tt...+++++++++tt+++++++++tt++++++++...tt",
        "tt...++++++++++++++++++++++++++++++...tt",
        "tt...++++tt+++++++++++++++++++tt+++...tt",
        "tt...++++tt+++++++++++++++++++tt+++...tt",
        "tt...++++++++++++++++++++++++++++++...tt",
        "tt...+++++++++tt+++++++++tt++++++++...tt",
        "tt...+++++++++tt+++++++++tt++++++++...tt",
        "tt...++++tt+++++++++++++++++++tt+++...tt",
        "tt...++++tt+++++++++++++++++++tt+++...tt",
        "tt...++++++++++++++++++++++++++++++...tt",
        "tt...++++++++++++++++++++++++++++++...tt",
        "tt...++++++++++++++++++++++++++++++...tt",
        "tt....................................tt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };

    // West Woods: dense trees, narrow paths and a clearing in the far west. Exit: east.
    static final String[] WEST = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tttttttttttttttttttttttttttttttttttttttt",
        "tttttttttttttttttttttttttttttttttttttttt",
        "ttt.......tttttttttttttttttttttttttttttt",
        "ttt.......tt################tttttttttttt",
        "ttt.......tt################tttttttttttt",
        "ttt.......####tttttttttttt##tttttttttttt",
        "ttt.......####tttttttttttt##tttttttttttt",
        "ttt.......tt##tttttttttttt##tttttttttttt",
        "ttt.......tt##tttttttttttt##tttttttttttt",
        "tttttttttttt##tttttttttttt##tttttttttttt",
        "tttttttttttt##tttttttttttt##############",
        "tttttttttttt##tttttttttttt##############",
        "tttttttttttt##tttttttttttttttttt##tttttt",
        "tttttttttttt##tt.........ttttttt##tttttt",
        "tttttttttttt##tt.........ttttttt##tttttt",
        "tttttttttttt####.........#########tttttt",
        "tttttttttttt####.........#########tttttt",
        "ttttttttttttt###.........ttttttttttttttt",
        "ttttttttttttt###.........ttttttttttttttt",
        "tttttttttttttttt.........ttttttttttttttt",
        "tttttttttttttttttttttttttttttttttttttttt",
        "tttttttttttttttttttttttttttttttttttttttt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };
}
