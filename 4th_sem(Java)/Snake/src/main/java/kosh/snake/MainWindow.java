package kosh.snake;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Util.changeStage("MainScene.fxml", primaryStage);
        primaryStage.show();
    }

    @FXML
    void initialize() {
        startButton.setOnAction(event -> Util.changeStage("LevelsScene.fxml", (Stage) startButton.getScene().getWindow()));
        recordsButton.setOnAction(event -> Util.changeStage("RecordsScene.fxml", (Stage) recordsButton.getScene().getWindow()));
        exitButton.setOnAction(event -> System.exit(0));
    }

    @FXML
    private Button exitButton;

    @FXML
    private Button recordsButton;

    @FXML
    private Button startButton;
}
