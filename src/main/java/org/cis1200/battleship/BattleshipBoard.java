package org.cis1200.battleship;

import java.util.ArrayList;

public class BattleshipBoard {
    public static final int BOARD_SIZE = 10;

    private Cell[][] board;
    private ArrayList<Ship> ships;
    private Ship[][] shipLocs;

    public BattleshipBoard() {
        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        shipLocs = new Ship[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();


        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = Cell.EMPTY;
                shipLocs[i][j] = null;
            }
        }
    }

    private boolean isValid(Ship ship, int row, int col, boolean isHor) {
        int shipSize = ship.getSize();

        // first checks the bounds of the ship and board location
        if (isHor) {
            if ((col + shipSize > BOARD_SIZE) || (row < 0) || (row >= BOARD_SIZE)) {
                return false;
            }
        } else {
            if ((row + shipSize > BOARD_SIZE) || (col < 0) || (col >= BOARD_SIZE)) {
                return false;
            }
        }

        if (isHor) {
            // next checks if the ship overlaps with another ship already placed
            for (int i = col; i < col + shipSize; i++) {
                if (board[row][i] == Cell.SHIP) {
                    return false;
                }
            }
        } else {
            for (int i = row; i < row + shipSize; i++) {
                if (board[i][col] == Cell.SHIP) {
                    return false;
                }
            }
        }
        return true;


    }

    public boolean placeShip(Ship ship, int row, int col, boolean isHor) {
        if (!(isValid(ship, row, col, isHor))) {
            return false;
        }

        if (isHor) {
            for (int i = col; i < col + ship.getSize(); i++) {
                board[row][i] = Cell.SHIP;
                shipLocs[row][i] = ship;
            }
        } else {
            for (int i = row; i < row + ship.getSize(); i++) {
                board[i][col] = Cell.SHIP;
                shipLocs[i][col] = ship;
            }
        }

        ships.add(ship);
        return true;
    }

    public ResultOfShot getShot(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return ResultOfShot.INVALID;
        }

        Cell curCell = board[row][col];
        if (curCell == Cell.HIT || curCell == Cell.MISS) {
            return ResultOfShot.REPEAT;

        }

        if (curCell == Cell.SHIP) {
            Ship hit = shipLocs[row][col];
            hit.hit();
            board[row][col] = Cell.HIT;
            if (hit.isSunk()) {
                return ResultOfShot.SUNK;
            } else {
                return ResultOfShot.HIT;
            }
        }

        board[row][col] = Cell.MISS;
        return ResultOfShot.MISS;
    }


    public Cell getCell(int row, int col) {
        return board[row][col];
    }

    public Ship getShipAtLocation(int row, int col) {
        return shipLocs[row][col];
    }

    public void reset() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = Cell.EMPTY;
                shipLocs[i][j] = null;
            }
        }
        for (Ship ship : ships) {
            ship.reset();
        }
        ships.clear();
    }

    public ArrayList<Ship> getShips() {
        return new ArrayList<>(ships);
    }

    public boolean isAllSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public void setCell(int row, int col, Cell cell) {
        if (row >= 0 && row < BOARD_SIZE) {
            if (col >= 0 && col < BOARD_SIZE) {
                board[row][col] = cell;
            }
        }
    }

    public boolean placeShipDuringLoad(Ship ship, int row, int col, boolean isHor) {
        if (isHor) {
            for (int i = col; i < col + ship.getSize(); i++) {
                if (i >= BOARD_SIZE || row < 0 || row >= BOARD_SIZE) {
                    return false;
                }
                shipLocs[row][i] = ship;
            }
        } else {
            for (int i = row; i < row + ship.getSize(); i++) {
                if (i >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                    return false;
                }
                shipLocs[i][col] = ship;
            }
        }
        ships.add(ship);
        return true;
    }




}
