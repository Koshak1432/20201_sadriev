package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MainMenuWindow {

    public MainMenuWindow() {
        menuStage.setTitle("Snake");
        Scene menuScene = new Scene(menuPane, Constants.INIT_WINDOW_WIDTH, Constants.INIT_WINDOW_HEIGHT);
        menuStage.setScene(menuScene);
        Util.setBackground(Constants.MAIN_MENU_BACK, menuPane);
        createButtons();
        controlButtons();
    }

    private void controlButtons() {
        menuButtons.get("start").setOnAction(event -> {
            LevelsWindow levelsWindow = new LevelsWindow();
            levelsWindow.showLevelsStage(menuStage);
        });

        menuButtons.get("records").setOnAction(event -> {
            RecordsWindow recordsWindow = new RecordsWindow();
            recordsWindow.showRecordsWindow(menuStage);
        });

        menuButtons.get("exit").setOnAction(event -> menuStage.close());
    }

    public static Stage getMenuStage() {
        return menuStage;
    }

    private void createButtons() {
        SnakeButton startButton = new SnakeButton("Start");
        SnakeButton recordsButton = new SnakeButton("Records");
        SnakeButton exitButton = new SnakeButton("Exit");
        Util.addButtonToMenu("start", startButton, menuPane, menuButtons);
        Util.addButtonToMenu("records", recordsButton, menuPane, menuButtons);
        Util.addButtonToMenu("exit", exitButton, menuPane, menuButtons);
    }

    private final Pane menuPane = new Pane();
    private static final Stage menuStage = new Stage();
    private final Map<String, Button> menuButtons = new HashMap<>();
}
