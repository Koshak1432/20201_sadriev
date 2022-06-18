package kosh.snake;

import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public class GameController {

    public void startGame(Stage primaryStage, int levelNum) {
        stage = primaryStage;
        loadLevel(levelNum);
        engine.addSubscriber(gameView);
        gameView.drawInitialField(engine.getField());
        gameView.showGame(stage);
        timer.start();
    }

    private void keyControl() {
        stage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> {
                    engine.getSnake().setDirection(Direction.UP);
                }
                case A, LEFT -> {
                    engine.getSnake().setDirection(Direction.LEFT);
                }
                case S, DOWN -> {
                    engine.getSnake().setDirection(Direction.DOWN);
                }
                case D, RIGHT -> {
                    engine.getSnake().setDirection(Direction.RIGHT);
                }
                case ESCAPE, SPACE -> paused = !paused;
            }
        });
    }

    private void loadLevel(int levelNum) {
        engine = new Engine(new Coordinates(5,1));
//        engine.loadField("/level" + levelNum + ".txt");

    }

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
                        System.out.println("GAME OVER");
                        MainMenuWindow menuWindow = new MainMenuWindow();
//                        GameOverController overController = new GameOverController();
//                        overController.start(stage, engine.getScore());
                        //fill score table(output to file)
                        //restart
//                    //gameover
                    }
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

//    private final GamePainter painter = new GamePainter();
    private final GameWindow gameView = new GameWindow();
    private Engine engine;
    private static Stage stage;
    private long lastTick = 0;
    private boolean paused = false;
}