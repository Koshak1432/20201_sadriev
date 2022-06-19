package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class GameOverWindow {
    public GameOverWindow() {
        createBackground(Constants.GAME_OVER_MENU_BACK);
        initScoreLabel();
        createButtons();
        controlButtons();
    }

    private void initScoreLabel() {
        scoreLabel = new Label();
        scoreLabel.setFont(new Font(Constants.LABEL_FONT_FAMILY, Constants.LABEL_FONT_SIZE));
        scoreLabel.setLayoutX(Constants.MENU_BUTTONS_START_X);
        scoreLabel.setLayoutY(Constants.MENU_BUTTONS_START_Y / 2.);
        gameOverPane.getChildren().add(scoreLabel);
    }

    public void showGameOver(Stage stage, int score) {
        scoreLabel.setText("Your score: " + score);
        stage.setScene(new Scene(gameOverPane, Constants.INIT_WINDOW_WIDTH, Constants.INIT_WINDOW_HEIGHT));
        stage.show();
    }

    private void controlButtons() {
        buttons.get("back").setOnAction(event -> {
            MainMenuWindow mainWindow = new MainMenuWindow();
        });
    }

    private void createButtons() {
        SnakeButton backButton = new SnakeButton("Back");
        addButtonToMenu("back", backButton);
    }

    private void addButtonToMenu(String name, Button button) {
        button.setLayoutX(Constants.MENU_BUTTONS_START_X);
        button.setLayoutY(Constants.MENU_BUTTONS_START_Y + buttons.size() * Constants.MENU_BUTTONS_OFFSET);
        buttons.put(name, button);
        gameOverPane.getChildren().add(button);
    }


    private void createBackground(String backName) {
        gameOverPane.setBackground(new Background(Util.createBackImage(backName)));
    }


    private final Map<String, Button> buttons = new HashMap<>();
    private final Pane gameOverPane = new Pane();
    private Label scoreLabel;
}
