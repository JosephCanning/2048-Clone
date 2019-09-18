/*
 *  Name: Joseph Canning
 *  Class: CSCI-242
 *  Professor: Hansen
 *  Assignment: Project 1: 2048
 *  Due Date: 9/25/17
 *
 *  Program Purpose:    This class (Play2048) is a clone of Gabriele Cirulli's game, 2048, with simple ASCII graphics.
 *                      This version functions identically to the original in its logic. The player shifts a grid of
 *                      'tiles,' each containing a number, up, down, right, or left to combine like tiles into single,
 *                      bigger tiles. This action is repeated with the goal of having a tile of the value 2048 present
 *                      on the grid. Once this value is present, the player has won the game. If for every possible
 *                      action no tile is able to shift its position, the player has lost. Options are given to quit
 *                      and reset the game at any time. Additionally, the program tracks the user's best score through
 *                      multiple games as well as their current score.
 */

import java.util.Random;

public class Play2048 {

    // Class primitives
    private static int highScore; // the highest score reached while Play2048 is running

    // Instance primitives
    //private boolean running; // if true, game runs; if false, it does not
    private boolean tileMoved; // true if a tile moved as a result of the player's last action
    private boolean hasWon; // true if the 2048 'tile' is present
    private boolean hasLost; // true if no productive moves are possible
    private boolean testingMoves; // true while testing if any moves are possible
    private int newPosY; // assigned random value for new tile's y-coordinate
    private int newPosX; // assigned random value for new tile's x-coordinate
    private int score; // holds player's score

    // Instance objects
    private int[][] board; // holds the 'tiles' used as game pieces
    private Random rand; // determines position, value of new 'tiles'

    // public methods

    /*
     *  The only constructor for the Play2048 class. It initializes all objects: board, input, and rand. It then 'runs'
     *  itself thereby starting a new game.
     */
    public Play2048() {

        board = new int[4][4];
        rand = new Random();
        run();

    }

    // Private methods

    /*
     *  EXPLANATION OF ALL SHIFT METHODS:   This program has four 'shift' methods (one for each direction) each in
     *                                      charge of moving and combining the appropriate tiles based on whatever
     *                                      command the player has typed in. Each of these methods have two 'helper'
     *                                      methods. One is for moving tile; the other is for combining them.
     *                                      The shift method calls its move method, then its combine method, and then
     *                                      its move method once more. This sequence ensures all tiles are moved as
     *                                      far in the desired direction as possible even after combination.
     *
     * EXPLANATION OF ALL MOVE METHODS:     Each move method iterates through board using two for loops with the first
     *                                      loop incrementing an int to a value of 3, and the second incrementing an int
     *                                      to a value of 2. For horizontal movement the first int is used to reach
     *                                      all arrays. The second int is used within the array to check all adjacent
     *                                      values. For vertical movement, the opposite approach is used. Various if
     *                                      statements check if values should be moved within or across arrays when
     *                                      appropriate. A third for loop decrements an int starting at the current
     *                                      value of the second int. This new int is used in the same way as the second,
     *                                      but ensures any tiles have been moved as far as possible.
     *
     * EXPLANATION OF ALL COMBINE METHODS:  All combine methods use a similar nested for loop to the move methods;
     *                                      however, the second loop always runs 'counter' to the movement. That is, if
     *                                      movement is left then the loop will decrement a variable starting at a value
     *                                      of 3. This reversal is to prevent combined tiles from combining with
     *                                      additional tiles. A single if statement checks for equality of adjacent
     *                                      values.
     *
     *                                      NOTE:   if statements referencing the 'testingMoves' boolean prevent actual
     *                                              movement from occurring. See checkWinOrLose() for more details.
     *                                              The 'tileMoved' boolean is set true if any tiles moved.
     *                                              See populateTiles() for more details.
     */

    // Moves tiles up; used in shiftUp()
    private void moveUp() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[j][i] == 0 && board[j + 1][i] != 0) {

                    if (!testingMoves) {
                        board[j][i] = board[j + 1][i];
                        board[j + 1][i] = 0;
                    }
                    tileMoved = true;

                    if (j != 0) {
                        for (int k = j + 1; k > 0; k--) {
                            if (board[k - 1][i] == 0 && board[k][i] != 0) {

                                if (!testingMoves) {
                                    board[k - 1][i] = board[k][i];
                                    board[k][i] = 0;
                                }

                            }
                        }
                    }

                }

            }
        }

    }

    // combines appropriate tiles
    private void combineUp() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[j][i] == board[j + 1][i] && board[j][i] != 0) {

                    if (!testingMoves) {
                        board[j][i] *= 2;
                        score += board[j][i];
                        board[j + 1][i] = 0;
                    }
                    tileMoved = true;

                }

            }
        }

    }

    // See above
    public void shiftUp() {

        moveUp();
        combineUp();
        moveUp();

    }

    // Moves tiles down; used in shiftDown()
    private void moveDown() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[j + 1][i] == 0 && board[j][i] != 0) {

                    if (!testingMoves) {
                        board[j + 1][i] = board[j][i];
                        board[j][i] = 0;
                    }
                    tileMoved = true;

                    if (j != 0) {
                        for (int k = j + 1; k > 0; k--) {
                            if (board[k][i] == 0 && board[k - 1][i] != 0) {

                                if (!testingMoves) {
                                    board[k][i] = board[k - 1][i];
                                    board[k - 1][i] = 0;
                                }

                            }
                        }
                    }

                }

            }
        }

    }

    // combines appropriate tiles
    private void combineDown() {

        for (int i = 0; i < 4; i++) {
            for (int j = 3; j > 0; j--) {

                if (board[j][i] == board[j - 1][i] && board[j][i] != 0) {

                    if (!testingMoves) {
                        board[j][i] *= 2;
                        score += board[j][i];
                        board[j - 1][i] = 0;
                    }
                    tileMoved = true;

                }

            }
        }

    }

    // See above
    public void shiftDown() {

        moveDown();
        combineDown();
        moveDown();

    }

    // Moves tiles right; used in shiftRight()
    private void moveRight() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[i][j + 1] == 0 && board[i][j] != 0) {

                    if (!testingMoves) {
                        board[i][j + 1] = board[i][j];
                        board[i][j] = 0;
                    }
                    tileMoved = true;

                    if (j != 0) {
                        for (int k = j + 1; k > 0; k--) {
                            if (board[i][k] == 0 && board[i][k - 1] != 0) {

                                if (!testingMoves) {
                                    board[i][k] = board[i][k - 1];
                                    board[i][k - 1] = 0;
                                }

                            }
                        }
                    }

                }

            }
        }

    }

    // combines appropriate tiles
    private void combineRight() {

        for (int i = 0; i < 4; i++) {
            for (int j = 3; j > 0; j--) {

                if (board[i][j] == board[i][j - 1] && board[i][j - 1] != 0) {

                    if (!testingMoves) {
                        board[i][j] *= 2;
                        score += board[i][j];
                        board[i][j - 1] = 0;
                    }
                    tileMoved = true;

                }

            }
        }

    }

    // See above
    public void shiftRight () {

        moveRight();
        combineRight();
        moveRight();

    }

    // Moves tiles left; used in shiftLeft()
    private void moveLeft() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[i][j] == 0 && board[i][j + 1] != 0) {

                    if (!testingMoves) {
                        board[i][j] = board[i][j + 1];
                        board[i][j + 1] = 0;
                    }
                    tileMoved = true;

                    if (j != 0) {
                        for (int k = j + 1; k > 0; k--) {
                            if (board[i][k - 1] == 0 && board[i][k] != 0) {

                                if (!testingMoves) {
                                    board[i][k - 1] = board[i][k];
                                    board[i][k] = 0;
                                }

                            }
                        }
                    }

                }

            }
        }

    }

    // combines appropriate tiles
    private void combineLeft() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[i][j] == board[i][j + 1] && board[i][j] != 0) {

                    if (!testingMoves) {
                        board[i][j] *= 2;
                        score += board[i][j];
                        board[i][j + 1] = 0;
                    }
                    tileMoved = true;

                }

            }
        }

    }

    // See above
    public void shiftLeft() {

        moveLeft();
        combineLeft();
        moveLeft();

    }

    /*
     *  hasZeroes():    Used in populateBoard() to prevent endless loop. Returns true if 'board' contains 0 in any of
     *                  its arrays.
     */
    private boolean hasZeroes() {

        boolean zeroes = false;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                if (board[i][j] == 0) {
                    zeroes = true;
                }

            }
        }
        return zeroes;
    }

    /*
     * populateBoard(): Called in run() and update(). Places a new tile in 'board.' Checks if any zeroes are
     *                  present in 'board,' then generates two random values to represent the new tile's position.
     *                  This new tile has a 90% probability of equalling 2 and a 10% probability of being 4.
     */
    private void populateBoard() {

        if (hasZeroes()) {
            do {
                newPosY = rand.nextInt(4);
                newPosX = rand.nextInt(4);

            } while (board[newPosY][newPosX] != 0);
        }

        if (tileMoved && rand.nextInt(10) > 0) {
            board[newPosY][newPosX] = 2;
        } else if (tileMoved) {
            board[newPosY][newPosX] = 4;
        }

        newPosY = 0;
        newPosX = 0;

    }

    /*
     * checkWinOrLose():    Called in update(). Utilizes the 'testingMoves' boolean to run shift methods without
     *                      causing movement; only the 'tileMoved' boolean is affected by the shift methods called
     *                      here. If a tile does not move as a result of any action, the player has lost the game, and
     *                      'hasLost' is set to true. If a 2048 tile is present, the player has won, and 'hasWon' is
     *                      set to true.
     */
    private void checkWinOrLose() {

        tileMoved = false;
        testingMoves = true;
        shiftUp();
        shiftDown();
        shiftRight();
        shiftLeft();
        testingMoves = false;

        if (!tileMoved) {
            hasLost = true;
        } else {

            tileMoved = false;

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {

                    if (board[i][j] == 2048) {
                        hasWon = true;
                    }

                }
            }
        }
    }

    /*
     * update():    Called within run() while 'running' is true. It reverts the value of 'tileMoved,' checks if the
     *              player has won or lost, prints 'board,' gets player input, places a new tile, then updates scores
     *              and tells the player if they have won or lost.
     */
    public void update() {

        populateBoard();
        checkWinOrLose();
        tileMoved = false;

        if (score > highScore) {
            highScore = score;
        }

    }

    /*
     * run():   Called upon the initialization of Play2048. Places two new tiles on the board to start the game. Rules
     *          and instructions are then supplied. A loop runs update() while 'running' is true. If running becomes
     *          false, the program terminates.
     */
    private void run() {

        tileMoved = true;
        populateBoard();
        populateBoard();
        tileMoved = false;

    }

    // Getter methods for use in Gui2048:
    public int[][] getBoard() {
        return board;
    }

    public int getScore() { return score; }

    public int getHighScore() { return highScore; }

    public boolean hasWon() { return hasWon; }

    public boolean hasLost() { return hasLost; }

    // DEBUG
    public void endGame(byte c) {
        if (c == 1) {
            hasWon = true;
        } else if (c == 0) {
            hasLost = true;
        } else {
            hasLost = true;
        }
    }

}