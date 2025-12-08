package org.cis1200.battleship;

public class Shot {
    private int row;
    private int col;
    private ResultOfShot result;
    private boolean myShot;

    public Shot(int row, int col, ResultOfShot result, boolean myShot) {
        this.row = row;
        this.col = col;
        this.result = result;
        this.myShot = myShot;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ResultOfShot getResult() {
        return result;
    }

    public boolean isMyShot() {
        return myShot;
    }


    public String toString() {
        if  (myShot) {
            return "You shot at (" + row + ", " + col + "): " + result;
        } else {
            return "Computer shot at (" + row + ", " + col + "): " + result;
        }
    }
}
