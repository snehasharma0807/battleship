package org.cis1200.battleship;

import java.util.Random;

public class BattleshipGame {
    private BattleshipBoard myBoard;
    private BattleshipBoard oppBoard;
    private boolean isMyTurn;
    private GameState currState;
    private Random random;
    private Opponent opponent;

    public BattleshipGame() {
        myBoard = new BattleshipBoard();
        oppBoard = new BattleshipBoard();
        isMyTurn = true;
        currState = GameState.MY_TURN;
        random = new Random();

        opponent = new Opponent();

    }


    public GameState getCurrState() {
        return currState;
    }

    public BattleshipBoard getMyBoard() {
        return myBoard;
    }

    public BattleshipBoard getOppBoard() {
        return oppBoard;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public boolean placeMyShip (Ship ship, int row, int col, boolean isHor) {
        if (currState != GameState.MY_TURN) {
            return false;
        } else {
            return myBoard.placeShip(ship, row, col, isHor);
        }
    }

    public void finishMyPlacement() {
        if (currState == GameState.MY_TURN) {
            currState = GameState.OPP_TURN;
            placeOppShips();
            currState = GameState.PLAYING_CURRENTLY;
        }
    }

    public void placeOppShips() {
        for (Ship ship : new Ship[] {new Carrier(), new Battleship(), new Cruiser(), new Submarine(), new Destroyer()}) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(BattleshipBoard.BOARD_SIZE);
                int col = random.nextInt(BattleshipBoard.BOARD_SIZE);
                boolean isHorizontal = random.nextBoolean();
                placed = oppBoard.placeShip(ship, row, col, isHorizontal);
            }
        }
    }

    public ResultOfShot fireShot(int row, int col) {
        if ((currState != GameState.PLAYING_CURRENTLY) || (!isMyTurn)) {
            return ResultOfShot.INVALID;
        }

        ResultOfShot resultOfShot = oppBoard.getShot(row, col);

        if ((resultOfShot == ResultOfShot.HIT) || (resultOfShot == ResultOfShot.SUNK) || (resultOfShot == ResultOfShot.MISS)) {
            opponent.recordOppShot(row, col, resultOfShot);
            if (oppBoard.isAllSunk()) {
                currState = GameState.GAME_ENDED;
                return resultOfShot;
            }
            isMyTurn = false;
        }
        return resultOfShot;
    }

    public int[] oppFireShot() {
        if (currState != GameState.PLAYING_CURRENTLY || isMyTurn) {
            return null;
        }

        int[] shot = opponent.chooseOppShot();
        int row = shot[0];
        int col = shot[1];

        ResultOfShot resultOfShot = myBoard.getShot(row, col);
        opponent.recordOppShot(row, col, resultOfShot);

        if (myBoard.isAllSunk()) {
            currState = GameState.GAME_ENDED;
        }

        isMyTurn = true;
        return new int[] {row, col};
    }

    public boolean isGameOver() {
        return (currState == GameState.GAME_ENDED);
    }

    public String getWinner() {
        if (myBoard.isAllSunk()) {
            return "computer";
        } else if (oppBoard.isAllSunk()) {
            return "you";
        }
        return null;
    }

    public void reset() {
        myBoard.reset();
        oppBoard.reset();
        isMyTurn = true;
        currState = GameState.MY_TURN;
        opponent = new Opponent();
    }
}
