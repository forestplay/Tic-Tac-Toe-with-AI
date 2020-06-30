package tictactoe;

import java.util.Random;
import java.util.Scanner;

public class Main {
    static String[] playerMark = new String[]{"X", "O"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Input command: ");
            String[] startingState = scanner.nextLine().split(" ");

            if (startingState[0].equals("exit"))
                break;

            try {
                if (startingState.length != 3) {
                    throw new Exception("Bad parameters!");
                } else if (!startingState[0].equals("start")) {
                    throw new Exception("Bad parameters!");
                }
                String[] player = new String[]{startingState[1], startingState[2]};

                Table table = new Table("_________");
                System.out.println(table);

                int nowPlaying = 0;
                while (gameState(table).matches("Game not finished")) {
                    switch (player[nowPlaying]) {
                        case "user":
                            takeATurn(scanner, table, nowPlaying);
                            break;
                        case "easy":
                            System.out.println("Making move level \"easy\"");
                            easyAI(table, nowPlaying);
                            break;
                        case "medium":
                            System.out.println("Making move level \"medium\"");
                            mediumAI(table, nowPlaying);
                            break;
                        case "hard":
                            System.out.println("Making move level \"hard\"");
                            hardAI(table, nowPlaying);
                            break;
                        case "help":
                            System.out.println("To start a game, use:\n" +
                                    "  start [user|easy|medium|hard] [user|easy|medium|hard]\n" +
                                    "To make a move, use:\n" +
                                    "  X Y where X is row and Y is column\n" +
                                    "  1 1 refers to upper left\n" +
                                    "  2 2 refers to center\n" +
                                    "  1 3 refers to upper right\n");
                            break;
                        default:
                            throw new Exception("Unknown play mode");
                    }
                    System.out.println(table);
                    nowPlaying = (nowPlaying == 0) ? 1 : 0;
                }
                System.out.println(gameState(table));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String gameState(Table table) throws Exception {
        if (Math.abs(table.countOccurrences("X") - table.countOccurrences("O")) > 1) {
            return "Impossible game position";
        }

        boolean xWins = false;
        boolean oWins = false;

        // check rows
        for (int x = 1; x <= 3; x++) {
            String row = table.getCell(new Position(x, 1)) + table.getCell(new Position(x, 2)) + table.getCell(new Position(x, 3));
            if (row.equals("XXX")) xWins = true;
            if (row.equals("OOO")) oWins = true;
        }

        // check columns
        for (int y = 1; y <= 3; y++) {
            String column = table.getCell(new Position(1, y)) + table.getCell(new Position(2, y)) + table.getCell(new Position(3, y));
            if (column.equals("XXX")) xWins = true;
            if (column.equals("OOO")) oWins = true;
        }

        // check diagonal up
        String diagonalUp = table.getCell(new Position(1, 1)) + table.getCell(new Position(2, 2)) + table.getCell(new Position(3, 3));
        if (diagonalUp.equals("XXX")) xWins = true;
        if (diagonalUp.equals("OOO")) oWins = true;

        // check diagonal down
        String diagonalDown = table.getCell(new Position(3, 1)) + table.getCell(new Position(2, 2)) + table.getCell(new Position(1, 3));
        if (diagonalDown.equals("XXX")) xWins = true;
        if (diagonalDown.equals("OOO")) oWins = true;

        if (xWins && oWins) return "Impossible both winners";
        if (xWins == oWins && table.countOccurrences("_") > 0) return "Game not finished";
        if (xWins) return "X wins";
        if (oWins) return "O wins";
        return "Draw";
    }

    public static Position winInOneMove(Table table, String playerMark) throws Exception {
        String regex = "^" + playerMark + "+";
        String sequence1 = playerMark + playerMark + "_";
        String sequence2 = playerMark + "_" + playerMark;
        String sequence3 = "_" + playerMark + playerMark;

        // check rows
        for (int x = 1; x <= 3; x++) {
            String row = table.getCell(new Position(x, 1)) + table.getCell(new Position(x, 2)) + table.getCell(new Position(x, 3));
            if (row.equals(sequence1) || row.equals(sequence2) || row.equals(sequence3)) {
                int colPosition = row.replaceFirst(regex, "").length();
                if (table.cellOpen(new Position(x, colPosition)))
                    return new Position(x, colPosition);
            }
        }

        // check columns
        for (int y = 1; y <= 3; y++) {
            String column = table.getCell(new Position(1, y)) + table.getCell(new Position(2, y)) + table.getCell(new Position(3, y));
            if (column.equals(sequence1) || column.equals(sequence2) || column.equals(sequence3)) {
                int length = column.replaceFirst(regex, "").length();
                int rowPosition = length == 3 ? 1 : length == 2 ? 2 : 3;
                if (table.cellOpen(new Position(rowPosition, y)))
                    return new Position(rowPosition, y);
            }
        }

        // check diagonal down
        String diagonalDown = table.getCell(new Position(1, 1)) + table.getCell(new Position(2, 2)) + table.getCell(new Position(3, 3));
        if (diagonalDown.equals(sequence1) || diagonalDown.equals(sequence2) || diagonalDown.equals(sequence3)) {
            int length = diagonalDown.replaceFirst(regex, "").length();
            int position = length == 3 ? 1 : length == 2 ? 2 : 3;
            if (table.cellOpen(new Position(position, position)))
                return new Position(position, position);
        }

        // check diagonal up
        String diagonalUp = table.getCell(new Position(3, 1)) + table.getCell(new Position(2, 2)) + table.getCell(new Position(1, 3));
        if (diagonalUp.equals(sequence1) || diagonalUp.equals(sequence2) || diagonalUp.equals(sequence3)) {
            int rowPosition = diagonalUp.replaceFirst(regex, "").length();
            int colPosition = rowPosition == 3 ? 1 : rowPosition == 2 ? 2 : 3;
            if (table.cellOpen(new Position(rowPosition, colPosition)))
                return new Position(rowPosition, colPosition);
        }

        // no win possible this turn
        return null;
    }

    public static void takeATurn(Scanner scanner, Table table, int player) throws Exception {
        String[] mark = new String[]{"X", "O"};

        while (true) {
            System.out.print("Enter the coordinates: ");
            String input = scanner.nextLine();
            if (!input.matches("[0-9] [0-9]")) {
                System.out.println("You should enter numbers!");
                continue;
            }
            if (!input.matches("[1-3] [1-3]")) {
                System.out.println("Coordinates should be from 1 to 3!");
                continue;
            }

            String[] values = input.split(" ");
            int x = Integer.parseInt(values[0]);
            int y = Integer.parseInt(values[1]);

            if (table.cellOpen(new Position(x, y))) {
                table.setCell(new Position(x, y), mark[player]);
                break;
            } else {
                System.out.println("This cell is occupied! Choose another one!");
            }
        }
    }

    public static void easyAI(Table table, int playerIndex) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        while (true) {
            int x = random.nextInt(3) + 1;
            int y = random.nextInt(3) + 1;
            if (table.cellOpen(new Position(x, y))) {
                table.setCell(new Position(x, y), playerMark[playerIndex]);
                break;
            }
        }
    }

    public static void mediumAI(Table table, int playerIndex) throws Exception {
        // Play to win
        Position myBestPosition = winInOneMove(table, playerMark[playerIndex]);
        Position opponentBestPosition = winInOneMove(table, playerMark[playerIndex == 0 ? 1 : 0]);
        if (myBestPosition != null) {
            // Play to win
            table.setCell(myBestPosition, playerMark[playerIndex]);
        } else if (opponentBestPosition != null) {
            // Play to block
            table.setCell(opponentBestPosition, playerMark[playerIndex]);
        } else {
            // Play random position
            easyAI(table, playerIndex);
        }
    }

    public static void hardAI(Table table, int playerIndex) throws Exception {
        if (gameState(table).matches("Game not finished")) {
            int bestValue = Integer.MIN_VALUE;
            Position bestPosition = null;
            for (int x = 1; x <= 3; x++)
                for (int y = 1; y <= 3; y++) {
                    Position position = new Position(x, y);
                    if (table.cellOpen(position)) {
                        table.setCell(position, playerMark[playerIndex]);
                        int moveValue = minimax(table, 8, false, playerIndex == 0 ? 1 : 0);
                        table.setCell(position, "_");
                        if (moveValue > bestValue) {
                            bestValue = moveValue;
                            bestPosition = position;
                        }
                    }
                }
            if (bestPosition == null) {
                throw new Exception("Impossible play");
            }
            table.setCell(bestPosition, playerMark[playerIndex]);
        }
    }


    private static int minimax(Table table, int depth, boolean maximizingPlayer, int playerIndex) throws Exception {
        if (depth == 0 || gameState(table).equals("Draw")) {
            return 0;
        } else if (gameState(table).equals("X wins") || gameState(table).equals("O wins")) {
            return maximizingPlayer ? -10 : 10;
        }

        int bestValue;
        if (maximizingPlayer) {
            bestValue = Integer.MIN_VALUE;
            for (int x = 1; x <= 3; x++)
                for (int y = 1; y <= 3; y++) {
                    Position position = new Position(x, y);
                    if (table.cellOpen(position)) {
                        table.setCell(position, playerMark[playerIndex]);
                        bestValue = Integer.max(bestValue, minimax(table, depth - 1, false, playerIndex == 0 ? 1 : 0));
                        table.setCell(position, "_");
                    }
                }
        } else {
            bestValue = Integer.MAX_VALUE;
            for (int x = 1; x <= 3; x++)
                for (int y = 1; y <= 3; y++) {
                    Position position = new Position(x, y);
                    if (table.cellOpen(position)) {
                        table.setCell(position, playerMark[playerIndex]);
                        bestValue = Integer.min(bestValue, minimax(table, depth - 1, true, playerIndex == 0 ? 1 : 0));
                        table.setCell(position, "_");
                    }
                }
        }
        return bestValue;
    }
}


class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) throws Exception {
        if (x < 1 || x > 3 || y < 1 || y > 3)
            throw new Exception("Position value out of bounds");
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }
}

class Table {
    String table;

    public Table(String table) throws Exception {
        if (table.length() != 9) throw new Exception("table string doesn't fit");
        this.table = table;
    }

    @Override
    public String toString() {
        var rtnValue = new StringBuilder();
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                rtnValue.append(getCell(x, y)).append(" ");
            }
            rtnValue.append("\n");
        }
        return rtnValue.toString();
    }

    public int countOccurrences(String playerMark) {
        return table.replaceAll("[^" + playerMark + "]", "").length();
    }

    public boolean cellOpen(Position position) {
        return !getCell(position).matches("[XO]");
    }

    public void setCell(Position position, String playerMark) {
        setCell(position.getX(), position.getY(), playerMark);
    }

    public String getCell(Position position) {
        return getCell(position.getX(), position.getY());
    }

    private String getCell(int x, int y) {
        return Character.toString(table.charAt((x - 1) * 3 + (y - 1)));
    }

    private void setCell(int x, int y, String playerMark) {
        table = table.substring(0, ((x - 1) * 3 + (y - 1))) + playerMark + table.substring(((x - 1) * 3 + (y - 1)) + 1);
    }
}