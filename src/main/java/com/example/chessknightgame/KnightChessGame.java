package com.example.chessknightgame;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class KnightChessGame extends Application {

    private static final int BOARD_SIZE = 8;
    private int[][] board;
    private boolean gameOver;
    private int moveCount;
    private int currentRow;
    private int currentCol;
    private Stage primaryStage;
    private boolean firstMove = true;
    private Preferences preferences;
    private int highScore;
    private boolean gameWon = false;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        preferences = Preferences.userRoot().node(this.getClass().getName());
        highScore = preferences.getInt("highScore", 0);

        initializeBoard();

        GridPane gridPane = createGridPane();
        drawBoard(gridPane);

        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Knight Chess Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializeBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        gameOver = false;
        moveCount = 0;
        currentRow = 0;
        currentCol = 0;
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Button button = new Button();
                button.setMinSize(50, 50);
                button.setMaxSize(50, 50);
                button.setId("button_" + row + "_" + col);
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(event -> handleSquareClick(finalRow, finalCol, gridPane));

                gridPane.add(button, col, row);
            }
        }

        return gridPane;
    }

    private void drawBoard(GridPane gridPane) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Button button = (Button) gridPane.lookup("#button_" + row + "_" + col);

                if (board[row][col] > 0) {
                    button.setText(Integer.toString(board[row][col]));
                    button.setStyle("-fx-background-color: #a0522d; -fx-text-fill: white;");
                    button.setDisable(true);
                } else {
                    String squareColor;
                    if (moveCount == 0) {
                        squareColor = ((row + col) % 2 == 0) ? "#f0d9b5" : "#b58863";
                    } else if (row == currentRow && col == currentCol) {
                        squareColor = ((row + col) % 2 == 0) ? "#f0d9b5" : "#b58863";
                    } else if (isValidMove(row, col, currentRow, currentCol)) {
                        squareColor = "#ffff00";
                    } else {
                        squareColor = ((row + col) % 2 == 0) ? "#f0d9b5" : "#b58863";
                    }

                    button.setText("");
                    button.setStyle("-fx-background-color: " + squareColor + ";");
                    button.setDisable(false);
                }
            }
        }
    }

    private void handleSquareClick(int row, int col, GridPane gridPane) {
        if (gameOver || gameWon) {
            initializeBoard();
            drawBoard(gridPane);
            return;
        }

        if (firstMove) {
            currentRow = row;
            currentCol = col;
            moveCount++;
            board[row][col] = moveCount;
            firstMove = false;
        } else {
            if (isValidMove(row, col, currentRow, currentCol)) {
                moveCount++;
                board[row][col] = moveCount;

                if (moveCount == BOARD_SIZE * BOARD_SIZE) {
                    gameWon = true;
                    drawBoard(gridPane);
                    displayGameWon(gridPane);
                    return;
                } else {
                    currentRow = row;
                    currentCol = col;
                }
            }
        }

        drawBoard(gridPane);

        if (!hasValidMove() && !firstMove) {
            gameOver = true;
            drawBoard(gridPane);
            displayGameOver(gridPane);
        }
    }

    private boolean isValidMove(int row, int col, int currentRow, int currentCol) {
        if (firstMove) {
            return true;
        }

        int rowDiff = Math.abs(row - currentRow);
        int colDiff = Math.abs(col - currentCol);
        return rowDiff == 1 && colDiff == 2 || rowDiff == 2 && colDiff == 1;
    }

    private void displayGameOver(GridPane gridPane) {
        GridPane gameOverPane = new GridPane();
        gameOverPane.setAlignment(Pos.CENTER);
        gameOverPane.setVgap(10);

        gameOverPane.setStyle("-fx-background-color: #f0f0f0; -fx-opacity: 0.9;");

        Text gameOverText = new Text("Game Over!");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        gameOverText.setFill(Color.RED);

        if (moveCount > highScore) {
            highScore = moveCount;
            preferences.putInt("highScore", highScore);
        }

        Text highScoreText = new Text("High Score: " + highScore);
        highScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        highScoreText.setFill(Color.BLACK);

        Text scoreText = new Text("Your Score: " + moveCount);
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setFill(Color.BLACK);

        Button restartButton = new Button("Restart Game");
        restartButton.setStyle("-fx-background-color: #a0522d; -fx-text-fill: white;");
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        restartButton.setOnAction(event -> {
            KnightChessGame newGame = new KnightChessGame();
            newGame.start(primaryStage);
        });

        gameOverPane.add(gameOverText, 0, 0);
        gameOverPane.add(highScoreText, 0, 1);
        gameOverPane.add(scoreText, 0, 2);
        gameOverPane.add(restartButton, 0, 3);

        GridPane.setHalignment(gameOverText, HPos.CENTER);
        GridPane.setHalignment(highScoreText, HPos.CENTER);
        GridPane.setHalignment(scoreText, HPos.CENTER);
        GridPane.setHalignment(restartButton, HPos.CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(gridPane, gameOverPane);

        Scene gameOverScene = new Scene(stackPane);
        primaryStage.setScene(gameOverScene);
        primaryStage.setResizable(false);
    }

    private void displayGameWon(GridPane gridPane) {
        GridPane gameWonPane = new GridPane();
        gameWonPane.setAlignment(Pos.CENTER);
        gameWonPane.setVgap(10);

        gameWonPane.setStyle("-fx-background-color: #f0f0f0; -fx-opacity: 0.9;");

        Text gameWonText = new Text("You win!");
        gameWonText.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        gameWonText.setFill(Color.GREEN);

        if (moveCount > highScore) {
            highScore = moveCount;
            preferences.putInt("highScore", highScore);
        }

        Button restartButton = new Button("Restart Game");
        restartButton.setStyle("-fx-background-color: #a0522d; -fx-text-fill: white;");
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        restartButton.setOnAction(event -> {
            KnightChessGame newGame = new KnightChessGame();
            newGame.start(primaryStage);
        });

        gameWonPane.add(gameWonText, 0, 0);
        gameWonPane.add(restartButton, 0, 1);

        GridPane.setHalignment(gameWonText, HPos.CENTER);
        GridPane.setHalignment(restartButton, HPos.CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(gridPane, gameWonPane);

        Scene gameWonScene = new Scene(stackPane);
        primaryStage.setScene(gameWonScene);
        primaryStage.setResizable(false);
    }

    private boolean hasValidMove() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isValidMove(row, col, currentRow, currentCol) && board[row][col] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}