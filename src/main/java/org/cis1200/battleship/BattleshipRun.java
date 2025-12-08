package org.cis1200.battleship;


import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;


public class BattleshipRun implements Runnable {
    public void run() {
        JFrame frame = new JFrame("Battleship");
        frame.setLocation(300,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel statusPanel = new JPanel();
        frame.add(statusPanel, BorderLayout.SOUTH);
        JLabel statusLabel = new JLabel("Place your ships.");
        statusPanel.add(statusLabel);

        GameBoard gameBoard = new GameBoard(statusLabel);
        frame.add(gameBoard, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        frame.add(controlPanel,  BorderLayout.NORTH);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> gameBoard.reset());
        controlPanel.add(resetButton);

        JButton statsButton = new JButton("View Stats");
        statsButton.addActionListener(e -> {showStats(frame, gameBoard);}); // add stats formatted
        controlPanel.add(statsButton);

        JButton shotHistoryButton = new JButton("Shot History");
        shotHistoryButton.addActionListener(e -> {showShots(frame, gameBoard);});
        controlPanel.add(shotHistoryButton);

        JButton instructionsButton = new JButton("Instructions");
        instructionsButton.addActionListener(e -> {showInstructions();});
        controlPanel.add(instructionsButton);

        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> {
            try {
                gameBoard.getGame().saveGame("battleship_save.txt");
                JOptionPane.showMessageDialog(frame, "Saved game successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Unable to save game!");
            }
        });
        controlPanel.add(saveButton);

        JButton loadButton = new JButton("Load Game");
        loadButton.addActionListener(e -> {
            try {
                gameBoard.getGame().loadGame("battleship_save.txt");
                gameBoard.updateAfterLoad();
                gameBoard.repaint();
                JOptionPane.showMessageDialog(frame, "Loaded game successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Unable to load game!");
            }
        });
        controlPanel.add(loadButton);

        frame.pack();
        frame.setVisible(true);
        gameBoard.reset();

    }

    private void showStats(JFrame frame, GameBoard gameBoard) {
        GameStats stats = gameBoard.getGame().getGameStats();
        String message = "Battleship Statistics: \n" + stats.formatStats();
        JOptionPane.showMessageDialog(frame, message);
    }

    private void showShots(JFrame frame, GameBoard gameBoard) {
        BattleshipGame battleshipGame = gameBoard.getGame();

        if (battleshipGame.getTotalShots() == 0) {
            JOptionPane.showMessageDialog(frame, "You need at least one shot!");
            return;
        }

        JTextArea textArea = new JTextArea(battleshipGame.getShotsFormatted());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400,400));
        JOptionPane.showMessageDialog(frame, scrollPane, "Battleship Shots", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInstructions() {
        JOptionPane.showMessageDialog(null, "Objective: Sink all the opponents ships before they sink yours. \n" +
                "How to Play:\n" +
                "1. Place your ships on your board by clicking. You can rotate ships by clicking the R key.\n" +
                "2. Click Start when done.\n" +
                "3. Click on cells on your opponent's board to fire.\n" +
                "4. A red cell means you hit a ship. A green cell means you missed.\n" +
                "\nAdditional Features:\n" +
                "   -- View Stats: See all of your previous wins/losses.\n" +
                "   -- Shot History: View all of the previous shots, fired in order.",
                "Instructions", JOptionPane.INFORMATION_MESSAGE
        );
    }

}
