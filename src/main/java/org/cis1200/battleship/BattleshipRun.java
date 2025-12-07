package org.cis1200.battleship;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


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

        frame.pack();
        frame.setVisible(true);
        gameBoard.reset();

    }

}
