package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LevelsWindow {

    public LevelsWindow() {
        levelsStage.setScene(levelsScene);

    }

    private void addButtonToMenu(String name, Button button) {
        button.setLayoutX(Constants.MENU_START_X + levelsButtons.size() * Constants.MENU_BUTTONS_OFFSET);
        button.setLayoutY(Constants.MENU_START_Y);
        levelsButtons.put(name, button);
        levelsPane.getChildren().add(button);
    }

    private void createButtons() {
        SnakeButton startButton = new SnakeButton("Level 1");
        SnakeButton recordsButton = new SnakeButton("Level 2");
        SnakeButton exitButton = new SnakeButton("Back");
        addButtonToMenu("level1", startButton);
        addButtonToMenu("level2", recordsButton);
        addButtonToMenu("back", exitButton);
    }

    private Map<String, Button> levelsButtons = new HashMap<>();

    private Pane levelsPane = new Pane();
    private Stage levelsStage = new Stage();
    private Scene levelsScene = new Scene(levelsPane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
}
