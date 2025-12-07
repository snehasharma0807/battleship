package org.cis1200.battleship;

public abstract class Ship {

    private final String name;
    private final int size;
    private int hits;

    public Ship (String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = 0;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void hit() {
        if (hits < size) {
            hits++;
        }
    }

    public void reset() {
        hits = 0;
    }

    public boolean isSunk() {
        return hits >= size;
    }

}
