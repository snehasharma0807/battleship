package org.cis1200.battleship;

public enum Cell {
    EMPTY, // no ship, not fired on
    SHIP, // ship, not fired on
    HIT, //  ship, fired on
    MISS // no ship, fired on
}
