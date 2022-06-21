package kosh.snake;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class GameController {

    public void startGame(Stage primaryStage, int levelNum) {
        stage = primaryStage;
        if (!loadLevel(levelNum)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Can't load level" + levelNum);
            alert.setContentText("Check levels files");
            alert.showAndWait();
            MainMenuWindow mainMenuWindow = new MainMenuWindow();
            return;
        }
        GameWindow gameView = new GameWindow(engine.getField().getWidth(), engine.getField().getHeight());
        engine.addSubscriber(gameView);
        gameView.drawInitialField(engine.getField());
        gameView.showGame(stage);
        timer.start();
    }

    private void keyControl() {
        stage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> engine.getSnake().setDirection(Direction.UP);
                case A, LEFT -> engine.getSnake().setDirection(Direction.LEFT);
                case S, DOWN -> engine.getSnake().setDirection(Direction.DOWN);
                case D, RIGHT -> engine.getSnake().setDirection(Direction.RIGHT);
                case ESCAPE, SPACE -> paused = !paused;
            }
        });
    }

    /*
    * Calls engine to load level
    * @param levelNum integer number of level to load
    * @return true if successfully loaded, else false
    * */
    private boolean loadLevel(int levelNum) {
        this.levelNum = levelNum;
        if (!engine.loadField("level" + levelNum + ".txt")) {
            System.err.println("Can't load field, check levels files");
            return false;
        }
        return true;
    }

    /*
    * Timer that triggers the game engine to make step
    * */
    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            keyControl();
            if (!paused) {
                if (now - lastTick > Constants.TIMEOUT / engine.getSnake().getSpeed()) {
                    engine.getSnake().updatePrevDirection();
                    lastTick = now;
                    if (!engine.makeStep()) {
                        timer.stop();
                        GameOverWindow gameOverWindow = new GameOverWindow();
                        gameOverWindow.showGameOver(stage, levelNum, engine.getScore());
                    }
                }
            }
        }
    };

    private final Engine engine = new Engine();
    private Stage stage;
    private long lastTick = 0;
    private boolean paused = false;
    private int levelNum;
}