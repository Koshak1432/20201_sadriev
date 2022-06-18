package kosh.snake;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LevelsController {

    public void start(Stage primaryStage) {
        stage = primaryStage;
        Util.changeStage("LevelsScene.fxml", stage);
    }

    @FXML
    private Button backButton;
    @FXML
    private Button level1Button;
    @FXML
    private Button level2Button;

    @FXML
    void initialize() {
        backButton.setOnAction(event -> Util.changeStage("MainScene.fxml", stage));
        level1Button.setOnAction(event -> {
//            gameController.loadLevel(1);
            gameController.startGame(stage, 1);
        });

        level2Button.setOnAction(event -> {
//            gameController.loadLevel(1); //2
            gameController.startGame(stage, 2);
        });
    }

    private static Stage stage;
    GameController gameController = new GameController();
}
