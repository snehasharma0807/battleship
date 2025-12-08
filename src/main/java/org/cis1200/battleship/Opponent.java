package org.cis1200.battleship;

import java.util.*;

public class Opponent {
    private boolean[][] locsFiredAtAlready;
    private Random random;
    private boolean isSearch;
//    private boolean foundShip;
    private ArrayList<int[]> listOfTargets;
    private Boolean isHor;


    public Opponent() {
        locsFiredAtAlready = new boolean[BattleshipBoard.BOARD_SIZE][BattleshipBoard.BOARD_SIZE];
        listOfTargets = new ArrayList<>();
        random = new Random();
//        foundShip = false;
        isSearch = false;
        isHor = null;
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
            listOfTargets.add(new int[]{row, col});
            isSearch = true;

            if (listOfTargets.size() == 2) {
                int[] first = listOfTargets.get(0);
                int[] second = listOfTargets.get(1);
                isHor = (first[0] == second[0]);
            }

//            foundShip = true;
            // add targets around the fired at location to try next
//            addTargetsNearby(row, col);
        } else if (resultOfShot == ResultOfShot.SUNK) {
//            if (listOfTargets.isEmpty()) {
//                // the ship has been sunk, return to default state
//                foundShip = false;
//            }
            resetMode();
        }
    }

    private void resetMode() {
        isSearch = false;
        listOfTargets.clear();
        isHor = null;
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

//    public int[] chooseOppShot() {
//        if (foundShip && !listOfTargets.isEmpty()) {
//            int[] newTargets = listOfTargets.remove(0);
//
//            while (locsFiredAtAlready[newTargets[0]][newTargets[1]]) {
//                if (listOfTargets.isEmpty()) {
//                    foundShip = false;
//                    return randomShot();
//                }
//                newTargets = listOfTargets.remove(0);
//            }
//            return newTargets;
//        }
//        return randomShot();
//    }
    public int[] chooseOppShot() {
        if (!isSearch) {
            return randomShot();
        }
        if (listOfTargets.size() == 1) {
            return getAdjacentTargets();
        } else {
            return getTargetsInLine();
        }
    }

    private int[] getAdjacentTargets() {
        int[] hit = listOfTargets.get(0);
        int[] verticalDirections = {-1, 0, 1, 0};
        int[] horizontalDirections = {0, 1, 0, -1};

        for (int i = 0; i < verticalDirections.length; i++) {
            int nR = hit[0] + verticalDirections[i];
            int nC = hit[1] + horizontalDirections[i];

            if (nR >= 0 && nR < BattleshipBoard.BOARD_SIZE &&  nC >= 0 && nC < BattleshipBoard.BOARD_SIZE) {
                if (!locsFiredAtAlready[nR][nC]) {
                    return new int[]{nR, nC};
                }
            }
        }
        resetMode();
        return randomShot();
    }

    private int[] getTargetsInLine() {
        int minR = Integer.MAX_VALUE;
        int minC = Integer.MAX_VALUE;
        int maxR = Integer.MIN_VALUE;
        int maxC = Integer.MIN_VALUE;

        for (int[] hit : listOfTargets) {
            minR = Math.min(minR, hit[0]);
            minC = Math.min(minC, hit[1]);
            maxR = Math.max(maxR, hit[0]);
            maxC = Math.max(maxC, hit[1]);
        }

        if (isHor) {
            int row = listOfTargets.get(0)[0];
            if (row >= 0 && row < BattleshipBoard.BOARD_SIZE &&  maxC + 1 >= 0 && maxC+1 < BattleshipBoard.BOARD_SIZE) {
                if (!locsFiredAtAlready[row][maxC+1]) {
                    return new int[]{row, maxC+1};
                }
            }
            if (row >= 0 && row < BattleshipBoard.BOARD_SIZE &&  minC - 1 >= 0 && minC-1 < BattleshipBoard.BOARD_SIZE) {
                if  (!locsFiredAtAlready[row][minC-1]) {
                    return new int[]{row, minC-1};
                }
            }
        } else {
            int col =  listOfTargets.get(0)[1];
            if (maxR + 1 >= 0 && maxR + 1 < BattleshipBoard.BOARD_SIZE &&  col >= 0 && col < BattleshipBoard.BOARD_SIZE) {
                if (!locsFiredAtAlready[maxR+1][col]) {
                    return new int[]{maxR+1, col};
                }
            }
            if (minR - 1 >= 0 && minR - 1 < BattleshipBoard.BOARD_SIZE &&  col >= 0 && col < BattleshipBoard.BOARD_SIZE) {
                if   (!locsFiredAtAlready[minR-1][col]) {
                    return new int[]{minR-1, col};
                }
            }
        }
        resetMode();
        return randomShot();
    }
}