package kosh.snake;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LevelsController extends Application {

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

    }

    @FXML
    private Button backButton;

    @FXML
    private Button level1Button;

    @FXML
    private Button level2Button;

    @FXML
    void initialize() {
        backButton.setOnAction(event -> {
            Util.changeStage("MainScene.fxml", (Stage) backButton.getScene().getWindow());
        });
    }

    private Stage stage;
}
