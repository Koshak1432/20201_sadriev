package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MainMenuWindow {

    public MainMenuWindow() {
        menuStage.setTitle("Snake");
        Scene menuScene = new Scene(menuPane, Constants.INIT_WINDOW_WIDTH, Constants.INIT_WINDOW_HEIGHT);
        menuStage.setScene(menuScene);
        createBackground(Constants.MAIN_MENU_BACK);
        createButtons();
        controlButtons();
    }

    private void controlButtons() {
        menuButtons.get("start").setOnAction(event -> {
            LevelsWindow levelsWindow = new LevelsWindow();
            levelsWindow.showLevelsStage(menuStage);
        });

        menuButtons.get("records").setOnAction(event -> {
            //todo
//            RecordsWindow recordsWindow = new RecordsWindow();
//            recordsWindow.showRecords();
        });

        menuButtons.get("exit").setOnAction(event -> menuStage.close());
    }

    private void createBackground(String backName) {
        menuPane.setBackground(new Background(Util.createBackImage(backName)));
    }

    public static Stage getMenuStage() {
        return menuStage;
    }

    private void addButtonToMenu(String name, Button button) {
        button.setLayoutX(Constants.MENU_BUTTONS_START_X);
        button.setLayoutY(Constants.MENU_BUTTONS_START_Y + menuButtons.size() * Constants.MENU_BUTTONS_OFFSET);
        menuButtons.put(name, button);
        menuPane.getChildren().add(button);
    }

    private void createButtons() {
        SnakeButton startButton = new SnakeButton("Start");
        SnakeButton recordsButton = new SnakeButton("Records");
        SnakeButton exitButton = new SnakeButton("Exit");
        addButtonToMenu("start", startButton);
        addButtonToMenu("records", recordsButton);
        addButtonToMenu("exit", exitButton);
    }

    private final Pane menuPane = new Pane();
    private static final Stage menuStage = new Stage();
    private final Map<String, Button> menuButtons = new HashMap<>();
}
