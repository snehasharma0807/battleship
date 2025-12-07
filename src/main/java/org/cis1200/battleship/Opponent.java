package org.cis1200.battleship;

import java.util.*;

public class Opponent {
    private boolean[][] locsFiredAtAlready;
    private Random random;
    private boolean foundShip;
    private ArrayList<int[]> listOfTargets;


    public Opponent() {
        locsFiredAtAlready = new boolean[BattleshipBoard.BOARD_SIZE][BattleshipBoard.BOARD_SIZE];
        listOfTargets = new ArrayList<>();
        random = new Random();
        foundShip = false;
    }

    public int[] randomShot() {
        int row = random.nextInt(BattleshipBoard.BOARD_SIZE);
        int col = random.nextInt(BattleshipBoard.BOARD_SIZE);
        while (locsFiredAtAlready[row][col]) {
            row = random.nextInt(BattleshipBoard.BOARD_SIZE);
            col = random.nextInt(BattleshipBoard.BOARD_SIZE);
        }

        return new int[]{row, col};
    }

    public void recordOppShot(int row, int col, ResultOfShot resultOfShot) {
        locsFiredAtAlready[row][col] = true;

        if (resultOfShot == ResultOfShot.HIT) {
            foundShip = true;
            // add targets around the fired at location to try next
            addTargetsNearby(row, col);
        } else if (resultOfShot == ResultOfShot.SUNK) {
            if (listOfTargets.isEmpty()) {
                // the ship has been sunk, return to default state
                foundShip = false;
            }
        }
    }

    public void addTargetsNearby(int row, int col) {
        // up
        int[] verticalDirections = {-1, 0, 1, 0};
        int[] horizontalDirections = {0, 1, 0, -1};

        for (int i = 0; i < verticalDirections.length; i++) {
            int nR = row + verticalDirections[i];
            int nC = col + horizontalDirections[i];

            if (nR >= 0 && nR < BattleshipBoard.BOARD_SIZE &&  nC >= 0 && nC < BattleshipBoard.BOARD_SIZE) {
                if (!locsFiredAtAlready[nR][nC]) {
                    listOfTargets.add(new int[]{nR, nC});
                }
            }
        }

    }

    public int[] chooseOppShot() {
        if (foundShip && !listOfTargets.isEmpty()) {
            int[] newTargets = listOfTargets.remove(0);

            while (locsFiredAtAlready[newTargets[0]][newTargets[1]]) {
                if (listOfTargets.isEmpty()) {
                    foundShip = false;
                    return randomShot();
                }
                newTargets = listOfTargets.remove(0);
            }
            return newTargets;
        }
        return randomShot();
    }
}