package org.cis1200.battleship;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;


public class BoardTest {

    @Test
    public void testInitialBoardIsEmpty() {
        BattleshipBoard board = new BattleshipBoard();
        for (int row = 0; row < BattleshipBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < BattleshipBoard.BOARD_SIZE; col++) {
                assertEquals(Cell.EMPTY, board.getCell(row, col));
            }
        }
    }

    @Test
    public void testPlaceShipHorizontally() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer = new Destroyer();

        boolean placed = board.placeShip(destroyer, 0, 0, true);
        assertTrue(placed);
        assertEquals(Cell.SHIP, board.getCell(0, 0));
        assertEquals(Cell.SHIP, board.getCell(0, 1));
        assertEquals(Cell.EMPTY, board.getCell(0, 2));
    }

    @Test
    public void testPlaceShipVertically() {
        BattleshipBoard board = new BattleshipBoard();
        Ship cruiser = new Cruiser();

        boolean placed = board.placeShip(cruiser, 2, 3, false);
        assertTrue(placed);
        assertEquals(Cell.SHIP, board.getCell(2, 3));
        assertEquals(Cell.SHIP, board.getCell(3, 3));
        assertEquals(Cell.SHIP, board.getCell(4, 3));
        assertEquals(Cell.EMPTY, board.getCell(5, 3));
    }

    @Test
    public void testCantPlaceShipOutOfBounds() {
        BattleshipBoard board = new BattleshipBoard();
        Ship carrier = new Carrier(); // size 5

        // Would go off the right edge
        boolean placed = board.placeShip(carrier, 0, 7, true);
        assertFalse(placed);

        // Would go off the bottom edge
        placed = board.placeShip(carrier, 8, 0, false);
        assertFalse(placed);
    }

    @Test
    public void testCantPlaceOverlappingShips() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer1 = new Destroyer();
        Ship destroyer2 = new Destroyer();

        board.placeShip(destroyer1, 0, 0, true); // Places at (0,0) and (0,1)
        boolean placed = board.placeShip(destroyer2, 0, 1, false); // Tries to overlap at (0,1)

        assertFalse(placed);
    }

    @Test
    public void testShotHit() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer = new Destroyer();
        board.placeShip(destroyer, 5, 5, true);

        ResultOfShot result = board.getShot(5, 5);
        assertEquals(ResultOfShot.HIT, result);
        assertEquals(Cell.HIT, board.getCell(5, 5));
    }

    @Test
    public void testShotMiss() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer = new Destroyer();
        board.placeShip(destroyer, 5, 5, true);

        ResultOfShot result = board.getShot(3, 3);
        assertEquals(ResultOfShot.MISS, result);
        assertEquals(Cell.MISS, board.getCell(3, 3));
    }

    @Test
    public void testShotSunk() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer = new Destroyer(); // size 2
        board.placeShip(destroyer, 0, 0, true);

        board.getShot(0, 0);
        ResultOfShot result = board.getShot(0, 1);

        assertEquals(ResultOfShot.SUNK, result);
        assertTrue(destroyer.isSunk());
    }

    @Test
    public void testCannotShootSameSpotTwice() {
        BattleshipBoard board = new BattleshipBoard();

        board.getShot(4, 4);
        ResultOfShot result = board.getShot(4, 4);

        assertEquals(ResultOfShot.REPEAT, result);
    }

    @Test
    public void testAllShipsSunk() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer = new Destroyer(); // size 2
        board.placeShip(destroyer, 0, 0, true);

        assertFalse(board.isAllSunk());

        board.getShot(0, 0);
        assertFalse(board.isAllSunk());

        board.getShot(0, 1);
        assertTrue(board.isAllSunk());
    }

    @Test
    public void testBoardReset() {
        BattleshipBoard board = new BattleshipBoard();
        Ship destroyer = new Destroyer();
        board.placeShip(destroyer, 0, 0, true);
        board.getShot(0, 0);

        board.reset();

        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        assertTrue(board.getShips().isEmpty());
    }

    @Test
    public void testInitialState() {
        BattleshipGame game = new BattleshipGame();
        assertEquals(GameState.MY_TURN, game.getCurrState());
        assertTrue(game.isMyTurn());
        assertFalse(game.isGameOver());
    }

    @Test
    public void testPlayerShipPlacement() {
        BattleshipGame game = new BattleshipGame();
        Ship destroyer = new Destroyer();
        assertTrue(game.placeMyShip(destroyer, 0, 0, true));
        BattleshipBoard board = game.getMyBoard();
        assertEquals(Cell.SHIP, board.getCell(0, 0));
        assertEquals(Cell.SHIP, board.getCell(0, 1));
        assertEquals(Cell.EMPTY, board.getCell(0, 2));
    }


    @Test
    public void testGameReset() {
        BattleshipGame game = new BattleshipGame();
        game.placeMyShip(new Destroyer(), 5, 5, true);
        game.finishMyPlacement();
        game.fireShot(5, 5);

        game.reset();

        assertEquals(GameState.MY_TURN, game.getCurrState());
        assertTrue(game.isMyTurn());
        assertFalse(game.isGameOver());
        assertTrue(game.getMyBoard().getShips().isEmpty());
    }

    @Test
    public void testShipHit() {
        Ship d = new  Destroyer();
        assertFalse(d.isSunk());
        assertEquals(0, d.getHits());

        d.hit();
        assertEquals(1, d.getHits());
        assertFalse(d.isSunk());

        d.hit();
        assertEquals(2, d.getHits());
        assertTrue(d.isSunk());
    }


    @Test
    public void testShipCannotBeHitMoreThanItsLength() {
        Ship d = new  Destroyer();
        d.hit();
        d.hit();
        d.hit();
        assertEquals(2, d.getHits());
    }

    @Test
    public void testShipReset() {
        Ship d = new  Destroyer();
        d.hit();
        assertEquals(1, d.getHits());
        d.reset();
        assertEquals(0, d.getHits());
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        BattleshipGame game = new BattleshipGame();

        game.placeMyShip(new Destroyer(), 5, 5, true);
        game.finishMyPlacement();

        game.fireShot(5, 5);
        game.oppFireShot();
        game.fireShot(6, 6);

        game.saveGame("test_save.txt");

        BattleshipGame loadGame = new BattleshipGame();
        loadGame.loadGame("test_save.txt");

        assertEquals(game.getCurrState(), loadGame.getCurrState());
        assertEquals(game.isMyTurn(), loadGame.isMyTurn());
        assertEquals(game.getMyBoard().getShips().size(), loadGame.getMyBoard().getShips().size());
    }

    @Test
    public void testLoadNonexistentFile() {
        BattleshipGame game = new BattleshipGame();
        assertThrows(IOException.class, () -> game.loadGame("test_nonexistent.txt"));
    }

    @Test
    public void testShotHistoryInOrder() {
        BattleshipGame game = new BattleshipGame();

        game.placeMyShip(new Destroyer(), 5, 5, true);
        game.finishMyPlacement();
        game.fireShot(5, 5);
        game.oppFireShot();
        game.fireShot(6, 6);

        TreeMap<Integer, Shot> shotHistory = game.getShots();

        assertEquals(3,  shotHistory.size());
        assertEquals(0, shotHistory.firstKey().intValue());
        assertEquals(2, shotHistory.lastKey().intValue());
    }


}