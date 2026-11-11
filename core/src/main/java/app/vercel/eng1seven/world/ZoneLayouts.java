package app.vercel.eng1seven.world;

// The five zone layouts, loosely based on the University of York's Campus East,
// written top row first in GameMap's symbols: '.' grass, '#' path, '+' plaza (also used for
// building footprints), 't' tree, '~' water. Every layout is 40x24 tiles.
// Exit gaps sit at tile columns 19-20 (top and bottom edges) and tile rows 11-12 (left and right).
final class ZoneLayouts {
    private ZoneLayouts() {
    }

    // Campus East (the hub): The Pathway and Baird Lane cross in the middle, with the Catalyst,
    // York Law School, the Science Park, the Theatre building and David Kato College around them,
    // Lakeside Way along the south, a pond to the north-west and the edge of the lake to the north-east.
    static final String[] CAMPUS = {
        "ttttttttttttttttttt##ttttttttttttttttttt",
        "tt.................##.................tt",
        "tt.~~~~~~..++++++..##..+++++..........tt",
        "tt.~~~~~~..++++++..##..+++++...~~~~~~.tt",
        "tt.~~~~~~..++++++..##..+++++...~~~~~~.tt",
        "tt.~~~~~~..++++++..##..+++++...~~~~~~.tt",
        "tt.................##..+++++...~~~~~~.tt",
        "tt.................##..+++++++++++....tt",
        "tt..tttt...........##..+++++++++++....tt",
        "tt..tttt...........##..+++++++++++....tt",
        "tt.................##.................tt",
        "########################################",
        "########################################",
        "tt.................##.................tt",
        "tt.+++++....++++++.##............tttt.tt",
        "tt.+++++....++++++.##............tttt.tt",
        "tt.+++++....++++++.##.................tt",
        "tt..........++++++.##...++++++++......tt",
        "tt.................##...++++++++......tt",
        "tt.................##...++++++++......tt",
        "tt####################################tt",
        "tt.~~~~~~~~~~......##.................tt",
        "tt.~~~~~~~~~~......##.................tt",
        "ttttttttttttttttttt##ttttttttttttttttttt"
    };

    // Goodricke College: college blocks inside the Deramore Lane loop, courtyard at the top. Exit: south.
    static final String[] NORTH = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tt....................................tt",
        "tt.............++++++++++.............tt",
        "tt.~~~~........++++++++++.............tt",
        "tt.~~~~........++++++++++.............tt",
        "tt.~~~~........++++++++++.............tt",
        "tt.............++++##++++.............tt",
        "tt......########################......tt",
        "tt......########################......tt",
        "tt......##.........##.........##......tt",
        "tt......##.++++++..##..++++++.##......tt",
        "tt......##.++++++..##..++++++.##......tt",
        "tt......##.++++++..##..++++++.##......tt",
        "tt......##.++++++..##..++++++.##......tt",
        "tt......##.........##.........##......tt",
        "tt......########################......tt",
        "tt......########################......tt",
        "tt.................##.................tt",
        "tt.tttttttttt......##......tttttttttt.tt",
        "tt.tttttttttt......##......tttttttttt.tt",
        "tt.tttttttttt......##......tttttttttt.tt",
        "tt.tttttttttt......##......tttttttttt.tt",
        "tt.tttttttttt......##......tttttttttt.tt",
        "ttttttttttttttttttt##ttttttttttttttttttt"
    };

    // The Lake: Lakeside Way runs along the shore and a pier crosses the water. Exit: west.
    static final String[] EAST = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tt....................................tt",
        "tt..ttttttt...........##~~~~~~~~~~~~~~tt",
        "tt..ttttttt...........##~~~~~~~~~~~~~~tt",
        "tt..ttttttt...........##~~~~~~~~~~~~~~tt",
        "tt..ttttttt...........##~~~~~~~~~~~~~~tt",
        "tt....................##~~~~~~~~~~~~~~tt",
        "tt............tttt....##~~~~~~~~~~~~~~tt",
        "tt............tttt....##~~~~~~~~~~~~~~tt",
        "tt....................##~~~~~~~~~+++++tt",
        "tt....................##~~~~~~~~~+++++tt",
        "#################################+++++tt",
        "#################################+++++tt",
        "tt....................##~~~~~~~~~+++++tt",
        "tt....................##~~~~~~~~~+++++tt",
        "tt....................##~~~~~~~~~~~~~~tt",
        "tt....................##~~~~~~~~~~~~~~tt",
        "tt...tttttttt.........##~~~~~~~~~~~~~~tt",
        "tt...tttttttt.........##~~~~~~~~~~~~~~tt",
        "tt...tttttttt.........##~~~~~~~~~~~~~~tt",
        "tt...tttttttt.........##~~~~~~~~~~~~~~tt",
        "tt...tttttttt.........##~~~~~~~~~~~~~~tt",
        "tt....................................tt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };

    // Ron Cooke Hub (final zone): the hub building and its piazza beside the lake. Exit: north.
    static final String[] SOUTH = {
        "ttttttttttttttttttt##ttttttttttttttttttt",
        "tt.................##.................tt",
        "tt.tttttttttt......##.................tt",
        "tt.tttttttttt......##......##.........tt",
        "tt.tttttttttt......##......##~~~~~~~~~tt",
        "tt.tttttttttt......##......##~~~~~~~~~tt",
        "tt.tttttttttt......##......##~~~~~~~~~tt",
        "tt.................##......##~~~~~~~~~tt",
        "tt.................##......##~~~~~~~~~tt",
        "tt........+++++++++##++++++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++tt++++++++tt++##~~~~~~~~~tt",
        "tt........+++tt++++++++tt++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++tt++++++++tt++##~~~~~~~~~tt",
        "tt........+++tt++++++++tt++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt........+++++++++++++++++##~~~~~~~~~tt",
        "tt....................................tt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };

    // Anne Lister College: The Pathway leads to the college quad, with a pond to the south. Exit: east.
    static final String[] WEST = {
        "tttttttttttttttttttttttttttttttttttttttt",
        "tt....................................tt",
        "tt....................................tt",
        "tt.+++++++.....++++++++++.............tt",
        "tt.+++++++.....++++++++++.............tt",
        "tt.+++++++####.++++...................tt",
        "tt.+++++++####.++++...................tt",
        "tt.+++++++..##.++++...................tt",
        "tt.+++++++..##.++++........tttttttttt.tt",
        "tt..........##.++++........tttttttttt.tt",
        "tt..........##........................tt",
        "tt..........############################",
        "tt..........############################",
        "tt....................................tt",
        "tt.~~~~~~~~....++++++++...+++.........tt",
        "tt.~~~~~~~~....++++++++...+++.........tt",
        "tt.~~~~~~~~....++++++++...+++.........tt",
        "tt.~~~~~~~~....++++++++...+++.........tt",
        "tt.~~~~~~~~...............+++.........tt",
        "tt.~~~~~~~~...............+++.........tt",
        "tt.~~~~~~~~...............+++.........tt",
        "tt.~~~~~~~~...........................tt",
        "tt....................................tt",
        "tttttttttttttttttttttttttttttttttttttttt"
    };
}
