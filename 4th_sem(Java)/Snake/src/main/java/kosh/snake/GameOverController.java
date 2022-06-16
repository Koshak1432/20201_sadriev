package kosh.snake;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameOverController {
    GameOverController() {

    }
    @FXML
    private Button backToMenuButton;

    @FXML
    private Label scoreLabel;

    public void start(Stage primaryStage, int score) {
        stage = primaryStage;
        stage.setTitle("Game over");
        Util.changeStage("GameOverScene.fxml", stage);
    }

    @FXML
    void initialize() {
        scoreLabel.setText("Your score: " + score);
        System.out.println("I'm here!, score is: " + score);
        backToMenuButton.setOnAction(event -> Util.changeStage("MainScene.fxml", stage));
    }

    private static Stage stage;
    private int score;
}
