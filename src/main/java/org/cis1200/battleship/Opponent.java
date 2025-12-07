package org.cis1200.battleship;

import java.util.*;

public class Opponent {
    private boolean[][] locsFiredAtAlready;
    private Random random;
    private boolean foundShip;
    private ArrayList<int[]> listOfTargets;
    private int[] firstHit;
    private int[] secondHit;
    private boolean doWeKnowDirectionOfShipYet;


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

        System.out.println("Recorded shot at (" + row + "," + col + "): " + resultOfShot);
        System.out.println("foundShip: " + foundShip + ", hasDirection: " + doWeKnowDirectionOfShipYet);
        System.out.println("Targets remaining: " + listOfTargets.size());


        if (resultOfShot == ResultOfShot.HIT) {

            if (!foundShip) {
                // first hit on a new ship!
                foundShip = true;
                firstHit = new int[]{row, col};
                secondHit = null;
                doWeKnowDirectionOfShipYet = false;
                listOfTargets.clear();
                addTargetsNearby(row, col);
            } else if (!doWeKnowDirectionOfShipYet) {
                // second hit on the same ship
                secondHit = new int[]{row, col};

                if (firstHit[0] == secondHit[0] || firstHit[1] == secondHit[1]) {
                    doWeKnowDirectionOfShipYet = true;
                    listOfTargets.clear();
                    //add direction based targets
                    addSmartTargets();
                } else {
                    addTargetsNearby(row, col);
                }


            } else {
                // continue firing in the same direction, update second hit
                int rowDifference = secondHit[0] - firstHit[0];
                int colDifference = secondHit[1] - firstHit[1];

                int distanceToFirst = Math.abs(row - firstHit[0]) + Math.abs(col - firstHit[1]);
                int distanceToSecond = Math.abs(row - secondHit[0]) + Math.abs(col - secondHit[1]);

                if (distanceToSecond == 1) {
                    secondHit = new int[]{row, col};
                } else if (distanceToFirst == 1) {
                    firstHit = new int[]{row, col};
                }

                // continue firing in the same direction
                listOfTargets.clear();
                // add direction based targets
                addSmartTargets();
            }

        } else if (resultOfShot == ResultOfShot.SUNK) {
            // when a ship is sunk we should reset everything
            foundShip = false;
            doWeKnowDirectionOfShipYet = false;
            firstHit = null;
            secondHit = null;
            listOfTargets.clear();
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

    public void addSmartTargets() {
        int rowDifference = secondHit[0] - firstHit[0];
        int colDifference = secondHit[1] - firstHit[1];

        int nR = secondHit[0] + rowDifference;
        int nC = secondHit[1] + colDifference;

        if (nR >= 0 && nR < BattleshipBoard.BOARD_SIZE &&  nC >= 0 && nC < BattleshipBoard.BOARD_SIZE && !locsFiredAtAlready[nR][nC]) {
            listOfTargets.add(new int[]{nR, nC});
        }

        nR = firstHit[0] - rowDifference;
        nC = firstHit[1] - colDifference;

        if (nR >= 0 && nR < BattleshipBoard.BOARD_SIZE &&  nC >= 0 && nC < BattleshipBoard.BOARD_SIZE && !locsFiredAtAlready[nR][nC]) {
            listOfTargets.add(new int[]{nR, nC});
        }
    }

    public int[] chooseOppShot() {
        if (foundShip && !listOfTargets.isEmpty()) {
            int[] newTargets = listOfTargets.remove(0);

            while (locsFiredAtAlready[newTargets[0]][newTargets[1]]) {
                if (listOfTargets.isEmpty()) {
                    if (!doWeKnowDirectionOfShipYet && firstHit != null) {
                        addTargetsNearby(firstHit[0], firstHit[1]);
                        if (listOfTargets.isEmpty()) {
                            foundShip = false;
                            firstHit = null;
                            return randomShot();
                        }
                    } else {
                        foundShip = false;
                        doWeKnowDirectionOfShipYet = false;
                        firstHit = null;
                        secondHit = null;
                        return randomShot();
                    }

                }
                newTargets = listOfTargets.remove(0);
            }


            return newTargets;
        }


        return randomShot();
    }
}
