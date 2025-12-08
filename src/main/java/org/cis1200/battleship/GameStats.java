package org.cis1200.battleship;

import java.io.*;

public class GameStats {

    private int myWins;
    private int opponentWins;
    private static String statsFile = "battleshipStats.txt";

    public GameStats() {
        myWins = 0;
        opponentWins = 0;
        loadStats();

    }

    public int getMyWins() {
        return myWins;
    }

    public int getOpponentWins() {
        return opponentWins;
    }

    public int getTotalGames() {
        return myWins + opponentWins;
    }

    public void recordMyWin() {
        myWins++;
        saveStats();
    }

    public void recordOpponentWin() {
        opponentWins++;
        saveStats();
    }

    public void resetStats() {
        myWins = 0;
        opponentWins = 0;
        saveStats();
    }

    public void saveStats() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(statsFile));
            writer.write("my_wins: " + myWins + "\n");
            writer.write("opponent_wins: " + opponentWins + "\n");
            writer.write("total_games: " + getTotalGames() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing stats file: " + e.getMessage());
        }
    }

    private void loadStats() {
        File file = new File(statsFile);
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = reader.readLine()) != null) {
                if (ln.startsWith("my_wins: ")) {
                    myWins = Integer.parseInt(ln.substring(9));
                } else if (ln.startsWith("opponent_wins: ")) {
                    opponentWins = Integer.parseInt(ln.substring(15));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading stats file: " + e.getMessage());
            myWins = 0;
            opponentWins = 0;
        }
    }

    public String formatStats() {
        return "Your Wins: " + myWins + "\nOpponent Wins: "
                + opponentWins + "\nTotal Games: " + getTotalGames() +
                " | Win Rate: " + getWinRate();
    }

    public String getWinRate() {
        if (getTotalGames() == 0) {
            return "0.0";
        }
        double value = (double) myWins / getTotalGames();
        return String.format("%.2f", value);
    }

}
