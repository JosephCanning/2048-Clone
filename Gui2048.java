/*
 *  Name: Joseph Canning
 *  Class: CSCI-242
 *  Assignment: Project 3 - 2048GUI
 *  Professor: Stuart Hansen
 *  Date Due: 10/16/2017
 *
 *  Program Purpose:    This program provides a GUI frontend for the previous Play2048 project. It extends Application
 *                      and uses various classes from javafx. It has only passing resemblance to the original game's
 *                      interface, but it utilizes a TilePane containing Labels with borders to mimic its tiles. There
 *                      are no animations or color changes. Upon winning or losing, a message and a prompt to restart
 *                      or quit is displayed. Score is always displayed below the tiles. The player can either use
 *                      buttons along the bottom of the window to control the game, or they can use the W, S, A, and D
 *                      keys or the arrow keys.
 */

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Gui2048 extends Application {

    // Private constants
    private final int WIN_WIDTH = 400; // Width of the window
    private final int WIN_HEIGHT = 538; // Height of the window

    // Private variables
    private int scheme; // Color scheme
    private VBox vbox; // The vertical container for all GUI elements
    private TilePane buttons; // Holds movement Buttons
    private TilePane scores; // Holds Labels showing scores
    private TilePane prompts; // Holds the yes/no Buttons
    private TilePane grid; // Holds sixteen Labels to make a grid of tiles
    private Button yes; // Restarts game upon win/loss
    private Button no; // Quits game upon win/loss
    private Label score; // Shows current score
    private Label highScore; // Shows top score
    private Label message; // Tells player if they have won or lost the game
    private ArrayList<Label> tiles; // Holds the sixteen Labels that go in grid
    private int[][] board; // Copies game's board
    private FadeTransition fadeMessage;
    private FadeTransition fadePrompts;
    private Animation tileChange;
    private SequentialTransition endGame;
    private Play2048 game; // Object to handle all game logic

    /*
     * start(Stage stage):  The program starts here. It runs the initGame() method which sets up the interface and
     *                      creates a Play2048 object.
     */
    @Override
    public void start(Stage stage) throws Exception {
        initGame(stage);
    }

    /*
     * initTiles(): This method initializes the game board and its associated GUI elements. Labels are added
     *              to an ArrayList called tiles. Each Label inside tiles is then added to a TilePane
     *              named grid. The TilePane ensures even spacing across the screen. There are sixteen tiles in total,
     *              forming a 4x4 grid. The tile's value is displayed as text within its associated Label. All tiles
     *              have an initial value of zero. Finally, grid is added to vbox and updateTiles() is run in order
     *              to properly update the values of the tiles.
     */
    private void initTiles() {

        grid.setAlignment(Pos.CENTER);
        grid.setPrefTileHeight(WIN_WIDTH / 4);
        grid.setPrefTileWidth(WIN_WIDTH / 4);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setStyle("-fx-background-color: #EEEEEE;");
        scheme = 1;

        for (int i = 0; i < 16; i++) {

            tiles.add(new Label());
            tiles.get(i).setAlignment(Pos.CENTER);
            tiles.get(i).setMinSize(WIN_WIDTH / 4, WIN_WIDTH / 4);
            tiles.get(i).setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 45));
            tiles.get(i).setTextFill(Paint.valueOf("#FFFFFF"));
            tiles.get(i).setStyle("-fx-background-color: #AAE090;");
            tiles.get(i).setText("0");
            tiles.get(i).setBorder(new Border(new BorderStroke(Color.SNOW, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            grid.getChildren().add(tiles.get(i));

        }

        vbox.getChildren().add(grid);
        updateTiles();

    }

    /*
     * initControls(Stage stage):   All options for controlling the game are defined and set up in this method.
     *                              First, the Buttons to restart (yes) or quit (no) the game after the player has won
     *                              or lost are set up near the bottom of the screen. These prompts are hidden until
     *                              the game is finished. Both prompts are put in their own TilePane, prompts; prompts
     *                              is placed into vbox. Second, four Buttons are set up along the bottom of the
     *                              screen: left, right, up, down. Each Button moves the tiles in its respective
     *                              direction. All four Buttons are placed in a TilePane, buttons; button is put into
     *                              vbox. Finally, keyboard controls are set up; W, S, A, and D function like the
     *                              movement buttons, the arrow keys another alternative movement option, R restarts
     *                              the game, and ESCAPE quits the game.
     */
    private void initControls(Stage stage) {

        // Prompts
        yes = new Button("Yes");
        yes.setPrefWidth(45);
        yes.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        yes.setTextFill(Paint.valueOf("#FFFFFF"));
        yes.setStyle("-fx-background-color: #557055;");
        yes.setVisible(false);
        prompts.getChildren().add(yes);
        yes.setOnAction((e) -> reset());

        no = new Button("No");
        no.setPrefWidth(45);
        no.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        no.setTextFill(Paint.valueOf("#FFFFFF"));
        no.setStyle("-fx-background-color: #705555;");
        no.setVisible(false);
        prompts.getChildren().add(no);
        no.setOnAction((e) -> System.exit(0));

        prompts.setAlignment(Pos.CENTER);
        prompts.setHgap(10);
        prompts.setMinSize(WIN_WIDTH, 55);
        vbox.getChildren().add(prompts);

        // Buttons
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setHgap(2);

        Button left = new Button("Left");
        left.setMinWidth((WIN_WIDTH / 4) - 2);
        left.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        left.setTextFill(Paint.valueOf("#FFFFFF"));
        left.setStyle("-fx-background-color: #AAAAAA;");
        buttons.getChildren().add(left);
        left.setOnAction((e) -> shiftLeft());

        Button right = new Button("Right");
        right.setMinWidth((WIN_WIDTH / 4) - 2);
        right.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        right.setTextFill(Paint.valueOf("#FFFFFF"));
        right.setStyle("-fx-background-color: #AAAAAA;");
        buttons.getChildren().add(right);
        right.setOnAction((e) -> shiftRight());

        Button up = new Button("Up");
        up.setMinWidth((WIN_WIDTH / 4) - 2);
        up.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        up.setTextFill(Paint.valueOf("#FFFFFF"));
        up.setStyle("-fx-background-color: #AAAAAA;");
        buttons.getChildren().add(up);
        up.setOnAction((e) -> shiftUp());

        Button down = new Button("Down");
        down.setMinWidth((WIN_WIDTH / 4) - 2);
        down.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        down.setTextFill(Paint.valueOf("#FFFFFF"));
        down.setStyle("-fx-background-color: #AAAAAA;");
        buttons.getChildren().add(down);
        down.setOnAction((e) -> shiftDown());

        vbox.getChildren().add(buttons);

        // Keys
        stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {

            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                shiftUp();
            } else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
                shiftDown();
            } else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                shiftRight();
            } else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                shiftLeft();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            } else if (e.getCode() == KeyCode.R) {
                reset();
            } else if (e.getCode() == KeyCode.L) {
                game.endGame((byte) 1);
                update();
            } else if (e.getCode() == KeyCode.K) {
                debugColors();
            } else if (e.getCode() == KeyCode.DIGIT1) {
                scheme = 1;
                update();
            } else if (e.getCode() == KeyCode.DIGIT2) {
                scheme = 2;
                update();
            } else if (e.getCode() == KeyCode.DIGIT3) {
                scheme = 3;
                update();
            }

        });

    }

    /*
     * initLabels():    All text Labels (excluding the tiles) are set up here. First, the score and highScore labels are
     *                  set to appear below the grid of tiles. score displays the player's current score and highScore
     *                  displays the highest score the player has achieved. Lastly, a Label named message is placed
     *                  below the scores. It displays nothing upon initialization, but is set to display an appropriate
     *                  message once the player has won or lost the game. The scores have their own TilePane. All Labels
     *                  are placed in vbox.
     */
    private void initLabels() {

        score.setText("Score: " + game.getScore());
        score.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 20));
        score.setTextFill(Paint.valueOf("#AAAAAA"));
        score.setMinWidth(200);
        score.setAlignment(Pos.CENTER);
        scores.getChildren().add(score);

        highScore.setText("High Score: " + game.getHighScore());
        highScore.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 20));
        highScore.setTextFill(Paint.valueOf("#AAAAAA"));
        highScore.setMinWidth(200);
        highScore.setAlignment(Pos.CENTER);
        scores.getChildren().add(highScore);

        scores.setAlignment(Pos.CENTER);
        vbox.getChildren().add(scores);

        message.setAlignment(Pos.CENTER);
        message.setMinSize(WIN_WIDTH, 30);
        message.setFont(Font.font("sans serif", FontWeight.BOLD, FontPosture.REGULAR, 23));
        message.setTextFill(Paint.valueOf("#AAC080"));
        vbox.getChildren().add(message);

    }

    private void initAnims() {

        fadeMessage = new FadeTransition(Duration.millis(800), message);
        fadePrompts = new FadeTransition(Duration.millis(250), prompts);
        endGame = new SequentialTransition();

        fadeMessage.setFromValue(0);
        fadeMessage.setToValue(1);
        endGame.getChildren().add(fadeMessage);

        fadePrompts.setFromValue(0);
        fadePrompts.setToValue(1);
        endGame.getChildren().add(fadePrompts);

        tileChange = new FillTransition();

    }

    /*
     * updateTiles():   This method makes the tiles displayed on the GUI match those defined within game's board array.
     *                  A mimic board int array copies game's board. Two other ints are defined: row and col. A for
     *                  loop is set to run through the entirety of tiles and update the values of row and col to assign
     *                  the appropriate value from board to each Label in tiles. maskZeroes() is run last to make the
     *                  GUI look a little cleaner.
     */
    private void updateTiles() {

        board = game.getBoard();
        int row = 0;
        int col = 0;

        for (int i = 0; i < 16; i++, col++) {

            if (i % 4 == 0 && i != 0) {
                row++;
                col = 0;
            }

            tiles.get(i).setText(board[row][col] + "");
            colorize();

        }

    }

    /*
     * colorize():  This method uses three formulas to assign a red, green, and blue value to tiles based on their
     *              value. To prevent monotonous color, low values are assigned explicit values.
     */
    private void colorize() {

        int r1;
        int r2;
        int g1;
        int g2;
        int b1;
        int b2;
        int num;

        for (int i = 0; i < 16; i++) {

            num = Integer.parseInt(tiles.get(i).getText());

            if (num == 0) {

                r2 = findRVal(num);
                g2 = findGVal(num);
                b2 = findBVal(num);
                transTile(tiles.get(i), findColor(num / 2), findColor(num));
                tiles.get(i).setStyle("-fx-background-color: rgb(" + r2 + ", " + g2 + ", " + b2 + "); -fx-text-fill: rgb(" + r2 + ", " + g2 + ", " + b2 + ");");

            } else {

                r2 = findRVal(num);
                g2 = findGVal(num);
                b2 = findBVal(num);
                transTile(tiles.get(i), findColor(num / 2), findColor(num));
                tiles.get(i).setStyle("-fx-background-color: rgb(" + r2 + ", " + g2 + ", " + b2 + ");");

            }
        }

    }

    private void debugColors() {

        int num = 2;

        for (Label i : tiles) {

            i.setText(num + "");
            i.setBackground(new Background(new BackgroundFill(findColor(num), CornerRadii.EMPTY, Insets.EMPTY)));
            i.setTextFill(Paint.valueOf("#FFFFFF"));
            num *= 2;

        }

    }

    private int findRVal(int num) {

        if (scheme == 1) {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 170;
                case 4:
                    return 190;
                default:
                    return ((num * 4) + 100) % 215;

            }
        } else if (scheme == 2) {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 170;
                case 4:
                    return 212;
                default:
                    return ((num * 4) + 130) % 255;

            }
        } else {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 111;
                case 4:
                    return 210;
                default:
                    return ((num * 4) + 90) % 222;

            }
        }

    }

    private int findGVal(int num) {

        if (scheme == 1) {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 95;
                case 4:
                    return 100;
                default:
                    return (int)((Math.pow(num, 3.173)) + 139) % 182;

            }
        } else if (scheme == 2) {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 224;
                case 4:
                    return 43;
                default:
                    return (int)((Math.pow(num, -1.7)) + (num % 150)) % 255;

            }
        } else {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 106;
                case 4:
                    return 111;
                default:
                    return (int)((Math.pow(num, 1.15)) + 86) % 184;

            }
        }

    }

    private int findBVal(int num) {

        if (scheme == 1) {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 110;
                case 4:
                    return 70;
                default:
                    return (int)((Math.PI * num) + num * Math.sin(Math.PI)) % 178;

            }
        } else if (scheme == 2) {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 177;
                case 4:
                    return 120;
                default:
                    return (int)(Math.atan(num) + 179) % 255;

            }
        } else {
            switch (num) {

                case 0:
                    return 200;
                case 2:
                    return 175;
                case 4:
                    return 100;
                default:
                    return (int)((Math.PI * num) + 50 * Math.tan(Math.PI)) % 153;

            }
        }

    }

    private Color findColor(int num) {
        return Color.rgb(findRVal(num), findGVal(num), findBVal(num));
    }


    /*
     * update():    Whenever the player makes a move, update() is run to make the appropriate response to their action.
     *              First, game's own update method is run to handle all necessary game logic. updateTiles() is then
     *              run so that the GUI corresponds with game's board. Scores are updated. Finally, a check is made to
     *              determine if the player has won or lost the game. In both cases an appropriate message is placed
     *              in message and the user is prompted to restart or quit.
     */
    private void update() {

        game.update();
        updateTiles();
        score.setText("Score: " + game.getScore());
        highScore.setText("High Score: " + game.getHighScore());

        if (game.hasLost()) {
            message.setTextFill(Paint.valueOf("#C5252A"));
            message.setText("Game over! Play Again?");
            endGame.play();
            showPrompts();
        } else if (game.hasWon()) {
            message.setTextFill(Paint.valueOf("#AAC080"));
            message.setText("You're a winner! Play Again?");
            endGame.play();
            showPrompts();
        }

    }

    /*
     * initGame(Stage stage):   All parts of the game are initialized within this method. All other initialization
     *                          methods are run after all objects used within them are instantiated. A new Scene is
     *                          created to contain vbox and to be the size of the window. The method is run upon the
     *                          game being started.
     */
    private void initGame(Stage stage) {

        game = new Play2048();
        vbox = new VBox();
        buttons = new TilePane();
        scores = new TilePane();
        prompts = new TilePane();
        grid = new TilePane();
        score = new Label();
        highScore = new Label();
        message = new Label();
        tiles = new ArrayList<>();
        vbox.setStyle("-fx-background-color: #EEEEEE;");
        initTiles();
        initLabels();
        initControls(stage);
        initAnims();
        stage.setResizable(false);
        stage.setTitle("2048");
        stage.setScene(new Scene(vbox, WIN_WIDTH, WIN_HEIGHT));
        stage.show();

    }

    /*
     * shiftRight():    Moves all tiles right and updates GUI elements appropriately.
     */
    private void shiftRight() {

        game.shiftRight();
        update();

    }

    /*
     * shiftLeft():    Moves all tiles left and updates GUI elements appropriately.
     */
    private void shiftLeft() {

        game.shiftLeft();
        update();

    }

    /*
     * shiftUp():    Moves all tiles up and updates GUI elements appropriately.
     */
    private void shiftUp() {

        game.shiftUp();
        update();

    }

    /*
     * shiftDown():    Moves all tiles down and updates GUI elements appropriately.
     */
    private void shiftDown() {

        game.shiftDown();
        update();

    }

    /*
     * reset(): Instantiates a new Play2048 object in game, updates the GUI, and ensures any end-game messages/buttons
     *          are not displayed. Effectively restarts the game.
     */
    private void reset() {

        game = new Play2048();
        updateTiles();
        score.setText("Score: " + game.getScore());
        hidePrompts();

    }

    /*
     * showPrompts():   Sets the end-game prompts to be visible.
     */
    private void showPrompts() {

        yes.setVisible(true);
        no.setVisible(true);

    }

    /*
     * hidePrompts():   Sets the end-game prompts to be invisible. Any text in message is cleared.
     */
    private void hidePrompts() {

        yes.setVisible(false);
        no.setVisible(false);
        message.setText("");

    }

}