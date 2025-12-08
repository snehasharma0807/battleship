package org.cis1200.battleship;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;


public class BattleshipGame {
    private BattleshipBoard myBoard;
    private BattleshipBoard oppBoard;
    private boolean isMyTurn;
    private GameState currState;
    private Random random;
    private Opponent opponent;
    private GameStats stats;
    private TreeMap<Integer, Shot> shots;
    private int shotCounter;

    public BattleshipGame() {
        myBoard = new BattleshipBoard();
        oppBoard = new BattleshipBoard();
        isMyTurn = true;
        currState = GameState.MY_TURN;
        random = new Random();

        opponent = new Opponent();
        stats = new GameStats();

        shots = new TreeMap<>();
        shotCounter = 0;

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
//            opponent.recordOppShot(row, col, resultOfShot);

            recordShot(row, col, resultOfShot, true);

            if (oppBoard.isAllSunk()) {
                stats.recordMyWin();
                currState = GameState.GAME_ENDED;
                return resultOfShot;
            }
            isMyTurn = false;
        }
        return resultOfShot;
    }

    public void oppFireShot() {
        if (currState != GameState.PLAYING_CURRENTLY || isMyTurn) {
            return;
        }

        int[] shot = opponent.chooseOppShot();



        int row = shot[0];
        int col = shot[1];

        ResultOfShot resultOfShot = myBoard.getShot(row, col);
        recordShot(row, col, resultOfShot, false);
        opponent.recordOppShot(row, col, resultOfShot);

        if (myBoard.isAllSunk()) {
            stats.recordOpponentWin();
            currState = GameState.GAME_ENDED;
        }

        isMyTurn = true;
//        return new int[] {row, col};
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
        shots.clear();
        shotCounter = 0;
    }

    public GameStats getGameStats() {
        return stats;
    }

    public void recordShot(int row, int col, ResultOfShot resultOfShot, boolean myFire) {
        Shot shot = new Shot(row, col, resultOfShot, myFire);
        shots.put(shotCounter, shot);
        shotCounter++;
    }

    public TreeMap<Integer, Shot> getShots() {
        return shots;
    }

    public int getTotalShots() {
        return shotCounter;
    }

    public int getMyShotCount() {
        int count = 0;
        for (Shot shot : shots.values()) {
            if (shot.isMyShot()) {
                count++;
            }
        }
        return count;
    }

    public int getOppShotCount() {
        int count = 0;
        for (Shot shot : shots.values()) {
            if (!shot.isMyShot()) {
                count++;
            }
        }
        return count;
    }

    public String getShotsFormatted() {
        if (shots.isEmpty()) {
            return "No shots fired yet.";
        }

        StringBuilder toReturn  = new StringBuilder("Shot History: \n\n\n");
        for (Shot shot : shots.values()) {
            toReturn.append(shot.toString()).append("\n\n");
        }
        toReturn.append("\n\nTotal Shots: ").append(getTotalShots()).append("\n");
        toReturn.append("Your Shots: ").append(getMyShotCount()).append("\n");
        toReturn.append("Opponent Shots: ").append(getOppShotCount()).append("\n");
        return toReturn.toString();
    }


}
