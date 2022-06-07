package kosh.snake;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GameController {

    public void start(Stage primaryStage) {
        stage = primaryStage;
        timer.start();
    }

    public GameController(Coordinates startPos, int width, int height) {
        engine = new Engine(startPos, width, height);

    }

    private void keyControl() {
        stage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W, UP -> engine.makeStep(Direction.UP);
                    case A, LEFT -> engine.makeStep(Direction.LEFT);
                    case S, DOWN -> engine.makeStep(Direction.DOWN);
                    case D, RIGHT -> engine.makeStep(Direction.RIGHT);
                }
            }
        });
    }

    public void loadLevel(int levelNum) {

    }

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (now - lastActivated > timeout) {
                lastActivated = now;
                scoreLabel.setText(String.valueOf(engine.getScore()));
                keyControl();
                if (!engine.snakeIsAlive()) {
                    timer.stop();
                    //gameover
                }
            }
        }
    };

    public void setTimer(boolean run) {
        if (run) {
            timer.start();
        } else {
            timer.stop();
        }
    }


    private final GamePainter painter = new GamePainter();
    private Stage stage;
    private final Engine engine;
    private Label scoreLabel = new Label("Score: ");
    private long lastActivated = 0;
    private final int timeout = 500000000;
}
