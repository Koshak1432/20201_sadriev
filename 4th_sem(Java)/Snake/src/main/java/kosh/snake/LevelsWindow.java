package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LevelsWindow {

    public LevelsWindow() {
        levelsStage.setScene(levelsScene);
        createBackground(Constants.LEVELS_MENU_BACK);
        createButtons();
        controlButtons();
    }

    private void controlButtons() {
        buttons.get("level1").setOnAction(event -> {
            gameController.startGame(levelsStage, 1);
        });

        buttons.get("level2").setOnAction(event -> {

        });

        buttons.get("back").setOnAction(event -> {
            MainMenuWindow mainWindow = new MainMenuWindow();
        });
    }

    private void createBackground(String backName) {
        levelsPane.setBackground(new Background(Util.createBackImage(backName)));
    }

    private void addButtonToMenu(String name, Button button) {
        button.setLayoutX(Constants.MENU_START_X);
        button.setLayoutY(Constants.MENU_START_Y + buttons.size() * Constants.MENU_BUTTONS_OFFSET);
        buttons.put(name, button);
        levelsPane.getChildren().add(button);
    }

    public void showLevelsStage(Stage menuStage) {
        levelsStage = menuStage;
        levelsStage.setScene(levelsScene);
        levelsStage.show();
    }

    private void createButtons() {
        SnakeButton level1Button = new SnakeButton("Level 1");
        SnakeButton level2Button = new SnakeButton("Level 2");
        SnakeButton backButton = new SnakeButton("Back");
        addButtonToMenu("level1", level1Button);
        addButtonToMenu("level2", level2Button);
        addButtonToMenu("back", backButton);
    }

    GameController gameController = new GameController();
    private final Map<String, Button> buttons = new HashMap<>();
    private final Pane levelsPane = new Pane();
    private Stage levelsStage = new Stage();
    private final Scene levelsScene = new Scene(levelsPane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
}
