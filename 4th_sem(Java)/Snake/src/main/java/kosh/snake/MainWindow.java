package kosh.snake;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setTitle("Snake");
        Util.changeStage("MainScene.fxml", stage);
    }

    @FXML
    void initialize() {
        startButton.setOnAction(event -> {
            LevelsController levelsController = new LevelsController();
            levelsController.start(stage);
        });
        recordsButton.setOnAction(event -> Util.changeStage("RecordsScene.fxml", stage));
        exitButton.setOnAction(event -> System.exit(0));
    }

    private static Stage stage;
    @FXML
    private Button exitButton;
    @FXML
    private Button recordsButton;
    @FXML
    private Button startButton;
}
