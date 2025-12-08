package org.cis1200.battleship;

import java.io.*;
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

    public boolean placeMyShip(Ship ship, int row, int col, boolean isHor) {
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
        for (Ship ship : new Ship[] {new Carrier(), new Battleship(),
            new Cruiser(), new Submarine(), new Destroyer()}) {
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

        if ((resultOfShot == ResultOfShot.HIT) || (resultOfShot == ResultOfShot.SUNK)
                || (resultOfShot == ResultOfShot.MISS)) {
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

    public void saveGame(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        try {
            writer.write("battleshipSave\n");
            writer.write("gameState:" + currState + "\n");
            writer.write("myTurn:" + isMyTurn + "\n");
            writer.write("shots:" + shotCounter + "\n");

            writer.write("playerBoard:\n");
            saveBoardCells(writer, myBoard);

            writer.write("myShips:\n");
            saveShips(writer, myBoard);

            writer.write("opponentBoard:\n");
            saveBoardCells(writer, oppBoard);

            writer.write("oppShips:\n");
            saveShips(writer, oppBoard);

            writer.write("oppState:\n");
            saveOppState(writer);

            writer.write("shotHistory:\n");
            saveShotHistory(writer);


            writer.write("end\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            writer.close();
        }
    }

    public void loadGame(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        try {
            String ln = reader.readLine();
            if (!ln.equals("battleshipSave")) {
                throw new IOException();
            }

            myBoard = new BattleshipBoard();
            oppBoard = new BattleshipBoard();
            opponent = new Opponent();
            shots = new TreeMap<>();
            shotCounter = 0;

            while ((ln = reader.readLine()) != null) {
                ln = ln.trim();
                if (ln.equals("end")) {
                    break;
                }
                if (ln.startsWith("gameState:")) {
                    currState = GameState.valueOf(ln.split(":")[1]);
                } else if (ln.startsWith("myTurn:")) {
                    isMyTurn = Boolean.parseBoolean(ln.split(":")[1]);
                } else if (ln.startsWith("shots:")) {
                    shotCounter = Integer.parseInt(ln.split(":")[1]);
                } else if (ln.startsWith("playerBoard:")) {
                    loadBoardCells(reader, myBoard);
                } else if (ln.startsWith("opponentBoard:")) {
                    loadBoardCells(reader, oppBoard);
                } else if (ln.startsWith("myShips:")) {
                    loadShips(reader, myBoard);
                } else if (ln.startsWith("oppShips:")) {
                    loadShips(reader, oppBoard);
                } else if (ln.startsWith("oppState:")) {
                    loadOppState(reader, oppBoard);
                } else if (ln.startsWith("shotHistory:")) {
                    loadShotHistory(reader);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            reader.close();
        }
    }

    private void saveBoardCells(BufferedWriter writer, BattleshipBoard board) throws IOException {
        for (int row = 0; row < BattleshipBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < BattleshipBoard.BOARD_SIZE; col++) {
                Cell cell = board.getCell(row, col);
                char c = cellFormattedToSave(cell);
                writer.write(c);
                if (col < BattleshipBoard.BOARD_SIZE - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
        }
        writer.write("boardEnd\n");
    }

    private char cellFormattedToSave(Cell cell) {
        if (cell == Cell.EMPTY) {
            return 'E';
        } else if (cell == Cell.SHIP) {
            return 'S';
        } else if (cell == Cell.HIT) {
            return 'H';
        } else if (cell == Cell.MISS) {
            return 'M';
        } else {
            return 'E';
        }
    }

    private void saveShips(BufferedWriter writer, BattleshipBoard board) throws IOException {
        for (Ship ship : board.getShips()) {
            int[] pos = findShipPosition(board, ship);
            if (pos != null) {
                writer.write(ship.getClass().getSimpleName() + "," +
                        pos[0] + "," + pos[1] + "," + pos[2] + "," + ship.getHits() + "\n");
            }
        }
        writer.write("shipEnd\n");
    }

    private int[] findShipPosition(BattleshipBoard board, Ship target) {
        for (int row = 0; row < BattleshipBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < BattleshipBoard.BOARD_SIZE; col++) {
                Ship shipAtPos = board.getShipAtLocation(row, col);
                if (shipAtPos == target) {
                    if (col + 1 < BattleshipBoard.BOARD_SIZE &&
                        (board.getShipAtLocation(row, col + 1) == target)) {
                        return new int[]{row, col, 1};
                    } else {
                        return new int[]{row, col, 0};
                    }
                }
            }
        }
        return null;
    }

    private void saveOppState(BufferedWriter writer) throws IOException {
        boolean[][] fired = opponent.getLocsFiredAt();
        for (int row = 0; row < BattleshipBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < BattleshipBoard.BOARD_SIZE; col++) {
                if (fired[row][col]) {
                    writer.write(row + "," + col + "\n");
                }
            }
        }
        writer.write("oppStateEnd\n");
    }

    private void saveShotHistory(BufferedWriter writer) throws IOException {
        for (Integer shot : shots.keySet()) {
            Shot s =  shots.get(shot);
            writer.write(shot + "," + s.getRow() + "," + s.getCol() +
                    "," + s.getResult() + "," + s.isMyShot() + "\n");
        }
        writer.write("shotHistoryEnd\n");
    }

    private void loadBoardCells(BufferedReader reader, BattleshipBoard board) throws IOException {
        for (int row = 0; row < BattleshipBoard.BOARD_SIZE; row++) {
            String ln = reader.readLine();
            if (ln == null) {
                throw new IOException();
            }

            ln = ln.trim();
            String[] cells = ln.split(",");
            if (cells.length != BattleshipBoard.BOARD_SIZE) {
                throw new IOException();
            }

            for (int col = 0; col < BattleshipBoard.BOARD_SIZE; col++) {
                Cell cell = charToCell(cells[col].trim().charAt(0));
                board.setCell(row, col, cell);
            }
        }
        String endLine = reader.readLine();
        if (endLine == null) {
            throw new IOException();
        }
    }

    private Cell charToCell(char c) {
        if (c == 'E') {
            return Cell.EMPTY;
        } else if (c == 'S') {
            return Cell.SHIP;
        } else if (c == 'H') {
            return Cell.HIT;
        } else if (c == 'M') {
            return Cell.MISS;
        } else {
            return Cell.EMPTY;
        }
    }

    private void loadShips(BufferedReader reader, BattleshipBoard board) throws IOException {
        String ln;
        while ((ln = reader.readLine()) != null) {
            ln = ln.trim();
            if (ln.isEmpty()) {
                continue;
            } else if (ln.equals("shipEnd")) {
                break;
            }

            String[] ships  = ln.split(",");
            String className = ships[0].trim();
            int row = Integer.parseInt(ships[1].trim());
            int col = Integer.parseInt(ships[2].trim());
            boolean isHor = Integer.parseInt(ships[3].trim()) == 1;
            int hits = Integer.parseInt(ships[4].trim());

            Ship ship = switch (className) {
                case "Carrier" -> new Carrier();
                case "Battleship" -> new Battleship();
                case "Cruiser" -> new Cruiser();
                case "Submarine" -> new Submarine();
                default -> new Destroyer();
            };

            boolean placed = board.placeShipDuringLoad(ship, row, col, isHor);

            for (int i = 0; i < hits; i++) {
                ship.hit();
            }


        }
    }

    private void loadOppState(BufferedReader reader, BattleshipBoard board) throws IOException {
        String ln;
        while ((ln = reader.readLine()) != null) {
            ln = ln.trim();
            if (ln.isEmpty()) {
                continue;
            }
            if (ln.equals("oppStateEnd")) {
                break;
            }

            String[] opps  = ln.split(",");
            if (opps.length == 2) {
                int row = Integer.parseInt(opps[0].trim());
                int col = Integer.parseInt(opps[1].trim());
                opponent.markAsFired(row, col);
            }
        }
    }

    private void loadShotHistory(BufferedReader reader) throws IOException {
        String ln;
        while ((ln = reader.readLine()) != null) {
            ln = ln.trim();
            if (ln.isEmpty()) {
                continue;
            }
            if (ln.equals("shotHistoryEnd")) {
                break;
            }

            String[] shotss  = ln.split(",");
            int shotNum = Integer.parseInt(shotss[0].trim());
            int row = Integer.parseInt(shotss[1].trim());
            int col = Integer.parseInt(shotss[2].trim());
            ResultOfShot result = ResultOfShot.valueOf(shotss[3].trim());
            boolean player = Boolean.parseBoolean(shotss[4].trim());
            Shot shot = new Shot(row, col, result, player);
            shots.put(shotNum, shot);
        }
    }


}
