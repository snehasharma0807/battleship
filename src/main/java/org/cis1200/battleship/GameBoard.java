package org.cis1200.battleship;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameBoard extends JPanel{
    private BattleshipGame game;
    private JLabel statusLabel;
    private JButton startButton;

    private Ship currShip = null;
    private int shipPlacementIndex = 0;
    private boolean isHor = true;
    private Ship[] shipsToPlace = {new Carrier(), new Battleship(), new Cruiser(), new Submarine(), new Destroyer()};

    public GameBoard(JLabel initStatus) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.lightGray);

        game = new BattleshipGame();
        statusLabel = initStatus;

        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                handleMouseClick(e.getX(), e.getY());
            }
        });

        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if ((e.getKeyCode() == KeyEvent.VK_LEFT) || e.getKeyCode() == KeyEvent.VK_R){
                    isHor = !isHor;
                    updateStatus();
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseMoved(MouseEvent e){
                if (game.getCurrState() == GameState.MY_TURN) {
                    repaint();
                }
            }
        });

        startButton = new JButton("Start");
        startButton.setBackground(Color.lightGray);
        startButton.addActionListener(e -> startGame());
        startButton.setVisible(false);
        setLayout(null);
        startButton.setBounds(300, 360, 120, 30);
        add(startButton);
    }

    public void reset() {
        game.reset();
        shipPlacementIndex = 0;
        currShip = shipsToPlace[shipPlacementIndex];
        isHor = true;
        startButton.setVisible(false);
        updateStatus();
        repaint();
        requestFocusInWindow();
    }

    private void handleMouseClick(int x, int y){
        requestFocusInWindow();
        GameState state = game.getCurrState();

        if (state == GameState.MY_TURN){
            handlePlacementClick(x, y);
        } else if (state == GameState.PLAYING_CURRENTLY) {
            handleBattleClick(x, y);
        }

        updateStatus();
        repaint();
    }

    private void handlePlacementClick(int x, int y){
        if (currShip == null) {
            return;
        }

        int col = x / 40;
        int row = y / 40;

        if (col < 0 || col >= 10 || row < 0 || row >= 10) {
            return;
        }

        boolean placed = game.placeMyShip(currShip, row, col, isHor);
        if (placed) {
            shipPlacementIndex = shipPlacementIndex + 1;
            if (shipPlacementIndex >= shipsToPlace.length) {
                currShip = null;
                startButton.setVisible(true);
            } else {
                currShip = shipsToPlace[shipPlacementIndex];
                isHor = true;
            }
        }
    }

    private void handleBattleClick(int x, int y){
        if (!game.isMyTurn() || game.isGameOver()) {
            return;
        }

        int col = (x / 40) - 10;
        int row = (y / 40);

        if (col < 0 || col >= 10 || row < 0 || row >= 10) {
            return;
        }

        ResultOfShot result = game.fireShot(row, col);
        if (result == ResultOfShot.INVALID || result == ResultOfShot.REPEAT) {
            return;
        }

        if (!game.isGameOver() && !game.isMyTurn()) {
            Timer timer = new Timer(1, e -> {
                game.oppFireShot();
                updateStatus();
                repaint();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void startGame() {
        game.finishMyPlacement();
        startButton.setVisible(false);
        updateStatus();
        repaint();
        requestFocusInWindow();
    }

    private void updateStatus() {
        GameState state = game.getCurrState();

        if (state == GameState.MY_TURN) {
            if (currShip != null) {
                if (isHor) {
                    statusLabel.setText("Place " + currShip.getName() + " (size " + currShip.getSize() + " ). Click \"R\" to rotate. It is horizontal right now.");
                } else {
                    statusLabel.setText("Place " + currShip.getName() + " (size " + currShip.getSize() + " ). Click \"R\" to rotate. It is vertical right now.");
                }


            } else {
                statusLabel.setText("Click \"Start Game\" to begin!");
            }
        } else if (state == GameState.PLAYING_CURRENTLY) {
            if (game.isMyTurn()) {
                statusLabel.setText("Your turn; click opponent's board to fire.");
            } else {
                statusLabel.setText("Opponent's turn to fire.");
            }
        } else if (state == GameState.GAME_ENDED) {
            statusLabel.setText("Game over. " + game.getWinner() + " wins!");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g, game.getMyBoard(), 0, true);
        drawBoard(g, game.getOppBoard(), 400, false);

        g.setColor(Color.black);
        g.fillRect(398, 0, 4, 400);

        g.setColor(Color.black);
        g.drawString("Your Board", 0, 420);
        g.drawString("Opponent Board", 400, 420);

    }

    private void drawBoard (Graphics g, BattleshipBoard board, int x1, boolean showShips) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int x = x1 + col * 40;
                int y = row * 40;

                Cell cell = board.getCell(row, col);

                Color color = Color.WHITE;
                if (cell == Cell.HIT) {
                    color = Color.RED;
                } else if (cell == Cell.MISS) {
                    color = Color.GREEN;
                } else if (cell == Cell.SHIP && showShips) {
                    color = Color.DARK_GRAY;
                }

                g.setColor(color);
                g.fillRect(x, y, 40, 40);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, 40, 40);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 450);
    }

}
